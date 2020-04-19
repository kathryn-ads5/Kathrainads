package com.kathryn.kathryn.Settings;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.BuildConfig;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class Settings extends AppCompatPreferenceActivity {
    private static final String TAG = Settings.class.getSimpleName();
    public String getTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(SettingsMain.getMainColor())));

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings.MainPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        Preference appSharPreferencee, appRatingPreference, aboutPreference, appVersionPreference, feedbackPrefrence,
                faqPreference, privacy_policyPreference, title_termsPreference;
        SettingsMain settingsMain;
        RestService restService;
        Context context;
        JSONObject responseJsonObject, feadbackJsonObject;
        PreferenceScreen mainSection;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            context = getActivity();
            settingsMain = new SettingsMain(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(SettingsMain.getMainColor()));
            }
            if (settingsMain.getAppOpen()) {
                restService = UrlController.createService(RestService.class);
            } else
                restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

            // feedback preference click listener
//            PreferenceCategory preferenceCategory= (PreferenceCategory)findPreference(getString(R.string.pref_header_about));
//            preferenceCategory.setTitle("About");

            mainSection = (PreferenceScreen) findPreference(getString(R.string.mainSection));
//            aboutSection = (PreferenceCategory) findPreference(getString(R.string.aboutSection));
            appSharPreferencee = findPreference(getString(R.string.appShare));
            appRatingPreference = findPreference(getString(R.string.appRating));
            aboutPreference = findPreference(getString(R.string.aboutPreference));
            appVersionPreference = findPreference(getString(R.string.versionPreference));
            feedbackPrefrence = findPreference(getString(R.string.feedbackPrefrence));
            privacy_policyPreference = findPreference(getString(R.string.p_policyPreference));
            title_termsPreference = findPreference(getString(R.string.termsPreference));
            faqPreference = findPreference(getString(R.string.faqPreference));

            //            Preference aboutPreference = findPreference(getString(R.string.about));
