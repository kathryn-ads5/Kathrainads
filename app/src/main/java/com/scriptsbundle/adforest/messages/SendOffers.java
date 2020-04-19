package com.kathryn.kathryn.messages;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.kathryn.kathryn.helper.SendReciveONClickListner;
import com.kathryn.kathryn.messages.adapter.ItemSendRecMesageAdapter;
import com.kathryn.kathryn.modelsList.messageSentRecivModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendOffers extends Fragment {
    RecyclerView recyclerView;
    SettingsMain settingsMain;

    ArrayList<messageSentRecivModel> listitems = new ArrayList<>();
    TabLayout tabLayout;

    int currentPage = 1, nextPage = 1, totalPage = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = true, hasNextPage = false;
    ItemSendRecMesageAdapter itemSendRecMesageAdapter;
    ProgressBar progressBar;
    RestService restService;

    public SendOffers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_offers, container, false);

        tabLayout = getActivity().findViewById(R.id.tabs);
        settingsMain = new SettingsMain(getActivity());

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = MyLayoutManager.getChildCount();
                    totalItemCount = MyLayoutManager.getItemCount();
                    pastVisiblesItems = MyLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if (hasNextPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                adforest_loadMore(nextPage);
                            }
                        }
                    }
                }
            }
        });


        adforest_getAllData();
        return view;
    }

    private void adforest_getAllData() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Call<ResponseBody> myCall = restService.getSendOffers(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SendOffers Responc", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info SendOffers object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                adforest_initializeList(response.getJSONObject("data"));

                                itemSendRecMesageAdapter = new ItemSendRecMesageAdapter(getActivity(), listitems);
                                if (listitems.size() > 0 & recyclerView != null) {
                                    recyclerView.setAdapter(itemSendRecMesageAdapter);

                                    itemSendRecMesageAdapter.setOnItemClickListener(new SendReciveONClickListner() {
                                        @Override
                                        public void onItemClick(messageSentRecivModel item) {
                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                                            intent.putExtra("adId", item.getId());
                                            intent.putExtra("senderId", item.getSender_id());
                                            intent.putExtra("recieverId", item.getReceiver_id());
                                            intent.putExtra("is_block",item.getIsBlock());
                                            intent.putExtra("type", item.getType());
                                            startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                        }
                                    });
                                }
                            } else {
                                if (response.getJSONObject("data").getBoolean("is_redirect")) {
                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    settingsMain.setUserVerified(false);
                                    getActivity().finish();
                                } else
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
                        Log.d("info SendOffers Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info SendOffers error", String.valueOf(t));
                        Log.d("info SendOffers error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }

            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loadMore(int nextPag) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            params.addProperty("page_number", nextPag);

            Log.d("info sendOffers More", params.toString());
            Call<ResponseBody> myCall = restService.postLoadMoreSendOffers(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info sendOffers More", "Responce" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info sendOffer More obj", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                currentPage = jsonObjectPagination.getInt("current_page");
                                totalPage = jsonObjectPagination.getInt("max_num_pages");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
                                JSONArray jsonArrayMessage = response.getJSONObject("data").getJSONObject("sent_offers").getJSONArray("items");
                                for (int i = 0; i < jsonArrayMessage.length(); i++) {

                                    messageSentRecivModel item = new messageSentRecivModel();

                                    item.setId(jsonArrayMessage.getJSONObject(i).getString("ad_id"));
                                    item.setType("sent");
                                    item.setName(jsonArrayMessage.getJSONObject(i).getString("message_ad_title"));
                                    item.setSender_id(jsonArrayMessage.getJSONObject(i).getString("message_sender_id"));
                                    item.setReceiver_id(jsonArrayMessage.getJSONObject(i).getString("message_receiver_id"));
                                    item.setIsBlock(jsonArrayMessage.getJSONObject(i).getString("is_block"));
//                                    item.setTopic(jsonArrayMessage.getJSONObject(i).getString("message_author_name"));
                                    item.setMessageRead(jsonArrayMessage.getJSONObject(i).getBoolean("message_read_status"));
                                    item.setTumbnail(jsonArrayMessage.getJSONObject(i).getJSONArray("message_ad_img")
                                            .getJSONObject(0).getString("thumb"));

                                    listitems.add(item);

                                }
                                loading = true;
//                                recyclerView.setAdapter(itemSendRecMesageAdapter);
                                try {
                                    itemSendRecMesageAdapter.notifyDataSetChanged();

                                } catch (NullPointerException e) {
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
                    progressBar.setVisibility(View.GONE);
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
                        Log.d("info sendOffers List", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info sendOffer More err", String.valueOf(t));
                        Log.d("info sendOffer More err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
//            UrlController.adforest_post(getActivity(), settingsMain.getUserEmail(), settingsMain.getUserPassword(), UrlController.URL_FOR_GET_Messages_POST, params, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    // If the response is JSONObject instead of expected JSONArray
//
//                    if (response.length() > 0) {
//
//                        try {
//                            if (response.getBoolean("success")) {
//                                Log.d("info data object", "" + response.getJSONObject("data"));
//
//                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");
//
//                                nextPage = jsonObjectPagination.getInt("next_page");
//                                currentPage = jsonObjectPagination.getInt("current_page");
//                                totalPage = jsonObjectPagination.getInt("max_num_pages");
//                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
//                                JSONArray jsonArrayMessage = response.getJSONObject("data").getJSONObject("sent_offers").getJSONArray("items");
//                                for (int i = 0; i < jsonArrayMessage.length(); i++) {
//
//                                    messageSentRecivModel item = new messageSentRecivModel();
//
//                                    item.setId(jsonArrayMessage.getJSONObject(i).getString("ad_id"));
//                                    item.setType("sent");
//                                    item.setName(jsonArrayMessage.getJSONObject(i).getString("message_ad_title"));
//                                    item.setSender_id(jsonArrayMessage.getJSONObject(i).getString("message_sender_id"));
//                                    item.setReceiver_id(jsonArrayMessage.getJSONObject(i).getString("message_receiver_id"));
////                                    item.setTopic(jsonArrayMessage.getJSONObject(i).getString("message_author_name"));
//                                    item.setTumbnail(jsonArrayMessage.getJSONObject(i).getJSONArray("message_ad_img")
//                                            .getJSONObject(0).getString("thumb"));
//
//                                    listitems.add(item);
//
//                                }
//                                loading = true;
////                                recyclerView.setAdapter(itemSendRecMesageAdapter);
//                                try {
//                                    itemSendRecMesageAdapter.notifyDataSetChanged();
//
//                                }
//                                catch (NullPointerException e )
//                                {
//                                    e.printStackTrace();
//                                }
//
//                            } else {
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            SettingsMain.hideDilog();
//                        }
//                    }
//                    SettingsMain.hideDilog();
//
//                    progressBar.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable
//                        throwable, JSONObject errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                    SettingsMain.hideDilog();
//                    Log.d("info", "" + errorResponse + throwable);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    super.onFailure(statusCode, headers, responseString, throwable);
//                    SettingsMain.hideDilog();
//
//                    Log.d("info", "" + responseString + throwable);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                    super.onFailure(statusCode, headers, throwable, errorResponse);
//                    SettingsMain.hideDilog();
//                    Log.d("info", "" + errorResponse + throwable);
//                }
//
//                @Override
//                public void onFinish() {
//                    super.onFinish();
//                    SettingsMain.hideDilog();
//                }
//
//                @Override
//                public void onCancel() {
//                    super.onCancel();
//                    SettingsMain.hideDilog();
//                }
//            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_initializeList(JSONObject jsonObjectData) {

        try {
            tabLayout.getTabAt(0).setText(jsonObjectData.getJSONObject("title").getString("sent"));
            tabLayout.getTabAt(1).setText(jsonObjectData.getJSONObject("title").getString("receive"));
            tabLayout.getTabAt(2).setText(jsonObjectData.getJSONObject("title").getString("blocked"));
            getActivity().setTitle(jsonObjectData.getJSONObject("title").getString("main"));

            listitems.clear();

            JSONArray jsonArrayMessage = jsonObjectData.getJSONObject("sent_offers").getJSONArray("items");
            for (int i = 0; i < jsonArrayMessage.length(); i++) {

                messageSentRecivModel item = new messageSentRecivModel();

                item.setId(jsonArrayMessage.getJSONObject(i).getString("ad_id"));
                item.setName(jsonArrayMessage.getJSONObject(i).getString("message_ad_title"));
                item.setTopic(jsonArrayMessage.getJSONObject(i).getString("message_author_name"));
                item.setType("sent");
                item.setSender_id(jsonArrayMessage.getJSONObject(i).getString("message_sender_id"));
                item.setReceiver_id(jsonArrayMessage.getJSONObject(i).getString("message_receiver_id"));
                item.setIsBlock(jsonArrayMessage.getJSONObject(i).getString("is_block"));
                item.setMessageRead(jsonArrayMessage.getJSONObject(i).getBoolean("message_read_status"));
                item.setTumbnail(jsonArrayMessage.getJSONObject(i).getJSONArray("message_ad_img")
                        .getJSONObject(0).getString("thumb"));

                listitems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
