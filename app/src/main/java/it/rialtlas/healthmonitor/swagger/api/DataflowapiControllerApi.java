package it.rialtlas.healthmonitor.swagger.api;

/*
 * DataFlow API
 * Some custom description of API.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@datagrafservizi.it
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.rialtlas.healthmonitor.swagger.ApiCallback;
import it.rialtlas.healthmonitor.swagger.ApiClient;
import it.rialtlas.healthmonitor.swagger.ApiException;
import it.rialtlas.healthmonitor.swagger.ApiResponse;
import it.rialtlas.healthmonitor.swagger.Configuration;
import it.rialtlas.healthmonitor.swagger.Pair;
import it.rialtlas.healthmonitor.swagger.ProgressRequestBody;
import it.rialtlas.healthmonitor.swagger.ProgressResponseBody;
import it.rialtlas.healthmonitor.swagger.model.DataFlowApiDto;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Response;

public class DataflowapiControllerApi {
    private ApiClient apiClient;

    public DataflowapiControllerApi() {
        this(Configuration.getDefaultApiClient());
    }

    public DataflowapiControllerApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for getAllActionByUserUsingGET
     * @param userId userId (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getAllActionByUserUsingGETCall(String userId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataflow/api/user/{userId}/task"
                .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call getAllActionByUserUsingGETValidateBeforeCall(String userId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException("Missing the required parameter 'userId' when calling getAllActionByUserUsingGET(Async)");
        }

        Call call = getAllActionByUserUsingGETCall(userId, progressListener, progressRequestListener);
        return call;

    }

    /**
     * getAllActionByUser
     *
     * @param userId userId (required)
     * @return Object
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Object getAllActionByUserUsingGET(String userId) throws ApiException {
        ApiResponse<Object> resp = getAllActionByUserUsingGETWithHttpInfo(userId);
        return resp.getData();
    }

    /**
     * getAllActionByUser
     *
     * @param userId userId (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Object> getAllActionByUserUsingGETWithHttpInfo(String userId) throws ApiException {
       Call call = getAllActionByUserUsingGETValidateBeforeCall(userId, null, null);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * getAllActionByUser (asynchronously)
     *
     * @param userId userId (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call getAllActionByUserUsingGETAsync(String userId, final ApiCallback<Object> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = getAllActionByUserUsingGETValidateBeforeCall(userId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getAllDocsByUserUsingGET
     * @param userId userId (required)
     * @param workflowId workflowId (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getAllDocsByUserUsingGETCall(String userId, String workflowId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataflow/api/user/{userId}/workflow/{workflowId}/docs"
                .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId.toString()))
                .replaceAll("\\{" + "workflowId" + "\\}", apiClient.escapeString(workflowId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call getAllDocsByUserUsingGETValidateBeforeCall(String userId, String workflowId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException("Missing the required parameter 'userId' when calling getAllDocsByUserUsingGET(Async)");
        }

        // verify the required parameter 'workflowId' is set
        if (workflowId == null) {
            throw new ApiException("Missing the required parameter 'workflowId' when calling getAllDocsByUserUsingGET(Async)");
        }


        Call call = getAllDocsByUserUsingGETCall(userId, workflowId, progressListener, progressRequestListener);
        return call;

    }

    /**
     * getAllDocsByUser
     *
     * @param userId userId (required)
     * @param workflowId workflowId (required)
     * @return Object
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Object getAllDocsByUserUsingGET(String userId, String workflowId) throws ApiException {
        ApiResponse<Object> resp = getAllDocsByUserUsingGETWithHttpInfo(userId, workflowId);
        return resp.getData();
    }

    /**
     * getAllDocsByUser
     *
     * @param userId userId (required)
     * @param workflowId workflowId (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Object> getAllDocsByUserUsingGETWithHttpInfo(String userId, String workflowId) throws ApiException {
        Call call = getAllDocsByUserUsingGETValidateBeforeCall(userId, workflowId, null, null);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * getAllDocsByUser (asynchronously)
     *
     * @param userId userId (required)
     * @param workflowId workflowId (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call getAllDocsByUserUsingGETAsync(String userId, String workflowId, final ApiCallback<Object> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = getAllDocsByUserUsingGETValidateBeforeCall(userId, workflowId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getWorkFloReadyToStartwByUserIdUsingGET
     * @param userId userId (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getWorkFloReadyToStartwByUserIdUsingGETCall(String userId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataflow/api/user/{userId}/workflows/readyToStart"
                .replaceAll("\\{" + "userId" + "\\}", apiClient.escapeString(userId.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call getWorkFloReadyToStartwByUserIdUsingGETValidateBeforeCall(String userId, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {

        // verify the required parameter 'userId' is set
        if (userId == null) {
            throw new ApiException("Missing the required parameter 'userId' when calling getWorkFloReadyToStartwByUserIdUsingGET(Async)");
        }


        Call call = getWorkFloReadyToStartwByUserIdUsingGETCall(userId, progressListener, progressRequestListener);
        return call;

    }

    /**
     * getWorkFloReadyToStartwByUserId
     *
     * @param userId userId (required)
     * @return Object
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Object getWorkFloReadyToStartwByUserIdUsingGET(String userId) throws ApiException {
        ApiResponse<Object> resp = getWorkFloReadyToStartwByUserIdUsingGETWithHttpInfo(userId);
        return resp.getData();
    }

    /**
     * getWorkFloReadyToStartwByUserId
     *
     * @param userId userId (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Object> getWorkFloReadyToStartwByUserIdUsingGETWithHttpInfo(String userId) throws ApiException {
        Call call = getWorkFloReadyToStartwByUserIdUsingGETValidateBeforeCall(userId, null, null);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * getWorkFloReadyToStartwByUserId (asynchronously)
     *
     * @param userId userId (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call getWorkFloReadyToStartwByUserIdUsingGETAsync(String userId, final ApiCallback<Object> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = getWorkFloReadyToStartwByUserIdUsingGETValidateBeforeCall(userId, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for getWorkFlowByUserIdUsingPOST
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getWorkFlowByUserIdUsingPOSTCall(DataFlowApiDto dataFlowApiDto, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = dataFlowApiDto;

        // create path and map variables
        String localVarPath = "/dataflow/api/user/workflows";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
                "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call getWorkFlowByUserIdUsingPOSTValidateBeforeCall(DataFlowApiDto dataFlowApiDto, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {

        // verify the required parameter 'dataFlowApiDto' is set
        if (dataFlowApiDto == null) {
            throw new ApiException("Missing the required parameter 'dataFlowApiDto' when calling getWorkFlowByUserIdUsingPOST(Async)");
        }


        Call call = getWorkFlowByUserIdUsingPOSTCall(dataFlowApiDto, progressListener, progressRequestListener);
        return call;

    }

    /**
     * getWorkFlowByUserId
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @return Map&lt;String, Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Map<String, Object> getWorkFlowByUserIdUsingPOST(DataFlowApiDto dataFlowApiDto) throws ApiException {
        ApiResponse<Map<String, Object>> resp = getWorkFlowByUserIdUsingPOSTWithHttpInfo(dataFlowApiDto);
        return resp.getData();
    }

    /**
     * getWorkFlowByUserId
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @return ApiResponse&lt;Map&lt;String, Object&gt;&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Map<String, Object>> getWorkFlowByUserIdUsingPOSTWithHttpInfo(DataFlowApiDto dataFlowApiDto) throws ApiException {
        Call call = getWorkFlowByUserIdUsingPOSTValidateBeforeCall(dataFlowApiDto, null, null);
        Type localVarReturnType = new TypeToken<Map<String, Object>>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * getWorkFlowByUserId (asynchronously)
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call getWorkFlowByUserIdUsingPOSTAsync(DataFlowApiDto dataFlowApiDto, final ApiCallback<Map<String, Object>> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = getWorkFlowByUserIdUsingPOSTValidateBeforeCall(dataFlowApiDto, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Map<String, Object>>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for healthUsingGET
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call healthUsingGETCall(final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataflow/api/healthcheck";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call healthUsingGETValidateBeforeCall(final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {


       Call call = healthUsingGETCall(progressListener, progressRequestListener);
        return call;

    }

    /**
     * health
     *
     * @return Object
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Object healthUsingGET() throws ApiException {
        ApiResponse<Object> resp = healthUsingGETWithHttpInfo();
        return resp.getData();
    }

    /**
     * health
     *
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Object> healthUsingGETWithHttpInfo() throws ApiException {
       Call call = healthUsingGETValidateBeforeCall(null, null);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * health (asynchronously)
     *
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call healthUsingGETAsync(final ApiCallback<Object> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = healthUsingGETValidateBeforeCall(progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
    /**
     * Build call for startWorkFlowUsingPOST
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @param progressListener Progress listener
     * @param progressRequestListener Progress request listener
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call startWorkFlowUsingPOSTCall(String dataFlowApiDto, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataflow/api/user/workflow/start"
                .replaceAll("\\{" + "dataFlowApiDto" + "\\}", apiClient.escapeString(dataFlowApiDto.toString()));

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = {
                "*/*"
        };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {
                "application/json"
        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if(progressListener != null) {
            apiClient.getHttpClient().networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                }
            });
        }

        String[] localVarAuthNames = new String[] {  };
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private Call startWorkFlowUsingPOSTValidateBeforeCall(String dataFlowApiDto, final ProgressResponseBody.ProgressListener progressListener, final ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException {

        // verify the required parameter 'dataFlowApiDto' is set
        if (dataFlowApiDto == null) {
            throw new ApiException("Missing the required parameter 'dataFlowApiDto' when calling startWorkFlowUsingPOST(Async)");
        }


        Call call = startWorkFlowUsingPOSTCall(dataFlowApiDto, progressListener, progressRequestListener);
        return call;

    }

    /**
     * startWorkFlow
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @return Object
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public Object startWorkFlowUsingPOST(String dataFlowApiDto) throws ApiException {
        ApiResponse<Object> resp = startWorkFlowUsingPOSTWithHttpInfo(dataFlowApiDto);
        return resp.getData();
    }

    /**
     * startWorkFlow
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<Object> startWorkFlowUsingPOSTWithHttpInfo(String dataFlowApiDto) throws ApiException {
       Call call = startWorkFlowUsingPOSTValidateBeforeCall(dataFlowApiDto, null, null);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * startWorkFlow (asynchronously)
     *
     * @param dataFlowApiDto dataFlowApiDto (required)
     * @param callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     */
    public Call startWorkFlowUsingPOSTAsync(String dataFlowApiDto, final ApiCallback<Object> callback) throws ApiException {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener() {
                @Override
                public void update(long bytesRead, long contentLength, boolean done) {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        Call call = startWorkFlowUsingPOSTValidateBeforeCall(dataFlowApiDto, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>(){}.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
