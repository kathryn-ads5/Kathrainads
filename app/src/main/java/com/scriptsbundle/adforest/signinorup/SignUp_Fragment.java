package com.kathryn.kathryn.signinorup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static android.content.Context.MODE_PRIVATE;

public class SignUp_Fragment extends Fragment implements OnClickListener
        , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    private static FragmentManager fragmentManager;
    Activity activity;
    RestService restService;
    boolean is_verify_on = false;
    String term_page_id;
    RelativeLayout leftSideAttributLayout;
    private boolean mIntentInProgress = true;
    private View view;
    private EditText fullName, emailId, mobileNumber,
            password;
    private TextView login, titleTV, orTV, terms_conditionsText, tex;
    private Button signUpButton, fbloginButton;
    private SignInButton gmailLoginButton;
    private CheckBox terms_conditions;
    private CallbackManager callbackManager;
    private SettingsMain settingsMain;
    private GoogleApiClient mGoogleApiClient;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        activity = getActivity();
        settingsMain = new SettingsMain(activity);
        restService = UrlController.createService(RestService.class);

        adforest_initViews();
        adforest_setDataToViews();
        fbSetup();
        mIntentInProgress = true;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setListeners();
        return view;
    }

    // Initialize all views
    @SuppressWarnings("ResourceType")
    private void adforest_initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        fullName = view.findViewById(R.id.fullName);
        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        password = view.findViewById(R.id.password);
        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);

        terms_conditions = view.findViewById(R.id.terms_conditions);
        terms_conditionsText = view.findViewById(R.id.terms_conditionsText);

        fbloginButton = view.findViewById(R.id.fbLogin);
        gmailLoginButton = view.findViewById(R.id.gmailLogin);

        titleTV = view.findViewById(R.id.title);
        orTV = view.findViewById(R.id.or);

        signUpButton.setTextColor(Color.parseColor(settingsMain.getMainColor()));
        signUpButton.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, settingsMain.getMainColor(), "#00000000", settingsMain.getMainColor(), 2));

        /*LinearLayout linearLayout = view.findViewById(R.id.myLinearLayout);

        for (int i = 0; i < linearLayout.getChildCount(); i++)
            if (linearLayout.getChildAt(i) instanceof EditText) {

                Drawable[] compoundDrawables = ((EditText) linearLayout.getChildAt(i)).getCompoundDrawables();
                Drawable leftCompoundDrawable = compoundDrawables[0];
                ((EditText) linearLayout.getChildAt(i)).setCompoundDrawables(null,null,leftCompoundDrawable,null);

            }*/

        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            @SuppressWarnings("deprecation") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception ignored) {
        }

        leftSideAttributLayout = view.findViewById(R.id.btnLL);
        leftSideAttributLayout.removeAllViews();
        if (settingsMain.getfbButn()) {
            fbloginButton.setVisibility(View.VISIBLE);
            leftSideAttributLayout.addView(fbloginButton);
        }
        if (settingsMain.getGooglButn()) {
            gmailLoginButton.setVisibility(View.VISIBLE);
            leftSideAttributLayout.addView(gmailLoginButton);
        }
        if (!settingsMain.getfbButn() && !settingsMain.getGooglButn()) {
            orTV.setVisibility(View.GONE);
            leftSideAttributLayout.setVisibility(View.GONE);
        }
    }

    void adforest_setDataToViews() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            adforest_getRegisterViews();

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    public void adforest_getRegisterViews() {
        Call<ResponseBody> myCall = restService.getRegisterView(UrlController.AddHeaders(getActivity()));
        myCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                try {
                    if (responseObj.isSuccessful()) {
                        Log.d("info Register Responce", "" + responseObj.toString());

                        JSONObject response = new JSONObject(responseObj.body().string());
                        if (response.getBoolean("success")) {
                            Log.d("info Register object", "" + response.getJSONObject("data"));

                            orTV.setText(response.getJSONObject("data").getString("separator"));
                            titleTV.setText(response.getJSONObject("data").getString("heading"));

                            password.setHint(response.getJSONObject("data").getString("password_placeholder"));
                            fullName.setHint(response.getJSONObject("data").getString("name_placeholder"));
                            mobileNumber.setHint(response.getJSONObject("data").getString("phone_placeholder"));
                            emailId.setHint(response.getJSONObject("data").getString("email_placeholder"));

                            signUpButton.setText(response.getJSONObject("data").getString("form_btn"));
//                            fbloginButton.setText(response.getJSONObject("data").getString("facebook_btn"));
//                            gmailLoginButton.setText(response.getJSONObject("data").getString("google_btn"));
                            login.setText(response.getJSONObject("data").getString("login_text"));
                            is_verify_on = response.getJSONObject("data").getBoolean("is_verify_on");
                            if (!response.getJSONObject("data").getString("term_page_id").isEmpty()) {
                                term_page_id = response.getJSONObject("data").getString("term_page_id");
                                terms_conditionsText.setText(
                                        Html.fromHtml(response.getJSONObject("data").getString("terms_text")));
                                terms_conditionsText.setClickable(true);
                                terms_conditionsText.setMovementMethod(LinkMovementMethod.getInstance());
                                terms_conditionsText.setOnClickListener((View v) -> {
                                    adforest_showTermsDialog();
                                });
                            } else
                                terms_conditionsText.setText(response.getJSONObject("data").getString("terms_text"));
                            SettingsMain.hideDilog();

                        } else {
                            Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SettingsMain.hideDilog();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                SettingsMain.hideDilog();
                Log.d("info Register error", String.valueOf(t));
                Log.d("info Register error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
            }
        });
    }

    private void adforest_showTermsDialog() {

        final Dialog dialog;
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_termsand_condition);
        final TextView terms_conditionsTitle = dialog.findViewById(R.id.terms_conditionsTitle);
        final WebView webView = dialog.findViewById(R.id.webViewTermsAndCondition);
        Button button = dialog.findViewById(R.id.cancel_button);
        button.setText(settingsMain.getGenericAlertCancelText());
        button.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        webView.setScrollContainer(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setTextSize(WebSettings.TextSize.SMALLER);

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("page_id", term_page_id);
            Log.d("info Send terms id =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postGetCustomePages(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info terms responce ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info terms object", "" + response.getJSONObject("data"));
                                terms_conditionsTitle.setText(response.getJSONObject("data").getString("page_title"));
                                webView.loadDataWithBaseURL(null, response.getJSONObject("data").getString("page_content"), "text/html", "UTF-8", null);
                                dialog.show();
                            } else {
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
                        Log.d("info CustomPages ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info CustomPages err", String.valueOf(t));
                        Log.d("info CustomPages err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
        button.setOnClickListener(v -> dialog.dismiss());


    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
        fbloginButton.setOnClickListener(this);
        gmailLoginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbLogin:
                adforest_loginToFacebook();
                break;
            case R.id.signUpBtn:

                // Call adforest_checkValidation method
                adforest_checkValidation();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().adforest_replaceLoginFragment();
                break;
            case R.id.gmailLogin:

                adforest_signInForSociel();
                Log.e("asdfsd", "display name: ");

                break;
        }

    }

    // Check Validation Method
    private void adforest_checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0)
            fullName.setError("!");
        else if (getEmailId.equals("") || getEmailId.length() == 0)
            emailId.setError("!");
        else if (getMobileNumber.equals("") || getMobileNumber.length() == 0)
            mobileNumber.setError("!");
        else if (getPassword.equals("") || getPassword.length() == 0)
            password.setError("!");
            // Check if email id valid or not
        else if (!m.find())
            emailId.setError("!");
            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            terms_conditions.setError("!");
        else {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
            editor.putString("isSocial", "false");
            editor.apply();
            adforest_signUp(getFullName, getEmailId, getMobileNumber, getPassword);
        }
    }


    void adforest_signUp(final String name, final String email, final String phone, final String pswd) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("name", name);
            params.addProperty("email", email);
            params.addProperty("phone", phone);
            params.addProperty("password", pswd);

            Log.d("info Register", "" + params.toString());
            Call<ResponseBody> myCall = restService.postRegister(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SignUp Responce", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                if (is_verify_on) {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                    Utils.user_id = response.getJSONObject("data").getString("id");
                                    Log.d("info SignUp Data", "" + response.getJSONObject("data"));
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            fragmentManager
                                                    .beginTransaction()
                                                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                                    .replace(R.id.frameContainer,
                                                            new VerifyAccount_Fragment(),
                                                            Utils.VerifyAccount_Fragment).commit();
                                        }
                                    }, 1000);

                                } else {
                                    Log.d("info SignUp Data", "" + response.getJSONObject("data"));
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                    settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                    settingsMain.setUserEmail(email);
                                    settingsMain.setUserPassword(pswd);
                                    settingsMain.setUserName(name);
                                    settingsMain.setUserPhone(phone);
                                    settingsMain.isAppOpen(false);
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                    activity.finish();
                                }
                            } else {
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
                    SettingsMain.hideDilog();
                    Log.d("info SignUp error", String.valueOf(t));
                    Log.d("info SignUp error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loginToFacebook() {

        if (SettingsMain.isConnectingToInternet(activity)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Toast.makeText(activity, "Sorry .No internet connectivity found.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getFBStats(AccessToken accessToken) {
        // Application code
        Log.i("tag_Here", "getFb");

        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        // Application code
                        try {
                            Log.i("tag_Here", response.toString());
                            Log.i("tag", "Obj " + object.toString());

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                            editor.putString("isSocial", "true");
                            editor.apply();

                            adforest_loginSocialMedia(object.getString("email"));

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields",
                "id,first_name,last_name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    // FB SETUP CALLS
    private void fbSetup() {
        //noinspection deprecation
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        callbackManager = CallbackManager.Factory.create();
        new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    Log.i("tag", "In From ONcreate");
                    Log.i("tag", "go to home");
                } else {
                    Log.i("tag", "Else In From ONcreate");
                    Log.i("tag", "Goto splash");
                }
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult result) {
                        // TODO Auto-generated method stub
                        Log.i("tag", "Success ");
                        getFBStats(result.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub
                        Log.i("tag", "On Cancel ");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // TODO Auto-generated method stub
                        Log.i("tag", "Error " + error);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
            editor.putString("isSocial", "true");
            editor.apply();

            adforest_loginSocialMedia(acct != null ? acct.getEmail() : null);

        } else {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        }
                    });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
//        if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
//            AnalyticsTrackers.getInstance().trackScreenView("Sign Up");
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void adforest_signInForSociel() {

        if (mIntentInProgress) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
            mIntentInProgress = false;
        } else {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                Log.d("s", "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    private void adforest_loginSocialMedia(final String email) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("type", "social");
            RestService restService = UrlController.createService(RestService.class, email, "1122", getContext());
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoginScoial respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                settingsMain.setUserEmail(email);
                                settingsMain.setUserPassword("1122");
                                settingsMain.isAppOpen(false);
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "true");
                                editor.apply();

                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                activity.finish();
                            } else {
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
                    SettingsMain.hideDilog();
                    Log.d("info LoginScoial error", String.valueOf(t));
                    Log.d("info LoginScoial error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

}
