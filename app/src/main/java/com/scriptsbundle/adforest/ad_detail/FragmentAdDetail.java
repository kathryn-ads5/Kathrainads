package com.kathryn.kathryn.ad_detail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonObject;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlAssetsImageGetter;
import org.sufficientlysecure.htmltextview.HtmlResImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.countdownview.DynamicConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Biding.Biding_ViewPagerFragment;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.full_screen_image.FullScreenViewActivity;
import com.kathryn.kathryn.adapters.ItemMainHomeRelatedAdapter;
import com.kathryn.kathryn.adapters.ItemRatingListAdapter;
import com.kathryn.kathryn.helper.BlogCommentOnclicklinstener;
import com.kathryn.kathryn.helper.OnItemClickListener2;
import com.kathryn.kathryn.home.EditAdPost;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.messages.Message;
import com.kathryn.kathryn.modelsList.blogCommentsModel;
import com.kathryn.kathryn.modelsList.catSubCatlistModel;
import com.kathryn.kathryn.modelsList.myAdsModel;
import com.kathryn.kathryn.profile.RatingFragment;
import com.kathryn.kathryn.public_profile.FragmentPublic_Profile;
import com.kathryn.kathryn.utills.AdsTimerConvert;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.RuntimePermissionHelper;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class FragmentAdDetail extends Fragment implements Serializable, RuntimePermissionHelper.permissionInterface {
    public static String myId;


    public static JSONObject jsonObjectBidTabs;
    public static String buttonPress;
    Dialog dialog;
    RelativeLayout relativeLayoutFeature,reactionLayout;
    SettingsMain settingsMain;
    ItemRatingListAdapter itemSendRecMesageAdapter;
    myAdsModel item;
    TextView textViewAdName, textViewLocation, textViewSeen, textViewDate, textViewPrice, textViewLastLogin;
    TextView shareBtn, addToFavBtn, reportBtn, verifyBtn, textViewRateNo, textViewUserName, textViewRelated, textViewDescript;
    TextView messageBtn, callBtn, bidBtn, textViewNotify, getDirectionBtn, textViewFeatured, makeFeatureBtn, featuredText, bidStatisticsBtn;
    HtmlTextView htmlTextView;
    LinearLayout linearLayout2, linearLayout1, linearLayoutOuter, linearLayoutLoadMoreRatings;
    RatingBar ratingBar;
    ImageView imageViewProfile;
    BannerSlider bannerSlider;
    List<Banner> banners;
    ArrayList<String> imageUrls;
    JSONObject jsonObjectSendMessage, jsonObjectCallNow, jsonObjectBidNow, jsonObjectReport,
            jsonObjectShareInfo, jsonObjectRatingInfo, jsonObjectPagination, blockUserObject, JsonObjectData;
    RecyclerView mRecyclerView, ratingRecylerView;
    View temphide;
    LinearLayout linearLayout, linearLayoutSubmitRating, ratingLoadLayout;
    int noOfCol = 2;
    CardView cardViewBidSec, cardViewRating;
    TextView textViewTotBid, textViewHighBid, textViewLowBid, textViewTotBidtext, textViewHighBidtext, textViewLowBidtext, textViewRatingTitle,
            textViewRatingNotEdit, textViewRatingButton, textViewNoCurrentRating, textViewRatingTitleTop, ratingLoadMoreButton,
            tv_adType, tv_blockUser, editAdd;
    RestService restService;
    NestedScrollView nestedScroll;
    EditText editTextRattingComment;
    SimpleRatingBar simpleRatingBar;
    boolean LoadMoreDialogOpen = false;
    Dialog loadMoreRating;
    CountdownView countDown;
    //    FrameLayout youtube_view;
    FrameLayout youtube_view;
    RuntimePermissionHelper runtimePermissionHelper;
    Dialog callDialog;
    private ArrayList<catSubCatlistModel> list = new ArrayList<>();
    private ArrayList<blogCommentsModel> listitems = new ArrayList<>();
    private String phoneNumber;
    private String adAuthorId;
    private JSONObject jsonObjectStaticText;
    private double latitude = 0.0;
    private double longitude = 0.0;
Boolean ratinghideshow;
RelativeLayout reactions;
    public FragmentAdDetail() {
        // Required empty public constructor
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad_detail, container, false);
        settingsMain = new SettingsMain(getActivity());

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            myId = bundle.getString("id", "0");
//            comntId = bundle.getString("r_id", "");
//            reactionId = bundle.getString("c_id", "");
        }
