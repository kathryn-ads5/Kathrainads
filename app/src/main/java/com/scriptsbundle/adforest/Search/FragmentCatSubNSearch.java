package com.kathryn.kathryn.Search;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.Search.adapter.ItemCatgorySubListAdapter;
import com.kathryn.kathryn.ad_detail.Ad_detail_activity;
import com.kathryn.kathryn.adapters.ItemSearchFeatureAdsAdapter;
import com.kathryn.kathryn.helper.CatSubCatOnclicklinstener;
import com.kathryn.kathryn.home.FragmentAllCategories;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.messages.Message;
import com.kathryn.kathryn.modelsList.catSubCatlistModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.NestedScroll;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class FragmentCatSubNSearch extends Fragment {

    public static String requestFrom = "";

    ArrayList<catSubCatlistModel> searchedAdList = new ArrayList<>();
    ArrayList<catSubCatlistModel> featureAdsList = new ArrayList<>();
    RecyclerView MyRecyclerView, recyclerViewFeatured;
    //EditText editTextSearch;
    SettingsMain settingsMain;
    TextView textViewTitleFeature, textViewFilterText;
    ItemCatgorySubListAdapter itemCatgorySubListAdapter;
    ItemSearchFeatureAdsAdapter itemSearchFeatureAdsAdapter;
    LinearLayout viewProductLayout;
    JSONObject jsonObjectPagination;
    JsonObject lastSentParamas;
    LinearLayout linearLayoutCollapse;
    Spinner spinnerFilter;
    //    Button spinnerFilterText;
    LinearLayout linearLayoutFilter;
    boolean isSort = false;
    RelativeLayout relativeLayoutSpiner;
    NestedScrollView scrollView;
    boolean loading = true, hasNextPage = false;
    ProgressBar progressBar;
    int currentPage = 1, nextPage = 1, totalPage = 0;
    String myId, title, ad_country, nearby_latitude, nearby_longitude, nearby_distance, ad_cats1, resquestFrom;
    RestService restService;
    GridLayoutManager MyLayoutManager2;
    private JSONObject jsonObjectFilterSpinner;
    private boolean spinnerTouched2 = false;

    public FragmentCatSubNSearch() {
        // Required empty public constructor
    }

    public void adforest_recylerview_autoScroll(final int duration, final int pixelsToMove, final int delayMillis,
                                                final RecyclerView recyclerView, final GridLayoutManager gridLayoutManager
            , final ItemSearchFeatureAdsAdapter adapter) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                try {
                    if (count < adapter.getItemCount()) {
                        if (count == adapter.getItemCount() - 1) {
                            flag = false;
                        } else if (count == 0) {
                            flag = true;
                        }
                        if (flag) count++;
                        else count--;

                        recyclerView.smoothScrollToPosition(count);
                        handler.postDelayed(this, duration);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        };

        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cat_subcatlist, container, false);
        scrollView = view.findViewById(R.id.scrollView);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myId = bundle.getString("id", "");
            title = bundle.getString("title", "");
            ad_country = bundle.getString("ad_country", "");
            nearby_latitude = bundle.getString("nearby_latitude", "");
            nearby_longitude = bundle.getString("nearby_longitude", "");
            nearby_distance = bundle.getString("nearby_distance", "");
            ad_cats1 = bundle.getString("ad_cats1", "");
            resquestFrom = bundle.getString("RequestFrom", "");
        }
        settingsMain = new SettingsMain(getActivity());

        linearLayoutCollapse = view.findViewById(R.id.linearLayout);
        linearLayoutFilter = view.findViewById(R.id.filter_layout);
        textViewFilterText = view.findViewById(R.id.textViewFilter);
        spinnerFilter = view.findViewById(R.id.spinner);
