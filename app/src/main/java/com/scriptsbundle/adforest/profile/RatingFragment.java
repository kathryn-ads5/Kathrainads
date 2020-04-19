package com.kathryn.kathryn.profile;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

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
import com.kathryn.kathryn.adapters.ItemRatingListAdapter;
import com.kathryn.kathryn.adapters.ItemRatingListUserAdapter;
import com.kathryn.kathryn.helper.BlogCommentOnclicklinstener;
import com.kathryn.kathryn.modelsList.blogCommentsModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class RatingFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<blogCommentsModel> listitems = new ArrayList<>();

    SettingsMain settingsMain;
    ItemRatingListAdapter itemSendRecMesageAdapter;
    ItemRatingListUserAdapter itemRatingListUserAdapter;
    EditText bidComment;
    String adId;
    TextView bidBtn, bidInfo, textViewHeading;

    TextView textViewEmptyList;
    SimpleRatingBar ratingBar;

    String replyId, mesage;
    boolean isProfile = false;
    LinearLayout linearLayoutHide;
    RestService restService;
//RelativeLayout ratingReaction;
    public RatingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating_list, container, false);

        Bundle bundle = getArguments();
        adId = bundle.getString("id");
        isProfile = bundle.getBoolean("isprofile");
//            ratingReaction=view.findViewById(R.id.reactions);
        settingsMain = new SettingsMain(getActivity());

        textViewHeading = view.findViewById(R.id.textView15);
        bidBtn = view.findViewById(R.id.bidBtn);
        bidInfo = view.findViewById(R.id.textView14);
        ratingBar = view.findViewById(R.id.ratingBar2);
        bidComment = view.findViewById(R.id.editText3);

        bidBtn.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        textViewEmptyList = view.findViewById(R.id.textView16);

        linearLayoutHide = view.findViewById(R.id.linhide);
        if (isProfile) {
            linearLayoutHide.setVisibility(View.GONE);
        }
        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        adforest_getData();
        bidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bidComment.getText().toString().isEmpty()) {
                    adforest_postBid(false);
                }
                if (bidComment.getText().toString().isEmpty()) {
                    bidComment.setError("");
                }
            }
        });

        return view;
    }

    private void adforest_postBid(boolean b) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            if (b) {
                params.addProperty("author_id", replyId);
                params.addProperty("ratting", "");
                params.addProperty("comments", mesage);
                params.addProperty("is_reply", true);
            } else {
                params.addProperty("author_id", adId);
                params.addProperty("ratting", ratingBar.getRating());
                params.addProperty("comments", bidComment.getText().toString());
                params.addProperty("is_reply", false);
            }


            Log.d("info sendPost Rating", params.toString());

            Call<ResponseBody> myCall = restService.postProfileRating(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Rating Details Res", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info postRating object", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                adforest_initializeList(response.getJSONObject("data").getJSONArray("rattings"));
//                                ratingReaction.setVisibility(View.GONE);
                                ratingBar.setRating(0);
                                bidComment.setText("");

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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info postRating ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Post Rating err", String.valueOf(t));
                        Log.d("info Post Rating err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }



/*
    private void adforest_loadMore(int nextPag) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JSONObject params = new JSONObject();

            try {
                params.put("page_number", nextPag);

                Log.d("info data object", "" + params.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            UrlController.adforest_get(getActivity(), settingsMain.getUserEmail(), settingsMain.getUserPassword(), UrlController.URL_FOR_PROFILE_Rating, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray

                    if (response.length() > 0) {

                        try {
                            if (response.getBoolean("success")) {
                                Log.d("info data object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                JSONArray timeline = response.getJSONObject("data").getJSONArray("bids");

                                for (int i = 0; i < timeline.length(); i++) {

                                    bidModel item = new bidModel();
                                    JSONObject firstEvent;
                                    try {
                                        firstEvent = (JSONObject) timeline.get(i);
                                        if (firstEvent != null) {

                                            item.setBidId(firstEvent.getString("post_id"));
                                            item.setBidDate(firstEvent.getString("title"));
                                            item.setBidImage(firstEvent.getString("comments"));
                                            item.setBidMessage(firstEvent.getString("date"));
                                            item.setBidPhoneNumber(firstEvent.getString("read_more"));
                                            item.setBidPrice(firstEvent.getString("image"));
                                            item.setBidUserNmae(firstEvent.getString("has_image"));

                                            listitems.add(item);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                loading = true;
                                itemSendRecMesageAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            SettingsMain.hideDilog();
                        }
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable
                        throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    SettingsMain.hideDilog();
                    Log.d("info", "" + errorResponse + throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    SettingsMain.hideDilog();

                    Log.d("info", "" + responseString + throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    SettingsMain.hideDilog();
                    Log.d("info", "" + errorResponse + throwable);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    SettingsMain.hideDilog();
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    SettingsMain.hideDilog();
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }
*/

    private void adforest_getData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("author_id", adId);

            Log.d("info send Rating", params.toString());

            Call<ResponseBody> myCall = restService.postGetRatingDetails(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Rating Details Res", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info Rating Details obj", "" + response.getJSONObject("data"));

                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));

                                textViewHeading.setText(response.getJSONObject("data").getJSONObject("form").getString("title"));

                                bidComment.setHint(response.getJSONObject("data").getJSONObject("form").getString("textarea_text"));
                                bidBtn.setText(response.getJSONObject("data").getJSONObject("form").getString("btn"));
                                bidInfo.setText(response.getJSONObject("data").getJSONObject("form").getString("tagline"));

                                linearLayoutHide.setVisibility(response.getJSONObject("data").getBoolean("can_rate") ? View.VISIBLE : View.GONE);

                                textViewEmptyList.setText(response.getString("message"));
                                adforest_initializeList(response.getJSONObject("data").getJSONArray("rattings"));

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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Rating Details ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Rating Details err", String.valueOf(t));
                        Log.d("info Rating Details err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_initializeList(JSONArray jsonArray) {
        listitems.clear();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                blogCommentsModel item = new blogCommentsModel();
                item.setSetReaaction(false);
                item.setComntId(jsonObject1.getString("reply_id"));
                item.setName(jsonObject1.getString("name"));
                item.setMessage(jsonObject1.getString("comments"));
                item.setRating(jsonObject1.getString("stars"));
                item.setDate(jsonObject1.getString("date"));
                item.setImage(jsonObject1.getString("img"));
                item.setReply(jsonObject1.getString("reply_txt"));
                item.setCanReply(jsonObject1.getBoolean("can_reply"));
                item.setHasReplyList(jsonObject1.getBoolean("has_reply"));


                if (jsonObject1.getBoolean("has_reply")) {

                    ArrayList<blogCommentsModel> listitemsiner = new ArrayList<>();

                    JSONObject jsonObject11 = jsonObject1.getJSONObject("reply");

                    blogCommentsModel item11 = new blogCommentsModel();

                    item11.setName(jsonObject11.getString("name"));
                    item11.setMessage(jsonObject11.getString("comments"));
                    item11.setRating(jsonObject11.getString("stars"));
                    item11.setDate(jsonObject11.getString("date"));
                    item11.setImage(jsonObject11.getString("img"));
                    item11.setReply(jsonObject11.getString("reply_txt"));
                    item11.setCanReply(jsonObject11.getBoolean("can_reply"));

                    listitemsiner.add(item11);

                    item.setListitemsiner(listitemsiner);
                }

                listitems.add(item);
            }

            itemRatingListUserAdapter = new ItemRatingListUserAdapter(getActivity(), listitems);

            if (listitems.size() > 0 & recyclerView != null) {
                recyclerView.setAdapter(itemRatingListUserAdapter);

                itemRatingListUserAdapter.setOnItemClickListener(new BlogCommentOnclicklinstener() {
                    @Override
                    public void onItemClick(blogCommentsModel item) {
                        adforest_showDilogMessage(item.getComntId());
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listitems.isEmpty()) {
            textViewEmptyList.setVisibility(View.VISIBLE);
        } else {
            textViewEmptyList.setVisibility(View.GONE);
        }
    }

    void adforest_showDilogMessage(final String comntId) {
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
        message.setHint(bidComment.getHint());
        Cancel.setText(settingsMain.getAlertCancelText());
        Send.setText(settingsMain.getAlertOkText());

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals("")) {
                    replyId = comntId;
                    mesage = message.getText().toString();
                    adforest_postBid(true);
                    message.setText("");
                    dialog.dismiss();
                }
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
}
