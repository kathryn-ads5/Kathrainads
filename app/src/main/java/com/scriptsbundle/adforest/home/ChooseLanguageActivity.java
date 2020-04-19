package com.kathryn.kathryn.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import com.kathryn.kathryn.R;
import com.kathryn.kathryn.SplashScreen;
import com.kathryn.kathryn.adapters.ItemLanguagueAdapter;
import com.kathryn.kathryn.helper.GridSpacingItemDecoration;
import com.kathryn.kathryn.helper.ItemLanguageOnclicklinstener;
import com.kathryn.kathryn.helper.LocaleHelper;
import com.kathryn.kathryn.home.helper.chooseLanguageModel;
import com.kathryn.kathryn.utills.SettingsMain;

public class ChooseLanguageActivity extends AppCompatActivity {
    SettingsMain settingsMain;
    private ImageView logo;
    private TextView headingChooseLanguage, boldLang,NodataTextView;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout,NodataView;
    static String image, title1, title2, title3, isMultiLine, MainHeading;
    static JSONArray siteLanguages;


    List<chooseLanguageModel> languageModelLists = new ArrayList<>();

    public static void setData(String image, String title1, String title2, String MainHeading, JSONArray jsonArray) {
        ChooseLanguageActivity.image = image;
        ChooseLanguageActivity.title1 = title1;
        ChooseLanguageActivity.title2 = title2;


        ChooseLanguageActivity.MainHeading = MainHeading;
        ChooseLanguageActivity.siteLanguages = jsonArray;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(MainHeading);
        toolbar.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));

        settingsMain = new SettingsMain(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }


        relativeLayout = (RelativeLayout) findViewById(R.id.language_activiy);
        logo = (ImageView) findViewById(R.id.logo_pick_your_language);
        headingChooseLanguage = (TextView) findViewById(R.id.txt_pick_your_language);
        boldLang = (TextView) findViewById(R.id.lang);
//        NodataTextView=(TextView)findViewById(R.id.nodataTextView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_pick_your_language);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(ChooseLanguageActivity.this, 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(MyLayoutManager);
        int spacing = 30; // 50px
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));
        adforest_setAllLanguages();
        //blink();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void adforest_setAllLanguages() {

        ItemLanguagueAdapter adapter = new ItemLanguagueAdapter(getApplicationContext(), languageModelLists);
        headingChooseLanguage.setText(title1);
        boldLang.setText(title2);

        Picasso.with(getApplicationContext()).load(image).into(logo);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < siteLanguages.length(); i++) {
            chooseLanguageModel chooseLanguageModel = new chooseLanguageModel();
            JSONObject jsonObject = null;
            try {
                jsonObject = siteLanguages.getJSONObject(i);
                chooseLanguageModel.setTitle(jsonObject.getString("native_name"));
                chooseLanguageModel.setImage(jsonObject.getString("flag_url"));
                chooseLanguageModel.setLanguageCode(jsonObject.getString("code"));
                languageModelLists.add(chooseLanguageModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (siteLanguages == null) {
//                NodataTextView.setVisibility(View.VISIBLE);
//                NodataTextView.setTextColor(Color.parseColor(SettingsMain.getMainColor()));
//                NodataTextView.setText(title3);
//            }
        }
        adapter.setItemLanguageOnclicklinstener(new ItemLanguageOnclicklinstener() {
            @Override
            public void onItemClick(chooseLanguageModel chooseLanguageModel) {

                ChooseLanguageActivity.this.updateViews(chooseLanguageModel.getLanguageCode());
                Intent intent = new Intent(ChooseLanguageActivity.this.getApplicationContext(), SplashScreen.class);
//                Intent intent = new Intent(getApplicationContext(), ChooseLocationActivity.class);
//                                            startActivity(myIntent2);
                settingsMain.setLanguageCode(chooseLanguageModel.getLanguageCode());
                settingsMain.setLanguageChanged(true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ChooseLanguageActivity.this.startActivity(intent);
                ChooseLanguageActivity.this.finish();
            }
        });
    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 3000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        headingChooseLanguage = (TextView) findViewById(R.id.txt_pick_your_language);
                        boldLang = (TextView) findViewById(R.id.lang);
                        logo = (ImageView) findViewById(R.id.logo_pick_your_language);
                        if (logo.getVisibility() == View.VISIBLE) {
                            logo.setVisibility(View.INVISIBLE);
                        } else {
                            logo.setVisibility(View.VISIBLE);
                        }
                        if (boldLang.getVisibility() == View.VISIBLE) {
                            boldLang.setVisibility(View.INVISIBLE);
                        } else {
                            boldLang.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

}

