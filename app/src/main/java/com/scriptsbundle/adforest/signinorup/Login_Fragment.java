package com.kathryn.kathryn.signinorup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.home.AddNewAdPost;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;
import static com.kathryn.kathryn.R.anim;
import static com.kathryn.kathryn.R.drawable;
import static com.kathryn.kathryn.R.id;

public class Login_Fragment extends Fragment implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 0;
    protected static String user_email;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    View view;
    Activity activity;
    EditText emailid, password;
    Button loginButton, fbloginButton;
    private SignInButton gmailLoginButton;
    TextView forgotPassword, signUp, startExplore;
    LinearLayout loginLayout;
    SettingsMain settingsMain;
    LinearLayout linearLayoutLogo;
    ImageView imageViewLogo;
    TextView textViewWelcome, textViewOR;
    LinearLayout guestLayout;
    RelativeLayout leftSideAttributLayout;
    boolean is_verify_on = false;
    private boolean mIntentInProgress = true;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    boolean back_pressed = false;
    String clientId,redirectUri,scope;
    public Login_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.login_layout, container, false);
        activity = getActivity();
        settingsMain = new SettingsMain(activity);
        adforest_initViews();
        adforest_setDataToViews();
        fbSetup();
        setListeners();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return view;
    }

    // Initiate Views
    @SuppressLint("LongLogTag")
    private void adforest_initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();


        emailid = view.findViewById(id.login_emailid);
        password = view.findViewById(id.login_password);
        loginButton = view.findViewById(id.loginBtn);
        forgotPassword = view.findViewById(id.forgot_password);
        signUp = view.findViewById(id.createAccount);
        fbloginButton = view.findViewById(id.fbLogin);
        gmailLoginButton = view.findViewById(id.gmailLogin);
        fbloginButton.setVisibility(View.INVISIBLE);
        gmailLoginButton.setVisibility(View.INVISIBLE);
        loginLayout = view.findViewById(id.login_layout);
        startExplore = view.findViewById(id.startExplore);
        guestLayout = view.findViewById(R.id.guestLayout);
        textViewOR = view.findViewById(id.or);
        textViewWelcome = view.findViewById(id.welcomeTV);
        imageViewLogo = view.findViewById(id.logoimage);
        linearLayoutLogo = view.findViewById(id.logo);
        linearLayoutLogo.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        startExplore.setTextColor(Color.parseColor(SettingsMain.getMainColor()));
        startExplore.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, SettingsMain.getMainColor(), "#00000000", SettingsMain.getMainColor(), 2));

        loginButton.setTextColor(Color.parseColor(SettingsMain.getMainColor()));
        loginButton.setBackground(CustomBorderDrawable.customButton(3, 3, 3, 3, SettingsMain.getMainColor(), "#00000000", SettingsMain.getMainColor(), 2));


        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                anim.shake);

