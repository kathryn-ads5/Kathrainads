package com.kathryn.kathryn.home;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.Search.FragmentCatSubNSearch;
import com.kathryn.kathryn.helper.GridSpacingItemDecoration;
import com.kathryn.kathryn.helper.OnItemClickListener;
import com.kathryn.kathryn.home.adapter.ItemMainAllLocationAds;
import com.kathryn.kathryn.home.adapter.ItemMainAllLocationPoPUpHome;
import com.kathryn.kathryn.modelsList.homeCatListModel;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllLocationsPopUpHome extends Fragment {

    RecyclerView locationRecycler_view;
    ItemMainAllLocationPoPUpHome ItemMainAllLocationPoPUpHome;
    ArrayList<homeCatListModel> locationAdscat = new ArrayList<>();
    SettingsMain settingsMain;
    RestService restService;
    int next_page = 1;
    boolean has_next_page = false;
    public JSONObject responceData, pagination;
    Button btn_loadMore;
    String term_id = "";
    EditText searchView;
    private Context context;
    String locId;

    public FragmentAllLocationsPopUpHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        settingsMain = new SettingsMain(context);

        locationRecycler_view = view.findViewById(R.id.locationRecycler_view);
//        btn_loadMore = view.findViewById(R.id.btn_loadMore);
        searchView = view.findViewById(R.id.mSearch);
        searchView.requestFocus();

        locationRecycler_view.setHasFixedSize(true);
        locationRecycler_view.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(locationRecycler_view, false);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(context, 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        locationRecycler_view.setLayoutManager(MyLayoutManager);

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        next_page = 1;
        term_id = "";
        adforest_loadData();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ItemMainAllLocationPoPUpHome.getFilter().filter(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void adforest_loadData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("term_name", "ad_country");
            jsonObject.addProperty("term_id", term_id);
            jsonObject.addProperty("page_number", next_page);
            Log.d("info catLoc send", jsonObject.toString());

            Call<ResponseBody> myCall = restService.getHomeDetails((UrlController.AddHeaders(getActivity())));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info catLoc Resp", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info catLoc obj", "" + response.getJSONObject("data"));
                                responceData = response.getJSONObject("data");

                                searchView.setHint("Search Location");
                                adforest_setAllLocationAds(responceData.getJSONArray("cat_locations"));

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
                        Log.d("info catLoc ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info catLoc err", String.valueOf(t));
                        Log.d("info catLoc err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_setLoadMoreLocationAds(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            try {
                item.setId(jsonArray.getJSONObject(i).getString("term_id"));
                item.setTitle(jsonArray.optJSONObject(i).getString("name"));
                item.setThumbnail(jsonArray.optJSONObject(i).getString("term_img"));
                item.setAdsCount(jsonArray.getJSONObject(i).getString("count"));
                item.setHas_children(jsonArray.getJSONObject(i).getBoolean("has_children"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            locationAdscat.add(item);
        }
        ItemMainAllLocationPoPUpHome.notifyDataSetChanged();
    }

    private void adforest_setAllLocationAds(JSONArray jsonArray) {

        locationAdscat.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            try {
                item.setTitle(jsonArray.optJSONObject(i).getString("name"));
                item.setThumbnail(jsonArray.optJSONObject(i).getString("img"));
                item.setId(jsonArray.optJSONObject(i).getString("cat_id"));
                item.setAdsCount(jsonArray.getJSONObject(i).getString("count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            locationAdscat.add(item);
        }

        ItemMainAllLocationPoPUpHome = new ItemMainAllLocationPoPUpHome(context, locationAdscat, 2);
        locationRecycler_view.setAdapter(ItemMainAllLocationPoPUpHome);
        ItemMainAllLocationPoPUpHome.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(homeCatListModel item) {

                searchView.setText("");
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra("location_id", item.getId());
                intent.putExtra("location_name", item.getTitle());
////                      intent.putExtra("location_img",item.getThumbnail());
                startActivityForResult(intent, 1);
            }

        });

    }

    private void adforest_locationAds() throws JSONException {
//        TextView title = new TextView(getActivity());
//        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        title.setPadding(3, 3, 3, 3);
//        title.setPaddingRelative(3, 3, 3, 3);
//        title.setTextColor(Color.BLACK);
//        title.setLayoutParams(params3);
//        title.setTextSize(17);
//
//
//        CardView cardView = new CardView(getActivity());
//        cardView.setCardElevation(3);
//        cardView.setUseCompatPadding(true);
//        cardView.setRadius(0);
////        cardView.setContentPadding(5, 5, 5, 5);
//        cardView.setPaddingRelative(5, 5, 5, 5);
//
//        LinearLayout linearLayout = new LinearLayout(getActivity());
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//        RecyclerView recyclerView = new RecyclerView(getActivity());
//        recyclerView.setScrollContainer(false);
////        recyclerView.setPadding(5, 5, 5, 5);
//        recyclerView.setPaddingRelative(5, 5, 5, 5);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
//        linearLayout.addView(recyclerView);

//        try {
//            if (responceData.getJSONObject("cat_locations_btn").getBoolean("is_show")) {
//                Button button = new Button(getActivity());
//                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                buttonParams.setMargins(0, 10, 0, 20);
//                buttonParams.setMarginStart(20);
//                buttonParams.setMarginEnd(20);
//                button.setText(responceData.getJSONObject("cat_locations_btn").getString("text"));
//
//                button.setTextColor(Color.WHITE);
//                button.setTextSize(14);
//                button.setLayoutParams(buttonParams);
//                button.setTransformationMethod(null);
//                button.setBackground(CustomBorderDrawable.customButton(6, 6, 6, 6, settingsMain.getMainColor(), settingsMain.getMainColor(), settingsMain.getMainColor(), 3));
////                linearLayout.addView(button);
////                button.setOnClickListener(v -> {
////                    FragmentAllLocations fragmentAllLocations = new FragmentAllLocations();
////                    replaceFragment(fragmentAllLocations, "FragmentAllLocations");
////                });
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        cardView.addView(linearLayout);

//        HomeCustomLayout.addView(title);
//        HomeCustomLayout.addView(cardView);
//        try {
//            title.setText(responseData.getString("cat_locations_title"));
        adforest_setAllLocationAds(responceData.getJSONArray("cat_locations"), locationRecycler_view);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void adforest_setAllLocationAds(JSONArray jsonArray, RecyclerView locationRecycler_view) {

        locationAdscat.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            try {
                Log.d("info location icons", jsonArray.getJSONObject(i).getString("count"));
                item.setTitle(jsonArray.optJSONObject(i).getString("name"));
                item.setThumbnail(jsonArray.optJSONObject(i).getString("img"));
                item.setId(jsonArray.optJSONObject(i).getString("cat_id"));
                item.setAdsCount(jsonArray.getJSONObject(i).getString("count"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            locationAdscat.add(item);
        }

        GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        locationRecycler_view.setLayoutManager(MyLayoutManager);
        int spacing = 15; // 50px

        locationRecycler_view.addItemDecoration(new GridSpacingItemDecoration(1, spacing, false));

        ItemMainAllLocationPoPUpHome adapter = new ItemMainAllLocationPoPUpHome(context, locationAdscat, 1);
        locationRecycler_view.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(homeCatListModel item) {
//                FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
//                Bundle bundle = new Bundle();
//                bundle.putString("ad_country", item.getId());
//
//                fragment_search.setArguments(bundle);
//                replaceFragment(fragment_search, "FragmentCatSubNSearch");
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra("location_id", item.getId());
                intent.putExtra("location_name", item.getTitle());
////                      intent.putExtra("location_img",item.getThumbnail());
                startActivityForResult(intent, 1);

            }
        });

    }

    public void getArgs() {


        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentHome.locationDialog.dismiss();
                    FragmentHome.locationFragmentView = null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.remove(getFragmentManager().findFragmentById(R.id.locationSubCatFragment)).commit();
                    return true;
                }
                return false;
            }
        });

    }

    void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show();
//                Uri s = data.getData();
//
//                et_location.setText(s.toString());
            }

        }
    }
}
