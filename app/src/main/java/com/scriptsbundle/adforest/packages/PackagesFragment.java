package com.kathryn.kathryn.packages;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.OnItemClickListenerPackages;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.modelsList.PackagesModel;
import com.kathryn.kathryn.packages.adapter.ItemPackagesAdapter;
import com.kathryn.kathryn.packages.adapter.PaymentToastsModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.JsonObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PackagesFragment extends Fragment {

    public static final int PAYPAL_REQUEST_CODE = 123;
    RecyclerView recyclerView;
    ArrayList<PackagesModel> listitems = new ArrayList<>();
    SettingsMain settingsMain;
    ItemPackagesAdapter itemPackagesAdapter;
    RestService restService;
    boolean spinnerTouched = false;
    String packageId, packageType;
    String merchantKey, userCredentials;
    JSONObject responseJsonObject, jsonObjectResponse;
    String billing_error;
    private TextView textViewEmptyData;
    int BRAINTREE_REQUEST_CODE = 111;
    int SQUARE_UP_REQUEST_CODE = 999;
    String braintreePrice;

    JsonObject object1;
    //Braintree Params
    private static BraintreeGateway gateway;


    public PackagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packages, container, false);


        settingsMain = new SettingsMain(getActivity());


        if (!settingsMain.getBrainTreeModel().publicKey.equals("")) {
            if (settingsMain.getBrainTreeModel().mode.equals("sandbox")) {
                gateway = new BraintreeGateway(
                        Environment.SANDBOX,
                        settingsMain.getBrainTreeModel().merchant_id,
                        settingsMain.getBrainTreeModel().publicKey,
                        settingsMain.getBrainTreeModel().privateKey
                );
            } else {
                gateway = new BraintreeGateway(
                        Environment.PRODUCTION,
                        settingsMain.getBrainTreeModel().merchant_id,
                        settingsMain.getBrainTreeModel().publicKey,
                        settingsMain.getBrainTreeModel().privateKey
                );
            }
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        textViewEmptyData = view.findViewById(R.id.noPackagesfound);
        textViewEmptyData.setVisibility(View.GONE);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        adforest_getData();
        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void adforest_getData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getPackagesDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Packages Responce", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            final JSONObject response = new JSONObject(responseObj.body().string());
                            jsonObjectResponse = response;
                            getActivity().setTitle(response.getJSONObject("extra").getString("page_title"));

                            if (response.getBoolean("success")) {
                                final JSONObject responseData = response.getJSONObject("data");
                                Log.d("info Packages object", "" + response.getJSONObject("data"));
                                Log.d("info Packages object", "" + response.getJSONObject("data").getJSONArray("payment_types").length());

                                adforest_initializeList(response.getJSONObject("data").getJSONArray("products"), response.getJSONObject("data").getJSONArray("payment_types"));
                                responseJsonObject = response.getJSONObject("data");
                                billing_error = response.getJSONObject("extra").getString("billing_error");
                                itemPackagesAdapter = new ItemPackagesAdapter(getActivity(), listitems);

                                if (listitems.size() > 0 & recyclerView != null) {
                                    recyclerView.setAdapter(itemPackagesAdapter);

                                    itemPackagesAdapter.setOnItemClickListener(new OnItemClickListenerPackages() {
                                        @Override
                                        public void onItemClick(PackagesModel item) {
                                            Intent intent = new Intent(getActivity(), StripePayment.class);
                                            intent.putExtra("id", item.getBtnTag());
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onItemTouch() {
                                            Log.d("info Spinner Touched", "Real Touch Felt.");
                                            spinnerTouched = true;
                                        }

                                        @Override
                                        public void onItemSelected(final PackagesModel item, final int spinnerPosition) {
                                            if (spinnerTouched) {
                                                if (spinnerPosition > 0) {
                                                    adforest_byPackage(item, spinnerPosition);

                                                }
                                            }
                                        }
                                    });
                                }
                            } else {
                                textViewEmptyData.setVisibility(View.VISIBLE);
                                textViewEmptyData.setText(response.get("message").toString());
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("info Packages error", String.valueOf(t));
                    Log.d("info Packages error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_byPackage(final PackagesModel item, final int spinnerPosition) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(settingsMain.getGenericAlertTitle());
        alert.setCancelable(false);
        alert.setMessage(settingsMain.getGenericAlertMessage());
        alert.setPositiveButton(settingsMain.getGenericAlertOkText(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                switch (item.getSpinnerValue().get(spinnerPosition)) {

                    case "paystack":
                        Intent paystackIntent = new Intent(getActivity(), PayStack.class);
                        paystackIntent.putExtra("id", item.getBtnTag());
                        paystackIntent.putExtra("amount", item.getPackagesPrice());
                        paystackIntent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(paystackIntent);
                        break;
                    case "authorizedotnet":
                        Intent authorizeIntent = new Intent(getActivity(), AuthorizeNet.class);
                        authorizeIntent.putExtra("id", item.getBtnTag());
                        authorizeIntent.putExtra("amount", item.getPackagesPrice());
                        authorizeIntent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(authorizeIntent);
                        break;
                    case "payhere":
                        Intent payhereIntent = new Intent(getActivity(), PayHereIntegration.class);
                        payhereIntent.putExtra("id", item.getBtnTag());
                        payhereIntent.putExtra("packageName", item.getPlanType());
                        payhereIntent.putExtra("amount", item.getPackagesPrice());
                        payhereIntent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(payhereIntent);
                        break;
                    case "braintree":
                        if (settingsMain.getBrainTreeModel().publicKey.equals("")) {
                            Toast.makeText(getActivity(), "Braintree credentials not setup.", Toast.LENGTH_SHORT).show();
                        } else {
                            packageId = item.getBtnTag();
                            packageType = item.getSpinnerValue().get(spinnerPosition);
                            object1 = new JsonObject();
                            object1.addProperty("package_id", packageId);
                            object1.addProperty("payment_from", packageType);
                            braintreePrice = item.getPackagesPrice();
                            onBraintreeSubmit();
                        }

                        break;
                    case "worldpay":
                        Intent worldPayIntent = new Intent(getActivity(), WorldPayIntegration.class);
                        worldPayIntent.putExtra("id", item.getBtnTag());
                        worldPayIntent.putExtra("packageName", item.getPlanType());
                        worldPayIntent.putExtra("amount", item.getPackagesPrice());
                        worldPayIntent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(worldPayIntent);
                        break;
                    case "stripe":
                        Intent intent = new Intent(getActivity(), StripePayment.class);
                        intent.putExtra("id", item.getBtnTag());
                        intent.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                        startActivity(intent);
                        break;

                    case "paypal":
                        try {
                            if (responseJsonObject.getBoolean("is_paypal_key")) {
                                packageId = item.getBtnTag();
                                packageType = item.getSpinnerValue().get(spinnerPosition);

                                adforest_PayPal(item.getPackagesPrice(), responseJsonObject.getJSONObject("paypal"), item.getPlanType());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "app_inapp":
                        try {
                            if (!item.getJsonObject().getString("android").equals("")) {
                                packageId = item.getBtnTag();
                                packageType = item.getSpinnerValue().get(spinnerPosition);
                                Log.d("info purchase", item.getJsonObject().getString("android"));
                                Intent intent1 = new Intent(getActivity(), InAppPurchaseActivity.class);
                                JSONObject jsonObject = jsonObjectResponse.getJSONObject("extra").getJSONObject("android");
                                if (jsonObject.getBoolean("in_app_on")) {

                                    intent1.putExtra("id", item.getBtnTag());
                                    intent1.putExtra("packageType", item.getSpinnerValue().get(spinnerPosition));
                                    intent1.putExtra("porductId", item.getJsonObject().getString("android"));
                                    intent1.putExtra("activityName", jsonObject.getString("title_text"));
                                    intent1.putExtra("billing_error", billing_error);
                                    intent1.putExtra("one_time", jsonObject.getJSONObject("message").getString("one_time"));
                                    intent1.putExtra("no_market", jsonObject.getJSONObject("message").getString("no_market"));
                                    intent1.putExtra("LICENSE_KEY", jsonObject.getString("secret_code"));
                                    startActivity(intent1);
                                } else {
                                    showToast(item.getJsonObject().getString("message"));
                                }
                            } else
                                showToast(item.getJsonObject().getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        packageId = item.getBtnTag();
                        packageType = item.getSpinnerValue().get(spinnerPosition);
                        JsonObject object = new JsonObject();
                        object.addProperty("package_id", packageId);
                        object.addProperty("payment_from", packageType);
                        adforest_CheckOut(object);


                }

                dialog.dismiss();
            }
        });
        alert.setNegativeButton(settingsMain.getGenericAlertCancelText(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();


    }

    public void adforest_initializeList(JSONArray timeline, JSONArray packagesArray) {
        listitems.clear();

        for (int i = 0; i < timeline.length(); i++) {

            PackagesModel item = new PackagesModel();
            JSONObject firstEvent;
            try {
                firstEvent = (JSONObject) timeline.get(i);
                if (firstEvent != null) {

                    item.setBtnTag(firstEvent.getString("product_id"));

                    item.setPlanType(firstEvent.getString("product_title"));
                    item.setPrice(firstEvent.getString("product_price"));
                    item.setBtnText(firstEvent.getString("product_btn"));

                    item.setFreeAds(firstEvent.getString("free_ads_text") + ": " + firstEvent.getString("free_ads_value"));
                    item.setFeatureAds(firstEvent.getString("featured_ads_text") + ": " + firstEvent.getString("featured_ads_value"));
                    item.setValidaty(firstEvent.getString("days_text") + ": " + firstEvent.getString("days_value"));
                    item.setBumupAds(firstEvent.getString("bump_ads_text") + ": " + firstEvent.getString("bump_ads_value"));

                    if (firstEvent.has("allow_bidding_text")) {
                        item.setAllowBidding(firstEvent.getString("allow_bidding_text") + ": " + firstEvent.getString("allow_bidding_value"));
                    } else {
                        item.setAllowBidding(null);
                    }
                    if (firstEvent.has("num_of_images_text")) {
                        item.setNumOfImages(firstEvent.getString("num_of_images_text") + ": " + firstEvent.getString("num_of_images_val"));
                    } else {
                        item.setNumOfImages(null);
                    }
                    if (firstEvent.has("video_url_text")) {
                        item.setVideoUrl(firstEvent.getString("video_url_text") + ": " + firstEvent.getString("video_url_val"));
                    } else {
                        item.setVideoUrl(null);
                    }
                    if (firstEvent.has("allow_tags_text")) {
                        item.setAllowTags(firstEvent.getString("allow_tags_text") + ": " + firstEvent.getString("allow_tags_val"));

                    } else {
                        item.setAllowTags(null);
                    }
                    if (firstEvent.has("allow_cats_val")) {
                        item.setAllowCats(firstEvent.getString("allow_cats_text") + ": " + firstEvent.getString("allow_cats_val"));
                    } else {
                        item.setAllowCats(null);

                    }

                    if (firstEvent.has("sale_text")){
                       item.setRegularPrice(firstEvent.getString("regular_price"));
                        item.setSaleText(firstEvent.getString("sale_text"));

                    }
                    else{
                        item.setSaleText(null);
                    }
                    try {
                        item.setListTitleText(firstEvent.getString("allow_cats_text"));
                        if (firstEvent.has("allow_cats_val_arr")) {
                            item.setAllowCatsValue(firstEvent.getJSONArray("allow_cats_val_arr"));
                            Log.d("Loggg", item.getAllowCatsValue().toString());

                        } else {
                            item.setAllowCatsValue(null);
                        }
                        item.setReadMoreText(firstEvent.getString("allow_cats_text") + ": " + firstEvent.getString("see_all_cats"));
//                        Log.d("Loggg", item.getAllowCatsValue().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                    Log.d("info packages amount", firstEvent.getJSONObject("product_amount").toString());
                    item.setPackagesPrice(firstEvent.getJSONObject("product_amount").getString("value"));

                    item.setSpinnerData(packagesArray);
                    item.setSpinnerValue(packagesArray);
                    item.setJsonObject(firstEvent.getJSONObject("product_appCode"));
                    listitems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Packages");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void adforest_PayPal(String packagePayment, JSONObject jsonObject, String packageName) {
        PayPalConfiguration
                config = null;
        try {
            Log.d("info payment", jsonObject.toString());
            config = new PayPalConfiguration()
                    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                    // or live (ENVIRONMENT_PRODUCTION)
                    .environment(jsonObject.getString("mode"))
                    .clientId(jsonObject.getString("api_key"))
                    .merchantName(jsonObject.getString("merchant_name"))
                    .merchantPrivacyPolicyUri(Uri.parse(jsonObject.getString("privecy_url")))
                    .merchantUserAgreementUri(Uri.parse(jsonObject.getString("agreement_url")));
            Intent intent = new Intent(getActivity(), PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            getActivity().startService(intent);

            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(packagePayment)), jsonObject.getString("currency"), packageName,
                    PayPalPayment.PAYMENT_INTENT_SALE);

            //Creating Paypal Payment activity intent
            Intent intent1 = new Intent(getActivity(), PaymentActivity.class);

            //putting the paypal configuration to the intent
            intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            //Puting paypal payment to the intent
            intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult
            startActivityForResult(intent1, PAYPAL_REQUEST_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e("info ", "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        JsonObject params = new JsonObject();
                        params.addProperty("package_id", packageId);
                        params.addProperty("source_token", paymentId);
                        params.addProperty("payment_from", packageType);
                        params.addProperty("payment_client", payment_client);

                        adforest_CheckOut(params);

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
        if (requestCode == BRAINTREE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getNonce();
                performTransactions(paymentMethodNonce);
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void adforest_CheckOut(JsonObject params) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Log.d("info  object", params.toString());
            Call<ResponseBody> myCall = restService.postCheckout(params, UrlController.AddHeaders(getActivity()));
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
                                Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_getDataForThankYou() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(getActivity()));
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

                                Intent intent = new Intent(getActivity(), Thankyou.class);
                                intent.putExtra("data", responseData.getString("data"));
                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
                                startActivity(intent);
                                SettingsMain.hideDilog();
                            } else {
                                SettingsMain.hideDilog();
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


    public void onBraintreeSubmit() {
        if (!settingsMain.getBrainTreeModel().token_key.equals("")) {
            DropInRequest dropInRequest = new DropInRequest()
                    .clientToken(settingsMain.getBrainTreeModel().token_key);
            dropInRequest.disablePayPal();
            startActivityForResult(dropInRequest.getIntent(getActivity()), BRAINTREE_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
        }
    }


    public void performTransactions(String paymentMethodNonce) {

        TransactionRequest request = new TransactionRequest()
                .amount(new BigDecimal(braintreePrice))
                .paymentMethodNonce(paymentMethodNonce)
                .options()
                .submitForSettlement(true)
                .done();
        new AsyncTaskExample(request).execute();

    }

    private class AsyncTaskExample extends AsyncTask<String, String, Result<Transaction>> {
        TransactionRequest request;


        public AsyncTaskExample(TransactionRequest request) {
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            settingsMain.showDilog(getActivity());
            super.onPreExecute();
        }

        @Override
        protected Result<Transaction> doInBackground(String... strings) {
            Result<Transaction> result = null;
            try {
                result = gateway.transaction().sale(request);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result;

        }

        @Override
        protected void onPostExecute(Result<Transaction> transactionResult) {
            if (transactionResult != null) {
                if (transactionResult.isSuccess()) {
                    settingsMain.hideDilog();
                    Toast.makeText(getActivity(), PaymentToastsModel.payment_success, Toast.LENGTH_SHORT).show();
                    adforest_CheckOut(object1);
                } else {
                    settingsMain.hideDilog();
                    Toast.makeText(getActivity(), PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
                }
            } else {
                settingsMain.hideDilog();
                Toast.makeText(getActivity(), PaymentToastsModel.payment_failed + PaymentToastsModel.something_wrong, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(transactionResult);
        }
    }
}