//            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                public boolean onPreferenceClick(Preference preference) {
//                    sendFeedback(getActivity());
//                    return true;
//                }
//            });
            adforest_getSettings();
        }

        private void adforest_getSettings() {
            if (SettingsMain.isConnectingToInternet(getActivity())) {

                SettingsMain.showDilog(getActivity());
                Call<ResponseBody> myCall = restService.getAppExtraSettings(UrlController.AddHeaders(getActivity()));
                myCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                        try {
                            if (responseObj.isSuccessful()) {
                                Log.d("info appSettings Resp", "" + responseObj.toString());

                                JSONObject response = new JSONObject(responseObj.body().string());
                                if (response.getBoolean("success")) {
                                    responseJsonObject = response.getJSONObject("data");
                                    getActivity().setTitle(responseJsonObject.getString("page_title"));
                                    adforest_setGeneralSettings(responseJsonObject);
                                    adforest_setAboutSettings(responseJsonObject);


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
                            Log.d("info appSettings ", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info appSettings err", String.valueOf(t));
                            Log.d("info appSettings err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            } else {
                SettingsMain.hideDilog();
                Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            }
        }

        private void adforest_setAboutSettings(JSONObject responseJsonObject) {
            try {
//                aboutSection.setTitle(responseJsonObject.getJSONObject("sections").getString("About"));
                JSONObject aboutJsonObject = responseJsonObject.getJSONObject("about");
                if (!aboutJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(aboutPreference);
                } else {
                    aboutPreference.setTitle(responseJsonObject.getJSONObject("sections").getString("About"));

                    aboutPreference.setSummary(aboutJsonObject.getString("desc"));
                }

                JSONObject appVersionJsonObject = responseJsonObject.getJSONObject("app_version");
                if (!appVersionJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(appVersionPreference);
                } else {
                    appVersionPreference.setTitle(appVersionJsonObject.getString("title"));
                    appVersionPreference.setSummary(BuildConfig.VERSION_NAME);
                }

                JSONObject faqJsonObject = responseJsonObject.getJSONObject("faqs");
                if (!faqJsonObject.getBoolean("is_show") || faqJsonObject.getString("url").isEmpty()) {
                    mainSection.removePreference(faqPreference);
                } else {
                    final Uri uri = Uri.parse(faqJsonObject.getString("url"));
                    faqPreference.setTitle(faqJsonObject.getString("title"));

                    faqPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            return true;
                        }
                    });
                }

                JSONObject privacyJsonObject = responseJsonObject.getJSONObject("privacy_policy");
                if (!privacyJsonObject.getBoolean("is_show") || privacyJsonObject.getString("url").isEmpty()) {
                    mainSection.removePreference(privacy_policyPreference);
                } else {
                    final Uri uri = Uri.parse(privacyJsonObject.getString("url"));
                    privacy_policyPreference.setTitle(privacyJsonObject.getString("title"));

                    privacy_policyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            return true;
                        }
                    });
                }

                JSONObject termsJsonObject = responseJsonObject.getJSONObject("tandc");
                if (!termsJsonObject.getBoolean("is_show") || termsJsonObject.getString("url").isEmpty()) {
                    mainSection.removePreference(title_termsPreference);
                } else {
                    final Uri uri = Uri.parse(termsJsonObject.getString("url"));
                    title_termsPreference.setTitle(termsJsonObject.getString("title"));

                    title_termsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            return true;
                        }
                    });
                }
                feadbackJsonObject = responseJsonObject.getJSONObject("feedback");
                if (!feadbackJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(feedbackPrefrence);
                } else {
                    feedbackPrefrence.setTitle(feadbackJsonObject.getString("title"));
                    feedbackPrefrence.setSummary(feadbackJsonObject.getString("subline"));

                    feedbackPrefrence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            if (settingsMain.getAppOpen()) {
                                Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                            } else
                                adforest_showFeedbackDialog();
                            return true;
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void adforest_showFeedbackDialog() {
            final Dialog dialog = new Dialog(getActivity(), R.style.customDialog);
            dialog.setCanceledOnTouchOutside(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_send_feedback);
            //noinspection ConstantConditions
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

            Button Send = dialog.findViewById(R.id.send_button);
            Button Cancel = dialog.findViewById(R.id.cancel_button);
            final EditText et_email = dialog.findViewById(R.id.et_email);
            final EditText et_subject = dialog.findViewById(R.id.et_subject);
            final EditText textArea_information = dialog.findViewById(R.id.textArea_information);

            try {
                JSONObject formJsonObject = feadbackJsonObject.getJSONObject("form");
                et_email.setHint(formJsonObject.getString("email"));
                et_subject.setHint(formJsonObject.getString("title"));
                textArea_information.setHint(formJsonObject.getString("message"));
                Send.setText(formJsonObject.getString("btn_submit"));
                Cancel.setText(formJsonObject.getString("btn_cancel"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Send.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
            Cancel.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));


            Send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean check = true;
                    String email = et_email.getText().toString();
                    String subject = et_subject.getText().toString();
                    String message = textArea_information.getText().toString();
                    final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

                    Pattern p = Pattern.compile(regEx);

                    Matcher m = p.matcher(email);
                    if (email.isEmpty() || !m.find()) {
                        et_email.setError("");
                        et_email.requestFocus();
                        check = false;
                    } else if (subject.isEmpty()) {
                        et_subject.setError("");
                        et_subject.requestFocus();
                        check = false;
                    } else if (message.isEmpty()) {
                        textArea_information.setError("");
                        textArea_information.requestFocus();
                        check = false;
                    }
                    if (check) {
                        adforest_sendFeedBack(email, subject, message);
                        dialog.dismiss();
                    }

                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        private void adforest_setGeneralSettings(JSONObject responseJsonObject) {
            try {
                JSONObject appRatingJsonObject = responseJsonObject.getJSONObject("app_rating");
                final JSONObject appShareJsonObject = responseJsonObject.getJSONObject("app_share");

                if (!appShareJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(appSharPreferencee);
                } else {
                    appSharPreferencee.setTitle(appShareJsonObject.getString("title"));
                    appSharPreferencee.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");

                            try {
                                i.putExtra(Intent.EXTRA_SUBJECT, appShareJsonObject.getString("text"));
                                i.putExtra(Intent.EXTRA_TEXT, appShareJsonObject.getString("url"));
                                startActivity(Intent.createChooser(i, appShareJsonObject.getString("title")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                }
                if (!appRatingJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(appRatingPreference);
                } else {
                    appRatingPreference.setTitle(appRatingJsonObject.getString("title"));
                    appRatingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
                            }
                            return true;
                        }
                    });
                }
                if (!appRatingJsonObject.getBoolean("is_show") && !appShareJsonObject.getBoolean("is_show")) {
                    mainSection.removePreference(appRatingPreference);
                    mainSection.removePreference(appSharPreferencee);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void adforest_sendFeedBack(String email, String subject, String message) {

            if (SettingsMain.isConnectingToInternet(getActivity())) {
                SettingsMain.showDilog(getActivity());

                JsonObject params = new JsonObject();
                params.addProperty("subject", subject);
                params.addProperty("email", email);
                params.addProperty("message", message);
                Log.d("info sendFeedback Send", params.toString());

                Call<ResponseBody> myCall = restService.postSendFeedback(params, UrlController.AddHeaders(getActivity()));
                myCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                        try {
                            if (responseObj.isSuccessful()) {
                                Log.d("info feedback Resp", "" + responseObj.toString());

                                JSONObject response = new JSONObject(responseObj.body().string());
                                if (response.getBoolean("success")) {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                            Log.d("info feedback ", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info feedback err", String.valueOf(t));
                            Log.d("info feedback err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            } else {
                SettingsMain.hideDilog();
                Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}