package com.kathryn.kathryn.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.myAdsModel;
import com.squareup.picasso.Picasso;

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

public class MyAds_Rejected extends Fragment implements com.kathryn.kathryn.utills.RuntimePermissionHelper.permissionInterface{

    com.kathryn.kathryn.utills.SettingsMain settingsMain;
    TextView verifyBtn, textViewRateNo, textViewUserName, textViewLastLogin;
    TextView editProfBtn, textViewAdsSold, textViewTotalList, textViewInactiveAds, textViewEmptyData,textViewExppiry;
    RatingBar ratingBar;
    ImageView imageViewProfile;
    RecyclerView recyclerView;
    com.kathryn.kathryn.profile.adapter.ItemMyAdsAdapter adapter;
    int nextPage = 1;
    boolean loading = true, hasNextPage = false;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    com.kathryn.kathryn.utills.Network.RestService restService;
    private ArrayList<myAdsModel> list = new ArrayList<>();
    com.kathryn.kathryn.utills.RuntimePermissionHelper runtimePermissionHelper;
    String adID;
    Boolean isRejected;
    public MyAds_Rejected() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_myadd, container, false);

        settingsMain = new com.kathryn.kathryn.utills.SettingsMain(getActivity());
        runtimePermissionHelper = new com.kathryn.kathryn.utills.RuntimePermissionHelper(getActivity(), this);

        progressBar = view.findViewById(R.id.progressBar4);
        nestedScrollView = view.findViewById(R.id.mainScrollView);
        progressBar.setVisibility(View.GONE);

        textViewLastLogin = view.findViewById(R.id.loginTime);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);

        imageViewProfile = view.findViewById(R.id.image_view);
        ratingBar = view.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        editProfBtn = view.findViewById(R.id.editProfile);
        textViewEmptyData = view.findViewById(R.id.textView5);
        textViewEmptyData.setVisibility(View.GONE);
        textViewAdsSold = view.findViewById(R.id.share);
        textViewTotalList = view.findViewById(R.id.addfav);
        textViewInactiveAds = view.findViewById(R.id.report);
        textViewExppiry = view.findViewById(R.id.expired);
        restService = com.kathryn.kathryn.utills.UrlController.createService(com.kathryn.kathryn.utills.Network.RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        editProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new com.kathryn.kathryn.profile.EditProfile(), "EditProfile");
            }
        });

        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), 2);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);

        nestedScrollView.setOnScrollChangeListener(new com.kathryn.kathryn.utills.NestedScroll() {
            @Override
            public void onScroll() {

                if (loading) {
                    loading = false;
                    Log.d("info data object", "sdfasdfadsasdfasdfasdf");

                    if (hasNextPage) {
                        progressBar.setVisibility(View.VISIBLE);
                        adforest_loadMore(nextPage);
                    }
                }
            }
        });

        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    com.kathryn.kathryn.profile.RatingFragment fragment = new com.kathryn.kathryn.profile.RatingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", settingsMain.getUserLogin());
                    bundle.putBoolean("isprofile", true);
                    fragment.setArguments(bundle);

                    replaceFragment(fragment, "RatingFragment");
                }
                return true;
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        loadData();

        return view;
    }

    private void setAllViewsText(JSONObject jsonObject) {
        try {
            textViewLastLogin.setText(jsonObject.getString("last_login"));
            textViewUserName.setText(jsonObject.getString("display_name"));

            Picasso.with(getContext()).load(jsonObject.getString("profile_img"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageViewProfile);

            verifyBtn.setText(jsonObject.getJSONObject("verify_buton").getString("text"));
            verifyBtn.setBackground(com.kathryn.kathryn.utills.CustomBorderDrawable.customButton(0, 0, 0, 0,
                    jsonObject.getJSONObject("verify_buton").getString("color"),
                    jsonObject.getJSONObject("verify_buton").getString("color"),
                    jsonObject.getJSONObject("verify_buton").getString("color"), 3));

            textViewAdsSold.setText(jsonObject.getString("ads_sold"));
            textViewTotalList.setText(jsonObject.getString("ads_total"));
            textViewInactiveAds.setText(jsonObject.getString("ads_inactive"));
            textViewExppiry.setText(jsonObject.getString("ads_expired"));

            ratingBar.setNumStars(5);
            ratingBar.setRating(Float.parseFloat(jsonObject.getJSONObject("rate_bar").getString("number")));
            textViewRateNo.setText(jsonObject.getJSONObject("rate_bar").getString("text"));

            editProfBtn.setText(jsonObject.getString("edit_text"));
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();

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
    }

    private void adforest_loadMore(int nextPag) {

        if (com.kathryn.kathryn.utills.SettingsMain.isConnectingToInternet(getActivity())) {


            JsonObject params = new JsonObject();

            params.addProperty("page_number", nextPag);


            Log.d("info SendLoadMore Fav", params.toString());

            Call<ResponseBody> myCall = restService.postGetLoadMoreFavouriteAds(params, com.kathryn.kathryn.utills.UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoadMore Fav Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info LoadMore Fav obj", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                loadMoreList(response.getJSONObject("data"), response.getJSONObject("data").getJSONObject("text"));

                                loading = true;
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info LoadMore Fav ", "NullPointert Exception" + t.getLocalizedMessage());
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    } else {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        Log.d("info LoadMore Fav err", String.valueOf(t));
                        Log.d("info LoadMore Fav err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });

        } else {
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadData() {

        if (com.kathryn.kathryn.utills.SettingsMain.isConnectingToInternet(getActivity())) {

            if (!com.kathryn.kathryn.home.HomeActivity.checkLoading)
                com.kathryn.kathryn.utills.SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getRejectedAdsDetails(com.kathryn.kathryn.utills.UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info RejectedAds", "" + responseObj.toString());
                            com.kathryn.kathryn.home.HomeActivity.checkLoading = false;

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info RejectedAds obj", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");
                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                makeList(response.getJSONObject("data"), response.getJSONObject("data").getJSONObject("text"));
                                setAllViewsText(response.getJSONObject("data").getJSONObject("profile"));

                                if (list.size() > 0) {
                                    adapter = new com.kathryn.kathryn.profile.adapter.ItemMyAdsAdapter(getActivity(), list);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new com.kathryn.kathryn.helper.MyAdsOnclicklinstener() {
                                        @Override
                                        public void onItemClick(com.kathryn.kathryn.modelsList.myAdsModel item) {


                                            Intent intent = new Intent(getActivity(), com.kathryn.kathryn.ad_detail.Ad_detail_activity.class);
                                            intent.putExtra("adId", item.getAdId());
                                            intent.putExtra("is_rejected",isRejected);

                                            getActivity().startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                        }

                                        @Override
                                        public void delViewOnClick(final View v, int position) {

                                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                            alert.setTitle(settingsMain.getGenericAlertTitle());
                                            alert.setCancelable(false);
                                            alert.setMessage(settingsMain.getGenericAlertMessage());
                                            alert.setPositiveButton(settingsMain.getGenericAlertOkText(), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    del(v.getTag().toString());
                                                    dialog.dismiss();
                                                }
                                            });
                                            alert.setNegativeButton(settingsMain.getGenericAlertCancelText(), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            alert.show();
                                        }

                                        @Override
                                        public void editViewOnClick(View v, int position) {
                                            adID = v.getTag().toString();
                                            runtimePermissionHelper.requestLocationPermission(1);
                                            Toast.makeText(getContext(),"edit chala",Toast.LENGTH_SHORT).show();
                                        }

                                    });
                                } else {
                                    textViewEmptyData.setVisibility(View.VISIBLE);
                                    textViewEmptyData.setText(response.get("message").toString());
                                }
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();

                    } catch (JSONException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    Log.d("info FavouriteAds error", String.valueOf(t));
                    Log.d("info FavouriteAds error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    void makeList(JSONObject data, JSONObject texts) {
        list.clear();

        try {
            JSONArray jsonArray = data.getJSONArray("ads");

            Log.d("jsonaarry makelist = ", jsonArray.toString());
            if (jsonArray.length() > 0)
                for (int i = 0; i < jsonArray.length(); i++) {

                    com.kathryn.kathryn.modelsList.myAdsModel item = new com.kathryn.kathryn.modelsList.myAdsModel();
                    JSONObject object = jsonArray.getJSONObject(i);

                    item.setAdId(object.getString("ad_id"));
                    item.setName(object.getString("ad_title"));
                    item.setAdStatus(object.getJSONObject("ad_status").getString("status"));
                    item.setAdStatusValue(object.getJSONObject("ad_status").getString("status_text"));
                    item.setAdTypeText(object.getJSONObject("ad_status").getString("featured_type_text"));
                    item.setPrice(object.getJSONObject("ad_price").getString("price"));
                    item.setImage((object.getJSONArray("ad_images").getJSONObject(0).getString("thumb")));

                    item.setDelAd(texts.getString("delete_text"));
                    item.setEditAd(texts.getString("edit_text"));
                    item.setAdType(texts.getString("ad_type"));

                    item.setSpinerData(texts.getJSONArray("status_dropdown_name"));
                    item.setSpinerValue(texts.getJSONArray("status_dropdown_value"));

                    list.add(item);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void del(String tag) {

        if (com.kathryn.kathryn.utills.SettingsMain.isConnectingToInternet(getActivity())) {

            com.kathryn.kathryn.utills.SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", tag);
            Log.d("info Send FavAds Delete", params.toString());

            Call<ResponseBody> myCall = restService.postRemoveFavAd(params, com.kathryn.kathryn.utills.UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info FavAds Delete Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info FavAds object", "" + response.get("message"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                reload();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info FavAds Delete ", "NullPointert Exception" + t.getLocalizedMessage());
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    } else {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        Log.d("info FavAds Delete err", String.valueOf(t));
                        Log.d("info FavAds Delete err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void loadMoreList(JSONObject data, JSONObject texts) {
        try {
            JSONArray jsonArray = data.getJSONArray("ads");

            Log.d("jsonaarry is = ", jsonArray.toString());
            if (jsonArray.length() > 0)
                for (int i = 0; i < jsonArray.length(); i++) {

                    com.kathryn.kathryn.modelsList.myAdsModel item = new com.kathryn.kathryn.modelsList.myAdsModel();
                    JSONObject object = jsonArray.getJSONObject(i);

                    item.setAdId(object.getString("ad_id"));
                    item.setName(object.getString("ad_title"));
                    item.setAdStatus(object.getJSONObject("ad_status").getString("status"));
                    item.setAdStatusValue(object.getJSONObject("ad_status").getString("status_text"));
                    item.setPrice(object.getJSONObject("ad_price").getString("price"));
                    item.setImage((object.getJSONArray("ad_images").getJSONObject(0).getString("thumb")));

                    item.setDelAd(texts.getString("delete_text"));
                    item.setEditAd(texts.getString("edit_text"));

                    item.setAdType(texts.getString("ad_type"));

                    item.setSpinerData(texts.getJSONArray("status_dropdown_name"));
                    item.setSpinerValue(texts.getJSONArray("status_dropdown_value"));

                    list.add(item);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reload() {
        Fragment frg;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        frg = manager.findFragmentByTag("MyAds_Favourite");
        final FragmentTransaction ft = manager.beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                com.kathryn.kathryn.utills.AnalyticsTrackers.getInstance().trackScreenView("Favourite Ads");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessPermission(int code) {

        Intent in = new Intent(getActivity(), com.kathryn.kathryn.home.EditAdPost.class);
        in.putExtra("id", adID);
        getActivity().startActivity(in);
        getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
    }
}
