package com.kathryn.kathryn.ad_detail;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.adapters.ItemRatingListAdapter;
import com.kathryn.kathryn.helper.BlogCommentOnclicklinstener;
import com.kathryn.kathryn.modelsList.blogCommentsModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static com.kathryn.kathryn.ad_detail.FragmentAdDetail.myId;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdRating extends Fragment implements Serializable {

    ItemRatingListAdapter itemSendRecMesageAdapter;
    SettingsMain settingsMain;
    RecyclerView recyclerView;
    Button Cancel;
    LinearLayout linearLayout;
    TextView ratingLoadMoreButton, titleRating;
    JSONObject jsonObjectRatingInfo, jsonObjectPagination;
    RestService restService;
    ProgressBar progressBar;
    int currentPage;
    RelativeLayout reactionLayout;
    private ArrayList<blogCommentsModel> listitems = new ArrayList<>();

    public AdRating() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ad_rating, container, false);
        settingsMain = new SettingsMain(getActivity());
        reactionLayout = view.findViewById(R.id.reactions);
        Cancel = view.findViewById(R.id.cancel_button);
        linearLayout = view.findViewById(R.id.ratingLoadLayout);
        ratingLoadMoreButton = view.findViewById(R.id.ratingLoadMoreButton);
        titleRating = view.findViewById(R.id.titleRating);
        recyclerView = view.findViewById(R.id.loadMoreRecyclerView);
        progressBar = view.findViewById(R.id.progress_bar);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        Bundle bundle = getArguments();
        String ratingInfo = bundle.getString("jsonObjectRatingInfo");
        try {
            jsonObjectRatingInfo = new JSONObject(ratingInfo);
            jsonObjectPagination = jsonObjectRatingInfo.getJSONObject("pagination");
            Log.d("info adRating", jsonObjectRatingInfo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        ratingLoadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adforest_loadMoreRattings();
            }
        });

        adforest_setAllInitialize();
        return view;
    }

    private void adforest_setAllInitialize() {
        try {
            getActivity().setTitle(jsonObjectRatingInfo.getString("section_title"));
            titleRating.setText(jsonObjectRatingInfo.getString("section_title"));

            if (jsonObjectPagination.getBoolean("has_next_page")) {
                linearLayout.setVisibility(View.VISIBLE);
                ratingLoadMoreButton.setText(jsonObjectRatingInfo.getString("loadmore_btn"));
                ratingLoadMoreButton.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
            }
            adforest_initializeListRating(jsonObjectRatingInfo.getJSONArray("ratings"));
            itemSendRecMesageAdapter = new ItemRatingListAdapter(getActivity(), listitems);
            recyclerView.setAdapter(itemSendRecMesageAdapter);
            itemSendRecMesageAdapter.setOnItemClickListener(new BlogCommentOnclicklinstener() {
                @Override
                public void onItemClick(blogCommentsModel item) {
                    if (settingsMain.getAppOpen()) {
                        Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        adforest_ratingReplyDialog(item.getComntId());

                        currentPage = item.getPageNumber();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
        }
    }

    //     public  void adforest_postCommentRating(final String comntId){
//         String ad_id = null;
//        final String finalAd_id = ad_id;
//
//        if(SettingsMain.isConnectingToInternet(getActivity())){
//            SettingsMain.showDilog(getActivity());
//            JsonObject params2 = new JsonObject();
////            params2.addProperty("ad_id", myId);
////            params2.addProperty("ad_id", r_id);
////            params2.addProperty("ad_id", c_id);
//            params2.addProperty("ad_id", finalAd_id);
//                    params2.addProperty("comment_id", comntId);
//            Log.d("info send PostRating", params2.toString());
//            Call<ResponseBody> myCall = restService.postCommentRating(params2, UrlController.AddHeaders(getActivity()));
//myCall.enqueue(new Callback<ResponseBody>() {
//    @Override
//    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//        if (responseObj.isSuccessful()){
//            Toast.makeText(getContext(),"Aya react ma",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//    }
//});
//
//
//        }
//    }
    private void adforest_initializeListRating(JSONArray jsonArray) {
        try {
            if (jsonArray.length() > 0) {
                Log.d("info MoreRating details", jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    blogCommentsModel item = new blogCommentsModel();

                    item.setComntId(jsonObject1.getString("rating_id"));
                    item.setName(jsonObject1.getString("rating_author_name"));
                    item.setMessage(jsonObject1.getString("rating_text"));
                    item.setLikeReactionId(jsonObject1.getJSONObject("ad_reactions").getString("like"));
                    item.setHeartReactionId(jsonObject1.getJSONObject("ad_reactions").getString("love"));
                    item.setWowReactionId(jsonObject1.getJSONObject("ad_reactions").getString("wow"));
                    item.setAngryReactionId(jsonObject1.getJSONObject("ad_reactions").getString("angry"));
                    item.setRating(jsonObject1.getString("rating_stars"));
                    item.setDate(jsonObject1.getString("rating_date"));
                    item.setImage(jsonObject1.getString("rating_author_image"));
                    item.setReply(jsonObject1.getString("reply_text"));
                    item.setCanReply(jsonObject1.getBoolean("can_reply"));
                    item.setHasReplyList(jsonObject1.getBoolean("has_reply"));

                    item.setPageNumber(jsonObject1.getInt("current_page"));

                    if (jsonObject1.getBoolean("has_reply")) {

                        ArrayList<blogCommentsModel> listitemsiner = new ArrayList<>();

                        JSONArray jsonArray1 = jsonObject1.getJSONArray("reply");
                        for (int ii = 0; ii < jsonArray1.length(); ii++) {
                            JSONObject jsonObject11 = jsonArray1.getJSONObject(ii);

                            blogCommentsModel item11 = new blogCommentsModel();

                            item11.setName(jsonObject11.getString("rating_author_name"));
                            item11.setMessage(jsonObject11.getString("rating_text"));
                            item11.setRating(jsonObject11.getString("rating_user_stars"));
                            item11.setDate(jsonObject11.getString("rating_date"));
                            item11.setImage(jsonObject11.getString("rating_author_image"));
                            item11.setReply(jsonObject11.getString("reply_text"));
                            item11.setCanReply(jsonObject11.getBoolean("can_reply"));

                            listitemsiner.add(item11);
                        }
                        item.setListitemsiner(listitemsiner);
                    }
                    listitems.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
        }

    }

    void adforest_loadMoreRattings() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            progressBar.setVisibility(View.VISIBLE);
            JsonObject params = new JsonObject();
            try {
                params.addProperty("ad_id", jsonObjectRatingInfo.getString("ad_id"));
                params.addProperty("page_number", jsonObjectPagination.getInt("next_page"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("info SendLoadMre Rating", params.toString());

            Call<ResponseBody> myCall = restService.postGetMoreAdRating(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {

                            Log.d("info PostRating Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                try {

                                    response = response.getJSONObject("data");
                                    Log.d("info MoreRating Respnce", response.getJSONObject("pagination").toString());
                                    Log.d("info MoreRating Respnce", response.getJSONArray("ratings").toString());
                                    adforest_initializeListRating(response.getJSONArray("ratings"));
                                    jsonObjectPagination = response.getJSONObject("pagination");
                                    itemSendRecMesageAdapter.notifyDataSetChanged();
                                    if (!jsonObjectPagination.getBoolean("has_next_page")) {
                                        linearLayout.setVisibility(View.GONE);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);

                        e.printStackTrace();
                    } catch (IOException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("info makeFeature error", String.valueOf(t));
                    Log.d("info makeFeature error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_ratingReplyDialog(final String comntId) {
        String text = null, sendBtn = null, cancelBtn = null, ad_id = null;
        try {
            JSONObject dialogObject = jsonObjectRatingInfo.getJSONObject("rply_dialog");
            text = dialogObject.getString("text");
            sendBtn = dialogObject.getString("send_btn");
            cancelBtn = dialogObject.getString("cancel_btn");
            ad_id = jsonObjectRatingInfo.getString("ad_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog;
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        final EditText message = dialog.findViewById(R.id.editText3);
        message.setHint(text);
        Cancel.setText(cancelBtn);
        Send.setText(sendBtn);

        final String finalAd_id = ad_id;
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().isEmpty()) {
                    JsonObject params = new JsonObject();
                    params.addProperty("ad_id", finalAd_id);
                    params.addProperty("comment_id", comntId);
                    params.addProperty("page_number", currentPage);
                    params.addProperty("rating_comments", message.getText().toString());
                    adforest_postRating(params);
                    dialog.dismiss();
                }
                if (message.getText().toString().isEmpty())
                    message.setError("");
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void adforest_postRating(JsonObject params) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());


            Log.d("info send PostRating", params.toString());
            Call<ResponseBody> myCall = restService.postRating(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info PostRating Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                try {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    response = response.getJSONObject("data");
                                    for (int i = 0; i < listitems.size(); i++) {
                                        for (int ii = 0; ii < response.getJSONArray("ratings").length(); ii++) {
                                            if (listitems.get(i).getComntId().equals(response.
                                                    getJSONArray("ratings").getJSONObject(ii).getString("rating_id"))) {
                                                listitems.remove(i);
                                            }
                                        }
                                    }
                                    adforest_initializeListRating(response.getJSONArray("ratings"));
                                    itemSendRecMesageAdapter.notifyDataSetChanged();
                                    SettingsMain.hideDilog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            SettingsMain.hideDilog();
                        }
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (Exception e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Some error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info makeFeature error", String.valueOf(t));
                    Log.d("info makeFeature error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Rating & Reviews");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
