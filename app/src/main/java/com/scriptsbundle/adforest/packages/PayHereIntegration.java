package com.kathryn.kathryn.packages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.packages.adapter.PaymentToastsModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayHereIntegration extends AppCompatActivity {
    int PAYHERE_REQUEST = 111;
    TextView textView;
    ConstraintLayout mainLayout;
    ProgressDialog dialog;
    Button button;

    String packageType;
    private String packageId= "";
    String price;
    RestService restService;
    SettingsMain settingsMain;
    String packageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_here_integration);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");


        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            price = getIntent().getStringExtra("amount");
            packageName = getIntent().getStringExtra("packageName");
        }


        dialog.show();
        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {
            mainLayout = findViewById(R.id.mainLayout);
            mainLayout.setVisibility(View.GONE);
            textView = findViewById(R.id.textView);
            InitRequest req = new InitRequest();
            req.setMerchantId(settingsMain.getPayHereModel().getMerchant_id()); // Your Merchant ID
            req.setMerchantSecret(settingsMain.getPayHereModel().getMerchant_secret_id()); // Your Merchant secret
            req.setAmount(Double.parseDouble(price)); // Amount which the customer should pay
            req.setCurrency(settingsMain.getPayHereModel().getCurrency()); // Currency
            req.setOrderId("ItemNo12345"); // Unique ID for your payment transaction
            req.setItemsDescription(packageName);
            req.getCustomer().setFirstName(settingsMain.getPayHereModel().getFirst_name());
            req.getCustomer().setLastName(settingsMain.getPayHereModel().getLast_name());
            req.getCustomer().setEmail(settingsMain.getPayHereModel().getEmail());
            req.getCustomer().setPhone(settingsMain.getPayHereModel().getPhone());
            req.getCustomer().getAddress().setAddress(settingsMain.getPayHereModel().getAddress());
            req.getCustomer().getAddress().setCity(settingsMain.getPayHereModel().getCity());
            req.getCustomer().getAddress().setCountry(settingsMain.getPayHereModel().getCountry());

            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

            if (settingsMain.getPayHereModel().getMode().equals("sandbox"))
                PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
            else
                PHConfigs.setBaseUrl(PHConfigs.LIVE_URL);
            startActivityForResult(intent, PAYHERE_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO process response
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            String msg;
            if (response.isSuccess()) {
                dialog.dismiss();
                adforest_Checkout();
//                mainLayout.setVisibility(View.VISIBLE);
                msg = "Activity result:" + response.getData().toString();

                Log.d("PayhereResponse", msg);
            } else {
                msg = "Activity result:" + response.toString();
                Log.d("PayhereResponse", msg);
                Toast.makeText(this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else{
            dialog.dismiss();
            Toast.makeText(this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(PayHereIntegration.this)) {

            settingsMain.showDilog(PayHereIntegration.this);
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
                            } else{
                                dialog.dismiss();
                                Toast.makeText(PayHereIntegration.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                finish();
                            }


                        }else{
                            dialog.dismiss();
                            Toast.makeText(PayHereIntegration.this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                            finish();
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
                    dialog.dismiss();
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
            Toast.makeText(PayHereIntegration.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(PayHereIntegration.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(PayHereIntegration.this));
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

                                Intent intent = new Intent(PayHereIntegration.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                PayHereIntegration.this.finish();
                            } else {
                                dialog.dismiss();
                                SettingsMain.hideDilog();
                                Toast.makeText(PayHereIntegration.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    SettingsMain.hideDilog();
                    Log.d("info ThankYou error", String.valueOf(t));
                    Log.d("info ThankYou error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    finish();
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(PayHereIntegration.this, "Internet error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
