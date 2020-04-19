package com.kathryn.kathryn.messages;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Notification.Config;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.Ad_detail_activity;
import com.kathryn.kathryn.messages.adapter.ChatAdapter;
import com.kathryn.kathryn.modelsList.ChatMessage;
import com.kathryn.kathryn.modelsList.ChatTyping;
import com.kathryn.kathryn.modelsList.blockUserModel;
import com.kathryn.kathryn.userAndSellers.adapter.ItemBlockUserAdapter;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static android.view.View.GONE;
import static com.kathryn.kathryn.utills.SettingsMain.getMainColor;

public class ChatFragment extends Fragment implements View.OnClickListener {

    ArrayList<ChatMessage> chatlist;
    ChatAdapter chatAdapter;
    ListView msgListView;
    int nextPage = 1;
    boolean hasNextPage = false;
    String adId, senderId, recieverId, type, is_Block;
    SettingsMain settingsMain;
    TextView adName, adPrice, adDate, tv_typing;
    Button block_button;
    SwipeRefreshLayout swipeRefreshLayout;
    RestService restService;
    ChatTyping chatTypingModel;
    String userId;
    boolean typingStarted;
    int totalCount;
    //    FirebaseDatabase database, database2;
//    DatabaseReference myRef, myRef2;
    long delay = 10000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    String userName;
    String typingRecieverId;
    String typingText;
    TextView tv_chatTtile, tv_online;
    //    Boolean is_BlockBool;
    private EditText msg_edittext;
    TextView BlockedTextMessage;
    LinearLayout form, MessageContainer, Blocklayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ItemBlockUserAdapter itemBlockUserAdapter;
    private ArrayList<blockUserModel> blockUserModelArrayList = new ArrayList<>();
    int position;
    String blcokstoreText;
    ImageButton sendButton;

    //Boolean Is_Blocked;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_layout, container, false);
        settingsMain = new SettingsMain(getActivity());

//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("chatTyping");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            adId = bundle.getString("adId", "0");
            senderId = bundle.getString("senderId", "0");
            recieverId = bundle.getString("recieverId", "0");
            type = bundle.getString("type", "0");
            is_Block = bundle.getString("is_block", "");
        }
        BlockedTextMessage = view.findViewById(R.id.BlockedTextMessage);
        BlockedTextMessage.setText(settingsMain.getUserUnblock("Unblock_M"));
        MessageContainer = view.findViewById(R.id.messageContainer);
        adDate = view.findViewById(R.id.verified);
        adName = view.findViewById(R.id.loginTime);
        adPrice = view.findViewById(R.id.text_viewName);

        tv_typing = view.findViewById(R.id.tv_typing);
        block_button = view.findViewById(R.id.block_btn);
        block_button.setBackgroundColor(Color.parseColor(getMainColor()));

        adName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                intent.putExtra("adId", adId);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });


        block_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (block_button.getText().toString().equalsIgnoreCase("Block User")) {
                    adforest_BlockChat();

                } else {


                    adforest_UnBlockChat();

                }
            }

        });


        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        msg_edittext = view.findViewById(R.id.messageEditText);
        msgListView = view.findViewById(R.id.msgListView);
        tv_chatTtile = getActivity().findViewById(R.id.tv_chatTtile);
        tv_online = getActivity().findViewById(R.id.tv_online);
        sendButton = view.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.fieldradius);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(getMainColor()));
        sendButton.setBackground(wrappedDrawable);
