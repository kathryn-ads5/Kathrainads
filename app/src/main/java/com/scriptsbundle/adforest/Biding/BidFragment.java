package com.kathryn.kathryn.Biding;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Biding.adapter.ItemBidAdapter;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.FragmentAdDetail;
import com.kathryn.kathryn.modelsList.bidModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class BidFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<bidModel> listitems = new ArrayList<>();

    SettingsMain settingsMain;
    ItemBidAdapter itemSendRecMesageAdapter;
    EditText bidAmount, bidComment;
    String adId;
    TextView bidBtn, bidInfo, textViewNoBidInfo;
    LinearLayout linearLayoutBidForm;
    RestService restService;

    public BidFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bid_list, container, false);

        Bundle bundle = getArguments();
        adId = FragmentAdDetail.myId;

        LinearLayout linearLayout = getActivity().findViewById(R.id.ll11);
        linearLayout.setVisibility(View.GONE);
        settingsMain = new SettingsMain(getActivity());

        bidBtn = view.findViewById(R.id.bidBtn);
        bidInfo = view.findViewById(R.id.textView14);
        bidAmount = view.findViewById(R.id.editText4);
        bidComment = view.findViewById(R.id.editText3);

        bidBtn.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        textViewNoBidInfo = view.findViewById(R.id.textView18);
        textViewNoBidInfo.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        linearLayoutBidForm = view.findViewById(R.id.linearLayout);
        linearLayoutBidForm.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(MyLayoutManager);

        adforest_getData();

        bidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bidAmount.getText().toString().isEmpty() || !bidComment.getText().toString().isEmpty()) {
                    adforest_postBid();
                }
                if (bidComment.getText().toString().isEmpty()) {
                    bidComment.setError("");
                }
                if (bidAmount.getText().toString().isEmpty()) {
                    bidAmount.setError("");
                }
            }
        });
        return view;
    }

    private void adforest_postBid() {


        if (SettingsMain.isConnectingToInternet(getActivity())) {
            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adId);
            params.addProperty("bid_amount", bidAmount.getText().toString());
            params.addProperty("bid_comment", bidComment.getText().toString());

            Log.d("info BidPost", "" + params.toString());

            Call<ResponseBody> myCall = restService.postBid(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info BidPost Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info BidPost object", "" + response.getJSONObject("data"));
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                adforest_initializeList(response.getJSONObject("data").getJSONArray("bids"));

                                bidAmount.setText("");
                                bidComment.setText("");
//                                if (response.getBoolean("can_bid")) {
//                                    if (response.getJSONObject("data").getJSONArray("bids").length() == 0) {
//                                        textViewNoBidInfo.setText(response.getString("message"));
//                                        textViewNoBidInfo.setVisibility(View.VISIBLE);
//                                    } else {
//                                        textViewNoBidInfo.setVisibility(View.GONE);
//                                    }
//                                    linearLayoutBidForm.setVisibility(View.VISIBLE);
//
//                                } else {
//                                    textViewNoBidInfo.setVisibility(View.VISIBLE);
//                                    textViewNoBidInfo.setText(response.getString("message"));
//                                    linearLayoutBidForm.setVisibility(View.GONE);
//                                }

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
                    Log.d("info BidPost error", String.valueOf(t));
                    Log.d("info BidPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
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

            UrlController.adforest_get(getActivity(), settingsMain.getUserEmail(), settingsMain.getUserPassword(), UrlController.URL_FOR_AD_Bids, params, new JsonHttpResponseHandler() {
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

            Log.d("ad_id", adId);
            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adId);

            //post Type Mehtod for get Bid Details
            Log.d("info BidDetails", "" + params.toString());
            Call<ResponseBody> myCall = restService.getBidDetails(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info BidDetails Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info BidDetails Data", "" + response.getJSONObject("data"));

                                getActivity().setTitle(response.getJSONObject("data").getString("page_title"));

                                bidAmount.setHint(response.getJSONObject("data").getJSONObject("form").getString("bid_amount"));
                                bidComment.setHint(response.getJSONObject("data").getJSONObject("form").getString("bid_textarea"));
                                bidBtn.setText(response.getJSONObject("data").getJSONObject("form").getString("bid_btn"));
                                bidInfo.setText(response.getJSONObject("data").getJSONObject("form").getString("bid_info"));

                                if (response.getBoolean("can_bid")) {
                                    if (response.getJSONObject("data").getJSONArray("bids").length() == 0) {
                                        textViewNoBidInfo.setText(response.getString("message"));
                                        textViewNoBidInfo.setVisibility(View.VISIBLE);
                                    } else {
                                        textViewNoBidInfo.setVisibility(View.GONE);
                                    }
                                    if (!settingsMain.getAppOpen())
                                        linearLayoutBidForm.setVisibility(View.VISIBLE);
                                } else {
                                    textViewNoBidInfo.setVisibility(View.VISIBLE);
                                    textViewNoBidInfo.setText(response.getString("message"));
                                    linearLayoutBidForm.setVisibility(View.GONE);
                                }

                                adforest_initializeList(response.getJSONObject("data").getJSONArray("bids"));

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
                    Log.d("info SignUp error", String.valueOf(t));
                    Log.d("info SignUp error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_initializeList(JSONArray timeline) {
        listitems.clear();

        for (int i = 0; i < timeline.length(); i++) {

            bidModel item = new bidModel();
            JSONObject firstEvent;
            try {
                firstEvent = (JSONObject) timeline.get(i);
                if (firstEvent != null) {

                    item.setBidDate(firstEvent.getString("date"));
                    item.setBidImage(firstEvent.getString("profile"));
                    item.setBidMessage(firstEvent.getString("comment"));
                    item.setBidPhoneNumber(firstEvent.getString("phone"));
                    item.setBidPrice(firstEvent.getJSONObject("price").getString("price"));
                    item.setBidUserNmae(firstEvent.getString("name"));

                    listitems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        itemSendRecMesageAdapter = new ItemBidAdapter(getActivity(), listitems);

        if (listitems.size() > 0 & recyclerView != null) {
            recyclerView.setAdapter(itemSendRecMesageAdapter);
        }


    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Bid");

            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
