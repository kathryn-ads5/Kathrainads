package com.kathryn.kathryn.Blog;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Blog.adapter.ItemBlogCommentListAdapter;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.BlogCommentOnclicklinstener;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.modelsList.blogCommentsModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class BlogDetailFragment extends Fragment {

    Dialog dialog;
    SettingsMain settingsMain;

    TextView textViewAdName, textViewPostedBy, textViewTotalCmnt, textViewDate, textCmtReply, textViewCmntForm;
    TextView sendCmntBtn, loadMoreBtn, textViewMessage;

    LinearLayout linearLayoutComntForm;
    RelativeLayout relativeLayoutComntList;
    EditText editTextMessage;
    ImageView banerImage;

    RecyclerView recyclerView;
    RestService restService;
    ArrayList<blogCommentsModel> listitems = new ArrayList<>();

    WebView webView;
    String postId, quickRequestMessage;
    boolean loading = true, hasNextPage = false;
    int nextPage = 1;
    ProgressBar progressBar;
    ItemBlogCommentListAdapter itemSendRecMesageAdapter;

    public BlogDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog_detail, container, false);
        settingsMain = new SettingsMain(getActivity());
        banerImage = view.findViewById(R.id.image_view);
        progressBar = view.findViewById(R.id.progress_bar);

        textViewMessage = view.findViewById(R.id.textView7);
        linearLayoutComntForm = view.findViewById(R.id.linearLayout5);
        relativeLayoutComntList = view.findViewById(R.id.reliner);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            postId = bundle.getString("id", "0");
        }

        textViewMessage.setVisibility(View.GONE);
        webView = view.findViewById(R.id.webView);
        textViewAdName = view.findViewById(R.id.text_view_name);
        textViewPostedBy = view.findViewById(R.id.read_more);
        textViewTotalCmnt = view.findViewById(R.id.comments);
        textViewDate = view.findViewById(R.id.date);

        textCmtReply = view.findViewById(R.id.cmntsheading);
        textViewCmntForm = view.findViewById(R.id.cmntform);

        sendCmntBtn = view.findViewById(R.id.sendCmnt);
        loadMoreBtn = view.findViewById(R.id.loadMore);
        sendCmntBtn.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        loadMoreBtn.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        editTextMessage = view.findViewById(R.id.editText3);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
            linearLayoutComntForm.setVisibility(View.GONE);
        } else {
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
        }
        sendCmntBtn.setOnClickListener(view1 -> {
            if (!editTextMessage.getText().toString().equals(""))
                adforest_sendComment(postId, "", editTextMessage.getText().toString());
        });
        loadMoreBtn.setOnClickListener(view12 -> {
            if (loading) {
                loading = false;
                if (hasNextPage) {
                    progressBar.setVisibility(View.VISIBLE);
                    adforest_loadMore(nextPage);
                }
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);


        adforest_getPostDetail(postId);
        return view;

    }

    private void adforest_sendComment(String id, String cmntId, String message) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("comment_id", cmntId);
            params.addProperty("post_id", id);
            params.addProperty("message", message);
            Log.d("info SendComments", params.toString());

            Call<ResponseBody> myCall = restService.postComments(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SingleBlog", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info sendComment object", "" + response.getJSONObject("data"));
                                Log.d("info sendComment Extra", "" + response.getJSONObject("extra"));
                                quickRequestMessage = response.getJSONObject("extra").getString("message");
                                JSONObject jsonObjectPagination = response.getJSONObject("data")
                                        .getJSONObject("comments")
                                        .getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
                                adforest_showHideLoadMoreBtn();

                                Log.d("info sendComment pages", "" + jsonObjectPagination.toString());

                                adforest_initializeList(response.getJSONObject("data").getJSONObject("comments"));

                                adforest_showHideList("", false, false);

                                editTextMessage.setText("");
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                            loading = true;
                        } else {
                            Toast.makeText(getActivity(), quickRequestMessage, Toast.LENGTH_SHORT).show();
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    SettingsMain.hideDilog();
//                    Toast.makeText(getActivity(), "asdasdsad" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("info sendComments error", String.valueOf(t));
                    Log.d("info sendComments error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
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
            params.addProperty("post_id", postId);
            Log.d("info sendLdMore comment", "" + params.toString());

            Call<ResponseBody> myCall = restService.postLoadMoreComments(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info MoreComments Respo", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info MoreComments obj", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
                                adforest_showHideLoadMoreBtn();

                                try {
                                    JSONArray jsonArray = response.getJSONObject("data").getJSONArray("comments");
                                    Log.d("info MoreComments", "" + jsonArray.toString());

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                        blogCommentsModel item = new blogCommentsModel();

                                        item.setBlogId(jsonObject1.getString("blog_id"));
                                        item.setComntParentId(jsonObject1.getString("comment_parent"));
                                        item.setComntId(jsonObject1.getString("comment_id"));
                                        item.setName(jsonObject1.getString("comment_author"));
                                        item.setMessage(jsonObject1.getString("comment_content"));
                                        item.setDate(jsonObject1.getString("comment_date"));
                                        item.setImage(jsonObject1.getString("img"));
                                        item.setReply(jsonObject1.getString("reply_btn_text"));
                                        item.setHasReplyList(jsonObject1.getBoolean("has_childs"));
                                        item.setCanReply(jsonObject1.getBoolean("can_reply"));

                                        if (jsonObject1.getBoolean("has_childs")) {
                                            JSONArray jsonArrayiner = jsonObject1.getJSONArray("reply");

                                            ArrayList<blogCommentsModel> listitemsiner = new ArrayList<>();

                                            for (int j = 0; j < jsonArrayiner.length(); j++) {

                                                JSONObject jsonObject11 = jsonArrayiner.getJSONObject(j);

                                                blogCommentsModel item11 = new blogCommentsModel();

                                                item11.setBlogId(jsonObject11.getString("blog_id"));
                                                item11.setComntParentId(jsonObject11.getString("comment_parent"));
                                                item11.setComntId(jsonObject11.getString("comment_id"));
                                                item11.setName(jsonObject11.getString("comment_author"));
                                                item11.setMessage(jsonObject11.getString("comment_content"));
                                                item11.setDate(jsonObject11.getString("comment_date"));
                                                item11.setImage(jsonObject11.getString("img"));
                                                item11.setReply(jsonObject11.getString("reply_btn_text"));
                                                item11.setHasReplyList(jsonObject11.getBoolean("has_childs"));

                                                listitemsiner.add(item11);
                                            }
                                            item.setListitemsiner(listitemsiner);
                                        }

                                        listitems.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                loading = true;
                                itemSendRecMesageAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    SettingsMain.hideDilog();
                    progressBar.setVisibility(View.GONE);
                    Log.d("info MoreComments error", String.valueOf(t));
                    Log.d("info MoreComments error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);

            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_getPostDetail(String id) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("post_id", id);
            Log.d("info sendSingleBlog", id);

            Call<ResponseBody> myCall = restService.postGetBlogDetail(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SingleBlog", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            JSONObject response = new JSONObject(responseObj.body().string());
                            getActivity().setTitle(response.getJSONObject("extra").getString("page_title"));

                            getActivity().setTitle(response.getJSONObject("extra").getString("page_title"));
                            textCmtReply.setText(response.getJSONObject("extra").getString("comment_title"));
                            loadMoreBtn.setText(response.getJSONObject("extra").getString("load_more"));

                            textViewCmntForm.setText(response.getJSONObject("extra").getJSONObject("comment_form").getString("title"));
                            sendCmntBtn.setText(response.getJSONObject("extra").getJSONObject("comment_form").getString("btn_submit"));
                            editTextMessage.setHint(response.getJSONObject("extra").getJSONObject("comment_form").getString("textarea"));


                            if (response.getBoolean("success")) {
                                Log.d("info SingleBlog object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data")
                                        .getJSONObject("post").getJSONObject("comments")
                                        .getJSONObject("pagination");


                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
                                adforest_showHideLoadMoreBtn();

                                adforest_setData(response.getJSONObject("data").getJSONObject("post"));


                                if (response.getJSONObject("data").getJSONObject("post").getString("comment_status").equals("open")) {
                                    if (settingsMain.getAppOpen())
                                        linearLayoutComntForm.setVisibility(View.GONE);
                                    else
                                        adforest_showHideComnetForm(false);

                                } else {
                                    adforest_showHideComnetForm(true);
                                    adforest_showHideList(response.getJSONObject("data").getJSONObject("post").getString("comment_mesage"), false, true);
                                }

                                if (response.getJSONObject("data").getJSONObject("post").getBoolean("has_comment")) {
                                    adforest_initializeList(response.getJSONObject("data").getJSONObject("post").getJSONObject("comments"));
                                } else {
                                    adforest_showHideList(response.getJSONObject("data").getJSONObject("post").getString("comment_mesage"), true, true);
                                }

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info Blog error", String.valueOf(t));
                    Log.d("info Blog error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void adforest_setData(JSONObject data) {

        try {
            textViewAdName.setText(data.getString("title"));
            textViewTotalCmnt.setText(data.getString("comment_count"));
            textViewDate.setText(data.getString("date"));
            textViewPostedBy.setText(data.getString("author_name"));

            if (data.getBoolean("has_image"))
                Picasso.with(getContext()).load(data.getString("image"))
                        .error(R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(banerImage);
            else {
                banerImage.setVisibility(View.GONE);
            }
            webView.setScrollContainer(false);
            webView.getSettings().setAllowFileAccessFromFileURLs(false);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setDefaultFontSize(18);
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            webView.loadData(data.getString("desc"), "text/html; charset=utf-8", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adforest_initializeList(JSONObject jsonObject) {
        listitems.clear();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("comments");
            Log.d("info Comments", "" + jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                blogCommentsModel item = new blogCommentsModel();

                item.setBlogId(jsonObject1.getString("blog_id"));
                item.setComntParentId(jsonObject1.getString("comment_parent"));
                item.setComntId(jsonObject1.getString("comment_id"));
                item.setName(jsonObject1.getString("comment_author"));
                item.setMessage(jsonObject1.getString("comment_content"));
                item.setDate(jsonObject1.getString("comment_date"));
                item.setImage(jsonObject1.getString("img"));
                item.setReply(jsonObject1.getString("reply_btn_text"));
                item.setHasReplyList(jsonObject1.getBoolean("has_childs"));
                item.setCanReply(jsonObject1.getBoolean("can_reply"));

                if (jsonObject1.getBoolean("has_childs")) {
                    JSONArray jsonArrayiner = jsonObject1.getJSONArray("reply");

                    ArrayList<blogCommentsModel> listitemsiner = new ArrayList<>();

                    for (int j = 0; j < jsonArrayiner.length(); j++) {

                        JSONObject jsonObject11 = jsonArrayiner.getJSONObject(j);

                        blogCommentsModel item11 = new blogCommentsModel();

                        item11.setBlogId(jsonObject11.getString("blog_id"));
                        item11.setComntParentId(jsonObject11.getString("comment_parent"));
                        item11.setComntId(jsonObject11.getString("comment_id"));
                        item11.setName(jsonObject11.getString("comment_author"));
                        item11.setMessage(jsonObject11.getString("comment_content"));
                        item11.setDate(jsonObject11.getString("comment_date"));
                        item11.setImage(jsonObject11.getString("img"));
                        item11.setReply(jsonObject11.getString("reply_btn_text"));
                        item11.setHasReplyList(jsonObject11.getBoolean("has_childs"));

                        listitemsiner.add(item11);
                    }
                    item.setListitemsiner(listitemsiner);
                }

                listitems.add(item);
            }

            itemSendRecMesageAdapter = new ItemBlogCommentListAdapter(getActivity(), listitems);

            if (listitems.size() > 0 & recyclerView != null) {
                recyclerView.setAdapter(itemSendRecMesageAdapter);

                itemSendRecMesageAdapter.setOnItemClickListener(new BlogCommentOnclicklinstener() {
                    @Override
                    public void onItemClick(blogCommentsModel item) {
                        adforest_showDilogMessage(item.getComntId());
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void adforest_showDilogMessage(final String comntId) {
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

        Send.setText(sendCmntBtn.getText());
        Cancel.setText(settingsMain.getAlertCancelText());
        message.setHint(editTextMessage.getHint());

        Send.setOnClickListener(v -> {
            if (!message.getText().toString().equals("")) {
                adforest_sendComment(postId, comntId, message.getText().toString());
                message.setText("");
                dialog.dismiss();
            }
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    void adforest_showHideLoadMoreBtn() {
        loadMoreBtn.setVisibility(hasNextPage ? View.VISIBLE : View.GONE);
    }

    void adforest_showHideList(String msg, boolean b, boolean c) {
        relativeLayoutComntList.setVisibility(b ? View.GONE : View.VISIBLE);
        textViewMessage.setVisibility(c ? View.VISIBLE : View.GONE);
        textViewMessage.setText(msg);
    }

    void adforest_showHideComnetForm(boolean b) {
        linearLayoutComntForm.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Blog Details");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_sample, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
