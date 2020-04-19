package com.kathryn.kathryn.Shop;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.LocaleHelper;
import com.kathryn.kathryn.utills.SettingsMain;

public class shopActivity extends AppCompatActivity {
    public static String title;
    SettingsMain settingsMain;
    Context context;
    WebView webView;
//    TextView textView;
//    Spinner spinner;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        context = this;
        settingsMain = new SettingsMain(context);
        if (title.equals("")) {
            title = "shop";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(SettingsMain.getMainColor()));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        setTitle(title);
        if (SettingsMain.isConnectingToInternet(context)) {
            adforest_getData(settingsMain.getShopUrl());
        } else {
            Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.shopMenu);

        for (int i = 0; i < settingsMain.getShopMenu().size(); i++) {
            menuItem.getSubMenu().add(0, i + 1, Menu.NONE,
                    settingsMain.getShopMenu().get(i).getTitle());
            final int finalI1 = i;
            menuItem.getSubMenu().getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    adforest_getData(settingsMain.getShopMenu().get(finalI1).getUrl());
                    return false;
                }
            });
        }

        return true;
    }

    private void adforest_getData(String url) {
        final Map<String, String> map = new HashMap<>();
        map.put("Adforest-Shop-Request", "body");
        if (settingsMain.getAppOpen()) {
            webView.loadUrl(url, map);
        } else {
            String authToken = Credentials.basic(settingsMain.getUserEmail(), settingsMain.getUserPassword());
            Log.d("authToken",authToken);
            map.put("Authorization", authToken);
            if (SettingsMain.isSocial(context)) {
                map.put("AdForest-Login-Type", "social");
            }

            webView.loadUrl(url, map);
        }
        Log.d("Adforest-Shop-Request",url);
        Log.d("Authorization",settingsMain.getUserEmail());
        Log.d("Authorization",settingsMain.getUserPassword());
                webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler, String host, String realm) {
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, map);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                SettingsMain.showDilog(context);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SettingsMain.hideDilog();
            }
        });
//        Map<String, String> map = new HashMap<>();
//        String authToken = Credentials.basic(settingsMain.getUserEmail(), settingsMain.getUserPassword());
//        map.put("Authorization",authToken);
//        if (SettingsMain.isSocial(context)) {
//            map.put("AdForest-Login-Type", "social");
//        }
//        Log.d("info",map.toString());
//        webView.loadUrl("http://adforest-testapp.scriptsbundle.com/shop/",map);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}


