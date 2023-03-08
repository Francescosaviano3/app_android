package com.worldgn.connector;


class HttpServerResponse {
	private boolean mIsConnectionError;
	private String mServerResponse;
	public int mstatus;
	public Exception excemption;

	public HttpServerResponse(){

	}

	public HttpServerResponse(boolean error){
			mIsConnectionError = error;
			excemption = new NullPointerException("Default NPE");
	}

	public HttpServerResponse(boolean error,Exception exp){
		mIsConnectionError = error;
		excemption = exp;
	}

	public String response() {
		if(mServerResponse == null){
			mServerResponse = "";
		}
		return this.mServerResponse;
	}

	public boolean hasError() {
		return this.mIsConnectionError;
	}

	public void setConnectionError(boolean paramBoolean) {
		this.mIsConnectionError = paramBoolean;
	}

	public void setServerResponse(String paramString) {
		this.mServerResponse = paramString;
	}
	
	public void setexception(Exception ex){
		excemption = ex;
	}

	public  String r(){
		if(hasError()){
			String s = new String(String.valueOf(mstatus));
			s+= excemption.toString();
			return s;
		}else {
			return response();
		}
	}
}