//        spinnerFilterText=view.findViewById(R.id.spinnerafter);
//        spinnerFilterText.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        relativeLayoutSpiner = view.findViewById(R.id.rel1);

        viewProductLayout = view.findViewById(R.id.customOptionLL);

        textViewTitleFeature = view.findViewById(R.id.textView6);

        MyRecyclerView = view.findViewById(R.id.recycler_view);
        MyRecyclerView.setHasFixedSize(true);
        MyRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        recyclerViewFeatured = view.findViewById(R.id.recycler_view2);
        recyclerViewFeatured.setHasFixedSize(true);
        MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        recyclerViewFeatured.setLayoutManager(MyLayoutManager2);

        scrollView.setOnScrollChangeListener(new NestedScroll() {
            @Override
            public void onScroll() {
                if (loading) {
                    loading = false;
                    Log.d("info data object", "sdfasdfadsasdfasdfasdf");

                    if (hasNextPage) {
                        progressBar.setVisibility(View.VISIBLE);
                        adforest_loadmore();
                    }
                }
            }
        });

//        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
//                        scrollY > oldScrollY) {
//
//                    if (nextPage <= totalPage) {
//                        progressBar.setVisibility(View.VISIBLE);
//                        adforest_loadmore();
//
//                        Log.d("heeeeeeelo", nextPage + "==" + totalPage);
//
//                    } else {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//            }
//        });

        adforest_submitQuery("");
//        if (resquestFrom != null) {
//            ((Ad_detail_activity) getActivity()).updateApooi(new Ad_detail_activity.UpdateFragment() {
//                @Override
//                public void update(String ad) {
//                    title = ad;
//                    adforest_submitQuery("");
//                    scrollView.scrollTo(0, 0);
//                }
//            });
//        }
        if (requestFrom.equals("")) {

            ((HomeActivity) getActivity()).updateApi(new HomeActivity.UpdateFragment() {
                @Override
                public void updatefrag(String s) {
                    title = s;
                    adforest_submitQuery("");
                    scrollView.scrollTo(0, 0);

                }

                @Override
                public void updatefrag(String latitude, String longitude, String distance) {
                    nearby_latitude = latitude;
                    nearby_longitude = longitude;
                    nearby_distance = distance;
                    adforest_submitQuery("");
                }
            });
        }


        if (requestFrom.equals("")) {
            SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setEnabled(true);
        }


        return view;
    }

    private void adforest_submitQuery(String s) {

        JsonObject params = new JsonObject();
        if (!myId.equals("")) {
            params.addProperty(settingsMain.getAlertDialogMessage("catId"), myId);
        }
        if (!title.equals("")) {
            params.addProperty("ad_title", title);
        }
        if (isSort) {
            params.addProperty("sort", s);
        }
        if (!ad_country.equals("")) {
            params.addProperty("ad_country", ad_country);
        }
        if (!nearby_latitude.equals("") && !nearby_longitude.equals("")) {
            params.addProperty("nearby_latitude", nearby_latitude);
            params.addProperty("nearby_longitude", nearby_longitude);
            params.addProperty("nearby_distance", nearby_distance);
        }
        if (!ad_cats1.equals("")) {
            params.addProperty("ad_cats1", ad_cats1);
        }
        lastSentParamas = params;

        params.addProperty("page_number", 1);


        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());
            Log.d("info Send MenuSearch =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postGetMenuSearchData(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info MenuSearch Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info MenuSearch object", "" + response.getJSONObject("data"));
                                HomeActivity.checkLoading = false;

                                getActivity().setTitle(response.getJSONObject("extra").getString("title"));

                                if (response.getJSONObject("extra").getBoolean("is_show_featured")) {
                                    textViewTitleFeature.setVisibility(View.VISIBLE);
                                } else {
                                    textViewTitleFeature.setVisibility(View.GONE);
                                }
