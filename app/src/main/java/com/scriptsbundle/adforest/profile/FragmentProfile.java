package com.kathryn.kathryn.profile;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.messages.Block_Message_Fragment;
import com.kathryn.kathryn.userAndSellers.Blocked_UserFragment;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;


public class FragmentProfile extends Fragment {

    SettingsMain settingsMain;
    TextView verifyBtn, textViewRateNo, textViewUserName, textViewLastLogin;
    TextView editProfBtn, textViewAdsSold, textViewTotalList, textViewExppiry,textViewInactiveAds, textViewTitle;

    TextView textViewNameKey, textViewNameValue, textViewEmailkey, textViewEmailvalue, textViewPhonekey, textViewPhonevalue,
            textViewLocationkey, textViewLocationvalue, textViewAccTypekey, textViewAccTypeValue, textViewPackgTypekey,
            textViewPackgTypevalue, textViewFreeAdskey, textViewFreeAdsvalue, textViewFeatureAdskey,
            textViewFeatureAdsvalue, textViewExpirykey, textViewExpiryvalue, textViewVerify,
            textViewBumpAdsKey, textViewBumpAdsValue, textViewBlockKey, textViewBlockValue;
    ImageView imageViewProfile;
    RatingBar ratingBar;
    RestService restService;
    JSONObject jsonObject;
    LinearLayout bumpAdLayout, blockUserLayout;
    View bumdAdView, blockUserView;
    LinearLayout verifyPhoneLayout;
    JSONObject dialogText, alertDialog;
    Dialog dialog;

    @Override
    public void onStop() {
        super.onStop();
        if (settingsMain != null) {
            settingsMain.setUserVerified(true);
        }
    }

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        settingsMain = new SettingsMain(getActivity());
        textViewLastLogin = view.findViewById(R.id.loginTime);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);
        textViewVerify = view.findViewById(R.id.textViewVerify);
        verifyPhoneLayout = view.findViewById(R.id.verifyPhoneLayout);

        editProfBtn = view.findViewById(R.id.editProfile);

        textViewAdsSold = view.findViewById(R.id.share);
        textViewTotalList = view.findViewById(R.id.addfav);
        textViewInactiveAds = view.findViewById(R.id.report);
        textViewExppiry = view.findViewById(R.id.expired);
        imageViewProfile = view.findViewById(R.id.image_view);
        ratingBar = view.findViewById(R.id.ratingBar);

        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        textViewTitle = view.findViewById(R.id.textView);

        textViewNameKey = view.findViewById(R.id.textViewnamekey);
        textViewNameValue = view.findViewById(R.id.textViewNameValue);
        textViewEmailkey = view.findViewById(R.id.textViewEmailKey);
        textViewEmailvalue = view.findViewById(R.id.textViewEmailValue);
        textViewPhonekey = view.findViewById(R.id.textViewPhoneKey);
        textViewPhonevalue = view.findViewById(R.id.textViewPhoneValue);
        textViewLocationkey = view.findViewById(R.id.textViewLocationKey);
        textViewLocationvalue = view.findViewById(R.id.textViewLocationValue);
        textViewAccTypekey = view.findViewById(R.id.textViewAccount_typeKey);
        textViewAccTypeValue = view.findViewById(R.id.textViewAccount_typeValue);
        textViewPackgTypekey = view.findViewById(R.id.textViewPackageKey);
        textViewPackgTypevalue = view.findViewById(R.id.textViewPackageValue);
        textViewFreeAdskey = view.findViewById(R.id.textViewFreeAdsKey);
        textViewFreeAdsvalue = view.findViewById(R.id.textViewFreeAdsValue);
        textViewFeatureAdskey = view.findViewById(R.id.textViewFeaturAdsKey);
        textViewFeatureAdsvalue = view.findViewById(R.id.textViewFeaturAdsValue);
        textViewExpirykey = view.findViewById(R.id.textViewExpiryKey);
        textViewExpiryvalue = view.findViewById(R.id.textViewExpiryValue);

        textViewBlockKey = view.findViewById(R.id.textViewBlockKey);
        textViewBlockValue = view.findViewById(R.id.textViewBlockValue);
        blockUserLayout = view.findViewById(R.id.blockUserLayout);
        blockUserView = view.findViewById(R.id.blockUserView);
        textViewBlockValue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Blocked_UserFragment fragment = new Blocked_UserFragment();
