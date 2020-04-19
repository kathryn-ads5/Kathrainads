package com.kathryn.kathryn.packages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class StripePayment extends AppCompatActivity {

    SettingsMain settingsMain;

    EditText cardNumber, cvc;
    Spinner monthSpinner, yearSpinner;
    Button chkout;
    String cvcNo, cardNo;
    int month, year;
    TextView textViewCardNo, textViewExpTit, textViewMonth, textViewYear, textViewCVC;

    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;
    RestService restService;
    String packageType;
    PackagesFragment packagesFragment;
    private String PUBLISHABLE_KEY;  //pk_live_tkSrJzIUzdR9sDx7rLINyGHI //pk_test_07HcOQstgKo91LWCA2rd1a13
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        settingsMain = new SettingsMain(this);

        PUBLISHABLE_KEY = settingsMain.getKey("stripeKey");

        if (!getIntent().getStringExtra("id").equals("")) {
            id = getIntent().getStringExtra("id");
            packageType = getIntent().getStringExtra("packageType");
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));

        this.cardNumber = (EditText) findViewById(R.id.editText9);
        this.cvc = (EditText) findViewById(R.id.cvc);
        this.monthSpinner = (Spinner) findViewById(R.id.spinner);
        this.yearSpinner = (Spinner) findViewById(R.id.spinner2);
        this.chkout = (Button) findViewById(R.id.button4);

        this.textViewCardNo = (TextView) findViewById(R.id.textView23);
        this.textViewCVC = (TextView) findViewById(R.id.textView24);
        this.textViewExpTit = (TextView) findViewById(R.id.textView20);
        this.textViewMonth = (TextView) findViewById(R.id.textView21);
        this.textViewYear = (TextView) findViewById(R.id.textView22);
        packagesFragment = new PackagesFragment();


        chkout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);

        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adforest_checkoutStripe();

            }
        });

        // get view from server
        adforest_getViews();

    }

    private void adforest_checkoutStripe() {

        cvcNo = cvc.getText().toString();
        cardNo = cardNumber.getText().toString();
        month = getInteger(monthSpinner);
        year = getInteger(yearSpinner);

        Card card = new Card(cardNo, month, year, cvcNo);

        boolean validation = card.validateCard();
        if (validation) {
            if (SettingsMain.isConnectingToInternet(StripePayment.this)) {

                SettingsMain.showDilog(StripePayment.this);

                Stripe stripe = new Stripe(StripePayment.this, PUBLISHABLE_KEY);
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to your server
                                Log.e("token success", token.toString());
                                Log.e("token success", token.getId());
                                adforest_Checkout(id, token);
                            }

                            public void onError(Exception error) {
                                // Show localized error message
                                Log.e("token fail", error.getLocalizedMessage());
                                handleError(error.getLocalizedMessage());
                                SettingsMain.hideDilog();
                            }
                        }
                );
            } else {
                Snackbar.make(findViewById(android.R.id.content), settingsMain.getAlertDialogMessage("internetMessage"), Snackbar.LENGTH_LONG).show();
            }
        } else if (!card.validateNumber()) {
            handleError(stringCardError);
        } else if (!card.validateExpiryDate()) {
            handleError(stringExpiryError);
        } else if (!card.validateCVC()) {
            handleError(stringCVCError);
        } else {
            handleError(stringInvalidCard);
        }
    }

    //calling APi for geting views from server
    private void adforest_getViews() {

        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {

            SettingsMain.showDilog(StripePayment.this);

            Log.d("info packageId", id);
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
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(StripePayment.this,
                                        R.layout.spinner_item_medium, arrayList);

                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(StripePayment.this,
                                        R.layout.spinner_item_medium, StripePayment.this.getResources().getStringArray(R.array.month_array));
                                yearSpinner.setAdapter(adapter);
                                monthSpinner.setAdapter(adapter2);

                            } else {
                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(StripePayment.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_Checkout(String id, Token token) {

        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {

            JsonObject params = new JsonObject();
            params.addProperty("package_id", id);
            params.addProperty("source_token", token.getId());
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
                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private Integer getInteger(Spinner spinner) {
        try {
            return Integer.parseInt(spinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void handleError(String error) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(settingsMain.getAlertDialogTitle("error"));
        alert.setMessage(error);
        alert.setPositiveButton(settingsMain.getAlertOkText(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Checkout Process");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(StripePayment.this));
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

                                Intent intent = new Intent(StripePayment.this, Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                                StripePayment.this.finish();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(StripePayment.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
}