//                                spinnerFilterText.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Intent i = new Intent(getActivity(), SearchActivity.class);
////                                        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.zoomin));
//                                        startActivity(i);
//
//                                    }
//                                });
//

                                try {

                                    jsonObjectFilterSpinner = response.getJSONObject("topbar");
                                    final JSONArray dropDownJSONOpt = jsonObjectFilterSpinner.getJSONArray("sort_arr");
                                    final ArrayList<String> SpinnerOptions;
                                    SpinnerOptions = new ArrayList<>();
                                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                                        SpinnerOptions.add(dropDownJSONOpt.getJSONObject(j).getString("value"));
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_medium, SpinnerOptions);
                                    spinnerFilter.setAdapter(adapter);

                                    spinnerFilter.setOnTouchListener((v, event) -> {
                                        System.out.println("Real touch felt.");
                                        spinnerTouched2 = true;
                                        return false;
                                    });

                                    spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            if (spinnerTouched2) {
                                                try {
                                                    //Toast.makeText(getActivity(), "" + dropDownJSONOpt.getJSONObject(i).getString("key"), Toast.LENGTH_SHORT).show();
                                                    isSort = true;
                                                    adforest_submitQuery(dropDownJSONOpt.getJSONObject(i).getString("key"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            spinnerTouched2 = false;
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                jsonObjectPagination = response.getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
                                loading = true;

                                adforest_loadList(response.getJSONObject("data").getJSONObject("featured_ads"),
                                        response.getJSONObject("data").getJSONArray("ads"),
                                        response.getJSONObject("topbar"));

                                itemCatgorySubListAdapter = new ItemCatgorySubListAdapter(getActivity(), searchedAdList);
                                itemSearchFeatureAdsAdapter = new ItemSearchFeatureAdsAdapter(getActivity(), featureAdsList);

                                recyclerViewFeatured.setAdapter(itemSearchFeatureAdsAdapter);

                                if (settingsMain.isFeaturedScrollEnable()) {

                                    adforest_recylerview_autoScroll(settingsMain.getFeaturedScroolDuration(),
                                            40, settingsMain.getFeaturedScroolLoop(),
                                            recyclerViewFeatured, MyLayoutManager2, itemSearchFeatureAdsAdapter);
                                }
                                MyRecyclerView.setAdapter(itemCatgorySubListAdapter);

                                itemSearchFeatureAdsAdapter.setOnItemClickListener(new CatSubCatOnclicklinstener() {
                                    @Override
                                    public void onItemClick(catSubCatlistModel item) {
                                        Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                                        intent.putExtra("adId", item.getId());
                                        getActivity().startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                    }

                                    @Override
                                    public void onItemTouch(catSubCatlistModel item) {

                                    }

                                    @Override
                                    public void addToFavClick(View v, String position) {
                                    }
                                });

                                itemCatgorySubListAdapter.setOnItemClickListener(new CatSubCatOnclicklinstener() {
                                    @Override
                                    public void onItemClick(catSubCatlistModel item) {

                                        Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                                        intent.putExtra("adId", item.getId());
                                        getActivity().startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                    }

                                    @Override
                                    public void onItemTouch(catSubCatlistModel item) {

                                    }

                                    @Override
                                    public void addToFavClick(View v, String position) {

                                    }
                                });
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
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info MenuSearch ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info MenuSearch err", String.valueOf(t));
                        Log.d("info MenuSearch err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
        isSort = false;
    }

    private void adforest_loadmore() {

        lastSentParamas.addProperty("page_number", nextPage);
        Log.d("info loadMore MenuSrch=", "" + lastSentParamas.toString());

        if (SettingsMain.isConnectingToInternet(getActivity())) {
            Call<ResponseBody> myCall = restService.postGetMenuSearchData(lastSentParamas, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info MenuSearch Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info MenuSearch object", "" + response.getJSONObject("data"));

                                Log.d("info extra_obj", "" + response.getJSONObject("extra"));

                                jsonObjectPagination = response.getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                JSONArray searchAds = response.getJSONObject("data").getJSONArray("ads");

                                try {
                                    Log.d("info MenuSearch is = ", searchAds.toString());
                                    if (searchAds.length() > 0) {
                                        for (int i = 0; i < searchAds.length(); i++) {

                                            catSubCatlistModel item = new catSubCatlistModel();

                                            JSONObject object = searchAds.getJSONObject(i);

                                            item.setId(object.getString("ad_id"));
                                            item.setCardName(object.getString("ad_title"));
                                            item.setDate(object.getString("ad_date"));
                                            item.setAdViews(object.getString("ad_views"));
                                            item.setPath(object.getString("ad_cats_name"));
                                            item.setPrice(object.getJSONObject("ad_price").getString("price"));
                                            item.setImageResourceId((object.getJSONArray("images").getJSONObject(0).getString("thumb")));
                                            item.setLocation(object.getJSONObject("location").getString("address"));
                                            item.setIsturned(0);
                                            item.setIs_show_countDown(object.getJSONObject("ad_timer").getBoolean("is_show"));
                                            if (object.getJSONObject("ad_timer").getBoolean("is_show"))
                                                item.setTimer_array(object.getJSONObject("ad_timer").getJSONArray("timer"));
                                            searchedAdList.add(item);
                                        }
                                        MyRecyclerView.setAdapter(itemCatgorySubListAdapter);
                                        itemCatgorySubListAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                        loading = true;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info MenuSearch ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info MenuSearch err", String.valueOf(t));
                        Log.d("info MenuSearch err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_loadList(JSONObject featureObject, JSONArray searchAds, JSONObject filtertext) {
        searchedAdList.clear();
        featureAdsList.clear();
        try {
            Log.d("search jsonaarry is = ", searchAds.toString());
            if (searchAds.length() > 0)
                for (int i = 0; i < searchAds.length(); i++) {

                    catSubCatlistModel item = new catSubCatlistModel();

                    JSONObject object = searchAds.getJSONObject(i);

                    item.setId(object.getString("ad_id"));
                    item.setCardName(object.getString("ad_title"));
                    item.setDate(object.getString("ad_date"));
                    item.setAdViews(object.getString("ad_views"));
                    item.setPath(object.getString("ad_cats_name"));
                    item.setPrice(object.getJSONObject("ad_price").getString("price"));
                    item.setImageResourceId((object.getJSONArray("images").getJSONObject(0).getString("thumb")));
                    item.setLocation(object.getJSONObject("location").getString("address"));
                    item.setIsturned(0);
                    item.setIs_show_countDown(object.getJSONObject("ad_timer").getBoolean("is_show"));
                    if (object.getJSONObject("ad_timer").getBoolean("is_show"))
                        item.setTimer_array(object.getJSONObject("ad_timer").getJSONArray("timer"));
                    searchedAdList.add(item);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.d("feature Object is = ", featureObject.getJSONArray("ads").toString());
            textViewTitleFeature.setText(featureObject.getString("text"));
            if (featureObject.getJSONArray("ads").length() > 0)
                for (int i = 0; i < featureObject.getJSONArray("ads").length(); i++) {

                    catSubCatlistModel item = new catSubCatlistModel();
                    JSONObject object = featureObject.getJSONArray("ads").getJSONObject(i);

                    item.setAddTypeFeature(object.getJSONObject("ad_status").getString("featured_type_text"));
                    item.setId(object.getString("ad_id"));
                    item.setCardName(object.getString("ad_title"));
                    item.setDate(object.getString("ad_date"));
                    item.setAdViews(object.getString("ad_views"));
                    item.setPath(object.getString("ad_cats_name"));
                    item.setPrice(object.getJSONObject("ad_price").getString("price"));
                    item.setImageResourceId((object.getJSONArray("ad_images").getJSONObject(0).getString("thumb")));
                    item.setLocation(object.getJSONObject("ad_location").getString("address"));
                    item.setIsfav(object.getJSONObject("ad_saved").getInt("is_saved"));
                    item.setFavBtnText(object.getJSONObject("ad_saved").getString("text"));

                    item.setIs_show_countDown(object.getJSONObject("ad_timer").getBoolean("is_show"));
                    if (object.getJSONObject("ad_timer").getBoolean("is_show"))
                        item.setTimer_array(object.getJSONObject("ad_timer").getJSONArray("timer"));

                    item.setIsturned(1);
                    featureAdsList.add(item);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            textViewFilterText.setText(filtertext.getString("count_ads"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();

        featureAdsList.clear();
        searchedAdList.clear();

        nextPage = 1;
        currentPage = 1;
        totalPage = 0;
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Simple Search");
            super.onResume();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}