//                Block_Message_Fragment fragment=new Block_Message_Fragment();
                replaceFragment(fragment, "Block_Message_Fragment");
            }
            return true;
        });

        bumpAdLayout = view.findViewById(R.id.bumpAdLayout);
        textViewBumpAdsKey = view.findViewById(R.id.textViewBumpAdsKey);
        textViewBumpAdsValue = view.findViewById(R.id.textViewBumpAdsValue);
        bumdAdView = view.findViewById(R.id.bumdAdView);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        dialog = new Dialog(getActivity(), R.style.customDialog);

        adforest_setAllViewsText();
        ((HomeActivity) getActivity()).changeImage();

        editProfBtn.setOnClickListener(view1 -> replaceFragment(new EditProfile(), "EditProfile"));
        textViewVerify.setOnClickListener(v -> adforest_showDialog());

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                RatingFragment fragment = new RatingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", settingsMain.getUserLogin());
                bundle.putBoolean("isprofile", true);
                fragment.setArguments(bundle);

                replaceFragment(fragment, "RatingFragment");
            }
            return true;
        });

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        return view;
    }

    private void adforest_setAllViewsText() {


        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Call<ResponseBody> myCall = restService.getProfileDetails(UrlController.AddHeaders(getActivity()));
            Log.d("headers", UrlController.AddHeaders(getActivity()).toString());
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info profileGet Details", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info profileGet object", "" + response.getJSONObject("data"));
                                Log.d("info profileGet object", "" + response.getJSONObject("data"));

                                jsonObject = response.getJSONObject("data");

                                textViewTitle.setText(response.getJSONObject("extra_text").getString("profile_title"));
                                editProfBtn.setText(response.getJSONObject("extra_text").getString("profile_edit_title"));
                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));

                                textViewLastLogin.setText(jsonObject.getJSONObject("profile_extra").getString("last_login"));
                                textViewUserName.setText(jsonObject.getJSONObject("profile_extra").getString("display_name"));
                                settingsMain.setUserImage(jsonObject.getJSONObject("profile_extra").getString("profile_img"));

                                Picasso.with(getContext()).load(jsonObject.getJSONObject("profile_extra").getString("profile_img"))
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(imageViewProfile);

                                verifyBtn.setText(jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("text"));
                                verifyBtn.setBackground(CustomBorderDrawable.customButton(0, 0, 0, 0,
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"), 3));

                                if (response.getJSONObject("extra_text").getBoolean("is_verification_on")) {
                                    verifyPhoneLayout.setVisibility(View.VISIBLE);

                                    dialogText = response.getJSONObject("extra_text");
                                    Log.d("info color", jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"));
                                    alertDialog = response.getJSONObject("extra_text").getJSONObject("send_sms_dialog");
                                    textViewVerify.setText(response.getJSONObject("extra_text").getString("is_number_verified_text"));
                                    if (response.getJSONObject("extra_text").getBoolean("is_number_verified")) {
                                        textViewVerify.setClickable(false);
                                        textViewVerify.setBackground(CustomBorderDrawable.customButton(0, 0, 0, 0,
                                                jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                                jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                                jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"), 3));
                                    } else {
                                        textViewVerify.setClickable(true);
                                        textViewVerify.setPaintFlags(textViewVerify.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    }
                                }


                                textViewAdsSold.setText(jsonObject.getJSONObject("profile_extra").getString("ads_sold"));
                                textViewTotalList.setText(jsonObject.getJSONObject("profile_extra").getString("ads_total"));
                                textViewInactiveAds.setText(jsonObject.getJSONObject("profile_extra").getString("ads_inactive"));
                                textViewExppiry.setText(jsonObject.getJSONObject("profile_extra").getString("ads_expired"));

                                ratingBar.setNumStars(5);
                                ratingBar.setRating(Float.parseFloat(jsonObject.getJSONObject("profile_extra").getJSONObject("rate_bar").getString("number")));
                                textViewRateNo.setText(jsonObject.getJSONObject("profile_extra").getJSONObject("rate_bar").getString("text"));

                                textViewNameKey.setText(jsonObject.getJSONObject("display_name").getString("key"));
                                textViewAccTypekey.setText(jsonObject.getJSONObject("account_type").getString("key"));
                                textViewPhonekey.setText(jsonObject.getJSONObject("phone").getString("key"));
                                textViewEmailkey.setText(jsonObject.getJSONObject("user_email").getString("key"));
                                textViewLocationkey.setText(jsonObject.getJSONObject("location").getString("key"));
                                textViewPackgTypekey.setText(jsonObject.getJSONObject("package_type").getString("key"));
                                textViewFreeAdskey.setText(jsonObject.getJSONObject("simple_ads").getString("key"));
                                textViewFeatureAdskey.setText(jsonObject.getJSONObject("featured_ads").getString("key"));
                                textViewExpirykey.setText(jsonObject.getJSONObject("expire_date").getString("key"));

                                textViewNameValue.setText(jsonObject.getJSONObject("display_name").getString("value"));
                                textViewAccTypeValue.setText(jsonObject.getJSONObject("account_type").getString("value"));
                                textViewPhonevalue.setText(jsonObject.getJSONObject("phone").getString("value"));
                                textViewEmailvalue.setText(jsonObject.getJSONObject("user_email").getString("value"));
                                textViewLocationvalue.setText(jsonObject.getJSONObject("location").getString("value"));
                                textViewPackgTypevalue.setText(jsonObject.getJSONObject("package_type").getString("value"));
                                textViewFreeAdsvalue.setText(jsonObject.getJSONObject("simple_ads").getString("value"));
                                textViewFeatureAdsvalue.setText(jsonObject.getJSONObject("featured_ads").getString("value"));
                                textViewExpiryvalue.setText(jsonObject.getJSONObject("expire_date").getString("value"));

                                if (jsonObject.getBoolean("bump_ads_is_show")) {
                                    bumpAdLayout.setVisibility(View.VISIBLE);
                                    textViewBumpAdsKey.setText(jsonObject.getJSONObject("bump_ads").getString("key"));
                                    textViewBumpAdsValue.setText(jsonObject.getJSONObject("bump_ads").getString("value"));
                                    bumdAdView.setVisibility(View.VISIBLE);
                                }

                                if (jsonObject.getBoolean("blocked_users_show")) {
                                    blockUserLayout.setVisibility(View.VISIBLE);
                                    blockUserView.setVisibility(View.VISIBLE);
                                    textViewBlockKey.setText(jsonObject.getJSONObject("blocked_users").getString("key"));
                                    textViewBlockValue.setText(jsonObject.getJSONObject("blocked_users").getString("value"));
                                    textViewBlockValue.setPaintFlags(textViewBlockValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                }
                            } else {
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
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ProfileGet error", String.valueOf(t));
                    Log.d("info ProfileGet error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
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
                AnalyticsTrackers.getInstance().trackScreenView("Profile");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void adforest_showDialog() {
        String alertTitle = null, alertMessage = null, alertYesButton = null, alertNoButton = null;
        try {
            alertTitle = alertDialog.getString("title");
            alertMessage = alertDialog.getString("text");
            alertYesButton = alertDialog.getString("btn_send");
            alertNoButton = alertDialog.getString("btn_cancel");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(alertTitle);

        alert.setCancelable(false);
        alert.setMessage(alertMessage);
        alert.setPositiveButton(alertYesButton, (dialog, which) -> {
            adforest_sendMessage();
            dialog.dismiss();
        });
        alert.setNegativeButton(alertNoButton, (dialogInterface, i) -> dialogInterface.dismiss());
        alert.show();

    }

    private void adforest_sendMessage() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getVerifyCode(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info  VerifyCode Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info VerifyCode obj", "" + response.toString());
                                adforest_showDilogVerify(response.getJSONObject("data").getJSONObject("resend"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
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
                        Log.d("info VerifyCode ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info VerifyCode err", String.valueOf(t));
                        Log.d("info VerifyCode err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_showDilogVerify(JSONObject resendObject) {
        final long[] disableTime = {0};
        String resendText = null;
        try {
            disableTime[0] = TimeUnit.SECONDS.toMillis(resendObject.getInt("time"));
            resendText = resendObject.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("info time duration", "" + disableTime);
        final boolean[] disableButton = {true};
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_verify_phone);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);
        final Button resendCode = dialog.findViewById(R.id.resendCode_button);

        final EditText code = dialog.findViewById(R.id.editText);
        try {
            code.setHint(dialogText.getJSONObject("phone_dialog").getString("text_field"));
            Send.setText(dialogText.getJSONObject("phone_dialog").getString("btn_confirm"));
            Cancel.setText(dialogText.getJSONObject("phone_dialog").getString("btn_cancel"));
            resendCode.setText(dialogText.getJSONObject("phone_dialog").getString("btn_resend"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        resendCode.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));


        Send.setOnClickListener(v -> {

            if (!code.getText().toString().isEmpty()) {
                adforest_verifyMessage(code.getText().toString());
            } else
                code.setError("");
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        final String finalResendText = resendText;
        resendCode.setOnClickListener(v -> {

            if (disableButton[0]) {
                Toast.makeText(getActivity(), finalResendText, Toast.LENGTH_SHORT).show();
                resendCode.setEnabled(false);
                resendCode.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        getActivity().runOnUiThread(() -> {
                            resendCode.setEnabled(true);
                            disableButton[0] = false;
                        });
                    }
                }, disableTime[0]);
            }
            if (!disableButton[0]) {
                adforest_sendMessage();
            }
        });
        dialog.show();
    }

    private void adforest_verifyMessage(String verifyCode) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("verify_code", verifyCode);

            Log.d("info Send VerifyCode", params.toString());

            Call<ResponseBody> myCall = restService.postVerifyPhoneNumber(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info VerifyCode Res", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                dialog.dismiss();
                                Log.d("info VerifyCode object", "" + response.toString());
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                if (response.getJSONObject("data").getBoolean("is_number_verified")) {
                                    textViewVerify.setText(response.getJSONObject("data").getString("is_number_verified_text"));
                                    textViewVerify.setBackground(CustomBorderDrawable.customButton(0, 0, 0, 0,
                                            jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                            jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                            jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"), 3));
                                    textViewVerify.setClickable(false);

                                }
                            } else {
                                dialog.show();
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info VerifyCode ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info VerifyCode err", String.valueOf(t));
                        Log.d("info VerifyCode err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
}
