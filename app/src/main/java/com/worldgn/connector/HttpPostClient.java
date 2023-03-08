package com.worldgn.connector;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

class HttpPostClient {

	private static final Random random = new Random();

	private static final int BACKOFF_MILLI_SECONDS = 1500;

	private static final int MAX_ATTEMPTS = 3;

	private static final String USER_AGENT = "Android";
	private static final String IDENTIFIRE = "121212";

	public static HttpServerResponse postRequest(String serverUrl, Map<String, String> params) {
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		HttpServerResponse response = null;
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			response = post(serverUrl, params);
			if(!response.hasError()){
				break;
			}

			delay(backoff);

			backoff *= 2;
		}
		if(response == null){
			response = new HttpServerResponse(true);
		}
		return response;
	}



	private static HttpServerResponse post(String endpoint, Map<String, String> params) {
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		StringBuilder bodyBuilder = new StringBuilder();
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}

		String body = bodyBuilder.toString();

		if(endpoint.startsWith("http://")){
			return postHttp(endpoint,body);
		}

		return postHttps(endpoint,body);
	}

	private static HttpServerResponse postHttp(String endpoint, String body){
		HttpServerResponse response = new HttpServerResponse();

		URL url;
		HttpURLConnection urlConnection = null;

		try {
			url = new URL(endpoint);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			urlConnection.setRequestProperty("identifire", IDENTIFIRE);
			urlConnection.setDoOutput(true);

			if(!TextUtils.isEmpty(body)){
				writeOutputStream(urlConnection.getOutputStream(), body);
			}
			int responseCode = urlConnection.getResponseCode();

			response.mstatus = responseCode;

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String server_response = readInputStream(urlConnection
						.getInputStream());
				response.setServerResponse(server_response);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			response.setConnectionError(true);
			response.setexception(e);
		} catch (IOException e) {
			e.printStackTrace();
			response.setConnectionError(true);
			response.setexception(e);
		}
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
		return response;
	}


	private static HttpServerResponse postHttps(String endpoint,
                                                String body) {
		HttpServerResponse response = new HttpServerResponse();

		URL url;
		HttpsURLConnection urlConnection = null;

		try {
			url = new URL(endpoint);
			urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			urlConnection.setDoOutput(true);

			if(!TextUtils.isEmpty(body)){
				writeOutputStream(urlConnection.getOutputStream(), body);
			}
			int responseCode = urlConnection.getResponseCode();

			response.mstatus = responseCode;

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String server_response = readInputStream(urlConnection
						.getInputStream());
				response.setServerResponse(server_response);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			response.setConnectionError(true);
			response.setexception(e);
		} catch (IOException e) {
			e.printStackTrace();
			response.setConnectionError(true);
			response.setexception(e);
		}
		if (urlConnection != null) {
			urlConnection.disconnect();
			urlConnection = null;
		}
		return response;
	}

	private static void writeOutputStream(OutputStream out, String body)
			throws IOException {
		out.write(body.getBytes());
		out.flush();
		out.close();
	}

	private static String readInputStream(InputStream in) {
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response.toString();
	}

	private static void delay(long backoff){
		try {
			Thread.sleep(backoff);
		} catch (InterruptedException e1) {

		}
	}

}