// Setting text selector over textviews
        @SuppressWarnings("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            @SuppressWarnings("deprecation") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception ignored) {
        }
        leftSideAttributLayout = view.findViewById(id.btnLL);
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
            textViewOR.setVisibility(View.GONE);
            leftSideAttributLayout.setVisibility(View.GONE);
        }




    }




    void adforest_setDataToViews() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            adforest_getLoginViews();
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    public void adforest_getLoginViews() {
        RestService restService =
                UrlController.createService(RestService.class);
        Call<ResponseBody> myCall = restService.getLoginView(UrlController.AddHeaders(getActivity()));
        myCall.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                try {
                    if (responseObj.isSuccessful()) {
                        Log.d("info Login responce", "" + responseObj.toString());

                        JSONObject response = new JSONObject(responseObj.body().string());
                        if (response.getBoolean("success")) {
                            Log.d("info Login object", "" + response.getJSONObject("data"));

                            if (!response.getJSONObject("data").getString("logo").equals("")) {
                                Picasso.with(getContext()).load(response.getJSONObject("data").getString("logo"))
                                        .error(R.drawable.logo)
                                        .placeholder(drawable.logo)
                                        .into(imageViewLogo);
                                settingsMain.setAppLogo(response.getJSONObject("data").getString("logo"));
                            }
                            textViewOR.setText(response.getJSONObject("data").getString("separator"));
                            textViewWelcome.setText(response.getJSONObject("data").getString("heading"));
                            password.setHint(response.getJSONObject("data").getString("password_placeholder"));
                            emailid.setHint(response.getJSONObject("data").getString("email_placeholder"));
                            loginButton.setText(response.getJSONObject("data").getString("form_btn"));
//                            fbloginButton.setText(response.getJSONObject("data").getString("facebook_btn"));
//                            gmailLoginButton.setText(response.getJSONObject("data").getString("google_btn"));

                            signUp.setText(response.getJSONObject("data").getString("register_text"));
                            forgotPassword.setText(response.getJSONObject("data").getString("forgot_text"));

                            is_verify_on = response.getJSONObject("data").getBoolean("is_verify_on");
                            SettingsMain.hideDilog();

                            if (settingsMain.getAppOpen()) {
                                guestLayout.setVisibility(View.VISIBLE);
                                startExplore.setVisibility(View.VISIBLE);
                                startExplore.setText(response.getJSONObject("data").getString("guest_login"));
                                settingsMain.setUserName(response.getJSONObject("data").getString("guest_text"));
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
                Log.d("info Login error", String.valueOf(t));
                Log.d("info Login error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
            }
        });
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        fbloginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        gmailLoginButton.setOnClickListener(this);
        startExplore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case id.fbLogin:

                loginToFacebook();

                break;
            case id.loginBtn:
                adforest_checkValidation();
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;

            case id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(anim.right_enter, anim.left_out)
                        .replace(id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(anim.right_enter, anim.left_out)
                        .replace(id.frameContainer, new SignUp_Fragment(),
                                Utils.SignUp_Fragment).commit();
                break;
            case id.gmailLogin:
                signIn();
                break;
            case id.startExplore:
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                editor.putString("isSocial", "false");
                editor.apply();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(anim.right_enter, anim.left_out);
                activity.finish();
                settingsMain.setUserEmail("");
                settingsMain.setUserImage("");
                break;
        }
    }


    // Check Validation before adforest_login
    private void adforest_checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") && getEmailId.length() == 0) {
            if (getPassword.equals("") && getPassword.length() == 0) {
                loginLayout.startAnimation(shakeAnimation);
                emailid.setError("!");
                password.setError("!");
            }
        } else if (getEmailId.equals("") && getEmailId.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            emailid.requestFocus();
            emailid.setError("!");
        } else if (getPassword.equals("") && getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            password.setError("!");
            password.requestFocus();
        }
        // Check if email id is valid or not
        else if (!m.find()) {
            emailid.requestFocus();
            emailid.setError("!");
        }
        // Else do adforest_login and do your stuff
        else {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
            editor.putString("isSocial", "false");
            editor.apply();
            adforest_login(getEmailId, getPassword);
        }

    }

    private void adforest_login(final String email, final String pswd) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            Log.d("enteries are", "email=" + email + " passwrd =" + pswd);
            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("password", pswd);

            RestService restService =
                    UrlController.createService(RestService.class);
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoginPost responce", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                if (is_verify_on && !response.getJSONObject("data").getBoolean("is_account_confirm")) {
                                    Log.d("info Login Resp", "" + response.getJSONObject("data"));
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                    Utils.user_id = response.getJSONObject("data").getString("id");
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> fragmentManager
                                            .beginTransaction()
                                            .setCustomAnimations(anim.right_enter, anim.left_out)
                                            .replace(id.frameContainer,
                                                    new VerifyAccount_Fragment(),
                                                    Utils.VerifyAccount_Fragment).commit(), 1000);

                                } else {
                                    Log.d("info Login Post", "" + response.getJSONObject("data"));
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                    settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                    settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                    settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                    settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                    settingsMain.setUserEmail(email);
                                    settingsMain.setUserPassword(pswd);
                                    settingsMain.isAppOpen(false);
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    activity.overridePendingTransition(anim.right_enter, anim.left_out);
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
                    Log.d("info LoginPost error", String.valueOf(t));
                    Log.d("info LoginPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    private void loginToFacebook() {

        if (SettingsMain.isConnectingToInternet(activity)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Toast.makeText(activity, "Sorry .No internet connectivity found.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getFBStats(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.i("tag", "Obj " + object.toString());
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                            editor.putString("isSocial", "true");
                            editor.apply();


                            adforest_loginSocialMedia(object.getString("email"));

                        } catch (JSONException e) {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

//This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        backPressed();
                        break;
                }
                return true;
            }
        });
    }

    private void backPressed() {
        if (!back_pressed) {
            Toast.makeText(getContext(), settingsMain.getExitApp("exit"), Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        } else {

            //Alert dialog for exit form login screen

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(settingsMain.getAlertDialogTitle("info"));
            alert.setCancelable(false);
            alert.setMessage(settingsMain.getExitApp("exit"));
            alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                getActivity().finishAffinity();
                dialog.dismiss();
            });
            alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
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
                    status -> {
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
//            AnalyticsTrackers.getInstance().trackScreenView("Login");

        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void signIn() {

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
                opr.setResultCallback(googleSignInResult -> handleSignInResult(googleSignInResult));
            }
        }
    }


    private void adforest_loginSocialMedia(final String email) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("email", email);
            params.addProperty("type", "social");


            RestService restService =
                    UrlController.createService(RestService.class, email, "1122", getActivity());
            Call<ResponseBody> myCall = restService.postLogin(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoginScoial respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info LoginScoial", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                settingsMain.setUserLogin(response.getJSONObject("data").getString("id"));
                                settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                settingsMain.setUserEmail(email);
                                settingsMain.setUserPassword("1122");

                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "true");
                                editor.apply();
                                settingsMain.isAppOpen(false);
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                activity.overridePendingTransition(anim.right_enter, anim.left_out);
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
