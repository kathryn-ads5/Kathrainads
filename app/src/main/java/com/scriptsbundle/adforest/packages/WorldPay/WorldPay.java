package com.kathryn.kathryn.packages.WorldPay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.kathryn.kathryn.packages.WorldPay.WorldPayError.ERROR_NO_NETWORK;

@SuppressLint("StaticFieldLeak")
public class WorldPay {
    /**
     * Default debugging tag.
     */
    public static final String TAG = "WorldPay";

    /**
     * The library version.
     */
    public static final String VERSION = "0.1";

    private static final String REUSABLE = "reusable";
    private static final String CLIENT_KEY = "clientKey";
    private static final String PAYMENT_METHOD = "paymentMethod";

    private static WorldPay instance;
    private String clientKey;
    private boolean reusable = false;

    public WorldPay() {

    }

    /**
     * Instance accessor
     *
     * @return Returns the instance of the class
     */
    public static WorldPay getInstance() {
        if (instance == null) {
            instance = new WorldPay();
        }
        return instance;
    }

    /**
     * Enable or disable debugging messages on android console. <br>
     * Debugging is disabled by default.
     *
     * @param enable
     */
    public static void setDebug(boolean enable) {
        DebugLogger.setDebug(enable);
        DebugLogger.setTag(TAG);
    }

    /**
     * Enable or disable debugging messages on android console using a custom tag. <br>
     * Debugging is disabled by default.
     *
     * @param enable
     * @param customTag
     */
    public static void setDebug(boolean enable, String customTag) {
        DebugLogger.setDebug(enable);
        DebugLogger.setTag(customTag);
    }

    /**
     * Asynchronously creates a {@link ResponseCard} token.
     *
     * @param context  The {@link Context}.
     * @param card     The {@link Card} to tokenize.
     * @param callback The {@link WorldPayResponse} callback.
     * @return {@link AsyncTask}
     */
    public  AsyncTask<Void, Void, HttpServerResponse> createTokenAsyncTask(final Context context,
                                                                                 final Card card,
                                                                                 final WorldPayResponse callback) {
        return createTokenAsyncTask(context, card, null, callback);
    }

