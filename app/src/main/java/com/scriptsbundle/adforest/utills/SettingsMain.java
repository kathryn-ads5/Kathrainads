package com.kathryn.kathryn.utills;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.Shop.shopMenuModel;
import com.kathryn.kathryn.SplashScreen;
import com.kathryn.kathryn.home.AddNewAdPost;
import com.kathryn.kathryn.home.helper.AdPostImageModel;
import com.kathryn.kathryn.home.helper.CalanderTextModel;
import com.kathryn.kathryn.home.helper.Location_popupModel;
import com.kathryn.kathryn.home.helper.ProgressModel;
import com.kathryn.kathryn.modelsList.permissionsModel;

import com.kathryn.kathryn.packages.Authorize2.AuthorizeNetModel;
import com.kathryn.kathryn.packages.BrainTreeModel;
import com.kathryn.kathryn.packages.PayHereModel;
import com.kathryn.kathryn.packages.PaystackModel;
import com.kathryn.kathryn.packages.WorldPay.WorldPayModel;
import com.kathryn.kathryn.utills.NoInternet.NetwordStateManager;

public class SettingsMain {
    public static final String PREF_NAME = "com.adforest";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 2, NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 0;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;
    private static Dialog dialog1;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    public String languagecode = "";
    public String locationId = "";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SettingsMain(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /* Checking Internet Connection */
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }
        }
        return false;
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
                activeNetwork = cm.getActiveNetworkInfo();
                Log.d("info d ", activeNetwork.getType() + "" + activeNetwork.getTypeName());
            }
        }


        if (null != activeNetwork) {
            Log.d("info adssad", "adasd");
            if (activeNetwork.getType() == TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        Log.d("info d", getConnectivityStatus(context) + "");
        int conn = getConnectivityStatus(context);
        int status = 0;
        if (conn == TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    public static void enableInternetReceiver(Context context) {
        ComponentName component = new ComponentName(context, NetwordStateManager.class);

        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void disableInternetReceiver(Context context) {
        Log.d("info check net", "disable net");
        ComponentName component = new ComponentName(context, NetwordStateManager.class);
        context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public static boolean isInternetReceiverEnabled(Context context) {
        ComponentName component = new ComponentName(context, NetwordStateManager.class);
        int status = context.getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            return true;
        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            return false;
        }
        return false;

    }
//    public static void showDialog(Context context) {
//        SettingsMain settingsMain = new SettingsMain(context);
//               dialog1 = new Dialog(context, R.style.AppTheme);
////        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog1.setContentView(R.layout.spinkit_loader);
//        dialog1.setCancelable(false);
//        AnimatedCircleLoadingView animatedCircleLoadingView = dialog1.findViewById(R.id.circle_loading_view);
//        animatedCircleLoadingView.startDeterminate();
//        animatedCircleLoadingView.setPercent(10);
//        animatedCircleLoadingView.stopOk();
//        TextView textView = dialog1.findViewById(R.id.id_title);
//        textView.setText(settingsMain.getAlertDialogMessage("waitMessage"));
//        dialog1.show();
//    }
//
//    public static void hideDialog() {
//        dialog1.dismiss();
//    }

    public static void showDilog(Context context) {
        SettingsMain settingsMain = new SettingsMain(context);

        dialog1 = new Dialog(context, R.style.AppTheme);
//        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.dilog_progressbar);
        dialog1.setCancelable(false);
        TextView textView = dialog1.findViewById(R.id.id_title);
        textView.setText(settingsMain.getAlertDialogMessage("waitMessage"));
        dialog1.show();
    }

    public static void hideDilog() {
        dialog1.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                path = saveBitmap(inContext,inImage, Bitmap.CompressFormat.JPEG, "image/jpeg", "Title");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }
        return Uri.parse(path);
    }

    private static String saveBitmap(Context context,@NonNull final Bitmap bitmap,
                              @NonNull final Bitmap.CompressFormat format, @NonNull final String mimeType,
                              @NonNull final String displayName) throws IOException {
        final String relativeLocation = Environment.DIRECTORY_PICTURES;

        final ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();

        OutputStream stream = null;
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues);

            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            stream = resolver.openOutputStream(uri);

            if (stream == null) {
                throw new IOException("Failed to get output stream.");
            }

            if (bitmap.compress(format, 95, stream) == false) {
                throw new IOException("Failed to save bitmap.");
            }
        } catch (IOException e) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return relativeLocation;
    }

    public static String getRealPathFromURI(Context inContext, Uri uri) {

        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            Log.e(AddNewAdPost.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = inContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, null, null , null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    public static void reload(Context context, String tag) {
        Fragment frg;
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();

        frg = manager.findFragmentByTag(tag);
        final FragmentTransaction ft = manager.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    public static boolean isSocial(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String restoredText = pref.getString("isSocial", "false");
        return restoredText.equals("true");
    }

    public static Uri decodeFile(Context context, File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//       File destFile = new File(file, "img_"
//                + dateFormatter.format(new Date()).toString() + ".png");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Title", null);
        String path = saveImage(b,context);
        return Uri.parse(path);
    }


    private static String saveImage(Bitmap b, Context context) {

        Bitmap bitmap = b;

        String state = Environment.getExternalStorageState();
        String root = "";
        String fileName = "/MyImage" + System.currentTimeMillis() + "Image" + ".jpg";
        String parent = "App_Name";
        File mFile;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            root = Environment.getExternalStorageDirectory().toString();
            mFile = new File(root, parent);
            if (!mFile.isDirectory())
                mFile.mkdirs();

        } else {
            root = context.getFilesDir().toString();
            mFile = new File(root, parent);
            if (!mFile.isDirectory())
                mFile.mkdirs();
        }

        String strCaptured_FileName = root + "/App_Name" + fileName;

        File f = new File(strCaptured_FileName);

        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bytes);

            FileOutputStream fo;

            fo = new FileOutputStream(f);

            fo.write(bytes.toByteArray());
            fo.close();
            bitmap.recycle();

            System.gc();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strCaptured_FileName;
    }

    public static void adforest_changeRattingBarcolor(RatingBar ratingBar, Context context) {
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(context.getResources().getColor(R.color.gradientfifth), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(context.getResources().getColor(R.color.rattingBarColor), PorterDuff.Mode.SRC_ATOP);

    }

    public static String getUserId() {
        return pref.getString("login", "0");
    }

    public static String getLanguageCode() {
        return pref.getString("languageCode", "en");
    }

    public void setLanguageCode(String languageCode) {
        editor.putString("languageCode", languageCode);
        editor.commit();
    }

    public void setLocationId(String locationId) {
        editor.putString("locationId", locationId);
        editor.commit();
    }

    public static String getlocationId() {
        return pref.getString("locationId", "");
    }

    public static permissionsModel getPermissionsModel() {


        Gson gson = new Gson();
        String permissionsModel = pref.getString("permissionsModel", null);
        permissionsModel model = gson.fromJson(permissionsModel, permissionsModel.class);
        return model;
    }

    public static void setPermissionsModel(permissionsModel permissionsModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(permissionsModel);
        editor.putString("permissionsModel", toJson);
        editor.apply();

    }

    public static void setProgressModel(ProgressModel progressModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(progressModel);
        editor.putString("progressModel", toJson);
        editor.apply();

    }

    public static ProgressModel getProgressSettings(Context context) {


        Gson gson = new Gson();
        String progressModel = pref.getString("progressModel", null);
        ProgressModel model = gson.fromJson(progressModel, ProgressModel.class);
        return model;
    }

    public static String getMainColor() {
        return pref.getString("mainColor", "#000000");
    }

    public void setMainColor(String mainColor) {
        editor.putString("mainColor", mainColor);
        editor.commit();
    }

    public String getAlertDialogMessage(String type) {
        return pref.getString(type, "You Are Not Connected To Internet. Please Check Your Internet Connection and Try Again");
    }

    public void setAlertDialogMessage(String type, String msg) {
        editor.putString(type, msg);
        editor.commit();
    }

    public String getKey(String name) {
        return pref.getString(name, "");
    }

    public void setKey(String name, String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public String getAlertDialogTitle(String type) {
        return pref.getString(type, "Error");
    }

    public void setAlertDialogTitle(String type, String title) {
        editor.putString(type, title);
        editor.commit();
    }

    public String getExitApp(String type) {
        return pref.getString(type, "Are You Sure You Want Exit ?");
    }

    public void setExitApp(String type, String msg) {
        editor.putString(type, msg);
        editor.commit();
    }

    public String getUserUnblock(String type) {
        return pref.getString(type, "Unblock this user to send message.");
    }

    public void setUserUnblock(String type, String msg) {
        editor.putString(type, msg);
        editor.commit();
    }


    public String getAlertOkText() {
        return pref.getString("AlertOkText", "OK");
    }

    public void setAlertOkText(String msg) {
        editor.putString("AlertOkText", msg);
        editor.commit();
    }

    public String getAlertCancelText() {
        return pref.getString("AlertCancelText", "CANCEL");
    }

    public void setAlertCancelText(String msg) {
        editor.putString("AlertCancelText", msg);
        editor.commit();
    }

    public String getPaidMessage() {
        return pref.getString("setPaidMessage", "CANCEL");
    }

    public void setPaidMessage(String msg) {
        editor.putString("setPaidMessage", msg);
        editor.commit();
    }
    public String getImgReqMessage() {
        return pref.getString("setImgReqMessage", "CANCEL");
    }

    public void setImgReqMessage(String msg) {
        editor.putString("setImgReqMessage", msg);
        editor.commit();
    }

    public void setUserPhone(String userPhone) {
        editor.putString("phone", userPhone);
        editor.commit();
    }

    public String getUserImage() {
        return pref.getString("image", "0");
    }

    public void setUserImage(String userImage) {
        editor.putString("image", userImage);
        editor.commit();
    }

    public String getUserLogin() {
        return pref.getString("login", "0");
    }

    public void setUserLogin(String userLogin) {
        editor.putString("login", userLogin);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString("username", "UserName");
    }

    public void setUserName(String userName) {
        editor.putString("username", userName);
        editor.commit();

    }

    public String getUserEmail() {
        return pref.getString("useremail", "UserEmail");
    }

    public void setUserEmail(String userEmail) {
        editor.putString("useremail", userEmail);
        editor.commit();

    }

    public String getUserPassword() {
        return pref.getString("userPassword", "0");
    }

    public void setUserPassword(String UserPassword) {
        editor.putString("userPassword", UserPassword);
        editor.commit();
    }

    public boolean getAdsShow() {
        return pref.getBoolean("showAd", false);
    }

    public void setAdsShow(boolean value) {
        editor.putBoolean("showAd", value);
        editor.commit();
    }

    public boolean getInterstitalShow() {
        return pref.getBoolean("is_show_initial", false);
    }

    public void setInterstitalShow(boolean value) {
        editor.putBoolean("is_show_initial", value);
        editor.commit();
    }

    public boolean getBannerShow() {
        return pref.getBoolean("is_show_banner", false);
    }

    public void setBannerShow(boolean value) {
        editor.putBoolean("is_show_banner", value);
        editor.commit();
    }

    public void setAdsPosition(String value) {
        editor.putString("adsPosition", value);
        editor.commit();
    }

    public String getAdsPostion() {
        return pref.getString("adsPosition", "adsPosition");
    }

    public String getBannerAdsId() {
        return pref.getString("banner_id", "");
    }

    public void setBannerAdsId(String value) {
        editor.putString("banner_id", value);
        editor.commit();
    }

    public String getInterstitialAdsId() {
        return pref.getString("interstital_id", "");
    }

    public void setInterstitialAdsId(String value) {
        editor.putString("interstital_id", value);
        editor.commit();
    }

    public int getAdsInitialTime() {
        return Integer.parseInt(pref.getString("time_initial", "30"));
    }

    public void setAdsInitialTime(String value) {
        editor.putString("time_initial", value);
    }

    public int getAdsDisplayTime() {
        return Integer.parseInt(pref.getString("time", "30"));
    }

    public void setAdsDisplayTime(String value) {
        editor.putString("time", value);
    }

    public boolean getAnalyticsShow() {
        return pref.getBoolean("AnalyticsShow", false);
    }

    public void setAnalyticsShow(boolean value) {
        editor.putBoolean("AnalyticsShow", value);
        editor.commit();
    }

    public String getAnalyticsId() {
        return pref.getString("analyticsId", "");
    }

    public void setAnalyticsId(String analyticsId) {
        editor.putString("analyticsId", analyticsId);
        editor.commit();
    }

    public void setGoogleButn(boolean value) {
        editor.putBoolean("googleButton", value);
        editor.commit();
    }

    public boolean getGooglButn() {
        return pref.getBoolean("googleButton", false);
    }

    public void setfbButn(boolean value) {
        editor.putBoolean("fbButton", value);
        editor.commit();
    }

    public boolean getfbButn() {
        return pref.getBoolean("fbButton", false);
    }

    public String getFireBaseId() {
        return pref.getString("firebaseid", "");
    }

    public void setFireBaseId(String value) {
        editor.putString("firebaseid", value);
        editor.commit();
    }

    public boolean getRTL() {
        return pref.getBoolean("RTL", false);
    }

    public void setRTL(boolean value) {
        editor.putBoolean("RTL", value);
        editor.commit();
    }

    public String getYoutubeApi() {
        return pref.getString("youTubeApi", "");
    }

    public void setYoutubeApi(String value) {
        editor.putString("youTubeApi", value);
        editor.commit();
    }

    public String getGenericAlertTitle() {
        return pref.getString("title", "Confirm!");
    }

    public void setGenericAlertTitle(String title) {
        editor.putString("title", title);
        editor.commit();
    }

    public String getGenericAlertMessage() {
        return pref.getString("text", "Are You Sure You Want To Do This!");
    }


    public void setGenericAlertMessage(String title) {
        editor.putString("text", title);
        editor.commit();
    }

    public String getGenericAlertOkText() {
        return pref.getString("btn_ok", "OK");
    }

    public void setGenericAlertOkText(String title) {
        editor.putString("btn_ok", title);
        editor.commit();
    }

    public String getGenericAlertCancelText() {
        return pref.getString("btn_no", "Cancel");
    }

    public void setGenericAlertCancelText(String title) {
        editor.putString("btn_no", title);
        editor.commit();
    }

    public void isAppOpen(boolean appOpen) {
        editor.putBoolean("app_open", appOpen);
        editor.commit();
    }

    public boolean getAppOpen() {
        return pref.getBoolean("app_open", false);
    }

    public void checkOpen(boolean appOpen) {
        editor.putBoolean("checkOpen", appOpen);
        editor.commit();
    }

    public String getGuestImage() {
        return pref.getString("guest_image", "");
    }

    public void setGuestImage(String message) {
        editor.putString("guest_image", message);
        editor.commit();
    }

    public boolean getCheckOpen() {
        return pref.getBoolean("checkOpen", false);
    }

    public String getNoLoginMessage() {
        return pref.getString("noLoginmessage", "Please login to perform this action.");
    }

    public void setNoLoginMessage(String message) {
        editor.putString("noLoginmessage", message);
        editor.commit();
    }

    public boolean isFeaturedScrollEnable() {
        return pref.getBoolean("featured_scroll_enabled", false);
    }

    public void setFeaturedScrollEnable(boolean featuredScrollEnable) {
        editor.putBoolean("featured_scroll_enabled", featuredScrollEnable);
        editor.commit();
    }

    public int getFeaturedScroolDuration() {
        return pref.getInt("featured_duration", 40);

    }

    public void setFeaturedScroolDuration(int duration) {
        editor.putInt("featured_duration", duration);
        editor.commit();
    }

    public int getFeaturedScroolLoop() {
        return pref.getInt("featured_loop", 40);

    }

    public void setFeaturedScroolLoop(int duration) {
        editor.putInt("featured_loop", duration);
        editor.commit();
    }


    //region LocationPopup

    public String getAppLogo() {
        return pref.getString("appLogo", "");

    }

    public void setAppLogo(String appLogo) {
        editor.putString("appLogo", appLogo);
        editor.commit();
    }

    public void setPaymentCompletedMessage(String paymentCompletedMessage) {
        editor.putString("message", paymentCompletedMessage);
        editor.commit();
    }

    public String getpaymentCompletedMessage() {
        return pref.getString("message", "Order Places Succ");
    }

    public String getGpsTitle() {
        return pref.getString("gpsTitle", "GPS AppCompatPreferenceActivity");
    }

    public void setGpsTitle(String gpsTitle) {
        editor.putString("gpsTitle", gpsTitle);
        editor.commit();
    }

    public String getGpsText() {
        return pref.getString("gpsText", "GPS is not enabled. Do you want to go to settings menu?");
    }

    public void setGpsText(String gpsText) {
        editor.putString("gpsText", gpsText);
        editor.commit();
    }

    public String getGpsConfirm() {
        return pref.getString("gpsConfirm", "AppCompatPreferenceActivity");
    }

    public void setGpsConfirm(String gpsConfirm) {
        editor.putString("gpsConfirm", gpsConfirm);
        editor.commit();
    }

    public String getGpsCancel() {
        return pref.getString("gpsCancel", "Clear");
    }

    public void setGpsCancel(String gpsCancel) {
        editor.putString("gpsCancel", gpsCancel);
        editor.commit();
    }

    public void setShowNearby(boolean b) {
        editor.putBoolean("show_nearby", b);
        editor.commit();
    }

    public boolean getShowHome() {
        return pref.getBoolean("show_home", false);
    }

    public void setShowHome(boolean b) {
        editor.putBoolean("show_home", b);
        editor.commit();
    }

    public boolean getShowAdvancedSearch() {
        return pref.getBoolean("show_advanced", false);
    }

    public void setShowAdvancedSearch(boolean b) {
        editor.putBoolean("show_advanced", b);
        editor.commit();
    }

    public boolean getShowNearBy() {
        return pref.getBoolean("show_nearby", false);
    }

    public boolean getAdsPositionSorter() {
        return pref.getBoolean("ads_position_sorter", false);
    }

    public void setAdsPositionSorter(boolean b) {
        editor.putBoolean("ads_position_sorter", b);
        editor.commit();
    }

    public String getLatitude() {
        return pref.getString("nearby_latitude", "");
    }

    public void setLatitude(String latitude) {
        editor.putString("nearby_latitude", latitude);
        editor.commit();
    }

    //endregion

    public String getLongitude() {
        return pref.getString("nearby_longitude", "");
    }

    public void setLongitude(String longitude) {
        editor.putString("nearby_longitude", longitude);
        editor.commit();
    }

    public String getDistance() {
        return pref.getString("nearby_distance", "");
    }

    public void setDistance(String longitude) {
        editor.putString("nearby_distance", longitude);
        editor.commit();
    }

    public String getNotificationTitle() {
        return pref.getString("notificationTitle", "");
    }

    public void setNotificationTitle(String notificationTitle) {
        editor.putString("notificationTitle", notificationTitle);
        editor.commit();
    }

    public String getNotificationMessage() {
        return pref.getString("notificationMessage", "");
    }

    public void setNotificationMessage(String notificationMessage) {
        editor.putString("notificationMessage", notificationMessage);
        editor.commit();
    }

    public String getNotificationImage() {
        return pref.getString("notificationImage", "");
    }

    public void setNotificationImage(String notificationImage) {
        editor.putString("notificationImage", notificationImage);
        editor.commit();
    }

    public boolean getUserVerified() {
        return pref.getBoolean("UserVerified", true);
    }

    public void setUserVerified(boolean userVerified) {
        editor.putBoolean("UserVerified", userVerified);
        editor.commit();
    }

    public boolean isLanguageChanged() {
        return pref.getBoolean("isLanguageChanged", false);
    }

    public void setLanguageChanged(boolean b) {
        editor.putBoolean("isLanguageChanged", b);
        editor.commit();
    }

    public boolean isLocationChanged() {
        return pref.getBoolean("isLocationChanged", false);
    }

    public void setLocationChanged(boolean b) {
        editor.putBoolean("isLocationChanged", b);
        editor.commit();
    }

    public boolean isAdShowOrNot() {
        return pref.getBoolean("setShowAd", true);
    }

    public void setAdShowOrNot(boolean b) {
        editor.putBoolean("setShowAd", b);
        editor.commit();
    }

    public String getShopUrl() {

        return pref.getString("app_page_test_url", "");
    }

    public void setShopUrl(String set) {
        editor.putString("app_page_test_url", set);
        editor.commit();
    }

    public void setLocationPopupModel(Location_popupModel locationPopupModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(locationPopupModel);
        editor.putString("locationPopupModel", toJson);
        editor.apply();

    }

    public Location_popupModel getLocationPopupModel(Context context) {
        Gson gson = new Gson();
        String progressModel = pref.getString("locationPopupModel", null);
        Location_popupModel model = gson.fromJson(progressModel, Location_popupModel.class);
        return model;
    }

    public void setAdPostImageModel(AdPostImageModel AdPostImageModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(AdPostImageModel);
        editor.putString("AdPostImageModel", toJson);
        editor.apply();

    }

    public AdPostImageModel getAdPostImageModel(Context context) {
        Gson gson = new Gson();
        String progressModel = pref.getString("AdPostImageModel", null);
        AdPostImageModel model = gson.fromJson(progressModel, AdPostImageModel.class);
        return model;
    }

    public void setCalanderTextModel(CalanderTextModel calanderTextModel) {
        Gson gson = new Gson();
        String toJson = gson.toJson(calanderTextModel);
        editor.putString("calanderTextModel", toJson);
        editor.apply();

    }

    public CalanderTextModel getCalanderTextModel(Context context) {
        Gson gson = new Gson();
        String progressModel = pref.getString("calanderTextModel", null);
        CalanderTextModel model = gson.fromJson(progressModel, CalanderTextModel.class);
        return model;
    }

    public ArrayList<shopMenuModel> getShopMenu() {
        Type listOfBecons = new TypeToken<List<shopMenuModel>>() {
        }.getType();

        ArrayList<shopMenuModel> shop_menu = new Gson().fromJson(pref.getString("shop_menu", ""), listOfBecons);
        return shop_menu;
    }

    public void setShopMenu(ArrayList<shopMenuModel> shopListModes) {
        Type type = new TypeToken<List<shopMenuModel>>() {
        }.getType();

        String strBecons = new Gson().toJson(shopListModes, type);
        pref.edit().putString("shop_menu", strBecons).apply();
    }

    public static void savePopupSettings(AdForest_PopupModel model, Context context) {
        Gson gson = new Gson();
        String popupSetings = gson.toJson(model);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup_settings", popupSetings);
        editor.apply();

    }

    public static AdForest_PopupModel getPopupSettings(Context context) {


        Gson gson = new Gson();
        String popupSettings = PreferenceManager.getDefaultSharedPreferences(context).getString("popup_settings", null);
        AdForest_PopupModel model = gson.fromJson(popupSettings, AdForest_PopupModel.class);
        return model;
    }
//    public static final void saveTheme(int theme) {
//        editor.putInt("prefs.theme", theme).apply();
//    }
//
//    public static final int getSavedTheme() {
//        return pref.getInt("prefs.theme", -1);
//    }



//Payment Methods

    public static WorldPayModel worldPayModel;
    public static void setWorldPayCreds(WorldPayModel model){
        worldPayModel = model;
    }

    public static WorldPayModel getWorldPayCreds(){
        return worldPayModel;
    }


    public static PaystackModel paystackModel;

    public void setPaystackModel(PaystackModel model){
        paystackModel = model;
    }

    public PaystackModel getPaystackModel(){

        return paystackModel;
    }


    public static PayHereModel payhereModel;

    public void setPayHereModel(PayHereModel model){
        payhereModel = model;
    }

    public PayHereModel getPayHereModel(){

        return payhereModel;
    }


    public static BrainTreeModel brainTreeModel;

    public void setBrainTreeModel(BrainTreeModel model){
        brainTreeModel = model;
    }

    public BrainTreeModel getBrainTreeModel(){

        return brainTreeModel;
    }


    public static AuthorizeNetModel authorizeNetModel;

    public void setAuthorizeNetModel(AuthorizeNetModel model){
        authorizeNetModel = model;
    }

    public AuthorizeNetModel getAuthorizeNetModel(){

        return authorizeNetModel;
    }


}