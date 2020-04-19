package com.kathryn.kathryn.packages;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import androidx.appcompat.widget.Toolbar;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.packages.WorldPay.Card;
import com.kathryn.kathryn.packages.WorldPay.EndPoints.RetrofitWorldPayClient;
import com.kathryn.kathryn.packages.WorldPay.EndPoints.WorldPayEndPoint;
import com.kathryn.kathryn.packages.WorldPay.HttpServerResponse;
import com.kathryn.kathryn.packages.WorldPay.ResponseCard;
import com.kathryn.kathryn.packages.WorldPay.ResponseError;
import com.kathryn.kathryn.packages.WorldPay.WorldPay;
import com.kathryn.kathryn.packages.WorldPay.WorldPayError;
import com.kathryn.kathryn.packages.WorldPay.WorldPayResponse;
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
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WorldPayIntegration extends AppCompatActivity {

    SettingsMain settingsMain;

    EditText cardNumber, cvc,name;
    Spinner monthSpinner, yearSpinner;
    Button checkout;
    TextView textViewCardNo, textViewExpTit, textViewMonth, textViewYear, textViewCVC;
    ProgressDialog progressDialog;
    WorldPay worldPay;
    RestService restService;
    Card card;
    String clientKey;
    String serviceKey;
    String packageId, packageType, price;
    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;

    ProgressDialog dialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_pay_integration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            price = getIntent().getStringExtra("amount");
        }


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait...");
        clientKey = settingsMain.getWorldPayCreds().clientKey;
        serviceKey = settingsMain.getWorldPayCreds().serviceKey;
        name = findViewById(R.id.name);
        worldPay = WorldPay.getInstance();
        worldPay.setClientKey(clientKey);
        // decide whether you want to charge this card multiple times or only once
        worldPay.setReusable(true);
        // set validation type advanced or basic
        Card.setValidationType(Card.VALIDATION_TYPE_ADVANCED);

        progressDialog = new ProgressDialog(this);

        cardNumber = (EditText) findViewById(R.id.editText9);
        cvc = (EditText) findViewById(R.id.cvc);
        monthSpinner = (Spinner) findViewById(R.id.spinner);
        yearSpinner = (Spinner) findViewById(R.id.spinner2);
        checkout = (Button) findViewById(R.id.button4);

        checkout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        textViewCardNo = (TextView) findViewById(R.id.textView23);
        textViewCVC = (TextView) findViewById(R.id.textView24);
        textViewExpTit = (TextView) findViewById(R.id.textView20);
        textViewMonth = (TextView) findViewById(R.id.textView21);
        textViewYear = (TextView) findViewById(R.id.textView22);
        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);

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
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("2019");
        arrayList.add("2020");
        arrayList.add("2021");
        arrayList.add("2022");
        arrayList.add("2023");
        arrayList.add("2024");
        arrayList.add("2025");
        arrayList.add("2026");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(WorldPayIntegration.this,
                R.layout.spinner_item_medium, arrayList);
        yearSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(WorldPayIntegration.this,
                R.layout.spinner_item_medium, WorldPayIntegration.this.getResources().getStringArray(R.array.month_array));
        monthSpinner.setAdapter(adapter2);


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNo = cardNumber.getText().toString();
                int expiryMonth = getInteger(monthSpinner); //any month in the future
                int expiryYear = getInteger(yearSpinner); // any year in the future. '2018' would work also!
                String cvv = cvc.getText().toString();  // cvv of the test card



                if (validate()) {
                    card = new Card();
                    card.setHolderName(name.getText().toString());
                    card.setCardNumber(cardNo);
                    card.setCvc(cvv);
                    card.setExpiryMonth(String.valueOf(expiryMonth));
                    card.setExpiryYear(String.valueOf(expiryYear));
                    dialog.show();
                    AsyncTask<Void, Void, HttpServerResponse> createTokenAsyncTask = worldPay.createTokenAsyncTask(WorldPayIntegration.this, card, new WorldPayResponse() {

                        @Override
                        public void onSuccess(ResponseCard responseCard) {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", serviceKey);
                            headers.put("Content-Type", "application/json");

                            JsonObject params = new JsonObject();
                            int a = (int) Math.round(Double.parseDouble(price));
                            params.addProperty("amount", String.valueOf(a*100));
                            params.addProperty("token", responseCard.getToken());
                            params.addProperty("currencyCode", settingsMain.getWorldPayCreds().currencySign);
                            params.addProperty("orderDescription", packageType);

                            WorldPayEndPoint client = RetrofitWorldPayClient.getRetrofitInstance().create(WorldPayEndPoint.class);
                            Call<ResponseBody> call = client.chargeOrder(headers, params);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()){
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            if (jsonObject.getString("paymentStatus").equals("SUCCESS")) {
                                                dialog.dismiss();
                                                adforest_Checkout();
                                            } else {
                                                dialog.dismiss();
                                                settingsMain.hideDilog();
                                                Toast.makeText(WorldPayIntegration.this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            dialog.dismiss();
                                            settingsMain.hideDilog();
                                        }
                                    }else{
                                        dialog.dismiss();
                                        Toast.makeText(WorldPayIntegration.this, PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    dialog.dismiss();
                                    settingsMain.hideDilog();
                                    Toast.makeText(WorldPayIntegration.this, PaymentToastsModel.response_fail, Toast.LENGTH_SHORT).show();
                                }
                            });
                            //handle success
                        }

                        @Override
                        public void onResponseError(ResponseError responseError) {
                            dialog.dismiss();
                            settingsMain.hideDilog();
                                Toast.makeText(WorldPayIntegration.this, PaymentToastsModel.token_fail, Toast.LENGTH_SHORT).show();
                            //handle error
                        }

                        @Override
                        public void onError(WorldPayError worldpayError) {
                            dialog.dismiss();
                            settingsMain.hideDilog();
                            Toast.makeText(WorldPayIntegration.this, PaymentToastsModel.token_fail, Toast.LENGTH_SHORT).show();
                        }

                    });

                    createTokenAsyncTask.execute();
                }


            }
        });
        adforest_getViews();


    }




    private void adforest_getViews() {

        if (SettingsMain.isConnectingToInternet(WorldPayIntegration.this)) {

            SettingsMain.showDilog(WorldPayIntegration.this);

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
                                checkout.setText(jsonObjectThis.getString("btn_text"));

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
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(WorldPayIntegration.this,
                                        R.layout.spinner_item_medium, arrayList);

                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(WorldPayIntegration.this,
                                        R.layout.spinner_item_medium, WorldPayIntegration.this.getResources().getStringArray(R.array.month_array));
                                yearSpinner.setAdapter(adapter);
                                monthSpinner.setAdapter(adapter2);

                            } else {
                                Toast.makeText(WorldPayIntegration.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(WorldPayIntegration.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean validate() {
        if (name.getText().toString().equals("")) {
            name.setError("Name cannot be null/empty");
            name.requestFocus();
            return false;
        } else if (cardNumber.getText().toString().equals("")) {
            cardNumber.setError(stringCardError);
            cardNumber.requestFocus();
            return false;
        } else if (cardNumber.getText().toString().length() < 19) {
            cardNumber.setError(stringCardError);
            cardNumber.requestFocus();
            return false;
        }
        if (cvc.getText().toString().equals("")) {
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        } else if (cvc.getText().toString().length() < 3) {
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        }

        return true;
    }

    private Integer getInteger(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(WorldPayIntegration.this)) {

            settingsMain.showDilog(WorldPayIntegration.this);
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
                                settingsMain.hideDilog();
                            Toast.makeText(WorldPayIntegration.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
                        Log.d("info Checkout ", "Null Pointer  Exception" + t.getLocalizedMessage());
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
            Toast.makeText(WorldPayIntegration.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(WorldPayIntegration.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(WorldPayIntegration.this));
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

                                Intent intent = new Intent(WorldPayIntegration.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                WorldPayIntegration.this.finish();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(WorldPayIntegration.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(WorldPayIntegration.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


}