    /**
     * Asynchronously creates an {@link AlternativePaymentMethod} token.
     *
     * @param context                  The {@link Context}.
     * @param alternativePaymentMethod The {@link AlternativePaymentMethod} to tokenize.
     * @param callback                 The {@link WorldPayApmResponse} callback.
     * @return {@link AsyncTask}
     */
    public AsyncTask<Void, Void, HttpServerResponse> createTokenAsyncTask(final Context context,
                                                                          final AlternativePaymentMethod alternativePaymentMethod,
                                                                          final WorldPayApmResponse callback) {
        final WorldPayError worldPayError = new WorldPayError();
        if (!isNetworkConnected(context)) {
            worldPayError.setError(ERROR_NO_NETWORK, "There is no network connectivity");
            return null;
        }

        return new AsyncTask<Void, Void, HttpServerResponse>() {
            @Override
            protected HttpServerResponse doInBackground(Void... params) {
                DebugLogger.d("createTokenAsyncTask [start] ...");
                try {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put(REUSABLE, reusable);
                    jsonObject.put(CLIENT_KEY, clientKey);
                    jsonObject.put(PAYMENT_METHOD, alternativePaymentMethod.getAsJSONObject());

                    DebugLogger.d("Created req: " + jsonObject.toString());

                    return WorldPayHttp.getInstance().createToken(jsonObject.toString());
                } catch (JSONException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_CREATING_REQUEST_JSON,
                            "Error while trying to create the request :" + e.getMessage());
                } catch (IllegalStateException | IOException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_RESPONSE_CONNECTION, "Connection error : " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(final HttpServerResponse serverResponse) {
                if (serverResponse == null) {
                    if (worldPayError.getCode() == 0) {
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_UNKNOWN, "Error while trying to get response.");
                    }
                    callback.onError(worldPayError);
                    return;
                }
                if (serverResponse.getStatusCode() == 200) {
                    try {
                        final AlternativePaymentMethodToken alternativePaymentMethodToken = AlternativePaymentMethodToken.valueOf(serverResponse.getResponse());
                        callback.onSuccess(alternativePaymentMethodToken);
                    } catch (JSONException e) {
                        DebugLogger.e(e);
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_MALFORMED_JSON, "Json parsing failed.");
                        callback.onError(worldPayError);
                    }
                } else {
                    ResponseError responseError = new ResponseError();
                    String responseString = serverResponse.getResponse();
                    try {
                        if (responseString != null) {
                            responseError.parseJsonString(responseString);
                        } else {
                            responseError.setHttpStatusCode(serverResponse.getStatusCode());
                        }
                        callback.onResponseError(responseError);
                    } catch (JSONException e) {
                        DebugLogger.e(e);
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_MALFORMED_JSON, "Json parsing failed.");
                        callback.onError(worldPayError);
                    }
                }
            }
        };
    }

    /**
     * Asynchronously creates a {@link ResponseCard} token.
     *
     * @param context      The {@link Context}.
     * @param responseCard The {@link ResponseCard} to tokenize.
     * @param callback     The {@link WorldPayResponse} callback.
     * @return {@link AsyncTask}
     */
    public AsyncTask<Void, Void, HttpServerResponse> createTokenAsyncTask(final Context context,
                                                                          final ResponseCard responseCard,
                                                                          final WorldPayResponse callback) {
        return createTokenAsyncTask(context, null, responseCard, callback);
    }

    private AsyncTask<Void, Void, HttpServerResponse> createTokenAsyncTask(final Context context,
                                                                           final Card card,
                                                                           final ResponseCard responseCard,
                                                                           final WorldPayResponse callback) {
        final WorldPayError worldPayError = new WorldPayError();
        if (!isNetworkConnected(context)) {
            worldPayError.setError(ERROR_NO_NETWORK, "There is no network connectivity");
            return null;
        }

        return new AsyncTask<Void, Void, HttpServerResponse>() {
            @Override
            protected HttpServerResponse doInBackground(Void... params) {
                DebugLogger.d("createTokenAsyncTask [start] ...");
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(REUSABLE, reusable);
                    jsonObject.put(CLIENT_KEY, clientKey);
                    jsonObject.put(PAYMENT_METHOD, card.getAsJSONObject());

                    DebugLogger.d("Created req: " + jsonObject.toString());

                    return WorldPayHttp.getInstance().createToken(jsonObject.toString());
                } catch (JSONException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_CREATING_REQUEST_JSON,
                            "Error while trying to create the request :" + e.getMessage());
                } catch (IllegalStateException | IOException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_RESPONSE_CONNECTION, "Connection error : " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(HttpServerResponse serverResponse) {
                if (serverResponse == null) {
                    if (worldPayError.getCode() == 0) {
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_UNKNOWN, "Error while trying to get response.");
                    }
                    callback.onError(worldPayError);
                    return;
                }
                if (serverResponse.getStatusCode() == 200) {
                    ResponseCard responseCard = new ResponseCard();
                    try {
                        responseCard.parseJsonString(serverResponse.getResponse());
                        callback.onSuccess(responseCard);

                    } catch (JSONException e) {
                        DebugLogger.e(e);
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_MALFORMED_JSON, "Json parsing failed.");
                        callback.onError(worldPayError);
                    }
                } else {
                    ResponseError responseError = new ResponseError();

                    String responseString = serverResponse.getResponse();
                    try {
                        if (responseString != null) {
                            responseError.parseJsonString(responseString);
                        } else {
                            responseError.setHttpStatusCode(serverResponse.getStatusCode());
                        }
                        callback.onResponseError(responseError);
                    } catch (JSONException e) {
                        DebugLogger.e(e);
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_MALFORMED_JSON, "Json parsing failed.");
                        callback.onError(worldPayError);
                    }
                }
            }
        };
    }

    /**
     * Checks reusable token
     *
     * @param context
     * @param reusableToken
     * @param callback
     * @return {@link AsyncTask}
     */
    public AsyncTask<Void, Void, HttpServerResponse> reuseTokenAsyncTask(final Context context,
                                                                         final ReusableToken reusableToken,
                                                                         final WorldPayResponseReusableToken callback) {
        return reuseTokenAsyncTask(context, reusableToken, null, callback);
    }

    private AsyncTask<Void, Void, HttpServerResponse> reuseTokenAsyncTask(final Context context,
                                                                          final ReusableToken reusableToken,
                                                                          final ResponseCard responseCard,
                                                                          final WorldPayResponseReusableToken callback) {
        final WorldPayError worldPayError = new WorldPayError();
        if (!isNetworkConnected(context)) {
            worldPayError.setError(ERROR_NO_NETWORK, "There is no network connectivity");
            return null;
        }

        return new AsyncTask<Void, Void, HttpServerResponse>() {
            @Override
            protected HttpServerResponse doInBackground(Void... params) {
                DebugLogger.d("reuseTokenAsyncTask [start] ...");

                try {
                    JSONObject jsonObject = reusableToken.getAsJSONObject();

                    return WorldPayHttp.getInstance().reuseToken(reusableToken.getToken(), jsonObject.toString());
                } catch (JSONException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_CREATING_REQUEST_JSON,
                            "Error while trying to create the request :" + e.getMessage());
                } catch (IllegalStateException | IOException e) {
                    DebugLogger.e(e);
                    worldPayError.setError(WorldPayError.ERROR_RESPONSE_CONNECTION, "Connection error : " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(HttpServerResponse serverResponse) {
                if (serverResponse == null) {
                    if (worldPayError.getCode() == 0) {
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_UNKNOWN, "Error while trying to get response.");
                    }
                    callback.onError(worldPayError);
                    return;
                }
                DebugLogger.d("serverResponse " + serverResponse.getStatusCode());
                if (serverResponse.getStatusCode() == 200) {
                    callback.onSuccess();
                } else {
                    ResponseError responseError = new ResponseError();

                    String responseString = serverResponse.getResponse();
                    try {
                        if (responseString != null) {
                            responseError.parseJsonString(responseString);
                        } else {
                            responseError.setHttpStatusCode(serverResponse.getStatusCode());
                        }
                        callback.onResponseError(responseError);
                    } catch (JSONException e) {
                        DebugLogger.e(e);
                        worldPayError.setError(WorldPayError.ERROR_RESPONSE_MALFORMED_JSON, "Json parsing failed.");
                        callback.onError(worldPayError);
                    }
                }
            }
        };
    }

    public String getClientKey() {
        return clientKey;
    }

    /**
     * Set the client key provided from worldpay.com. You can not use the library without setting
     * the client key.
     *
     * @param clientKey
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    /**
     * Retrieves if the library uses stored cards multiple times or not.
     *
     * @return
     */
    public boolean isReusable() {
        return reusable;
    }

    /**
     * Set if the library will use stored cards multiple times or not.
     *
     * @param reusable
     */
    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    private boolean isNetworkConnected(Context context) {
        //check for connectivity permission
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(ACCESS_NETWORK_STATE, context.getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            //we do not know so return true
            DebugLogger.d("There is no connectivity permission.");
            return true;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


}
