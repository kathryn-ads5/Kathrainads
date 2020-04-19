package com.kathryn.kathryn.messages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.kathryn.kathryn.home.AddNewAdPost;
import com.kathryn.kathryn.modelsList.blockUserModel;
import com.kathryn.kathryn.userAndSellers.adapter.ItemBlockUserAdapter;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;


public class Block_Message_Fragment extends Fragment {
    RecyclerView recyclerView;
    RestService restService;
    SettingsMain settingsMain;
    ItemBlockUserAdapter itemBlockUserAdapter;
    String senderId, recieverId;
    JSONArray Blocklistempty;
    LinearLayout linearEmptyMessage;
    TextView textView;
    private ArrayList<blockUserModel> blockUserModelArrayList = new ArrayList<>();


    public Block_Message_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_block__message_, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            senderId = bundle.getString("senderId", "0");
            recieverId = bundle.getString("recieverId", "0");


        }
        super.onViewCreated(view, savedInstanceState);
        settingsMain = new SettingsMain(getActivity());
        linearEmptyMessage = view.findViewById(R.id.linearEmptyMessage);
        textView = view.findViewById(R.id.txtMessage);
        recyclerView = view.findViewById(R.id.blockedUserRecylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        final GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        adforest_getBlockedUser();
    }

    private void adforest_getBlockedUser() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());


            //post Type Mehtod for get Bid Details
            Call<ResponseBody> myCall = restService.getMessageBlockedUsers(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info blockUser Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Blocklistempty = response.getJSONArray("data");
                                if (Blocklistempty != null && Blocklistempty.length() > 0) {
                                    Log.d("success", response.toString());
                                    response.getJSONArray("data");
                                    Log.d("info blockUser Data", "" + response.getJSONArray("data"));
//                             JSONObject jsonObject=null;
//                                jsonObject=response.getJSONObject("user_id");
////                                jsonObject=response.getJSONObject("user_name");
////                                jsonObject=response.getJSONObject("user_img");
                                    adforest_initializeList(response.getJSONArray("data"));
                                } else {
//                                    AlertDialog.Builder alert1 = new AlertDialog.Builder(getActivity());
//                                    alert1.setTitle(settingsMain.getAlertDialogTitle("info"));
//                                    alert1.setCancelable(false);
//                                    alert1.setMessage(response.get("message").toString());
//                                    alert1.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
//
//                                        dialog.dismiss();
//                                    });
//                                    alert1.show();
//                                    return;
                                    linearEmptyMessage.setVisibility(View.VISIBLE);
                                    textView.setText(response.get("message").toString());
//                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                }
                            }
//                            else{
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
//
//                            }
//                            else if( response.getJSONArray("data")==null)
//                            {
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
//
//                            }
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info blockUser Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info blockUser error", String.valueOf(t));
                        Log.d("info blockUser error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    public void adforest_initializeList(JSONArray jsonArray) {
        blockUserModelArrayList.clear();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                blockUserModel item = new blockUserModel();
                item.setId(jsonObject.getString("user_id"));
                item.setImage(jsonObject.getString("user_img"));
                item.setLocaiton(jsonObject.getString("block_time"));
                item.setName(jsonObject.getString("user_name"));
                item.setText(jsonObject.getString("block_text"));

                blockUserModelArrayList.add(item);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        itemBlockUserAdapter = new ItemBlockUserAdapter(getActivity(), blockUserModelArrayList);
        recyclerView.setAdapter(itemBlockUserAdapter);
        itemBlockUserAdapter.setOnItemClickListener((item, position) -> adforest_unBlockUser(item.getId(), position));
    }

    void adforest_unBlockUser(String userId, final int position) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", userId);
            params.addProperty("receiver_id", recieverId);
            params.addProperty("sender_id", senderId);
            Log.d("info send blockUser", position + "" + params.toString());

            Call<ResponseBody> myCall = restService.postUserUnBlock(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info blockUser Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                blockUserModelArrayList.remove(position);
                                itemBlockUserAdapter.notifyItemRemoved(position);
                                itemBlockUserAdapter.notifyItemRangeChanged(position, blockUserModelArrayList.size());
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info blockUser Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info blockUser error", String.valueOf(t));
                        Log.d("info blockUser error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

}
