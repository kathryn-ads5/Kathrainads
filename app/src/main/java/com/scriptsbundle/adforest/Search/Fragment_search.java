package com.kathryn.kathryn.Search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonObject;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.Search.adapter.ItemCatgorySubListAdapter;
import com.kathryn.kathryn.Settings.Settings;
import com.kathryn.kathryn.SplashScreen;
import com.kathryn.kathryn.ad_detail.Ad_detail_activity;
import com.kathryn.kathryn.adapters.ItemSearchFeatureAdsAdapter;
import com.kathryn.kathryn.adapters.SpinnerAndListAdapter;
import com.kathryn.kathryn.helper.CatSubCatOnclicklinstener;
import com.kathryn.kathryn.home.helper.Location_popupModel;
import com.kathryn.kathryn.modelsList.catSubCatlistModel;
import com.kathryn.kathryn.modelsList.subcatDiloglist;
import com.kathryn.kathryn.utills.AnimationUtils;
import com.kathryn.kathryn.utills.NestedScroll;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Fragment_search extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemClickListener {

    ArrayList<catSubCatlistModel> searchedAdList = new ArrayList<>();
    ArrayList<catSubCatlistModel> featureAdsList = new ArrayList<>();
    RecyclerView MyRecyclerView, recyclerViewFeatured;
    Button searchBtn;
    //EditText editTextSearch;
    SettingsMain settingsMain;
    TextView textViewTitleFeature, textViewFilterText;
    ItemCatgorySubListAdapter itemCatgorySubListAdapter;
    ItemSearchFeatureAdsAdapter itemSearchFeatureAdsAdapter;
    LinearLayout viewProductLayout, linearhide;
    List<View> allViewInstance = new ArrayList<>();
    List<View> allViewInstanceforCustom = new ArrayList<>();
    JSONObject jsonObject, jsonObjectFilterSpinner, jsonObjectforCustom, jsonObjectPagination;
    ImageView imageViewCollapse;
    LinearLayout linearLayoutCollapse, linearLayoutCustom;
    RestService restService;
    Spinner spinnerFilter;
//    Button spinnerFilterText;
    LinearLayout linearLayoutFilter;
    String catID;
    boolean isSort = false, ison = false;
    RelativeLayout relativeLayoutSpiner;
    NestedScrollView scrollView;
    AutoCompleteTextView et;
    ProgressBar progressBar;
    boolean isLoading = false, hasNextPage = false;
    int currentPage = 1, nextPage = 1, totalPage = 0;
    GridLayoutManager MyLayoutManager2;
    String stringCAT_keyName, ad_title, requestFrom, ad_id,locationIdHomePOpup ;

    String longtitude = "", latitude = "";
    BubbleSeekBar bubbleSeekBarDistance;
    CrystalRangeSeekbar rangeSeekbar;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private PlacesClient placesClient;
    private Boolean spinnerTouched = false;
    private Boolean spinnerTouched2 = false, checkRequest = false;
    private Calendar myCalendar = Calendar.getInstance();


    public Fragment_search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        scrollView = getActivity().findViewById(R.id.scrollView);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            ad_title = bundle.getString("ad_title", "");
            ad_id = bundle.getString("catId", "");
            requestFrom = bundle.getString("requestFrom", "");
            locationIdHomePOpup=bundle.getString("ad_country","");
        }
        settingsMain = new SettingsMain(getActivity());
        searchBtn = view.findViewById(R.id.send_button);
        searchBtn.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        imageViewCollapse = getActivity().findViewById(R.id.collapse);

        linearhide = view.findViewById(R.id.linearhide);

        linearLayoutCollapse = view.findViewById(R.id.linearLayout);
        linearLayoutFilter = view.findViewById(R.id.filter_layout);
        textViewFilterText = view.findViewById(R.id.textViewFilter);
        spinnerFilter = view.findViewById(R.id.spinner);