//        reactionLayout=getActivity().findViewById(R.id.reactions);


        linearLayout = getActivity().findViewById(R.id.ll11);
        linearLayout.setVisibility(View.VISIBLE);
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);

        linearLayout.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        temphide = getActivity().findViewById(R.id.temphide);
        nestedScroll = view.findViewById(R.id.scrollViewUp);
        getDirectionBtn = view.findViewById(R.id.textView20);
        relativeLayoutFeature = view.findViewById(R.id.relMakeFeature);
        textViewFeatured = view.findViewById(R.id.relMakeFeatureTV);
        makeFeatureBtn = view.findViewById(R.id.btnMakeFeat);
        featuredText = view.findViewById(R.id.featuredText);

        textViewNotify = view.findViewById(R.id.textView19);
        bannerSlider = view.findViewById(R.id.banner_slider1);
        banners = new ArrayList<>();
        imageUrls = new ArrayList<>();

        messageBtn = getActivity().findViewById(R.id.message);
        callBtn = getActivity().findViewById(R.id.call);
        bidBtn = view.findViewById(R.id.bidBtn);
        youtube_view = view.findViewById(R.id.youtube_view);
        bidStatisticsBtn = view.findViewById(R.id.bidStatisticsBtn);
        editAdd = view.findViewById(R.id.editAdd);

        if (messageBtn != null)
            messageBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        if (callBtn != null)
            callBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        makeFeatureBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        bidBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        bidStatisticsBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        editAdd.setBackground(CustomBorderDrawable.customButton(6, 6, 6, 6, SettingsMain.getMainColor(), SettingsMain.getMainColor(), SettingsMain.getMainColor(), 3));

        cardViewBidSec = view.findViewById(R.id.card_view4);

        textViewTotBid = view.findViewById(R.id.textView8);
        textViewTotBidtext = view.findViewById(R.id.textView9);
        textViewHighBid = view.findViewById(R.id.textView10);
        textViewHighBidtext = view.findViewById(R.id.textView11);
        textViewLowBid = view.findViewById(R.id.textView12);
        textViewLowBidtext = view.findViewById(R.id.textView13);

        textViewAdName = view.findViewById(R.id.text_view_name);
        textViewLocation = view.findViewById(R.id.location);
        textViewSeen = view.findViewById(R.id.views);
        textViewDate = view.findViewById(R.id.date);
        textViewPrice = view.findViewById(R.id.prices);
        textViewLastLogin = view.findViewById(R.id.loginTime);
        shareBtn = view.findViewById(R.id.share);
        addToFavBtn = view.findViewById(R.id.addfav);
        reportBtn = view.findViewById(R.id.report);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);
        textViewRelated = view.findViewById(R.id.relatedText);
        htmlTextView = view.findViewById(R.id.html_text);
        ratingBar = view.findViewById(R.id.ratingBar);
        imageViewProfile = view.findViewById(R.id.image_view);
        tv_adType = view.findViewById(R.id.tv_adType);
        tv_blockUser = view.findViewById(R.id.tv_block_user);

        textViewPrice.setTextColor(Color.parseColor(SettingsMain.getMainColor()));

        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        linearLayoutOuter = view.findViewById(R.id.ll1inner_location);
        linearLayout1 = view.findViewById(R.id.linearLayout1);
        linearLayout2 = view.findViewById(R.id.customLayout1);
        textViewDescript = view.findViewById(R.id.text_view_title);

        //Ratting Initialization
        cardViewRating = view.findViewById(R.id.card_viewRating);
        textViewRatingTitle = view.findViewById(R.id.ratingTitle);
        textViewRatingNotEdit = view.findViewById(R.id.ratingNotEdit);
        editTextRattingComment = view.findViewById(R.id.ratingEditText);
        textViewNoCurrentRating = view.findViewById(R.id.noCurrentRatingText);
        linearLayoutSubmitRating = view.findViewById(R.id.linearLayoutSubmitRating);
        textViewRatingTitleTop = view.findViewById(R.id.sectionTitleRating);
        ratingRecylerView = view.findViewById(R.id.ratingRecylerView);
        simpleRatingBar = view.findViewById(R.id.ratingbarAds);
        ratingLoadLayout = view.findViewById(R.id.ratingLoadLayout);
        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        ratingRecylerView.setLayoutManager(MyLayoutManager);
        ViewCompat.setNestedScrollingEnabled(ratingRecylerView, false);

        textViewRatingButton = view.findViewById(R.id.rating_button);
        textViewRatingButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        ratingLoadMoreButton = view.findViewById(R.id.ratingLoadMoreButton);
        ratingLoadMoreButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        countDown = view.findViewById(R.id.countDown);


        editAdd.setOnClickListener(view1 -> {

            Intent intent = new Intent(getContext(), EditAdPost.class);
            intent.putExtra("id", myId);
            startActivity(intent);

        });

        textViewRatingButton.setOnClickListener(v -> {
            if (!editTextRattingComment.getText().toString().isEmpty()) {
                JsonObject params = new JsonObject();
                params.addProperty("ad_id", myId);
                params.addProperty("rating", simpleRatingBar.getRating());
                params.addProperty("rating_comments", editTextRattingComment.getText().toString());
                adforest_postRating(params);
            }
            if (editTextRattingComment.getText().toString().isEmpty()) {
                editTextRattingComment.setError("");
            }
        });

        ratingLoadLayout.setOnClickListener(v -> {
            AdRating fragment = new AdRating();
            Bundle bundle1 = new Bundle();
            bundle1.putString("jsonObjectRatingInfo", jsonObjectRatingInfo.toString());
            fragment.setArguments(bundle1);
            replaceFragment(fragment, "AdRating");

        });


        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        if (messageBtn != null)
            messageBtn.setOnClickListener(view12 -> {
                try {
                    if (settingsMain.getAppOpen()) {
                        Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")) {
//                            MessagesFragment messagesFragment=new MessagesFragment();
//                            replaceFragment(messagesFragment,"MessagesFragment");
                            Intent intent = new Intent(getActivity(), Message.class);
                            intent.putExtra("receive", true);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                        } else
                            adforest_showDilogMessage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        if (callBtn != null)

            callBtn.setOnClickListener(view13 -> {
                try {
                    if (JsonObjectData.getBoolean("show_phone_to_login") && settingsMain.getAppOpen()) {
                        Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        adforest_showDilogCall();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        bidBtn.setOnClickListener(view14 -> {

            Biding_ViewPagerFragment fragment = new Biding_ViewPagerFragment();
            buttonPress = "bidButton";
            replaceFragment(fragment, "Biding_ViewPagerFragment");
        });
        bidStatisticsBtn.setOnClickListener(v -> {
            Biding_ViewPagerFragment fragment = new Biding_ViewPagerFragment();
            buttonPress = "bidStatisticsBtn";
            replaceFragment(fragment, "Biding_ViewPagerFragment");
        });
        countDown.setOnClickListener(v -> {
            Biding_ViewPagerFragment fragment = new Biding_ViewPagerFragment();
            buttonPress = "bidStatisticsBtn";
            replaceFragment(fragment, "Biding_ViewPagerFragment");
        });
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAdDetail.this.goToPublicProfile();
            }
        });

        reportBtn.setOnClickListener(view15 -> {
            try {
                if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")) {
                    Toast.makeText(getActivity(), JsonObjectData.getString("cant_report_txt"), Toast.LENGTH_SHORT).show();
                } else {
                    if (settingsMain.getAppOpen()) {
                        Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        adforest_showDilogReport();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        textViewUserName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                goToPublicProfile();
            }
            return true;
        });

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (settingsMain.getAppOpen()) {
                    Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    RatingFragment fragment = new RatingFragment();
                    Bundle bundle12 = new Bundle();
                    bundle12.putString("id", adAuthorId);
                    bundle12.putBoolean("isprofile", false);
                    fragment.setArguments(bundle12);

                    replaceFragment(fragment, "RatingFragment");
                }
            }
            return true;
        });

        shareBtn.setOnClickListener(view16 -> {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, jsonObjectShareInfo.getString("title"));
                i.putExtra(Intent.EXTRA_TEXT, jsonObjectShareInfo.getString("link"));
                startActivity(Intent.createChooser(i, jsonObjectShareInfo.getString("text")));
            } catch (Exception e) {
                //e.toString();
            }
        });

        getDirectionBtn.setOnClickListener(view17 -> {

            String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + textViewLocation.getText().toString() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
            getActivity().startActivity(intent);

            Log.d("info data object", longitude + "  ===   " + latitude);


        });

        makeFeatureBtn.setOnClickListener(view18 -> {
            if (settingsMain.getAppOpen()) {
                Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(settingsMain.getAlertDialogTitle("info"));
                alert.setCancelable(false);
                alert.setMessage(settingsMain.getAlertDialogMessage("confirmMessage"));
                alert.setPositiveButton(settingsMain.getAlertOkText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        adforest_makeFeature();

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(settingsMain.getAlertCancelText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        });

        addToFavBtn.setOnClickListener(view19 -> {
            if (settingsMain.getAppOpen()) {
                Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
            } else {
                adforest_addToFavourite();
            }
        });
        bannerSlider.setOnBannerClickListener(position -> {
            if (banners.size() > 0) {

                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
                i.putExtra("imageUrls", imageUrls);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
        tv_blockUser.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                adforest_blockUserDialog();
            }
            return true;
        });
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(MyLayoutManager2);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> adforest_recreateAdDetail());

        adforest_getAllData(myId,true);
//        youTubePlayerView.getYouTubePlayerWhenReady(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer::pause);


        return view;
    }

    private void adforest_getAllData(final String myId,Boolean isRejected) {
        this.myId = myId;
        nestedScroll.scrollTo(0, 0);
        youtube_view.setVisibility(View.GONE);
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            params.addProperty("is_rejected",isRejected );

            Log.d("info send AdDetails", "" + params.toString());

            Call<ResponseBody> myCall = restService.getAdsDetail(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdDetails Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info AdDetails object", "" + response.getJSONObject("data"));
                                Log.d("info ProfileDetails obj", "" + response.getJSONObject("data").getJSONObject("profile_detail"));
                                Log.d("info Bids Data", "" + response.getJSONObject("data").getJSONObject("static_text"));

                                JsonObjectData = response.getJSONObject("data");
                                if (response.getJSONObject("data").getString("notification").equals("")) {
                                    textViewNotify.setVisibility(View.GONE);
                                } else {
                                    textViewNotify.setVisibility(View.VISIBLE);
                                    textViewNotify.setText(response.getJSONObject("data").getString("notification"));
                                }

                                if (response.getJSONObject("data").getJSONObject("is_featured").getBoolean("is_show")) {
                                    relativeLayoutFeature.setVisibility(View.VISIBLE);
                                    makeFeatureBtn.setText(response.getJSONObject("data").getJSONObject("is_featured")
                                            .getJSONObject("notification").getString("btn"));
                                    textViewFeatured.setText(response.getJSONObject("data").getJSONObject("is_featured")
                                            .getJSONObject("notification").getString("text"));
                                    makeFeatureBtn.setTag(response.getJSONObject("data").getJSONObject("is_featured")
                                            .getJSONObject("notification").getInt("link"));
                                } else {
                                    relativeLayoutFeature.setVisibility(View.GONE);
                                }

                                noOfCol = response.getJSONObject("data").getJSONObject("ad_detail").getInt("fieldsData_column");

                                adforest_setAllViewsText(response.getJSONObject("data").getJSONObject("ad_detail"),
                                        response.getJSONObject("data").getJSONObject("profile_detail"),
                                        response.getJSONObject("data").getJSONObject("static_text"));

                                jsonObjectStaticText = response.getJSONObject("data").getJSONObject("static_text");

                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));

                                jsonObjectBidNow = response.getJSONObject("data").getJSONObject("bid_popup");
                                jsonObjectCallNow = response.getJSONObject("data").getJSONObject("call_now_popup");
                                jsonObjectReport = response.getJSONObject("data").getJSONObject("report_popup");
                                jsonObjectSendMessage = response.getJSONObject("data").getJSONObject("message_popup");
                                jsonObjectShareInfo = response.getJSONObject("data").getJSONObject("share_info");

                                //Rating View setAllTexts and Ratting
                                jsonObjectRatingInfo = response.getJSONObject("data").getJSONObject("ad_ratting");
                                if (jsonObjectRatingInfo.getBoolean("rating_show")) {
                                    cardViewRating.setVisibility(View.VISIBLE);
                                    adforest_setAllRattigns();/**/
                                }
                                if (jsonObjectRatingInfo.getBoolean("ad_rating_emojies")) {
                                    cardViewRating.setVisibility(View.VISIBLE);
                                    adforest_setAllRattigns();/**/
                                }


                                ItemMainHomeRelatedAdapter adapter = new ItemMainHomeRelatedAdapter(getActivity(), list);
                                mRecyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new OnItemClickListener2() {
                                    @Override
                                    public void onItemClick(catSubCatlistModel item) {
                                        Log.d("item_id", item.getId());
                                        FragmentAdDetail.myId = item.getId();
                                        adforest_recreateAdDetail();
                                        nestedScroll.scrollTo(0, 0);
                                    }
                                });

                                JSONObject adType = response.getJSONObject("data").
                                        getJSONObject("ad_detail").getJSONObject("ad_type_bar");
                                if (adType.getBoolean("is_show")) {
                                    tv_adType.setVisibility(View.VISIBLE);
                                    tv_adType.setText(adType.getString("text"));
                                }

                                if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive") && !settingsMain.getAppOpen()) {
                                    editAdd.setVisibility(View.VISIBLE);
                                    editAdd.setText(JsonObjectData.getString("edit_txt"));
                                }

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
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
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void adforest_setAllRattigns() {
        try {
            if (jsonObjectRatingInfo.getBoolean("can_rate") && !jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")
                    && !settingsMain.getAppOpen()) {
                linearLayoutSubmitRating.setVisibility(View.VISIBLE);
                textViewRatingTitle.setText(jsonObjectRatingInfo.getString("title"));
                editTextRattingComment.setHint(jsonObjectRatingInfo.getString("textarea_text"));
                textViewRatingButton.setText(jsonObjectRatingInfo.getString("btn"));
                if (!jsonObjectRatingInfo.getBoolean("is_editable")) {
                    textViewRatingNotEdit.setVisibility(View.VISIBLE);
                    textViewRatingNotEdit.setText(jsonObjectRatingInfo.getString("tagline"));
                }
            } else {
                if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive") && !settingsMain.getUserLogin().equals("0")) {
                    textViewNoCurrentRating.setVisibility(View.VISIBLE);
                    textViewNoCurrentRating.setText(jsonObjectRatingInfo.getString("can_rate_msg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adforest_initializeListRating();
//        adforest_commentrating();
    }

    private void adforest_initializeListRating() {
        try {
            listitems.clear();
            try {
                jsonObjectPagination = jsonObjectRatingInfo.getJSONObject("pagination");
                JSONArray jsonArray = jsonObjectRatingInfo.getJSONArray("ratings");
                if (jsonArray.length() > 0) {
//                    textViewNoCurrentRating.setVisibility(View.VISIBLE);
//                    textViewNoCurrentRating.setText(jsonObjectRatingInfo.getString("title"));
//                    textViewNoCurrentRating.setVisibility(View.GONE);
                    Log.d("info rating details", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        blogCommentsModel item = new blogCommentsModel();

                        item.setComntId(jsonObject1.getString("rating_id"));
                        item.setName(jsonObject1.getString("rating_author_name"));
                        item.setMessage(jsonObject1.getString("rating_text"));
//                        item.setSetReaaction(jsonObject1.getBoolean("ad_rating_emojies"));

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

                    itemSendRecMesageAdapter = new ItemRatingListAdapter(getActivity(), listitems);

                    if (listitems.size() > 0 & ratingRecylerView != null) {
                        textViewRatingTitleTop.setText(jsonObjectRatingInfo.getString("section_title"));
                        textViewRatingTitleTop.setVisibility(View.VISIBLE);

                        ratingRecylerView.setAdapter(itemSendRecMesageAdapter);

                        itemSendRecMesageAdapter.setOnItemClickListener(new BlogCommentOnclicklinstener() {
                            @Override
                            public void onItemClick(blogCommentsModel item) {
                                if (settingsMain.getAppOpen()) {
                                    Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    adforest_ratingReplyDialog(item.getComntId());
//                                    adforest_commentrating(item.getComntId(),);
                                }
                            }
                        });
                    }
                }
                if (jsonObjectPagination.getBoolean("has_next_page")) {
                    ratingLoadLayout.setVisibility(View.VISIBLE);
                    ratingLoadMoreButton.setText(jsonObjectRatingInfo.getString("loadmore_btn"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (listitems.isEmpty()) {
                textViewRatingTitleTop.setText(jsonObjectRatingInfo.getString("no_rating_message"));
                textViewRatingTitleTop.setVisibility(View.VISIBLE);
                if (jsonObjectRatingInfo.getBoolean("can_rate") && !jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")
                        && !settingsMain.getUserLogin().equals("0")) {
                    textViewNoCurrentRating.setVisibility(View.VISIBLE);
                    textViewNoCurrentRating.setText(jsonObjectRatingInfo.getString("no_rating"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void adforest_setAllViewsText(final JSONObject data, JSONObject profileText, JSONObject buttonTexts) {


        try {


            phoneNumber = data.getString("phone");
            adAuthorId = data.getString("ad_author_id");

            textViewAdName.setText(data.getString("ad_title"));
            textViewLocation.setText(data.getString("location_top"));

            if (data.getBoolean("is_feature")) {
                featuredText.setVisibility(View.VISIBLE);
                featuredText.setText(data.getString("is_feature_text"));
                featuredText.setBackgroundColor(Color.parseColor("#E52D27"));
            } else {
                featuredText.setVisibility(View.GONE);
            }
            if (!data.getJSONObject("location").getString("lat").equals("") || !data.getJSONObject("location").getString("long").equals("")) {
                latitude = Double.parseDouble(data.getJSONObject("location").getString("lat"));
                longitude = Double.parseDouble(data.getJSONObject("location").getString("long"));
                getDirectionBtn.setVisibility(View.VISIBLE);
            } else {
                getDirectionBtn.setVisibility(View.GONE);
            }
            if (data.getJSONObject("ad_timer").getBoolean("is_show")) {
                DynamicConfig.Builder dynamicConfigBuilder = new DynamicConfig.Builder();
                JSONArray timeArray = data.getJSONObject("ad_timer").getJSONArray("timer");
                JSONObject timer_strings = data.getJSONObject("ad_timer").getJSONObject("timer_strings");
                dynamicConfigBuilder.setShowDay(true)
                        .setShowHour(true)
                        .setShowMinute(true)
                        .setShowSecond(true)
                        .setSuffixDay(timer_strings.getString("days"))
                        .setSuffixHour(timer_strings.getString("hurs"))
                        .setSuffixMinute(timer_strings.getString("mins"))
                        .setSuffixSecond(timer_strings.getString("secs"))
                        .setSuffixGravity(Gravity.BOTTOM)
                        .setSuffixTextColor(Color.WHITE)
                        .setSuffixTextSize(10)
                        .setTimeTextColor(Color.WHITE)
                        .setTimeTextSize(16);
                countDown.dynamicShow(dynamicConfigBuilder.build());
                countDown.setVisibility(View.VISIBLE);

                Log.d("Info date", AdsTimerConvert.adforest_bidTimer(timeArray) + "");
                countDown.start(AdsTimerConvert.adforest_bidTimer(timeArray));
            }

            textViewSeen.setText(data.getString("ad_view_count"));

            textViewPrice.setText(data.getJSONObject("ad_price").getString("price"));
            textViewDate.setText(data.getString("ad_date"));

            if (textViewPrice.getText().toString().equals("")) {
                textViewPrice.setVisibility(View.GONE);
            } else {
                textViewPrice.setVisibility(View.VISIBLE);
            }

            //if there is any ad video show the video and play the video
            if (!data.getJSONObject("ad_video").getString("video_id").equals("") && !settingsMain.getYoutubeApi().equals("")) {
                youtube_view.setVisibility(View.VISIBLE);
                YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
                youTubePlayerFragment.initialize(settingsMain.getYoutubeApi(), new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                        if (!wasRestored) {
                            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                            try {
//                                player.loadVideo(data.getJSONObject("ad_video").getString("video_id"));
                                player.cueVideo(data.getJSONObject("ad_video").getString("video_id"));
                                player.setFullscreen(false);
                                player.setShowFullscreenButton(false);
                                player.play();
//                                player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
////Tell the player how to control the change
//                                player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener(){
//                                    @Override
//                                    public void onFullscreen(boolean arg0) {
//// do full screen stuff here, or don't. I started a YouTubeStandalonePlayer
//// to go to full screen
//                                    }});

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }


                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                        // YouTube error
                        String errorMessage = error.toString();
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.d("errorMessage:", errorMessage);
                    }
                });
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.youtube_view, youTubePlayerFragment).commit();
            }



            if (data.getJSONObject("ad_tags_show").getString("name").equals("")) {
                htmlTextView.setHtml(data.getString("ad_desc"), new HtmlResImageGetter(htmlTextView));
            } else
                htmlTextView.setHtml(data.getString("ad_desc") +
                                "<br><br><b><font color=\"black\">" + data.getJSONObject("ad_tags_show").getString("name") + "</b> : " +
                                data.getJSONObject("ad_tags_show").getString("value")
                        , new HtmlResImageGetter(htmlTextView));

            linearLayout1.removeAllViews();
            linearLayout2.removeAllViews();
            linearLayoutOuter.removeAllViews();

            if (noOfCol == 2) {

                JSONArray jsonArray = data.getJSONArray("fieldsData");
                for (int item = 0; item < jsonArray.length(); item++) {
                    HtmlTextView htmlTextView = new HtmlTextView(getActivity());
                    LinearLayout layout = null;
                    htmlTextView.setPadding(0, 0, 0, 10);
                    String s = jsonArray.getJSONObject(item).getString("type");
                    if ("textfield_url".equals(s)) {
                        htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : " +
                                "<a href=" + jsonArray.getJSONObject(item).getString("value") + ">" + JsonObjectData.getString("click_here_text") + "</a>", new HtmlResImageGetter(htmlTextView));
                    } else if ("color_field".equals(s)) {
                        Drawable mDrawable = getResources().getDrawable(R.drawable.bg_addetail_color);
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor(jsonArray.getJSONObject(item).getString("value")), PorterDuff.Mode.SRC_IN));
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setImageDrawable(mDrawable);

                        htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : ",
                                new HtmlAssetsImageGetter(htmlTextView));

                        layout = new LinearLayout(getActivity());
                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        layout.addView(htmlTextView);
                        layout.addView(imageView);
                    } else {
                        htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : " +
                                jsonArray.getJSONObject(item).getString("value"), new HtmlResImageGetter(htmlTextView));
                    }
                    htmlTextView.setTextColor(Color.BLACK);
                    if (item % 2 == 0) {
                        if (jsonArray.getJSONObject(item).getString("type").equals("color_field") && layout != null) {
                            linearLayout1.addView(layout);
                        } else {
                            linearLayout1.addView(htmlTextView);
                        }
                    } else {
                        if (jsonArray.getJSONObject(item).getString("type").equals("color_field") && layout != null) {
                            linearLayout2.addView(layout);
                        } else {
                            linearLayout2.addView(htmlTextView);
                        }
                    }
                }
            } else {
                JSONArray jsonArray = data.getJSONArray("fieldsData");
                for (int item = 0; item < jsonArray.length(); item++) {
                    HtmlTextView htmlTextView = new HtmlTextView(getActivity());
                    LinearLayout layout = null;
                    htmlTextView.setPadding(0, 0, 0, 10);
                    switch (jsonArray.getJSONObject(item).getString("type")) {
                        case "textfield_url":
                            htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : " +
                                    "<a href=" + jsonArray.getJSONObject(item).getString("value") + ">" + JsonObjectData.getString("click_here_text") + "</a>", new HtmlResImageGetter(htmlTextView));
                            break;
                        case "color_field":

                            Drawable mDrawable = getResources().getDrawable(R.drawable.bg_addetail_color);
                            mDrawable.setColorFilter(new
                                    PorterDuffColorFilter(Color.parseColor(jsonArray.getJSONObject(item).getString("value")), PorterDuff.Mode.SRC_IN));
                            ImageView imageView = new ImageView(getActivity());
                            imageView.setImageDrawable(mDrawable);
                            layout = new LinearLayout(getActivity());
                            layout.setOrientation(LinearLayout.HORIZONTAL);
                            layout.addView(htmlTextView);
                            layout.addView(imageView);

                            htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : ",
                                    new HtmlAssetsImageGetter(htmlTextView));
                            break;
                        default:
                            htmlTextView.setHtml("<b>" + jsonArray.getJSONObject(item).getString("key") + "</b> : " +
                                    jsonArray.getJSONObject(item).getString("value"), new HtmlResImageGetter(htmlTextView));
                            break;
                    }
                    htmlTextView.setTextColor(Color.BLACK);

                    if (jsonArray.getJSONObject(item).getString("type").equals("color_field") && layout != null) {
                        linearLayoutOuter.addView(layout);
                    } else {
                        linearLayoutOuter.addView(htmlTextView);
                    }

                }
            }

            HtmlTextView htmlTextView1 = new HtmlTextView(getActivity());
            htmlTextView1.setPadding(0, 0, 0, 10);
            htmlTextView1.setHtml("<b>" + data.getJSONObject("location").getString("title") + "</b> : " +
                    data.getJSONObject("location").getString("address"), new HtmlResImageGetter(htmlTextView));
            htmlTextView1.setTextColor(Color.BLACK);
            linearLayoutOuter.addView(htmlTextView1);

            banners.clear();
            imageUrls.clear();
            bannerSlider.removeAllBanners();
            for (int i = 0; i < data.getJSONArray("images").length(); i++) {
                banners.add(new RemoteBanner(data.getJSONArray("images").getJSONObject(i).getString("full")));
                banners.get(i).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            for (int ii = 0; ii < data.getJSONArray("slider_images").length(); ii++) {
                imageUrls.add(data.getJSONArray("slider_images").get(ii).toString());
                Log.d("info slider images", data.getJSONArray("slider_images").get(ii).toString());

            }

            if (banners.size() > 0)
                bannerSlider.setBanners(banners);

            shareBtn.setText(buttonTexts.getString("share_btn"));
            addToFavBtn.setText(buttonTexts.getString("fav_btn"));
            reportBtn.setText(buttonTexts.getString("report_btn"));
            if (messageBtn != null)
                messageBtn.setText(buttonTexts.getString("send_msg_btn"));
            if (callBtn != null)
                callBtn.setText(buttonTexts.getString("call_now_btn"));
            bidBtn.setText(buttonTexts.getString("bid_now_btn"));
            bidStatisticsBtn.setText(buttonTexts.getString("bid_stats_btn"));
            getDirectionBtn.setText(buttonTexts.getString("get_direction"));
            jsonObjectBidTabs = buttonTexts.getJSONObject("bid_tabs");

            if (!buttonTexts.getBoolean("show_call_btn") || !buttonTexts.getBoolean("show_megs_btn")) {
                if (temphide != null)
                    temphide.setVisibility(View.GONE);
            } else {
                if (temphide != null)
                    temphide.setVisibility(View.VISIBLE);
            }
//            callBtn.setVisibility(buttonTexts.getBoolean("show_call_btn") ? View.VISIBLE : View.GONE);
//            messageBtn.setVisibility(buttonTexts.getBoolean("show_megs_btn") ? View.VISIBLE : View.GONE);
            if (!buttonTexts.getBoolean("show_call_btn")) {
                if (messageBtn != null)
                    showHideButtons(messageBtn);
            }
            if (!buttonTexts.getBoolean("show_megs_btn")) {
                if (callBtn != null)
                    showHideButtons(callBtn);
            }

            if (buttonTexts.getBoolean("ad_bids_enable")) {
                cardViewBidSec.setVisibility(View.VISIBLE);

                textViewTotBid.setText(buttonTexts.getJSONObject("ad_bids").getString("total_text"));
                textViewTotBidtext.setText(buttonTexts.getJSONObject("ad_bids").getString("total"));
                textViewHighBid.setText(buttonTexts.getJSONObject("ad_bids").getString("max_text"));
                textViewHighBidtext.setText(buttonTexts.getJSONObject("ad_bids").getJSONObject("max").getString("price"));
                textViewLowBid.setText(buttonTexts.getJSONObject("ad_bids").getString("min_text"));
                textViewLowBidtext.setText(buttonTexts.getJSONObject("ad_bids").getJSONObject("min").getString("price"));
            } else {
                cardViewBidSec.setVisibility(View.GONE);
            }

            textViewLastLogin.setText(profileText.getString("last_login"));

            blockUserObject = buttonTexts.getJSONObject("block_user");
            if (blockUserObject.getBoolean("is_show") && !settingsMain.getAppOpen()) {
                tv_blockUser.setVisibility(View.VISIBLE);
                tv_blockUser.setText(blockUserObject.getString("text"));
                tv_blockUser.setPaintFlags(tv_blockUser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
            textViewUserName.setText(profileText.getString("display_name"));
            verifyBtn.setText(profileText.getJSONObject("verify_buton").getString("text"));
            textViewRateNo.setText(profileText.getJSONObject("rate_bar").getString("text"));

            verifyBtn.setBackground(CustomBorderDrawable.customButton(5, 5, 5, 5,
                    profileText.getJSONObject("verify_buton").getString("color"),
                    profileText.getJSONObject("verify_buton").getString("color"),
                    profileText.getJSONObject("verify_buton").getString("color"), 0));

            ratingBar.setNumStars(5);
            ratingBar.setRating(Float.parseFloat(profileText.getJSONObject("rate_bar").getString("number")));
            textViewRateNo.setText(profileText.getJSONObject("rate_bar").getString("text"));
            Picasso.with(getContext()).load(profileText.getString("profile_img"))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageViewProfile);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        list.clear();

        try {
            data.getJSONArray("related_ads");

            textViewRelated.setText(buttonTexts.getString("related_posts_title"));
            textViewDescript.setText(buttonTexts.getString("description_title"));

            Log.d("Related Ads array", "" + data.getJSONArray("related_ads").toString());

            for (int i = 0; i < data.getJSONArray("related_ads").length(); i++) {
                catSubCatlistModel item = new catSubCatlistModel();

                item.setId(data.getJSONArray("related_ads").getJSONObject(i).getString("ad_id"));
                item.setCardName(data.getJSONArray("related_ads").getJSONObject(i).getString("ad_title"));
                item.setDate(data.getJSONArray("related_ads").getJSONObject(i).getString("ad_date"));
                item.setPrice(data.getJSONArray("related_ads").getJSONObject(i).getJSONObject("ad_price").getString("price"));
                item.setLocation(data.getJSONArray("related_ads").getJSONObject(i).getJSONObject("ad_location").getString("address"));
                item.setImageResourceId(data.getJSONArray("related_ads").getJSONObject(i).getJSONArray("ad_images").getJSONObject(0).getString("thumb"));

                item.setIs_show_countDown(data.getJSONArray("related_ads").getJSONObject(i).getJSONObject("ad_timer").getBoolean("is_show"));
                if (data.getJSONArray("related_ads").getJSONObject(i).getJSONObject("ad_timer").getBoolean("is_show"))
                    item.setTimer_array(data.getJSONArray("related_ads").getJSONObject(i).getJSONObject("ad_timer").getJSONArray("timer"));

                Log.d("Related ads Image", "" + data.getJSONArray("related_ads").getJSONObject(i).getJSONArray("ad_images").getJSONObject(0).getString("thumb"));
                list.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void showHideButtons(TextView btnShowHide) {
        if (btnShowHide.getParent() != null) {
            ((ViewGroup) btnShowHide.getParent()).removeView(btnShowHide);
            linearLayout.removeAllViewsInLayout();
        }
        linearLayout.addView(btnShowHide);
    }

    void adforest_showDilogMessage() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        final EditText message = dialog.findViewById(R.id.editText3);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        try {
            Send.setText(jsonObjectSendMessage.getString("btn_send"));
            message.setHint(jsonObjectSendMessage.getString("input_textarea"));
            Cancel.setText(jsonObjectSendMessage.getString("btn_cancel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setOnClickListener(v -> {

            if (!message.getText().toString().isEmpty()) {
                adforest_sendMessage(message.getText().toString());
                dialog.dismiss();
            } else
                message.setError("");
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("ResourceAsColor")
    void adforest_showDilogCall() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_call);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Log.d("info call object", jsonObjectCallNow.toString() + phoneNumber);
        final TextView textViewCallNo = dialog.findViewById(R.id.textView2);
        final TextView verifiedOrNotText = dialog.findViewById(R.id.verifiedOrNotText);
        try {
            if (!jsonObjectCallNow.getBoolean("is_phone_verified") && phoneNumber.contains("+")) {
                phoneNumber = phoneNumber.replace("+", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textViewCallNo.setText(phoneNumber);

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);
        try {
            if (jsonObjectCallNow.getBoolean("phone_verification")) {
                verifiedOrNotText.setVisibility(View.VISIBLE);
                if (jsonObjectCallNow.getBoolean("is_phone_verified")) {
//                    Toast.makeText(getActivity(), "sadsadsa" + jsonObjectCallNow.getBoolean("is_phone_verified"), Toast.LENGTH_LONG).show();
                    verifiedOrNotText.setText(jsonObjectCallNow.getString("is_phone_verified_text"));
                    verifiedOrNotText.setBackgroundResource(R.drawable.ic_verified_green_logo);
                } else {
                    verifiedOrNotText.setText(jsonObjectCallNow.getString("is_phone_verified_text"));
                    verifiedOrNotText.setBackgroundResource(R.drawable.ic_oncall_red_logo);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        try {
            Send.setText(jsonObjectCallNow.getString("btn_send"));
            Cancel.setText(jsonObjectCallNow.getString("btn_cancel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Send.setOnClickListener(v -> runtimePermissionHelper.requestCallPermission(1));

        Cancel.setOnClickListener(v -> dialog.dismiss());
        callDialog = dialog;
        dialog.show();
    }

    void adforest_showDilogReport() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_report);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        final Spinner spinner = dialog.findViewById(R.id.spinner);
        final EditText editText = dialog.findViewById(R.id.editText3);

        item = new myAdsModel();

        try {
            Send.setText(jsonObjectReport.getString("btn_send"));
            editText.setHint(jsonObjectReport.getString("input_textarea"));
            Cancel.setText(jsonObjectReport.getString("btn_cancel"));

            item.setSpinerValue(jsonObjectReport.getJSONObject("select").getJSONArray("name"));
            item.setSpinerData(jsonObjectReport.getJSONObject("select").getJSONArray("value"));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, item.getSpinerData());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                adforest_sendReport(item.getSpinerValue().get(spinner.getSelectedItemPosition()), editText.getText().toString());
                dialog.dismiss();
            } else {
                editText.setError("");
            }

        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    void adforest_sendReport(String type, String message) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            params.addProperty("option", type);
            params.addProperty("comments", message);
            Log.d("info sendReport Status", params.toString());

            Call<ResponseBody> myCall = restService.postSendReport(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SendReport Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info SendReport Respon", "" + response.toString());

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
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info SendReport error", String.valueOf(t));
                    Log.d("info SendReport error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_addToFavourite() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            Log.d("info sendFavourite", myId);
            Call<ResponseBody> myCall = restService.postAddToFavourite(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdToFav Respon", "" + responseObj.toString());

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
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdToFav error", String.valueOf(t));
                    Log.d("info AdToFav error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_makeFeature() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            Log.d("info makeFeature", myId);
            Call<ResponseBody> myCall = restService.postMakeFeatured(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info makeFeature Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                SettingsMain.hideDilog();
                                adforest_recreateAdDetail();

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

    void adforest_sendMessage(String msg) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            params.addProperty("message", msg);
            Log.d("info sendMeassage", myId);

            Call<ResponseBody> myCall = restService.postSendMessageFromAd(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info sendMeassage Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
//                                MessagesFragment messagesFragment= new MessagesFragment();
//                                replace(messagesFragment,"MessagesFragment");
                                Intent intent = new Intent(getActivity(), Message.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);

                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info sendMeassage error", String.valueOf(t));
                    Log.d("info sendMeassage error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_ratingReplyDialog(final String comntId) {
        String text = null, sendBtn = null, cancelBtn = null;
        try {
            JSONObject dialogObject = jsonObjectRatingInfo.getJSONObject("rply_dialog");
            text = dialogObject.getString("text");
            sendBtn = dialogObject.getString("send_btn");
            cancelBtn = dialogObject.getString("cancel_btn");

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

        Send.setOnClickListener(v -> {
            if (!message.getText().toString().isEmpty()) {
                JsonObject params = new JsonObject();
                params.addProperty("ad_id", myId);
                params.addProperty("comment_id", comntId);
                params.addProperty("rating_comments", message.getText().toString());
                adforest_postRating(params);
                dialog.dismiss();
            }
            if (message.getText().toString().isEmpty())
                message.setError("");
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
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
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                SettingsMain.hideDilog();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            SettingsMain.hideDilog();
                            adforest_recreateAdDetail();
                            simpleRatingBar.setRating(0);
                            editTextRattingComment.setText("");
                        }
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
                    Log.d("info makeFeature error", String.valueOf(t));
                    Log.d("info makeFeature error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_blockUserDialog() {
        try {

            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
            alert.setTitle(blockUserObject.getString("popup_title"));
            alert.setCancelable(false);
            alert.setMessage(blockUserObject.getString("popup_text"));
            alert.setPositiveButton(blockUserObject.getString("popup_confirm"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    adforest_blockUser();
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton(blockUserObject.getString("popup_cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void adforest_blockUser() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", adAuthorId);
            Log.d("info Send terms id =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postBlockUser(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info terms responce ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                HomeActivity.activity.recreate();

                            } else {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                        Log.d("info CustomPages ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info CustomPages err", String.valueOf(t));
                        Log.d("info CustomPages err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {

        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Rating");
            super.onResume();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    //go to public profile fragment when click userProfile image or name
    public void goToPublicProfile() {
        FragmentPublic_Profile fragment = new FragmentPublic_Profile();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", adAuthorId);
        bundle.putString("requestFrom", "");
        fragment.setArguments(bundle);
        replaceFragment(fragment, "FragmentPublic_Profile");
    }

    public void adforest_recreateAdDetail() {
        FragmentAdDetail fragmentAdDetail = new FragmentAdDetail();
        Bundle bundle = new Bundle();
        bundle.putString("id", myId);
        fragmentAdDetail.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, fragmentAdDetail);
        transaction.commit();
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
    public void replace(Fragment someFragment, String tag) {

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

        if (fragment != fragment2) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
            transaction.replace(R.id.frameContainer, someFragment, tag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void adforestCall() {
        if (callDialog != null) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
            callDialog.dismiss();
        }
    }

    @Override
    public void onSuccessPermission(int code) {

        adforestCall();
    }


}
