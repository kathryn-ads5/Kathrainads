package com.kathryn.kathryn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.kathryn.kathryn.Settings.Settings;
import com.kathryn.kathryn.Shop.shopMenuModel;
import com.kathryn.kathryn.helper.LocaleHelper;
import com.kathryn.kathryn.home.ChooseLanguageActivity;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.home.helper.AdPostImageModel;
import com.kathryn.kathryn.home.helper.CalanderTextModel;
import com.kathryn.kathryn.home.helper.Location_popupModel;
import com.kathryn.kathryn.home.helper.ProgressModel;
import com.kathryn.kathryn.modelsList.permissionsModel;
import com.kathryn.kathryn.signinorup.MainActivity;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;


public class SplashScreen extends AppCompatActivity {

    public static JSONObject jsonObjectAppRating, jsonObjectAppShare;
    public static boolean gmap_has_countries = false, app_show_languages = false;
    public static JSONArray app_languages;
    public static String languagePopupTitle, languagePopupClose, gmap_countries;
    Activity activity;
    SettingsMain setting;
    JSONObject jsonObjectSetting;
    boolean isRTL = false;
    String gmap_lang;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

        activity = this;
        setting = new SettingsMain(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.kathryn.kathryn",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (SettingsMain.isConnectingToInternet(this)) {
            adforest_getSettings();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);
            alert.setTitle(setting.getAlertDialogTitle("error"));
            alert.setCancelable(false);
            alert.setMessage(setting.getAlertDialogMessage("internetMessage"));
            alert.setPositiveButton(setting.getAlertOkText(), (dialog, which) -> {
                dialog.dismiss();
                SplashScreen.this.recreate();
            });
            alert.show();
        }


    }
    public void adforest_getSettings() {
        RestService restService =
                UrlController.createService(RestService.class);
        try {

            Call<ResponseBody> myCall = restService.getSettings(UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info settings Responce", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                jsonObjectSetting = response.getJSONObject("data");
                                Log.d("info settings Responce", "" + jsonObjectSetting.toString());

                                setting.setMainColor(jsonObjectSetting.getString("main_color"));

                                isRTL = jsonObjectSetting.getBoolean("is_rtl");
                                setting.setRTL(isRTL);
                                setting.setAlertDialogTitle("error", jsonObjectSetting.getJSONObject("internet_dialog").getString("title"));
                                setting.setAlertDialogMessage("internetMessage", jsonObjectSetting.getJSONObject("internet_dialog").getString("text"));
                                setting.setAlertOkText(jsonObjectSetting.getJSONObject("internet_dialog").getString("ok_btn"));
                                setting.setAlertCancelText(jsonObjectSetting.getJSONObject("internet_dialog").getString("cancel_btn"));
                                setting.setPaidMessage(jsonObjectSetting.getString("app_paid_cat_text"));
                                setting.setImgReqMessage(jsonObjectSetting.getString("required_img"));
                                setting.setAlertDialogTitle("info", jsonObjectSetting.getJSONObject("alert_dialog").getString("title"));
                                setting.setAlertDialogMessage("confirmMessage", jsonObjectSetting.getJSONObject("alert_dialog").getString("message"));

                                setting.setAlertDialogMessage("waitMessage", jsonObjectSetting.getString("message"));

                                setting.setAlertDialogMessage("search", jsonObjectSetting.getJSONObject("search").getString("text"));
                                setting.setAlertDialogMessage("catId", jsonObjectSetting.getString("cat_input"));
                                setting.setAlertDialogMessage("location_type", jsonObjectSetting.getString("location_type"));
                                setting.setAlertDialogMessage("gmap_lang", jsonObjectSetting.getString("gmap_lang"));
                                setting.setExitApp("exit",jsonObjectSetting.getString("confirm_app_close"));
                                 setting.setUserUnblock("Unblock_M",jsonObjectSetting.getString("unblock_user_m"));
                                gmap_lang = jsonObjectSetting.getString("gmap_lang");

                                setting.setGoogleButn(jsonObjectSetting.getJSONObject("registerBtn_show").getBoolean("google"));
                                setting.setfbButn(jsonObjectSetting.getJSONObject("registerBtn_show").getBoolean("facebook"));

                                JSONObject alertDialog = jsonObjectSetting.getJSONObject("dialog").getJSONObject("confirmation");
                                setting.setGenericAlertTitle(alertDialog.getString("title"));
                                setting.setGenericAlertMessage(alertDialog.getString("text"));
                                setting.setGenericAlertOkText(alertDialog.getString("btn_ok"));
                                setting.setGenericAlertCancelText(alertDialog.getString("btn_no"));
                                setting.setAdShowOrNot(true);
                                setting.isAppOpen(jsonObjectSetting.getBoolean("is_app_open"));
                                setting.checkOpen(jsonObjectSetting.getBoolean("is_app_open"));
                                setting.setGuestImage(jsonObjectSetting.getString("guest_image"));

                                JSONObject jsonObjectLocationPopup = jsonObjectSetting.getJSONObject("location_popup");
                                Location_popupModel Location_popupModel = new Location_popupModel();
                                Location_popupModel.setSlider_number(jsonObjectLocationPopup.getInt("slider_number"));
                                Location_popupModel.setSlider_step(jsonObjectLocationPopup.getInt("slider_step"));
                                Location_popupModel.setLocation(jsonObjectLocationPopup.getString("location"));
                                Location_popupModel.setText(jsonObjectLocationPopup.getString("text"));
                                Location_popupModel.setBtn_submit(jsonObjectLocationPopup.getString("btn_submit"));
                                Location_popupModel.setBtn_clear(jsonObjectLocationPopup.getString("btn_clear"));
                                setting.setLocationPopupModel(Location_popupModel);

                                JSONObject jsonObjectLocationSettings = jsonObjectSetting.getJSONObject("gps_popup");
                                setting.setShowNearby(jsonObjectSetting.getBoolean("show_nearby"));
                                setting.setShowHome(jsonObjectSetting.getBoolean("show_home_icon"));
                                setting.setShowAdvancedSearch(jsonObjectSetting.getBoolean("show_adv_search_icon"));
                                setting.setGpsTitle(jsonObjectLocationSettings.getString("title"));
                                setting.setGpsText(jsonObjectLocationSettings.getString("text"));
                                setting.setGpsConfirm(jsonObjectLocationSettings.getString("btn_confirm"));
                                setting.setGpsCancel(jsonObjectLocationSettings.getString("btn_cancel"));
                                setting.setUserVerified(true);

                                setting.setAdsPositionSorter(jsonObjectSetting.getBoolean("ads_position_sorter"));


                                setting.setNotificationTitle("");
                                setting.setNotificationMessage("");
                                setting.setNotificationTitle("");

                                if (setting.getAppOpen()) {
                                    setting.setNoLoginMessage(jsonObjectSetting.getString("notLogin_msg"));
                                }

                                setting.setFeaturedScrollEnable(jsonObjectSetting.getBoolean("featured_scroll_enabled"));
                                if (setting.isFeaturedScrollEnable()) {
                                    setting.setFeaturedScroolDuration(jsonObjectSetting.getJSONObject("featured_scroll").getInt("duration"));
                                    setting.setFeaturedScroolLoop(jsonObjectSetting.getJSONObject("featured_scroll").getInt("loop"));
                                }

                                jsonObjectAppRating = jsonObjectSetting.getJSONObject("app_rating");
                                jsonObjectAppShare = jsonObjectSetting.getJSONObject("app_share");

                                gmap_has_countries = jsonObjectSetting.getBoolean("gmap_has_countries");
                                if (gmap_has_countries) {
                                    gmap_countries = jsonObjectSetting.getString("gmap_countries");
                                }
                                app_show_languages = jsonObjectSetting.getBoolean("app_show_languages");

                                if (app_show_languages) {
                                    languagePopupTitle = jsonObjectSetting.getString("app_text_title");
                                    languagePopupClose = jsonObjectSetting.getString("app_text_close");
                                    app_languages = jsonObjectSetting.getJSONArray("app_languages");

                                }

                                ProgressModel progressModel = new ProgressModel();
                                JSONObject progressJsonObject = jsonObjectSetting.getJSONObject("upload").getJSONObject("progress_txt");
                                progressModel.setTitle(progressJsonObject.getString("title"));
                                progressModel.setSuccessTitle(progressJsonObject.getString("title_success"));
                                progressModel.setFailTitles(progressJsonObject.getString("title_fail"));
                                progressModel.setSuccessMessage(progressJsonObject.getString("msg_success"));
                                progressModel.setFailMessage(progressJsonObject.getString("msg_fail"));
                                progressModel.setButtonText(progressJsonObject.getString("btn_ok"));
                                SettingsMain.setProgressModel(progressModel);

                                permissionsModel permissionsModel = new permissionsModel();
                                JSONObject permissionJsonObject = jsonObjectSetting.getJSONObject("permissions");
                                permissionsModel.setTitle(permissionJsonObject.getString("title"));
                                permissionsModel.setDesc(permissionJsonObject.getString("desc"));
                                permissionsModel.setBtn_goTo(permissionJsonObject.getString("btn_goto"));
                                permissionsModel.setBtnCancel(permissionJsonObject.getString("btn_cancel"));
                                SettingsMain.setPermissionsModel(permissionsModel);

                                AdPostImageModel adPostImageModel = new AdPostImageModel();
                                JSONObject adpostInmageJsonObject = jsonObjectSetting.getJSONObject("ad_post");
                                adPostImageModel.setImg_size(adpostInmageJsonObject.getString("img_size"));
                                adPostImageModel.setImg_message(adpostInmageJsonObject.getString("img_message"));
                                adPostImageModel.setDim_is_show(adpostInmageJsonObject.getBoolean("dim_is_show"));
                                if (adpostInmageJsonObject.getBoolean("dim_is_show")) {
                                    adPostImageModel.setDim_width(adpostInmageJsonObject.getString("dim_width"));
                                    adPostImageModel.setDim_height(adpostInmageJsonObject.getString("dim_height"));
                                    adPostImageModel.setDim_height_message(adpostInmageJsonObject.getString("dim_height_message"));
                                }
                                setting.setAdPostImageModel(adPostImageModel);
                                setting.setShopUrl(jsonObjectSetting.getString("app_page_test_url"));

                                ArrayList<shopMenuModel> menuModelArrayList = new ArrayList<>();
                                JSONArray jsonArray = jsonObjectSetting.getJSONArray("shop_menu");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    shopMenuModel menuModel = new shopMenuModel();
                                    menuModel.setTitle(jsonObject.getString("title"));
                                    menuModel.setUrl(jsonObject.getString("url"));
                                    menuModelArrayList.add(menuModel);
                                }
                                setting.setShopMenu(menuModelArrayList);
                                CalanderTextModel calanderTextModel = new CalanderTextModel();
                                JSONObject calenderJsonObject = jsonObjectSetting.getJSONObject("calander_text");

                                calanderTextModel.setBtn_ok(calenderJsonObject.getString("ok_btn"));
                                calanderTextModel.setBtn_cancel(calenderJsonObject.getString("cancel_btn"));
                                calanderTextModel.setTitle(calenderJsonObject.getString("date_time"));
                                setting.setCalanderTextModel(calanderTextModel);

                                setting.setAlertDialogMessage("search_text", jsonObjectSetting.getString("search_text"));


                                JSONArray site_languages = jsonObjectSetting.getJSONArray("site_languages");

                                ChooseLanguageActivity.setData(jsonObjectSetting.getString("wpml_logo"),
                                        jsonObjectSetting.getString("wpml_header_title_1"),
                                        jsonObjectSetting.getString("wpml_header_title_2"),
                                        jsonObjectSetting.getString("wpml_menu_text"),
                                        site_languages);
                                Log.d(" info_site_languages", site_languages.toString());


                                if (!prefs.getBoolean("firstTime", false)) {

                                    setting.setUserLogin("0");

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("firstTime", true);
                                    editor.apply();
                                    Intent myIntent = new Intent(getApplicationContext(), ChooseLanguageActivity.class);
                                    startActivity(myIntent);


                                    if (setting.getUserLogin().equals("0")) {
                                        if (jsonObjectSetting.getBoolean("is_wpml_active")) {
                                            Intent myIntent1 = new Intent(getApplicationContext(), ChooseLanguageActivity.class);
                                            startActivity(myIntent1);
                                        } else {
                                            Intent intent = new Intent(activity, MainActivity.class);
                                            startActivity(intent);
                                        }

                                    }
                                } else {
                                    final Handler handler = new Handler();

                                    handler.postDelayed(() -> {
                                        //Do something after 100ms

                                        if (setting.getUserLogin().equals("0")) {
                                            if (setting.getAppOpen()) {
                                                SharedPreferences.Editor editor = activity.getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                                editor.putString("isSocial", "false");
                                                editor.apply();
                                                Intent intent = new Intent(activity, HomeActivity.class);
                                                startActivity(intent);
                                                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                                activity.finish();
                                                setting.setUserEmail("");
                                                setting.setUserImage("");
                                            } else {
                                                SplashScreen.this.finish();

                                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                            }
                                        } else {
                                            SplashScreen.this.finish();

                                            setting.isAppOpen(false);
                                            Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                        }
                                        try {
                                            if (jsonObjectSetting.getBoolean("is_wpml_active")) {
                                                if (setting.isLanguageChanged()) {
                                                    updateViews(SettingsMain.getLanguageCode());
                                                } else updateViews("en");
                                            } else {
                                                updateViews(gmap_lang);
                                                setting.setLanguageCode(gmap_lang);
                                            }
//                                            if(jsonObjectSetting.getBoolean("app_top_location")) {
//                                                if (setting.isLocationChanged()) {
//                                                    updateView(SettingsMain.getlocationId());
//                                                } else {
//                                                    updateView(SettingsMain.getlocationId());
//                                                    setting.setLocationId(SettingsMain.getlocationId());
//                                                }
//
//                                            }
//                                            else{
//                                                SplashScreen.this.finish();
//                                                setting.isAppOpen(false);
//                                                Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
//                                                startActivity(intent);
//
//                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }, 2000);
                                }
                            } else {
                                Toast.makeText(activity, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("info settings error", String.valueOf(t));
                    Log.d("info settings error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);

    }

    private void updateView(String locationId) {
        LocaleHelper.setLocale(this, locationId);
    }

}