//        spinnerFilterText = view.findViewById(R.id.spinner);
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

        recyclerViewFeatured.setLayoutManager(MyLayoutManager2);

        scrollView.setOnScrollChangeListener(new NestedScroll() {
            @Override
            public void onScroll() {
                if (hasNextPage) {
                    if (!isLoading) {
                        isLoading = true;
                        progressBar.setVisibility(View.VISIBLE);
                        adforest_loadmore(adforest_getDataFromDynamicViews());
                        Log.d("heeeeeeelo", nextPage + "==" + totalPage);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        jsonObject = new JSONObject();
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        if (requestFrom.trim().equals("Home")) {
            spinnerFilter.setVisibility(View.GONE);
//            spinnerFilterText.setVisibility(View.GONE);
            imageViewCollapse.setVisibility(View.VISIBLE);
            AnimationUtils.slideDown(linearLayoutCollapse);
            imageViewCollapse.setImageResource(R.drawable.ic_controls);
            JsonObject params = new JsonObject();
            params.addProperty("ad_title", ad_title);
            params.addProperty("catId", ad_id);
            params.addProperty("page_number", 1);
            params.addProperty("ad_country",locationIdHomePOpup);
            adforest_search(params);
        } else
            adforest_getViews();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Fragment_search.this.adforest_submitQuery(Fragment_search.this.adforest_getDataFromDynamicViews(), "");
            }
        });
        imageViewCollapse.setOnClickListener(view12 -> {
            if (requestFrom.equals("Home") && !checkRequest) {
                adforest_getViews();
                checkRequest = true;
                requestFrom = "";
                spinnerFilter.setVisibility(View.VISIBLE);
//                spinnerFilterText.setVisibility(View.VISIBLE);

            }
            scrollView.scrollTo(0, 0);
            imageViewCollapse.setVisibility(View.VISIBLE);
            if (linearLayoutCollapse.getVisibility() == View.GONE) {
                AnimationUtils.slideUp(linearLayoutCollapse);
                imageViewCollapse.setImageResource(R.drawable.ic_remove_circle_outline);

            } else {
                AnimationUtils.slideDown(linearLayoutCollapse);
                imageViewCollapse.setImageResource(R.drawable.ic_controls);
            }
        });
        return view;
    }

    private void adforest_submitQuery(JsonObject params, String s) {

        if (isSort) {
            params.addProperty("sort", s);
        }

        if (adforest_getDataFromDynamicViewsForCustom() != null) {
            params.add("custom_fields", adforest_getDataFromDynamicViewsForCustom());
        }


        if (settingsMain.getShowNearBy()) {
            params.addProperty("nearby_latitude", longtitude);
            params.addProperty("nearby_longitude", latitude);
            if (bubbleSeekBarDistance != null)
                params.addProperty("nearby_distance", Integer.toString(bubbleSeekBarDistance.getProgress()));
        }

        params.addProperty("page_number", 1);

        adforest_search(params);

        isSort = false;
    }

    private void adforest_search(JsonObject params) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            Log.d("info Send SearchData =", "" + params.toString());

            SettingsMain.showDilog(getActivity());
            Call<ResponseBody> myCall = restService.postGetSearchNdLoadMore(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SearchData Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info SearchData object", "" + response.getJSONObject("data"));

                                Log.d("info SearchData extra", "" + response.getJSONObject("extra"));
                                if (requestFrom.trim().equals("Home")) {
                                    getActivity().setTitle(response.getJSONObject("extra").getString("title"));
                                }

                                if (response.getJSONObject("extra").getBoolean("is_show_featured")) {
                                    textViewTitleFeature.setVisibility(View.VISIBLE);
                                } else {
                                    textViewTitleFeature.setVisibility(View.GONE);
                                }
                                jsonObjectPagination = response.getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");

                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

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
                                        adforest_addToFavourite(position);
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

                                adforest_showFiler();

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
                        Log.d("info SearchData ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info SearchData err", String.valueOf(t));
                        Log.d("info SearchData err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loadmore(JsonObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        if (isSort) {
            jsonObject.addProperty("sort", "");
        }

        if (adforest_getDataFromDynamicViewsForCustom() != null) {
            jsonObject.add("custom_fields", adforest_getDataFromDynamicViewsForCustom());
        }
        jsonObject.addProperty("page_number", nextPage);

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            Log.d("info data object", "" + jsonObject.toString());
            Call<ResponseBody> myCall = restService.postGetSearchNdLoadMore(jsonObject, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info searchLoad Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info searchLoadMore obj", "" + response.getJSONObject("data"));

                                isLoading = false;
                                jsonObjectPagination = response.getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                JSONArray searchAds = response.getJSONObject("data").getJSONArray("ads");

                                try {
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
                        Log.d("info searchLoadMore ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info searchLoadMore err", String.valueOf(t));
                        Log.d("info searchLoadMore err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_getViews() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getSearchDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Search Details", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {

                                adforest_setViews(response);

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ProfileGet error", String.valueOf(t));
                    Log.d("info ProfileGet error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
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
                    item.setIsfav(1);
                    item.setFavBtnText(object.getJSONObject("ad_saved").getString("text"));
                    item.setIsturned(1);

                    item.setIs_show_countDown(object.getJSONObject("ad_timer").getBoolean("is_show"));
                    if (object.getJSONObject("ad_timer").getBoolean("is_show"))
                        item.setTimer_array(object.getJSONObject("ad_timer").getJSONArray("timer"));

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

        if (!requestFrom.equals("Home"))
            imageViewCollapse.performClick();
        if (requestFrom.equals("Home") && checkRequest)
            imageViewCollapse.performClick();
    }

    void adforest_setViews(JSONObject jsonObjec) {

        try {
            jsonObject = jsonObjec;
            Log.d("info Search Data ===== ", jsonObject.toString());
            JSONArray customOptnList = jsonObject.getJSONArray("data");

            getActivity().setTitle(jsonObject.getJSONObject("extra").getString("title"));
            searchBtn.setText(jsonObject.getJSONObject("extra").getString("search_btn"));
            stringCAT_keyName = jsonObject.getJSONObject("extra").getString("field_type_name");


            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {

                CardView cardView = new CardView(getActivity());
                cardView.setCardElevation(2);
                cardView.setUseCompatPadding(true);
                cardView.setRadius(0);
                cardView.setContentPadding(10, 10, 10, 10);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 10;
                params.bottomMargin = 10;
                cardView.setLayoutParams(params);

                final JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);
                TextView customOptionsName = new TextView(getActivity());
                customOptionsName.setAllCaps(true);
                customOptionsName.setTextColor(Color.BLACK);
                customOptionsName.setTextSize(12);
                customOptionsName.setPadding(10, 15, 10, 15);

                customOptionsName.setText(eachData.getString("title"));

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setPadding(5, 5, 5, 5);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                linearLayout.addView(customOptionsName);
                if (eachData.getString("field_type").equals("select")) {

                    final JSONArray dropDownJSONOpt = eachData.getJSONArray("values");
                    final ArrayList<subcatDiloglist> SpinnerOptions;
                    SpinnerOptions = new ArrayList<>();
                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        subcatDiloglist subDiloglist = new subcatDiloglist();
                        subDiloglist.setId(dropDownJSONOpt.getJSONObject(j).getString("id"));
                        subDiloglist.setName(dropDownJSONOpt.getJSONObject(j).getString("name"));
                        subDiloglist.setHasSub(dropDownJSONOpt.getJSONObject(j).getBoolean("has_sub"));
                        subDiloglist.setHasCustom(dropDownJSONOpt.getJSONObject(j).getBoolean("has_template"));
                        //String optionString = dropDownJSONOpt.getJSONObject(j).getString("name");
                        SpinnerOptions.add(subDiloglist);
                    }
                    final SpinnerAndListAdapter spinnerAndListAdapter;
                    spinnerAndListAdapter = new SpinnerAndListAdapter(getActivity(), SpinnerOptions, true);
                    final Spinner spinner = new Spinner(getActivity());

                    allViewInstance.add(spinner);
                    spinner.setAdapter(spinnerAndListAdapter);
                    spinner.setSelection(0, false);

                    spinner.setOnTouchListener((v, event) -> {
                        System.out.println("Real touch felt.");
                        spinnerTouched = true;
                        return false;
                    });

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (spinnerTouched) {
                                //String variant_name = dropDownJSONOpt.getJSONObject(position).getString("name");
                                if (position != 0) {
                                    final subcatDiloglist subcatDiloglistitem = (subcatDiloglist) selectedItemView.getTag();
                                    if (subcatDiloglistitem.isHasSub()) {


                                        if (SettingsMain.isConnectingToInternet(getActivity())) {

                                            SettingsMain.showDilog(getActivity());

                                            //for serlecting the Categoreis if Categoreis have SubCategoreis
                                            try {
                                                if (eachData.getString("field_type_name").equals("ad_cats1")) {

                                                    JsonObject params = new JsonObject();
                                                    params.addProperty("subcat", subcatDiloglistitem.getId());

                                                    Log.d("info sendSearch SubCats", "" + params.toString());

                                                    Call<ResponseBody> myCall = restService.postGetSearcSubCats(params, UrlController.AddHeaders(getActivity()));
                                                    myCall.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                                            try {
                                                                if (responseObj.isSuccessful()) {
                                                                    Log.d("info GetSubCats Resp", "" + responseObj.toString());

                                                                    JSONObject response = new JSONObject(responseObj.body().string());
                                                                    if (response.getBoolean("success")) {
                                                                        Log.d("info GetSubCats object", "" + response.getJSONObject("data"));
                                                                        spinnerTouched = false;

                                                                        adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, SpinnerOptions
                                                                                , spinnerAndListAdapter, spinner, eachData.getString("field_type_name"));

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
                                                            SettingsMain.hideDilog();
                                                            Log.d("info GetAdnewPost error", String.valueOf(t));
                                                            Log.d("info GetAdnewPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                                        }
                                                    });
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            //for serlecting the location if location have sabLocations
                                            try {
                                                if (eachData.getString("field_type_name").equals("ad_country")) {

                                                    JsonObject params1 = new JsonObject();
                                                    params1.addProperty("ad_country", subcatDiloglistitem.getId());
                                                    Log.d("info sendSearch Loctn", params1.toString() + eachData.getString("field_type_name"));

                                                    Call<ResponseBody> myCall = restService.postGetSearcSubLocation(params1, UrlController.AddHeaders(getActivity()));
                                                    myCall.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                                            try {
                                                                if (responseObj.isSuccessful()) {
                                                                    Log.d("info SubSearch Resp", "" + responseObj.toString());

                                                                    JSONObject response = new JSONObject(responseObj.body().string());
                                                                    if (response.getBoolean("success")) {
                                                                        Log.d("info SearchLctn object", "" + response.getJSONObject("data"));
                                                                        spinnerTouched = false;

                                                                        adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, SpinnerOptions
                                                                                , spinnerAndListAdapter, spinner, eachData.getString("field_type_name"));

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
                                                                Log.d("info SearchLctn ", "NullPointert Exception" + t.getLocalizedMessage());
                                                                settingsMain.hideDilog();
                                                            } else {
                                                                SettingsMain.hideDilog();
                                                                Log.d("info SearchLctn error", String.valueOf(t));
                                                                Log.d("info SearchLctn error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                                            }
                                                        }
                                                    });

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            SettingsMain.hideDilog();
                                            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
                                        }
                                        spinnerTouched = false;
                                    }

                                    try {

                                        Log.d("true===== ", "in Main ====  " + subcatDiloglistitem.isHasCustom());

                                        if (eachData.getBoolean("has_cat_template"))
                                            if (subcatDiloglistitem.isHasCustom()) {
                                                linearLayoutCustom.removeAllViews();
                                                allViewInstanceforCustom.clear();
                                                catID = subcatDiloglistitem.getId();
                                                adforest_showCustom();
                                                ison = true;
                                                Log.d("true===== ", "add All");


                                            } else {
                                                if (ison) {
                                                    linearLayoutCustom.removeAllViews();
                                                    allViewInstanceforCustom.clear();
                                                    ison = false;
                                                    Log.d("true===== ", "remove All");

                                                }
                                            }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    if (ison) {
                                        linearLayoutCustom.removeAllViews();
                                        allViewInstanceforCustom.clear();
                                        ison = false;
                                        Log.d("true===== ", "remove All");
                                    }
                                }
                            }
                            spinnerTouched = false;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    linearLayout.addView(spinner, 1);

                    if (eachData.getString("field_type_name").equals("ad_cats1")) {
                        linearLayoutCustom = new LinearLayout(getActivity());
                        linearLayoutCustom.setPadding(5, 5, 5, 5);
                        linearLayoutCustom.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.addView(linearLayoutCustom, 2);
                    }

                    cardView.addView(linearLayout);
                    viewProductLayout.addView(cardView);
                }
                if (eachData.getString("field_type").equals("textfield")) {
                    TextInputLayout til = new TextInputLayout(getActivity());
                    til.setHint(eachData.getString("title"));
                    EditText et = new EditText(getActivity());
                    til.addView(et);
                    allViewInstance.add(et);
                    cardView.addView(til);
                    viewProductLayout.addView(cardView);
                }
                if (eachData.getString("field_type").equals("glocation_textfield")) {
                    TextInputLayout til = new TextInputLayout(getActivity());

                    til.setHint(eachData.getString("title"));
                    et = new AutoCompleteTextView(getActivity());
                    placesClient = com.google.android.libraries.places.api.Places.createClient(getContext());
                    et.setOnItemClickListener(this);

                    et.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            manageAutoComplete(s.toString());

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    et.setOnItemClickListener(this);
                    // Create Filter
//                    AutocompleteFilter.Builder typeFilter = new AutocompleteFilter.Builder();
//                    if (SplashScreen.gmap_has_countries) {
//                        typeFilter.setTypeFilter(Place.TYPE_COUNTRY)
//                                .setCountry(SplashScreen.gmap_countries);
//                    }
//                    if (settingsMain.getAlertDialogMessage("location_type").equals("regions")) {
//                        typeFilter
//                                .setTypeFilter(TYPE_FILTER_ADDRESS);
//                    } else {
//                        typeFilter
//                                .setTypeFilter(TYPE_FILTER_REGIONS);
//                    }
                    til.addView(et);
                    allViewInstance.add(et);
                    cardView.addView(til);
                    viewProductLayout.addView(cardView);
                }

                if (eachData.getString("field_type").equals("range_textfield")) {
                    LinearLayout linearLayout1 = new LinearLayout(getActivity());
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                    TextInputLayout til = new TextInputLayout(getActivity());
                    TextInputLayout til2 = new TextInputLayout(getActivity());

                    til.setHint(eachData.getJSONArray("data").getJSONObject(0).getString("title"));
                    til2.setHint(eachData.getJSONArray("data").getJSONObject(1).getString("title"));

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.weight = 1;

                    EditText et = new EditText(getActivity());
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    EditText et2 = new EditText(getActivity());
                    et2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    til.addView(et);
                    til2.addView(et2);

                    til.setLayoutParams(params2);
                    til2.setLayoutParams(params2);
                    linearLayout1.addView(til);
                    linearLayout1.addView(til2);

                    linearLayout.addView(linearLayout1);
                    allViewInstance.add(linearLayout1);
                    cardView.addView(linearLayout);
                    viewProductLayout.addView(cardView);
                }
                if (settingsMain.getShowNearBy()) {
                    if (eachData.getString("field_type").equals("seekbar")) {
                        Location_popupModel Location_popupModel = settingsMain.getLocationPopupModel(getContext());
                        final BubbleSeekBar bubbleSeekBar = new BubbleSeekBar(getActivity());
                        bubbleSeekBar.getConfigBuilder()
                                .max(Location_popupModel.getSlider_number())
                                .sectionCount(Location_popupModel.getSlider_step())
                                .secondTrackColor(Color.parseColor(SettingsMain.getMainColor()))
                                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                                .showSectionMark()
                                .showThumbText()
                                .thumbTextSize(20)
                                .trackColor(Color.parseColor("#cccccc"))
                                .touchToSeek()
                                .build();
                        linearLayout.addView(bubbleSeekBar);
                        cardView.addView(linearLayout);
                        allViewInstance.add(bubbleSeekBar);
                        viewProductLayout.addView(cardView);
                        bubbleSeekBarDistance = bubbleSeekBar;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        spinnerFilterText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
//                Intent i = new Intent(getActivity(), SearchActivity.class);
//                startActivity(i);
//
//            }
//        });
        try {
            jsonObjectFilterSpinner = jsonObject.getJSONObject("topbar");


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
                            adforest_submitQuery(adforest_getDataFromDynamicViews(), dropDownJSONOpt.getJSONObject(i).getString("key"));
                            imageViewCollapse.performClick();

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
    }

    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

        if (SplashScreen.gmap_has_countries) {
            request.setCountry(SplashScreen.gmap_countries);
        }
        if (settingsMain.getAlertDialogMessage("location_type").equals("regions")) {
            request.setTypeFilter(TypeFilter.ADDRESS);
        } else {
            request
                    .setTypeFilter(TypeFilter.REGIONS);
        }
        request.setSessionToken(token)
                .setQuery(query);



        placesClient.findAutocompletePredictions(request.build()).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());

            }
            String[] data = places.toArray(new String[places.size()]); // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
            et.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String placeId = ids.get(position);
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
// Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            com.google.android.libraries.places.api.model.Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
            longtitude = Double.toString(place.getLatLng().longitude);
            latitude = Double.toString(place.getLatLng().latitude);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });


    }

    private void adforest_showCustom() {

        if (linearLayoutCustom != null) {

            if (SettingsMain.isConnectingToInternet(getActivity())) {

                JsonObject params = new JsonObject();
                params.addProperty("cat_id", catID);
                Log.d("info sendSearch CatID", catID);
                Call<ResponseBody> myCall = restService.postGetSearchDynamicFields(params, UrlController.AddHeaders(getActivity()));
                myCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                        try {
                            if (responseObj.isSuccessful()) {
                                Log.d("info searchDynamic Resp", "" + responseObj.toString());

                                JSONObject response = new JSONObject(responseObj.body().string());
                                if (response.getBoolean("success")) {
                                    Log.d("info searchDynamic obj", "" + response.getJSONArray("data"));
                                    adforest_setViewsForCustom(response);
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
                            Log.d("info searchDynamic ", "NullPointert Exception" + t.getLocalizedMessage());
                            settingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info searchDynamic err", String.valueOf(t));
                            Log.d("info searchDynamic err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void adforest_ShowDialog(JSONObject data, final subcatDiloglist main,
                                     final ArrayList<subcatDiloglist> spinnerOptionsout,
                                     final SpinnerAndListAdapter spinnerAndListAdapterout,
                                     final Spinner spinner1, final String field_type_name) {

        Log.d("info Dialog Data===== ", "adforest_ShowDialog");
        try {
            Log.d("info Dialog Data===== ", data.getJSONArray("values").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(getActivity(), R.style.PauseDialog);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_sub_cat);

        dialog.setTitle(main.getName());
        ListView listView = dialog.findViewById(R.id.listView);

        final ArrayList<subcatDiloglist> listitems = new ArrayList<>();
        final JSONArray listArray;
        try {
            listArray = data.getJSONArray("values");
            for (int j = 0; j < listArray.length(); j++) {
                subcatDiloglist subDiloglist = new subcatDiloglist();
                subDiloglist.setId(listArray.getJSONObject(j).getString("id"));
                subDiloglist.setName(listArray.getJSONObject(j).getString("name"));
                subDiloglist.setHasSub(listArray.getJSONObject(j).getBoolean("has_sub"));
                subDiloglist.setHasCustom(listArray.getJSONObject(j).getBoolean("has_template"));
                listitems.add(subDiloglist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SpinnerAndListAdapter spinnerAndListAdapter1 = new SpinnerAndListAdapter(getActivity(), listitems);
        listView.setAdapter(spinnerAndListAdapter1);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final subcatDiloglist subcatDiloglistitem = (subcatDiloglist) view.getTag();

            //Log.d("helllo" , spinnerOptionsout.adforest_get(1).getId() + " === " + spinnerOptionsout.adforest_get(1).getName());

            if (!spinnerOptionsout.get(1).getId().equals(subcatDiloglistitem.getId())) {

                if (subcatDiloglistitem.isHasSub()) {


                    if (SettingsMain.isConnectingToInternet(getActivity())) {

                        SettingsMain.showDilog(getActivity());
                        if (field_type_name.equals("ad_cats1")) {
                            JsonObject params = new JsonObject();
                            params.addProperty("subcat", subcatDiloglistitem.getId());

                            Log.d("info sendDiSubCats", params.toString() + field_type_name);

                            Call<ResponseBody> myCall = restService.postGetSearcSubCats(params, UrlController.AddHeaders(getActivity()));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                        if (responseObj.isSuccessful()) {
                                            Log.d("info DiSubCats Resp", "" + responseObj.toString());

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                Log.d("info DidSubCats object", "" + response.getJSONObject("data"));

                                                adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, spinnerOptionsout
                                                        , spinnerAndListAdapterout, spinner1, field_type_name);

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
                                        Log.d("info DidSubCats ", "NullPointert Exception" + t.getLocalizedMessage());
                                        settingsMain.hideDilog();
                                    } else {
                                        SettingsMain.hideDilog();
                                        Log.d("info DiaSubCats error", String.valueOf(t));
                                        Log.d("info DiaSubCats error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                    }
                                }
                            });
                        }
                        if (field_type_name.equals("ad_country")) {

                            JsonObject params1 = new JsonObject();
                            params1.addProperty("ad_country", subcatDiloglistitem.getId());
                            Log.d("info DiSubLocation", params1.toString() + field_type_name);

                            Call<ResponseBody> myCall = restService.postGetSearcSubLocation(params1, UrlController.AddHeaders(getActivity()));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                        if (responseObj.isSuccessful()) {
                                            Log.d("info DiSubLocation Resp", "" + responseObj.toString());

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                Log.d("info DiSubLocation obj", "" + response.getJSONObject("data"));

                                                adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, spinnerOptionsout
                                                        , spinnerAndListAdapterout, spinner1, field_type_name);

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
                                        Log.d("info DiSubLocation ", "NullPointert Exception" + t.getLocalizedMessage());
                                        settingsMain.hideDilog();
                                    } else {
                                        SettingsMain.hideDilog();
                                        Log.d("info DiSubLocation err", String.valueOf(t));
                                        Log.d("info DiSubLocation err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                    }
                                }
                            });

                        }
                    } else {
                        SettingsMain.hideDilog();
                        Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
                    }


                } else {

                    for (int ii = 0; ii < spinnerOptionsout.size(); ii++) {
                        if (spinnerOptionsout.get(ii).getId().equals(subcatDiloglistitem.getId())) {
                            spinnerOptionsout.remove(ii);
                            Log.d("info ===== ", "else of list inner is 1st button into for loop");
                            break;
                        }
                    }
                    Log.d("info ===== ", "else of list inner is 1st button out of for loop");

                    spinnerOptionsout.add(1, subcatDiloglistitem);
                    spinner1.setSelection(1, false);
                    spinnerAndListAdapterout.notifyDataSetChanged();

                }

                Log.d("true===== ", "in dalog ====  " + subcatDiloglistitem.isHasCustom());

                if (subcatDiloglistitem.isHasCustom()) {
                    linearLayoutCustom.removeAllViews();
                    allViewInstanceforCustom.clear();
                    catID = subcatDiloglistitem.getId();
                    adforest_showCustom();
                    Log.d("true===== ", "inter add All");

                } else {
                    linearLayoutCustom.removeAllViews();
                    allViewInstanceforCustom.clear();
                    ison = false;
                    Log.d("true===== ", "inter remove All");
                }
            } else {
                spinner1.setSelection(1, false);
                Log.d("info ===== ", "else of chk is 1st button out");

            }
            dialog.dismiss();
        });

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        try {
            Send.setText(jsonObject.getJSONObject("extra").getString("dialog_send"));
            Cancel.setText(jsonObject.getJSONObject("extra").getString("dialg_cancel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        Send.setOnClickListener(v -> {

            for (int i = 0; i < spinnerOptionsout.size(); i++) {
                if (spinnerOptionsout.get(i).getId().equals(main.getId())) {
                    spinnerOptionsout.remove(i);
                    Log.d("info ===== ", "send button in");
                    break;
                }
            }

            spinnerOptionsout.add(1, main);
            spinnerAndListAdapterout.notifyDataSetChanged();
            spinner1.setSelection(1, false);
            Log.d("info ===== ", "send button out");

            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }

    public JsonObject adforest_getDataFromDynamicViews() {
        JsonObject optionsObj = null;

        if (!requestFrom.equals("Home")) {
            try {
                JSONArray customOptnList = jsonObject.getJSONArray("data");
                optionsObj = new JsonObject();
                for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {
                    JSONObject eachData = customOptnList.getJSONObject(noOfViews);

                    if (eachData.getString("field_type").equals("select")) {
                        Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                        subcatDiloglist subcatDiloglist1 = (subcatDiloglist) spinner.getSelectedView().getTag();
                        JSONArray dropDownJSONOpt = eachData.getJSONArray("values");
                        String variant_name = dropDownJSONOpt.getJSONObject(spinner.getSelectedItemPosition()).getString("id");
                        Log.d("value id", variant_name + "");

                        optionsObj.addProperty(eachData.getString("field_type_name"),
                                "" + subcatDiloglist1.getId());
                    }
                    if (eachData.getString("field_type").equals("textfield")) {
                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        if (!textView.getText().toString().equalsIgnoreCase(""))
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        else
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        Log.d("variant_name", textView.getText().toString() + "");
                    }

                    if (eachData.getString("field_type").equals("glocation_textfield")) {
                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        if (!textView.getText().toString().equalsIgnoreCase(""))
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        else
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        Log.d("variant_name", textView.getText().toString() + "");
                    }
                    if (eachData.getString("field_type").equals("range_textfield")) {
                        LinearLayout linearLayout = (LinearLayout) allViewInstance.get(noOfViews);

                        TextInputLayout textView = (TextInputLayout) linearLayout.getChildAt(0);
                        TextInputLayout textView2 = (TextInputLayout) linearLayout.getChildAt(1);

                        if (textView.getEditText() != null && textView2.getEditText() != null)
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getEditText().getText().toString() + "-" +
                                    textView2.getEditText().getText().toString());
                    }
                }

                hideSoftKeyboard();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d("array us", (optionsObj != null ? optionsObj.toString() : null) + " ==== size====  " + allViewInstance.size());
        }
        return optionsObj;
    }

    public JsonObject adforest_getDataFromDynamicViewsForCustom() {
        JsonObject optionsObj = null;

        if (jsonObjectforCustom != null) {
            try {
                JSONArray customOptnList = jsonObjectforCustom.getJSONArray("data");
                optionsObj = new JsonObject();

                for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {

                    JSONObject eachData = customOptnList.getJSONObject(noOfViews);

                    if (eachData.getString("field_type").equals("select")) {
                        Spinner spinner = (Spinner) allViewInstanceforCustom.get(noOfViews);

                        subcatDiloglist subcatDiloglist1 = (subcatDiloglist) spinner.getSelectedView().getTag();
                        JSONArray dropDownJSONOpt = eachData.getJSONArray("values");
                        String variant_name = dropDownJSONOpt.getJSONObject(spinner.getSelectedItemPosition()).getString("id");
                        Log.d("value id", variant_name + "");

                        optionsObj.addProperty(eachData.getString("field_type_name"),
                                "" + subcatDiloglist1.getId());
                    }
                    if (eachData.getString("field_type").equals("textfield")) {
                        TextView textView = (TextView) allViewInstanceforCustom.get(noOfViews);
                        if (!textView.getText().toString().equalsIgnoreCase(""))
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        else
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getText().toString());
                        Log.d("variant_name", textView.getText().toString() + "");
                    }

                    if (eachData.getString("field_type").equals("radio")) {
                        RadioGroup radioGroup = (RadioGroup) allViewInstanceforCustom.get(noOfViews);
                        RadioButton selectedRadioBtn = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                        if (selectedRadioBtn != null) {
                            Log.d("variant_name", selectedRadioBtn.getTag().toString() + "");
                            optionsObj.addProperty(eachData.getString("field_type_name"),
                                    selectedRadioBtn.getTag().toString());
                        }
                    }
                    if (eachData.getString("field_type").equals("checkbox")) {
                        LinearLayout linearLayout = (LinearLayout) allViewInstanceforCustom.get(noOfViews);
                        Log.d("info if", "checkbox" + linearLayout);
                        JSONArray checkBoxJSONOpt = eachData.getJSONArray("values");
                        String values = "";
                        for (int j = 0; j < checkBoxJSONOpt.length(); j++) {
                            Log.d("info if", "for");
                            CheckBox chk = (CheckBox) linearLayout.getChildAt(j);
                            if (chk.isChecked()) {
                                Log.d("info if", "iffff");
                                values = values.concat("," + chk.getTag());
                            }
                        }
                        optionsObj.addProperty(eachData.getString("field_type_name"), values);
                    }
                    if (eachData.getString("field_type").equals("textfield_date")) {
                        LinearLayout linearLayout = (LinearLayout) allViewInstanceforCustom.get(noOfViews);
                        TextInputLayout textInputLayout = (TextInputLayout) linearLayout.getChildAt(0);
                        TextInputLayout textInputLayout1 = (TextInputLayout) linearLayout.getChildAt(1);
                        if (textInputLayout.getEditText() != null && textInputLayout1.getEditText() != null)
                            optionsObj.addProperty(eachData.getString("field_type_name"), textInputLayout.getEditText().getText().toString() + "|" +
                                    textInputLayout1.getEditText().getText().toString());

                    }
                    if (eachData.getString("field_type").equals("radio_color")) {
                        RadioGroup radioGroup = (RadioGroup) allViewInstanceforCustom.get(noOfViews);
                        RadioButton selectedRadioBtn = getActivity().findViewById(radioGroup.getCheckedRadioButtonId());
                        if (selectedRadioBtn != null) {
                            Log.d("variant_name", selectedRadioBtn.getTag().toString() + "");
                            optionsObj.addProperty(eachData.getString("field_type_name"),
                                    selectedRadioBtn.getTag().toString());
                        }
                    }
                    if (eachData.getString("field_type").equals("number_range")) {
                        LinearLayout linearLayout = (LinearLayout) allViewInstanceforCustom.get(noOfViews);
                        TextInputLayout textInputLayout = (TextInputLayout) linearLayout.getChildAt(0);
                        TextInputLayout textInputLayout1 = (TextInputLayout) linearLayout.getChildAt(1);
                        if (textInputLayout.getEditText() != null && textInputLayout1.getEditText() != null)
                            optionsObj.addProperty(eachData.getString("field_type_name"), textInputLayout.getEditText().getText().toString() + "-" +
                                    textInputLayout1.getEditText().getText().toString());
                    }
                    if (eachData.getString("field_type").equals("range_textfield")) {
                        LinearLayout linearLayout = (LinearLayout) allViewInstanceforCustom.get(noOfViews);

                        TextInputLayout textView = (TextInputLayout) linearLayout.getChildAt(0);
                        TextInputLayout textView2 = (TextInputLayout) linearLayout.getChildAt(1);

                        if (textView.getEditText() != null && textView2.getEditText() != null)
                            optionsObj.addProperty(eachData.getString("field_type_name"), textView.getEditText().getText().toString() + "-" +
                                    textView2.getEditText().getText().toString());
                    }

                }

                hideSoftKeyboard();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("array us custom", (optionsObj != null ? optionsObj.toString() : null) + " ==== size====  " + allViewInstanceforCustom.size());

        return optionsObj;
    }

    void adforest_setViewsForCustom(JSONObject jsonObjec) {

        try {
            jsonObjectforCustom = jsonObjec;
            Log.d("Custom data ===== ", jsonObjectforCustom.toString());
            JSONArray customOptnList = jsonObjectforCustom.getJSONArray("data");

            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {
                CardView cardView = new CardView(getActivity());
                cardView.setCardElevation(2);
                cardView.setUseCompatPadding(true);
                cardView.setRadius(0);
                cardView.setContentPadding(10, 10, 10, 10);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.topMargin = 10;
                params1.bottomMargin = 10;
                cardView.setLayoutParams(params1);

                JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);
                TextView customOptionsName = new TextView(getActivity());
                customOptionsName.setTextSize(12);
                customOptionsName.setAllCaps(true);
                customOptionsName.setTextColor(Color.BLACK);
                customOptionsName.setPadding(10, 15, 10, 15);
                customOptionsName.setText(eachData.getString("title"));

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                linearLayout.addView(customOptionsName);
                if (eachData.getString("field_type").equals("select")) {

                    final JSONArray dropDownJSONOpt = eachData.getJSONArray("values");
                    final ArrayList<subcatDiloglist> SpinnerOptions;
                    SpinnerOptions = new ArrayList<>();
                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        subcatDiloglist subDiloglist = new subcatDiloglist();
                        subDiloglist.setId(dropDownJSONOpt.getJSONObject(j).getString("id"));
                        subDiloglist.setName(dropDownJSONOpt.getJSONObject(j).getString("name"));
                        subDiloglist.setHasSub(dropDownJSONOpt.getJSONObject(j).getBoolean("has_sub"));
                        //String optionString = dropDownJSONOpt.getJSONObject(j).getString("name");
                        SpinnerOptions.add(subDiloglist);
                    }
                    final SpinnerAndListAdapter spinnerAndListAdapter;

                    spinnerAndListAdapter = new SpinnerAndListAdapter(getActivity(), SpinnerOptions);

                    final Spinner spinner = new Spinner(getActivity());

//                    allViewInstanceforCustom.add(spinner);
                    spinner.setAdapter(spinnerAndListAdapter);
                    spinner.setSelection(0, false);

                    spinner.setOnTouchListener((v, event) -> {
                        spinnerTouched = true;
                        return false;
                    });

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            if (spinnerTouched) {
                                //String variant_name = dropDownJSONOpt.getJSONObject(position).getString("name");
                                if (position != 0) {
                                    final subcatDiloglist subcatDiloglistitem = (subcatDiloglist) selectedItemView.getTag();

//                                    Toast.makeText(getActivity(), subcatDiloglistitem.getName(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });
                    allViewInstanceforCustom.add(spinner);
                    linearLayout.addView(spinner, 1);
                    cardView.addView(linearLayout);
                    linearLayoutCustom.addView(cardView);
                }
                if (eachData.getString("field_type").equals("textfield")) {
                    TextInputLayout til = new TextInputLayout(getActivity());
                    til.setHint(eachData.getString("title"));
                    EditText et = new EditText(getActivity());
                    til.addView(et);
                    allViewInstanceforCustom.add(et);
                    linearLayoutCustom.addView(til);
                }

                if (eachData.getString("field_type").equals("radio")) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 3;
                    params.bottomMargin = 3;

                    final JSONArray radioButtonJSONOpt = eachData.getJSONArray("values");
                    RadioGroup rg = new RadioGroup(getActivity()); //create the RadioGroup
                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {

                        RadioButton rb = new RadioButton(getActivity());

                        if (j == 0)
//                            rb.setChecked(true);
                            rb.setLayoutParams(params);
                        rb.setTag(radioButtonJSONOpt.getJSONObject(j).getString("id"));
                        rb.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        String optionString = radioButtonJSONOpt.getJSONObject(j).getString("name");
                        rb.setText(optionString);
                        rg.setOnCheckedChangeListener((group, checkedId) -> {
                            View radioButton = group.findViewById(checkedId);
                            String variant_name = radioButton.getTag().toString();
//                                Toast.makeText(getActivity(), variant_name + "", Toast.LENGTH_LONG).show();
                        });
                        rg.addView(rb, params);

                    }
                    allViewInstanceforCustom.add(rg);
                    linearLayout.addView(rg, params);
                    linearLayoutCustom.addView(linearLayout);
                }
                if (eachData.getString("field_type").equals("checkbox")) {
                    Log.d("info add", noOfCustomOpt + "");
                    LinearLayout linearLayout1 = new LinearLayout(getActivity());
                    linearLayout1.setOrientation(LinearLayout.VERTICAL);

                    JSONArray checkBoxJSONOpt = eachData.getJSONArray("values");

                    for (int j = 0; j < checkBoxJSONOpt.length(); j++) {

                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        checkBox.setTag(checkBoxJSONOpt.getJSONObject(j).getString("id"));
                        checkBox.setFocusable(true);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.topMargin = 3;
                        params2.bottomMargin = 3;
                        String optionString = checkBoxJSONOpt.getJSONObject(j).getString("name");
                        checkBox.setText(optionString);
                        linearLayout1.addView(checkBox, params2);
                    }

                    allViewInstanceforCustom.add(linearLayout1);
                    linearLayout.addView(linearLayout1);
                    cardView.addView(linearLayout);
                    linearLayoutCustom.addView(cardView);
                }
                if (eachData.getString("field_type").equals("textfield_date")) {
                    LinearLayout linearLayoutdate = new LinearLayout(getActivity());
                    linearLayoutdate.setOrientation(LinearLayout.HORIZONTAL);
                    TextInputLayout til1 = new TextInputLayout(getActivity());
                    TextInputLayout till = new TextInputLayout(getActivity());
                    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params3.weight = 1;
                    til1.setHint(eachData.getString("title"));
                    till.setHint(eachData.getString("title"));
                    EditText et = new EditText(getActivity());
                    EditText et2 = new EditText(getActivity());
                    til1.setLayoutParams(params3);
                    till.setLayoutParams(params3);
                    et.setTextSize(14);
                    et2.setTextSize(14);
                    et2.setFocusable(true);
                    et.setFocusable(true);
                    Drawable img = getResources().getDrawable(R.drawable.ic_calendar);
                    et.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
                    et2.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, img, null);
                    til1.addView(et);
                    till.addView(et2);

                    linearLayoutdate.addView(til1);
                    linearLayoutdate.addView(till);
                    cardView.setContentPadding(10, 20, 10, 20);
                    et.setClickable(false);
                    et.setFocusable(false);
                    et2.setClickable(false);
                    et2.setFocusable(false);

                    final EditText editText = et;
                    final EditText editText1 = et2;
                    editText1.setOnClickListener(view -> adforest_showDate(editText1));
                    editText.setOnClickListener(v -> adforest_showDate(editText));

                    linearLayout.addView(cardView);
                    allViewInstanceforCustom.add(linearLayoutdate);
                    cardView.addView(linearLayoutdate);
                    linearLayoutCustom.addView(linearLayout);
                }
                if (eachData.getString("field_type").equals("radio_color")) {
                    HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 3;
                    params.bottomMargin = 3;
                    horizontalScrollView.setLayoutParams(params);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = 3;
                    layoutParams.bottomMargin = 3;
                    layoutParams.setMarginEnd(5);

                    final JSONArray radioButtonJSONOpt = eachData.getJSONArray("values");
                    RadioGroup rg = new RadioGroup(getActivity()); //create the RadioGroup
                    rg.setOrientation(LinearLayout.HORIZONTAL);
                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {

                        RadioButton rb = new RadioButton(getActivity());
                        rg.addView(rb, layoutParams);
//                        rb.setChecked(true);
                        rb.setLayoutParams(layoutParams);
//                        rb.setHint(radioButtonJSONOpt.getJSONObject(j).getString("title"));

                        rb.setTag(radioButtonJSONOpt.getJSONObject(j).getString("id"));
                        rb.setBackgroundColor(Color.parseColor("#FFFFFF"));

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_enabled}, //disabled
                                        new int[]{android.R.attr.state_enabled} //enabled
                                },
                                new int[]{
                                        Color.BLACK //disabled
                                        , Color.parseColor(radioButtonJSONOpt.getJSONObject(j).getString("id")) //enabled
                                }
                        );


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            rb.setButtonTintList(colorStateList);//set the color tint list
                        }

                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                View radioButton = group.findViewById(checkedId);
//                                String variant_name = radioButton.getTag().toString();
//                                Toast.makeText(getActivity(), variant_name + "", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    allViewInstanceforCustom.add(rg);
                    horizontalScrollView.addView(rg);
                    linearLayout.addView(horizontalScrollView, params);
                    linearLayoutCustom.addView(linearLayout);
                }
                if (eachData.getString("field_type").equals("number_range")) {
                    // get seekbar from view
                    final CrystalRangeSeekbar rangeSeekbar = new CrystalRangeSeekbar(getActivity());
                    rangeSeekbar.setMinValue(Float.valueOf(eachData.getJSONObject("values").getString("min_val")));
                    rangeSeekbar.setMaxValue(Float.valueOf(eachData.getJSONObject("values").getString("max_val")));

                    rangeSeekbar.setBarColor(Color.parseColor("#eeeeee")).apply();
                    rangeSeekbar.setBarHighlightColor(Color.parseColor(SettingsMain.getMainColor()));
                    rangeSeekbar.setLeftThumbColor(Color.parseColor(SettingsMain.getMainColor())).apply();
                    rangeSeekbar.setRightThumbColor(Color.parseColor(SettingsMain.getMainColor())).apply();
                    rangeSeekbar.setRightThumbHighlightColor(Color.parseColor(SettingsMain.getMainColor()));
                    rangeSeekbar.setLeftThumbHighlightColor(Color.parseColor(SettingsMain.getMainColor()));
                    rangeSeekbar.setGap(30);
                    rangeSeekbar.setCornerRadius(5);
                    rangeSeekbar.apply();
                    LinearLayout linearLayoutHori = new LinearLayout(getActivity());
                    linearLayoutHori.setOrientation(LinearLayout.HORIZONTAL);
                    TextInputLayout til = new TextInputLayout(getActivity());
                    TextInputLayout til2 = new TextInputLayout(getActivity());

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.weight = 1;
                    final EditText et = new EditText(getActivity());
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    final EditText et2 = new EditText(getActivity());
                    et2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    til.addView(et);
                    til2.addView(et2);
                    til.setLayoutParams(params2);
                    til2.setLayoutParams(params2);
                    linearLayoutHori.addView(til);
                    linearLayoutHori.addView(til2);
                    rangeSeekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                        et.setText(String.valueOf(minValue));
                        et2.setText(String.valueOf(maxValue));
                    });
                    rangeSeekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue)));
                    allViewInstanceforCustom.add(linearLayoutHori);
                    linearLayout.addView(rangeSeekbar);
                    linearLayout.addView(linearLayoutHori);
                    cardView.addView(linearLayout);
                    linearLayoutCustom.addView(cardView);
                }
                if (eachData.getString("field_type").equals("range_textfield")) {
                    LinearLayout linearLayout1 = new LinearLayout(getActivity());
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                    TextInputLayout til = new TextInputLayout(getActivity());
                    TextInputLayout til2 = new TextInputLayout(getActivity());

                    til.setHint(eachData.getJSONArray("data").getJSONObject(0).getString("title"));
                    til2.setHint(eachData.getJSONArray("data").getJSONObject(1).getString("title"));

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params2.weight = 1;

                    EditText et = new EditText(getActivity());
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    EditText et2 = new EditText(getActivity());
                    et2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    til.addView(et);
                    til2.addView(et2);

                    til.setLayoutParams(params2);
                    til2.setLayoutParams(params2);
                    linearLayout1.addView(til);
                    linearLayout1.addView(til2);

                    linearLayout.addView(linearLayout1);
                    allViewInstanceforCustom.add(linearLayout1);
                    cardView.addView(linearLayout);
                    linearLayoutCustom.addView(cardView);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void adforest_showDate(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, java.util.Locale.getDefault());
                if (editText != null)
                    editText.setText(sdf.format(myCalendar.getTime()));
            }
        }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStop() {
        super.onStop();

        adforest_showFiler();
    }

    @Override
    public void onStart() {
        super.onStart();

        adforest_showFiler();
    }

    @Override
    public void onResume() {
        super.onResume();
        adforest_showFiler();
    }

    @Override
    public void onPause() {
        super.onPause();
        adforest_showFiler();
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

        allViewInstance.clear();
    }

    void adforest_showFiler() {

        if (MyRecyclerView.getAdapter() != null && MyRecyclerView.getAdapter().getItemCount() >= 0) {
            linearLayoutFilter.setVisibility(View.VISIBLE);
            if (MyRecyclerView.getAdapter().getItemCount() == 0) {
                relativeLayoutSpiner.setVisibility(View.GONE);
            } else
                relativeLayoutSpiner.setVisibility(View.VISIBLE);

        } else
            linearLayoutFilter.setVisibility(View.GONE);
    }

    void adforest_addToFavourite(String Id) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", Id);
            Log.d("info sendFavourite", Id);
            Call<ResponseBody> myCall = restService.postAddToFavourite(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdToFav Resp", "" + responseObj.toString());

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
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info AdToFav ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info AdToFav error", String.valueOf(t));
                        Log.d("info AdToFav error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
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
}
