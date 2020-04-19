package com.kathryn.kathryn.packages;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.packages.Authorize2.ApiClientAuthorize;
import com.kathryn.kathryn.packages.Authorize2.EndPointsAuthorize;
import com.kathryn.kathryn.packages.Authorize2.Json;
import com.kathryn.kathryn.packages.adapter.PaymentToastsModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizeNet extends AppCompatActivity {
    EndPointsAuthorize apiService;
    EditText cardNumber, cvc;
    Spinner monthSpinner, yearSpinner;
    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;
    String packageType;
    Button chkout;
    String cvcNo, cardNo;
    int month, year;
    SettingsMain settingsMain;
    RestService restService;
    TextView textViewCardNo, textViewExpTit, textViewMonth, textViewYear, textViewCVC;
    String packageId = "", price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_net);
        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            price = getIntent().getStringExtra("amount");
        }
        cardNumber = findViewById(R.id.editText9);
        cvc = findViewById(R.id.cvc);
        monthSpinner = findViewById(R.id.spinner);
        yearSpinner = findViewById(R.id.spinner2);
        chkout = findViewById(R.id.button4);
        chkout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        textViewCardNo = findViewById(R.id.textView23);
        textViewCVC = findViewById(R.id.textView24);
        textViewExpTit = findViewById(R.id.textView20);
        textViewMonth = findViewById(R.id.textView21);
        textViewYear = findViewById(R.id.textView22);
        apiService =
                ApiClientAuthorize.getClient().create(EndPointsAuthorize.class);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AuthorizeNet.this,
                R.layout.spinner_item_medium, AuthorizeNet.this.getResources().getStringArray(R.array.year_array));

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AuthorizeNet.this,
                R.layout.spinner_item_medium, AuthorizeNet.this.getResources().getStringArray(R.array.month_array));
        yearSpinner.setAdapter(adapter);
        monthSpinner.setAdapter(adapter2);
        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    getToken();
                }
            }
        });
        adforest_getViews();
        cardNumber.addTextChangedListener(new TextWatcher() {

            private static final char space = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }
        });


    }

    private Integer getInteger(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void adforest_getViews() {

        if (SettingsMain.isConnectingToInternet(AuthorizeNet.this)) {

            SettingsMain.showDilog(AuthorizeNet.this);

            Log.d("info packageId", packageId);
            Call<ResponseBody> myCall = restService.getStripeDetailsView(UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Stripe Responce", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {

                                Log.d("Info Stripe Data", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectThis = response.getJSONObject("data").getJSONObject("form");
                                setTitle(response.getJSONObject("data").getString("page_title"));

                                stringCardError = response.getJSONObject("data").getJSONObject("error").getString("card_number");
                                stringExpiryError = response.getJSONObject("data").getJSONObject("error").getString("expiration_date");
                                stringCVCError = response.getJSONObject("data").getJSONObject("error").getString("invalid_cvc");
                                stringInvalidCard = response.getJSONObject("data").getJSONObject("error").getString("card_details");

                                cardNumber.setHint(jsonObjectThis.getString("card_input_text"));
                                cvc.setHint(jsonObjectThis.getString("cvc_input_text"));
                                chkout.setText(jsonObjectThis.getString("btn_text"));

                                textViewCardNo.setText(jsonObjectThis.getString("card_input_text"));
                                textViewExpTit.setText(jsonObjectThis.getString("select_title"));
                                textViewMonth.setText(jsonObjectThis.getString("select_month"));
                                textViewYear.setText(jsonObjectThis.getString("select_year"));
                                textViewCVC.setText(jsonObjectThis.getString("cvc_input_text"));

                                JSONArray jsonArray = jsonObjectThis.getJSONArray("select_option_year");

                                ArrayList<String> arrayList = new ArrayList<>();
                                //Iterate the jsonArray and print the info of JSONObjects
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    arrayList.add(jsonArray.getString(i));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(AuthorizeNet.this,
                                        R.layout.spinner_item_medium, arrayList);

                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AuthorizeNet.this,
                                        R.layout.spinner_item_medium, AuthorizeNet.this.getResources().getStringArray(R.array.month_array));
                                yearSpinner.setAdapter(adapter);
                                monthSpinner.setAdapter(adapter2);

                            } else {
                                Toast.makeText(AuthorizeNet.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();

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
                    SettingsMain.hideDilog();
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(AuthorizeNet.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        cvcNo = cvc.getText().toString();
        cardNo = cardNumber.getText().toString();
        month = getInteger(monthSpinner);
        year = getInteger(yearSpinner);


        if (cardNo.equals("")) {
            cardNumber.setError(stringInvalidCard);
            cardNumber.requestFocus();
            return false;
        } else if (cardNo.length() < 16) {
            cardNumber.setError(stringInvalidCard);
            cardNumber.requestFocus();
            return false;
        } else if (cvcNo.equals("")) {
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        } else if (cvcNo.length() < 3) {
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        }
        return true;
    }

    private void getToken(){

        JsonObject jsonObject1 = new JsonObject();
        JsonObject jsonObject2 = new JsonObject();
        JsonObject jsonObject3 = new JsonObject();
        JsonObject jsonObject4 = new JsonObject();
        JsonObject jsonObject5 = new JsonObject();
        JsonObject jsonObject6 = new JsonObject();
        JsonObject jsonObject7 = new JsonObject();
        JsonObject jsonObject8 = new JsonObject();
        JsonObject jsonObject9 = new JsonObject();
        JsonObject jsonObject10 = new JsonObject();
        JsonObject finalJsonOject;

        try {


            jsonObject8.add("merchantAuthentication", jsonObject9);
            {
                jsonObject9.addProperty("name", settingsMain.getAuthorizeNetModel().name);
                jsonObject9.addProperty("transactionKey", settingsMain.getAuthorizeNetModel().transactionKey);
            }
            jsonObject7.addProperty("refId", settingsMain.getAuthorizeNetModel().referenceNum);
            {
                jsonObject5.addProperty("transactionType", "authCaptureTransaction");
                jsonObject4.addProperty("amount", price);
                jsonObject3.add("payment", jsonObject2);
                {
                    jsonObject2.add("creditCard", jsonObject1);
                    {
                        cardNo = cardNo.replace("-","");
                        jsonObject1.addProperty("cardNumber", cardNo);
                        jsonObject1.addProperty("expirationDate", yearSpinner.getSelectedItem().toString() + "-" + monthSpinner.getSelectedItem().toString());
                        jsonObject1.addProperty("cardCode", cvcNo);
                    }
                }
            }
            JsonObject jsonObject = Json.mergeObjects(jsonObject5, jsonObject4, jsonObject3);
            jsonObject6.add("transactionRequest", jsonObject);


            jsonObject10.add("createTransactionRequest", jsonObject7);
            jsonObject10.add("createTransactionRequest", jsonObject6);
            finalJsonOject = Json.mergeObjects(jsonObject8, jsonObject7, jsonObject6);
            jsonObject10.add("createTransactionRequest", finalJsonOject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        Call<ResponseBody> myCall = apiService.getToken(jsonObject10, map);
        settingsMain.showDilog(this);
        myCall.enqueue(new Callback<ResponseBody>() {


            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                try{
                        String res = responseObj.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        String status = jsonObject.getJSONObject("messages").getJSONArray("message").getJSONObject(0).getString("text");
                        if (status.equals("Successful.")) {
                            Toast.makeText(AuthorizeNet.this, PaymentToastsModel.payment_success, Toast.LENGTH_SHORT).show();
                            adforest_Checkout();
                        } else {
                            settingsMain.hideDilog();
                            Toast.makeText(AuthorizeNet.this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                        }


                }catch (Exception e){
                    e.printStackTrace();
                    settingsMain.hideDilog();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                settingsMain.hideDilog();
            }
        });
    }


    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(AuthorizeNet.this)) {

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
                                Toast.makeText(AuthorizeNet.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(AuthorizeNet.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(AuthorizeNet.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(AuthorizeNet.this));
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

                                Intent intent = new Intent(AuthorizeNet.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                AuthorizeNet.this.finish();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(AuthorizeNet.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("info ThankYou error", t.getMessage() + t.getCause() + t.fillInStackTrace());
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(AuthorizeNet.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

}

