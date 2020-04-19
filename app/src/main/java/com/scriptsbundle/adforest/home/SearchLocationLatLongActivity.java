package com.kathryn.kathryn.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
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

public class SearchLocationLatLongActivity extends AppCompatActivity {
    SettingsMain settingsMain;
    RelativeLayout relativeLayout;
    RecyclerView recyclerview;
    static String title1;
    static JSONArray siteLocations;
    List<ChooseLocationModel> locationModelList=new ArrayList<>();
    RestService restService;
    public static void setData( String title1, JSONArray jsonArray) {
        SearchLocationLatLongActivity.title1 = title1;



        SearchLocationLatLongActivity.siteLocations = jsonArray;
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
        recyclerview = findViewById(R.id.recyclerview_choose_location);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        setTitle(title1);
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(recyclerview, false);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(SearchLocationLatLongActivity.this, 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerview.setLayoutManager(MyLayoutManager);
        int spacing = 30; // 50px
        recyclerview.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));
        adforest_setAllLocations();

    }
    private void adforest_setAllLocations() {
        ItemLocationAdapter adapter=new ItemLocationAdapter(getApplicationContext(),locationModelList);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < siteLocations.length(); i++) {

            ChooseLocationModel chooseLocationModel= new ChooseLocationModel();
            JSONObject jsonObject = null;
            try {
                jsonObject=siteLocations.getJSONObject(i);
                chooseLocationModel.setLocationId(jsonObject.getString("location_id"));
                chooseLocationModel.setTitle(jsonObject.getString("location_name"));

                locationModelList.add(chooseLocationModel);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.setItemLocationOnclicklistener(new ItemLocationOnclicklistener() {
            @Override
            public void onItemClick(ChooseLocationModel chooseLocationModel) {
                SearchLocationLatLongActivity.this.updateViews(chooseLocationModel.getLocationId());

                try {
                    setResult(1, Intent.getIntent(chooseLocationModel.getTitle()));
                    finish();
                } catch (URISyntaxException e) {
                    finish();
                    e.printStackTrace();
                }
//                Intent intent = new Intent(SearchLocationLatLongActivity.this.getApplicationContext(), HomeActivity.class);
//                intent.putExtra("location_id", chooseLocationModel.getLocationId());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                SearchLocationLatLongActivity.this.startActivity(intent);
            }
        });
    }

    public void startFragment(Fragment someFragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frameContainer);

        if (fragment == null) {
            fragment = someFragment;

            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment)
                    .commit();
        }
    }
    public void replaceFragment(Fragment someFragment, String tag) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

        if (fragment != fragment2) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
            transaction.replace(R.id.frameContainer, someFragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
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
