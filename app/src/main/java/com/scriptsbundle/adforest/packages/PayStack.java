package com.kathryn.kathryn.packages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import com.kathryn.kathryn.packages.PaystackControllers.PaystackEndPoint;
import com.kathryn.kathryn.packages.PaystackControllers.RetrofitPaystackClient;
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

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayStack extends AppCompatActivity {

    SettingsMain settingsMain;

    EditText cardNumber, cvc;
    Spinner monthSpinner, yearSpinner;
    Button chkout;
    TextView textViewCardNo, textViewExpTit, textViewMonth, textViewYear, textViewCVC;
    ProgressDialog progressDialog;
    RestService restService;
    String packageType;
    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;
    private String packageId= "";
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_stack);


        settingsMain = new SettingsMain(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        PaystackSdk.initialize(getApplicationContext());

        PaystackSdk.setPublicKey(settingsMain.getPaystackModel().publicKey);

        if (!getIntent().getStringExtra("id").equals("")) {
            packageId = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
            price = getIntent().getStringExtra("amount");
        }



        cardNumber = (EditText) findViewById(R.id.editText9);
        cvc = (EditText) findViewById(R.id.cvc);
        monthSpinner = (Spinner) findViewById(R.id.spinner);
        yearSpinner = (Spinner) findViewById(R.id.spinner2);
        chkout = (Button) findViewById(R.id.button4);

        textViewCardNo = (TextView) findViewById(R.id.textView23);
        textViewCVC = (TextView) findViewById(R.id.textView24);
        textViewExpTit = (TextView) findViewById(R.id.textView20);
        textViewMonth = (TextView) findViewById(R.id.textView21);
        textViewYear = (TextView) findViewById(R.id.textView22);
        chkout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
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
        arrayList.add("19");
        arrayList.add("20");
        arrayList.add("21");
        arrayList.add("22");
        arrayList.add("23");
        arrayList.add("24");
        arrayList.add("25");
        arrayList.add("26");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PayStack.this,
                R.layout.spinner_item_medium, arrayList);
        yearSpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(PayStack.this,
                R.layout.spinner_item_medium, PayStack.this.getResources().getStringArray(R.array.month_array));
        monthSpinner.setAdapter(adapter2);


        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNo = cardNumber.getText().toString();
                int expiryMonth = getInteger(monthSpinner); //any month in the future
                int expiryYear = getInteger(yearSpinner); // any year in the future. '2018' would work also!
                String cvv = cvc.getText().toString();  // cvv of the test card

                settingsMain.showDilog(PayStack.this);

                Card card = new Card(cardNo, expiryMonth, expiryYear, cvv);
                if (card.isValid()) {
                    Charge charge = new Charge();
                    charge.setCard(card);
                    if (validate())
                        performCharge(charge);
                } else {
                    settingsMain.hideDilog();
                    Toast.makeText(PayStack.this, PaymentToastsModel.invalid_card_data, Toast.LENGTH_SHORT).show();
                }


                Charge charge = new Charge();
                charge.setCard(card);

            }
        });

        adforest_getViews();

    }


    private void adforest_getViews() {

        if (SettingsMain.isConnectingToInternet(PayStack.this)) {

            SettingsMain.showDilog(PayStack.this);

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
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(PayStack.this,
                                        R.layout.spinner_item_medium, arrayList);

                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(PayStack.this,
                                        R.layout.spinner_item_medium, PayStack.this.getResources().getStringArray(R.array.month_array));
                                yearSpinner.setAdapter(adapter);
                                monthSpinner.setAdapter(adapter2);

                            } else {
                                Toast.makeText(PayStack.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PayStack.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean validate(){
        if (cardNumber.getText().toString().equals("")){
            cardNumber.setError(stringCardError);
            cardNumber.requestFocus();
            return false;
        }else if (cardNumber.getText().toString().length()<19){
            cardNumber.setError(stringCardError);
            cardNumber.requestFocus();
            return false;
        }if (cvc.getText().toString().equals("")){
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        }else if (cvc.getText().toString().length()<3){
            cvc.setError(stringCVCError);
            cvc.requestFocus();
            return false;
        }

        return true;
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

    public void performCharge(Charge charge) {
        int a = (int) Math.round(Double.parseDouble(price));
        charge.setAmount(a*100);
        if (settingsMain.getPaystackModel().receivingEmail.equals("")){
            Toast.makeText(this, PaymentToastsModel.payment_cred, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        charge.setEmail(settingsMain.getPaystackModel().receivingEmail);
        PaystackSdk.chargeCard(PayStack.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                String reference = transaction.getReference();

                Map<String,String > headers = new HashMap<>();
                headers.put("Authorization","Bearer "+settingsMain.getPaystackModel().secretKey);
                PaystackEndPoint client = RetrofitPaystackClient.getRetrofitInstance().create(PaystackEndPoint.class);
                Call<ResponseBody> call = client.verifyPurchase(reference,headers);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.getJSONObject("data").getString("status").equals("success")){
                                    adforest_Checkout();
                                }else{
                                    settingsMain.hideDilog();
                                    progressDialog.dismiss();
                                    Toast.makeText(PayStack.this, PaymentToastsModel.payment_verify_fail, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                settingsMain.hideDilog();
                                Toast.makeText(PayStack.this, PaymentToastsModel.payment_verify_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        settingsMain.hideDilog();
                        Toast.makeText(PayStack.this, PaymentToastsModel.payment_verify_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                settingsMain.hideDilog();
                Toast.makeText(PayStack.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(PayStack.this)) {

            settingsMain.showDilog(PayStack.this);
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
                                Toast.makeText(PayStack.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(PayStack.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(PayStack.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(PayStack.this));
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

                                Intent intent = new Intent(PayStack.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                PayStack.this.finish();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(PayStack.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PayStack.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


}