/*        Drawable drawable = getResources().getDrawable(R.drawable.fieldradius).mutate();

        sendButton.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_IN);
sendButton.setBackground(drawable);*/
//        sendButton.setBackgroundColor(Color.parseColor(getMainColor()));
        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (hasNextPage) {
                    swipeRefreshLayout.setRefreshing(true);
                    msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                    msgListView.setStackFromBottom(false);
                    adforest_loadMore(nextPage);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        chatlist = new ArrayList<>();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    Toast.makeText(getActivity(), "there", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.d("Instant Message", "true");

                    String date = intent.getStringExtra("date");
                    String img = intent.getStringExtra("img");
                    String text = intent.getStringExtra("text");
                    String type = intent.getStringExtra("type");

                    String adIdCheck = intent.getStringExtra("adIdCheck");
                    String recieverIdCheck = intent.getStringExtra("recieverIdCheck");
                    String senderIdCheck = intent.getStringExtra("senderIdCheck");

                    if (adId.equals(adIdCheck) && recieverId.equals(recieverIdCheck)
                            && senderId.equals(senderIdCheck)) {
                        Log.d("Instant Message", "true");
                        ChatMessage item = new ChatMessage();
                        item.setImage(img);
                        item.setBody(text);
                        item.setDate(date);
                        item.setMine(type.equals("message"));
                        chatlist.add(item);
                        msgListView.setAdapter(chatAdapter);
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Instant Message", adIdCheck + recieverIdCheck + senderIdCheck);
                        Log.d("Instant Message", adId + senderId + recieverId);

                    }
                }
            }
        };

        adforest_getChat();

        return view;
    }




    //    private void adforest_checkLogin() {
