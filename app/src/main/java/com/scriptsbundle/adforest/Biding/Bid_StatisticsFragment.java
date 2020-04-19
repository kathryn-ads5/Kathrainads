package com.kathryn.kathryn.Biding;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kathryn.kathryn.Biding.adapter.ItemBidStatisticsAdapter;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.FragmentAdDetail;
import com.kathryn.kathryn.modelsList.bidStatisticsModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class Bid_StatisticsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<bidStatisticsModel> bidStatisticsList = new ArrayList<>();

    SettingsMain settingsMain;
    ItemBidStatisticsAdapter itemBidStatisticsAdapter;
    RestService restService;
    String adId;
    TextView textViewEmptyData,textviewmessage;

    public Bid_StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bid_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);


        textViewEmptyData = view.findViewById(R.id.textView5);
        settingsMain = new SettingsMain(getContext());
        adId = FragmentAdDetail.myId;

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        adforest_getData();
    }

    private void adforest_getData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

//            SettingsMain.showDilog(getActivity());

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

                                if (response.getJSONObject("data").getJSONArray("top_bidders").length() > 0) {
                                    adforest_initializeList(response.getJSONObject("data").getJSONArray("top_bidders"));
                                } else {
                                    textViewEmptyData.setVisibility(View.VISIBLE);
                                    textViewEmptyData.setText(response.getJSONObject("data").get("no_top_bidders").toString());
                                }
                            }else {
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

    public void adforest_initializeList(JSONArray jsonArray) {
        bidStatisticsList.clear();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                bidStatisticsModel item = new bidStatisticsModel();
                item.setUserName(jsonObject.getString("name"));
                item.setPostedText(jsonObject.getString("offer_by"));
                item.setProfileImage(jsonObject.getString("profile"));
                item.setDate(jsonObject.getString("date"));
                item.setPrice(jsonObject.getString("price"));
                item.setCount("" + (i + 1));

                bidStatisticsList.add(item);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        itemBidStatisticsAdapter = new ItemBidStatisticsAdapter(getActivity(), bidStatisticsList);
        recyclerView.setAdapter(itemBidStatisticsAdapter);
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Bid Statistics");

            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
