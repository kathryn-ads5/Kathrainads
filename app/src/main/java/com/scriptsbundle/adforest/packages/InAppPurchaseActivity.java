package com.kathryn.kathryn.packages;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class InAppPurchaseActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static String LICENSE_KEY = "";
    String porductId, packageId, packageType, activityName, billing_error, no_market = "", one_time = "";
    RestService restService;
    SettingsMain settingsMain;
    // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase);
        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            porductId = getIntent().getStringExtra("porductId");
            activityName = getIntent().getStringExtra("activityName");
            billing_error = getIntent().getStringExtra("billing_error");
            no_market = getIntent().getStringExtra("no_market");
            one_time = getIntent().getStringExtra("one_time");
            LICENSE_KEY = getIntent().getStringExtra("LICENSE_KEY");

        }
        settingsMain = new SettingsMain(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));

        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);
        setTitle(activityName);
        bp = new BillingProcessor(this, LICENSE_KEY, this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (BillingProcessor.isIabServiceAvailable(InAppPurchaseActivity.this)) {
                    if (bp.isOneTimePurchaseSupported()) {

                        bp.consumePurchase(porductId);
                        bp.purchase(InAppPurchaseActivity.this, porductId);
                    } else {
                        if (one_time.equals(""))
                            Toast.makeText(InAppPurchaseActivity.this, "One Time Purchase not Supported on your Device.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(InAppPurchaseActivity.this, one_time, Toast.LENGTH_SHORT).show();
                    }
                } else if (no_market.equals(""))
                    Toast.makeText(InAppPurchaseActivity.this, "Play Market app is not installed.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(InAppPurchaseActivity.this, no_market, Toast.LENGTH_SHORT).show();
            }
        }, 200);

    }

    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(InAppPurchaseActivity.this)) {

            JsonObject params = new JsonObject();
            params.addProperty("package_id", packageId);
            params.addProperty("payment_from", packageType);
            Log.d("info Send Checkout", params.toString());

            Call<ResponseBody> myCall = restService.postCheckout(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Checkout Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
                                settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                                adforest_getDataForThankYou();
                            } else
                                Toast.makeText(InAppPurchaseActivity.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(InAppPurchaseActivity.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(InAppPurchaseActivity.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(InAppPurchaseActivity.this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info ThankYou Details", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                JSONObject responseData = response.getJSONObject("data");

                                Log.d("info ThankYou object", "" + response.getJSONObject("data"));

                                Intent intent = new Intent(InAppPurchaseActivity.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                InAppPurchaseActivity.this.finish();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(InAppPurchaseActivity.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ThankYou error", String.valueOf(t));
                    Log.d("info ThankYou error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(InAppPurchaseActivity.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        Log.d("info purchase", "onProductPurchased: " + productId + details.toString());
        bp.consumePurchase(porductId);
        SettingsMain.showDilog(this);
        adforest_Checkout();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        showToast(billing_error);
        finish();
    }

    @Override
    public void onBillingInitialized() {
        Log.d("info purchase", "onBillingInitialized");

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("info string ", resultCode + "" + requestCode);
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
