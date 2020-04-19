package com.kathryn.kathryn.Blog;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.Blog.adapter.ItemBlogAdapter;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.BlogItemOnclicklinstener;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.modelsList.blogModel;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

public class BlogFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<blogModel> listitems = new ArrayList<>();

    SettingsMain settingsMain;
    int nextPage = 1;
    boolean loading = true, hasNextPage = false;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ItemBlogAdapter itemSendRecMesageAdapter;
    ProgressBar progressBar;
    RestService restService;

    public BlogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemblog_list, container, false);

        settingsMain = new SettingsMain(getActivity());

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);

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
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        adforest_getData();
        return view;
    }

    private void adforest_loadMore(int nextPag) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();

            params.addProperty("page_number", nextPag);

            Log.d("info Send Blog Page", "" + params.toString());

            Call<ResponseBody> myCall = restService.postLoadMoreBlogs(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info GetMoreBlog Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info GetMoreBlog object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                JSONArray timeline = response.getJSONObject("data").getJSONArray("post");

                                for (int i = 0; i < timeline.length(); i++) {

                                    blogModel item = new blogModel();
                                    JSONObject firstEvent;
                                    try {
                                        firstEvent = (JSONObject) timeline.get(i);
                                        if (firstEvent != null) {

                                            item.setPostId(firstEvent.getString("post_id"));
                                            item.setName(firstEvent.getString("title"));
                                            item.setComments(firstEvent.getString("comments"));
                                            item.setDate(firstEvent.getString("date"));
                                            item.setReadMore(firstEvent.getString("read_more"));
                                            item.setImage(firstEvent.getString("image"));
                                            item.setHasImage(firstEvent.getBoolean("has_image"));
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
                        }
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
                    SettingsMain.hideDilog();
                    Log.d("info GetMoreBlog error", String.valueOf(t));
                    Log.d("info GetMoreBlog error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_getData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getBlogsDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info BlogDetails", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            JSONObject response = new JSONObject(responseObj.body().string());
                            getActivity().setTitle(response.getJSONObject("extra").getString("page_title"));

                            if (response.getBoolean("success")) {
                                Log.d("info BlogGet object", "" + response.getJSONObject("data"));

                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");

                                nextPage = jsonObjectPagination.getInt("next_page");
                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");

                                adforest_initializeList(response.getJSONObject("data").getJSONArray("post"));

                                itemSendRecMesageAdapter = new ItemBlogAdapter(getActivity(), listitems);

                                if (listitems.size() > 0 & recyclerView != null) {
                                    recyclerView.setAdapter(itemSendRecMesageAdapter);

                                    itemSendRecMesageAdapter.setOnItemClickListener(new BlogItemOnclicklinstener() {
                                        @Override
                                        public void onItemClick(blogModel item) {

                                            BlogDetailFragment fragment = new BlogDetailFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", item.getPostId());
                                            fragment.setArguments(bundle);

                                            replaceFragment(fragment, "BlogDetailFragment");
                                        }
                                    });
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

    public void adforest_initializeList(JSONArray timeline) {
        listitems.clear();

        for (int i = 0; i < timeline.length(); i++) {

            blogModel item = new blogModel();
            JSONObject firstEvent;
            try {
                firstEvent = (JSONObject) timeline.get(i);
                if (firstEvent != null) {

                    item.setPostId(firstEvent.getString("post_id"));
                    item.setName(firstEvent.getString("title"));
                    item.setComments(firstEvent.getString("comments"));
                    item.setDate(firstEvent.getString("date"));
                    item.setReadMore(firstEvent.getString("read_more"));
                    item.setImage(firstEvent.getString("image"));
                    item.setHasImage(firstEvent.getBoolean("has_image"));

                    listitems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Blogs");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