//        database2 = FirebaseDatabase.getInstance();
//        myRef2 = database2.getReference("UserLogin");
//        String otherUserId;
//        if (type.equals("receive"))
//            otherUserId = senderId;
//        else
//            otherUserId = recieverId;
//
//        Log.d("info", otherUserId);
//        ChatUserModel userModel = new ChatUserModel(false, settingsMain.getUserLogin());
//
//        myRef2.child(settingsMain.getUserLogin()).onDisconnect().setValue(userModel);
//
//        myRef2.child(otherUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                ChatUserModel chatTypingModel = dataSnapshot.getValue(ChatUserModel.class);
//                // Check for null
//                if (chatTypingModel == null) {
//                    Log.e("info", "ChatTyping data is null!");
//                    tv_online.setVisibility(View.GONE);
//                } else {
//                    if (chatTypingModel.isOnline()) {
//                        tv_online.setVisibility(View.VISIBLE);
//                        tv_online.setText("online");
//                    } else
//                        tv_online.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w("info ", "Failed to read value.", error.toException());
//            }
//        });
//    }
//
//    private void adforest_typingIndicatoor() {
//        try {
//            if (type.equals("receive")) {
//                userId = recieverId;
//                typingRecieverId = senderId;
//            } else {
//                userId = senderId;
//                typingRecieverId = recieverId;
//            }
//            final Runnable input_finish_checker = new Runnable() {
//                public void run() {
//                    if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                        // TODO: do what you need here
//                        // ............
//                        // ............
////                        DoStaff();
//                        chatTypingModel = new ChatTyping(adId, senderId, recieverId, type, "", false);
//                        myRef.child(adId).child(userId).setValue(chatTypingModel);
//                        Log.d("info stoped ", "dsadsad");
//                    }
//                }
//            };
//            msg_edittext.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
//
//                        typingStarted = true;
//
//                    } else if (s.toString().trim().length() == 0 && typingStarted) {
//                        typingStarted = false;
//                    }
//                    chatTypingModel = new ChatTyping(adId, senderId, recieverId, type, s.toString(), typingStarted);
//                    myRef.child(adId).child(userId).setValue(chatTypingModel);
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (s.length() > 0) {
//                        last_text_edit = System.currentTimeMillis();
//                        handler.postDelayed(input_finish_checker, delay);
//                    } else {
//
//                    }
//                }
//            });
//
//            myRef.child(adId).child(typingRecieverId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    ChatTyping chatTypingModel = dataSnapshot.getValue(ChatTyping.class);
//                    // Check for null
//                    if (chatTypingModel == null) {
//                        Log.e("info", "ChatTyping data is null!");
//                        return;
//                    } else {
//                        if (userName != null) {
//                            String s = userName + " " + typingText + " ....";
//                            if (chatTypingModel.text.length() > 0 && chatTypingModel.type.equals("sent") &&
//                                    settingsMain.getUserLogin().equals(chatTypingModel.recieverId)) {
//                                tv_typing.setVisibility(View.VISIBLE);
//                                tv_typing.setText(s);
//                            }
//                            if (chatTypingModel.text.length() > 0 && chatTypingModel.type.equals("receive") &&
//                                    settingsMain.getUserLogin().equals(chatTypingModel.senderId)) {
//                                tv_typing.setVisibility(View.VISIBLE);
//                                tv_typing.setText(s);
//                            }
//                        }
//                        if (!chatTypingModel.typing && chatTypingModel.text.isEmpty()) {
//                            tv_typing.setVisibility(View.GONE);
//                        }
//                        Log.d("info ", chatTypingModel.type + "" + chatTypingModel.senderId + "" + typingRecieverId);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w("info ", "Failed to read value.", error.toException());
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void adforest_UnBlockChat() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            JsonObject params = new JsonObject();
            params.addProperty("sender_id", senderId);
            params.addProperty("recv_id", recieverId);
            Log.d("info BlockChat object", "" + params.toString());

            Call<ResponseBody> myCall = restService.postUserUnBlock(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    if (responseObj.isSuccessful()) {
                        Log.d("info Blockresponce", "" + responseObj.toString());
                        JSONObject response = null;
                        try {
                            response = new JSONObject(responseObj.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {

                            block_button.setText(response.getString("btn_text"));
                            sendButton.setVisibility(View.VISIBLE);
                            msg_edittext.setVisibility(View.VISIBLE);
                            BlockedTextMessage.setVisibility(GONE);

                            //Post it in a handler to make sure it gets called if coming back from a lifecycle method.
                            new Handler().post(new Runnable() {

                                @Override
                                public void run()
                                {
                                    Intent intent = getActivity().getIntent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                    getActivity().overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            });
                            Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }



    private void adforest_BlockChat() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            SettingsMain.showDilog(getActivity());
            JsonObject params = new JsonObject();
            params.addProperty("sender_id", senderId);
            params.addProperty("recv_id", recieverId);
            Log.d("info BlockChat object", "" + params.toString());

            Call<ResponseBody> myCall = restService.postUserBlock(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    if (responseObj.isSuccessful()) {
                        Log.d("info Blockresponce", "" + responseObj.toString());
                        JSONObject response = null;
                        try {
                            response = new JSONObject(responseObj.body().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {

                            block_button.setText(response.getString("btn_text"));
                            msg_edittext.setVisibility(GONE);
                            sendButton.setVisibility(GONE);
                            sendButton.setVisibility(View.GONE);
                            msg_edittext.setVisibility(GONE);
                            msgListView.setVisibility(GONE);
                            BlockedTextMessage.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SettingsMain.hideDilog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    private void adforest_getChat() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adId);
            params.addProperty("sender_id", senderId);
            params.addProperty("receiver_id", recieverId);
            params.addProperty("type", type);
            params.addProperty("is_block", is_Block);

            Log.d("info sendChat object", "" + params.toString());

            Call<ResponseBody> myCall = restService.postGetChatORLoadMore(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Chat Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info Chat object", "" + response.getJSONObject("data"));

                                getActivity().setTitle("");
                                block_button.setText(response.getJSONObject("data").getString("btn_text"));
                                tv_chatTtile.setText(response.getJSONObject("data").getString("page_title"));
                                userName = response.getJSONObject("data").getString("page_title");

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                adPrice.setText(response.getJSONObject("data").getJSONObject("ad_price").getString("price"));

                                String mystring = response.getJSONObject("data").getString("ad_title");
                                SpannableString content = new SpannableString(mystring);
                                content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                                adName.setText(content);
                                adName.setTextColor(Color.parseColor(getMainColor()));
                                adDate.setText(response.getJSONObject("data").getString("ad_date"));
                                typingText = response.getJSONObject("data").getString("is_typing");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                adforest_intList(response.getJSONObject("data").getJSONArray("chat"));

                                chatAdapter = new ChatAdapter(getActivity(), chatlist);
                                msgListView.setAdapter(chatAdapter);

                            } else {
                                if (is_Block.equals("true")) {
                                    sendButton.setVisibility(View.GONE);
                                    msg_edittext.setVisibility(GONE);
                                    msgListView.setVisibility(GONE);
                                    BlockedTextMessage.setVisibility(View.VISIBLE);
                                }

                                block_button.setText(response.getJSONObject("data").getString("btn_text"));
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
                        Log.d("info Chat Exception ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Chat error", String.valueOf(t));
                        Log.d("info Chat error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
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
            params.addProperty("ad_id", adId);
            params.addProperty("sender_id", senderId);
            params.addProperty("receiver_id", recieverId);
            params.addProperty("type", type);
//            params.addProperty("is_block", is_Block);

            params.addProperty("page_number", nextPag);

            Log.d("info LoadMore Chat", "" + params.toString());

            Call<ResponseBody> myCall = restService.postGetChatORLoadMore(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoadChat Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info LoadChat object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                JSONArray jsonArray = (response.getJSONObject("data").getJSONArray("chat"));

                                Collections.reverse(chatlist);

                                try {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ChatMessage item = new ChatMessage();
                                        item.setImage(jsonArray.getJSONObject(i).getString("img"));
                                        item.setBody(jsonArray.getJSONObject(i).getString("text"));
                                        item.setDate(jsonArray.getJSONObject(i).getString("date"));
                                        item.setMine(jsonArray.getJSONObject(i).getString("type").equals("message"));

                                        chatlist.add(item);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Collections.reverse(chatlist);
                                msgListView.setAdapter(chatAdapter);
                                chatAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        swipeRefreshLayout.setRefreshing(false);
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    SettingsMain.hideDilog();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info LoadChat Excptn ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info LoadChat error", String.valueOf(t));
                        Log.d("info LoadChat error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }


    }

    private void adforest_intList(JSONArray jsonArray) {
        chatlist.clear();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                ChatMessage item = new ChatMessage();
                item.setBody(jsonArray.getJSONObject(i).getString("text"));
                item.setImage(jsonArray.getJSONObject(i).getString("img"));
                item.setDate(jsonArray.getJSONObject(i).getString("date"));
                item.setMine(jsonArray.getJSONObject(i).getString("type").equals("message"));

                chatlist.add(item);
            }
            Collections.reverse(chatlist);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void adforest_sendTextMessage() {

        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        String message = msg_edittext.getEditableText().toString();
        msg_edittext.setText("");
        if (!message.equalsIgnoreCase("")) {

            if (SettingsMain.isConnectingToInternet(getActivity())) {

                SettingsMain.showDilog(getActivity());

                JsonObject params = new JsonObject();
                params.addProperty("ad_id", adId);
                params.addProperty("sender_id", senderId);
                params.addProperty("receiver_id", recieverId);
                params.addProperty("type", type);
                params.addProperty("message", message);
//                params.addProperty("is_block", is_Block);


                Log.d("info sendMessage Object", "" + params.toString());

                Call<ResponseBody> myCall = restService.postSendMessage(params, UrlController.AddHeaders(getActivity()));
                myCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                        try {
                            if (responseObj.isSuccessful()) {
                                Log.d("info sendMessage Resp", "" + responseObj.toString());

                                JSONObject response = new JSONObject(responseObj.body().string());
                                if (response.getBoolean("success")) {
                                    Log.d("successcheck", response.toString());
                                    Log.d("info sendMessage object", "" + response.getJSONObject("data"));
//
                                    adforest_intList(response.getJSONObject("data").getJSONArray("chat"));

                                    chatAdapter = new ChatAdapter(getActivity(), chatlist);
                                    msgListView.setAdapter(chatAdapter);

                                    msg_edittext.setText("");
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
                            Log.d("info sendMessage", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info sendMessage error", String.valueOf(t));
                            Log.d("info sendMessage error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            } else {
                SettingsMain.hideDilog();
                Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendMessageButton) {


            adforest_sendTextMessage();

        }
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Chat Box");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

    }

    private void saveValue(String value) {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("VAL", value).apply();

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);

        super.onPause();
    }
}
