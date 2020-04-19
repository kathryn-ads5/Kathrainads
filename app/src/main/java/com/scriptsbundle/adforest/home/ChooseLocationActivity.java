package com.kathryn.kathryn.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.adapters.ItemLocationAdapter;
import com.kathryn.kathryn.helper.GridSpacingItemDecoration;
import com.kathryn.kathryn.helper.ItemLocationOnclicklistener;
import com.kathryn.kathryn.helper.LocaleHelper;
import com.kathryn.kathryn.home.helper.ChooseLocationModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;


public class ChooseLocationActivity extends AppCompatActivity {
    SettingsMain settingsMain;
    RelativeLayout relativeLayout;
    TextView headingChooseLocation;
    RecyclerView recyclerview;
    static String image, title1, title2, title3, isMultiLine, MainHeading;
    static JSONArray siteLocations;
    List<ChooseLocationModel> locationModelList = new ArrayList<>();
    RestService restService;
    SwipeRefreshLayout swipeRefreshLayout;

    public static void setData(String title1, JSONArray jsonArray) {
        ChooseLocationActivity.title1 = title1;


        //        ChooseLanguageActivity.isMultiLine=isMultiLine;

        ChooseLocationActivity.siteLocations = jsonArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        settingsMain = new SettingsMain(this);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);
        relativeLayout=findViewById(R.id.location_activiy);
//        headingChooseLocation = findViewById(R.id.txt_choose_location);
        recyclerview = findViewById(R.id.recyclerview_choose_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        setTitle(title1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(recyclerview, false);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(ChooseLocationActivity.this, 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerview.setLayoutManager(MyLayoutManager);
        int spacing = 0; // 50px
        recyclerview.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));
        adforest_setAllLocations();
        swipeRefreshLayout=findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adforest_setAllLocations();
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },250);
            }
        });
    }

    private void adforest_setAllLocations() {
        ItemLocationAdapter adapter = new ItemLocationAdapter(getApplicationContext(), locationModelList);
//        headingChooseLocation.setText(title1);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        locationModelList.clear();

        for (int i = 0; i < siteLocations.length(); i++) {

            ChooseLocationModel chooseLocationModel = new ChooseLocationModel();
            JSONObject jsonObject = null;
            try {
                jsonObject = siteLocations.getJSONObject(i);
                chooseLocationModel.setLocationId(jsonObject.getString("location_id"));
                chooseLocationModel.setTitle(jsonObject.getString("location_name"));
                locationModelList.add(chooseLocationModel);
//                SettingsMain.hideDilog();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.setItemLocationOnclicklistener(new ItemLocationOnclicklistener() {
            @Override
            public void onItemClick(ChooseLocationModel chooseLocationModel) {
                ChooseLocationActivity.this.updateViews(chooseLocationModel.getLocationId());
                adforest_PostLocationId(chooseLocationModel.getLocationId());
//                ChooseLanguageActivity.this.updateViews(chooseLanguageModel.getLanguageCode());
//                Intent intent = new Intent(ChooseLocationActivity.this.getApplicationContext(), HomeActivity.class);
//                settingsMain.setLanguageCode(chooseLanguageModel.getLanguageCode());
//                settingsMain.setLocationId(chooseLocationModel.getLocationId());
//                settingsMain.setLocationChanged(true);
//                settingsMain.setLanguageChanged(true);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                ChooseLocationActivity.this.startActivity(intent);
//                ChooseLocationActivity.this.finish();
            }
        });
    }

    public void adforest_PostLocationId(String locationId) {
        if (SettingsMain.isConnectingToInternet(this)) {
            SettingsMain.showDilog(this);
            JsonObject params = new JsonObject();
            params.addProperty("location_id", locationId);
            Log.d("info post LocationId", "" + params.toString());
//            Call<ResponseBody> myCall = restService.postLocationID(params,UrlController.AddHeaders(this));
            Call<ResponseBody> mycall2 = restService.postLocationID(params, UrlController.AddHeaders(this));

            Log.d("resSErvice", restService.toString());
            mycall2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    if (responseObj.isSuccessful()) {
                        Log.d("info location Resp", "" + responseObj.toString());
                        JSONObject response = null;
                        try {
                            response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ChooseLocationActivity.this.getApplicationContext(), HomeActivity.class);
                                settingsMain.setLocationChanged(true);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ChooseLocationActivity.this.startActivity(intent);
                                ChooseLocationActivity.this.finish();
                            }
                             SettingsMain.hideDilog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String locationId) {
        LocaleHelper.setLocale(this, locationId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
