package com.kathryn.kathryn.home;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.kathryn.kathryn.Search.FragmentCatSubNSearch;
import com.kathryn.kathryn.helper.OnItemClickListener;
import com.kathryn.kathryn.home.adapter.ItemHomeAllCategories;
import com.kathryn.kathryn.modelsList.homeCatListModel;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllCategories extends Fragment {
    RecyclerView categoriesRecycler_view;
    ItemHomeAllCategories itemHomeAllCategories;
    SettingsMain settingsMain;
    RestService restService;
    int next_page = 1;
    boolean has_next_page = false;
    JSONObject responceData, pagination;
    Button btn_loadMore;
    String term_id = "";
    ArrayList<homeCatListModel> homeCatListModels = new ArrayList<>();
    EditText searchView;
    NestedScrollView nestedScrollView;
    private Context context;


    public FragmentAllCategories() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        settingsMain = new SettingsMain(context);

        categoriesRecycler_view = view.findViewById(R.id.categoriesRecycler_view);
        btn_loadMore = view.findViewById(R.id.btn_loadMore);
        searchView = view.findViewById(R.id.mSearch);
        nestedScrollView = view.findViewById(R.id.scrollView);
        searchView.requestFocus();

        categoriesRecycler_view.setHasFixedSize(true);
        categoriesRecycler_view.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(categoriesRecycler_view, false);
        GridLayoutManager MyLayoutManager = new GridLayoutManager(context, 1);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        categoriesRecycler_view.setLayoutManager(MyLayoutManager);

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
        } else
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());

        next_page = 1;
        term_id = "";
        adforest_loadData();

        btn_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("term_name", "ad_cats");
                jsonObject.addProperty("term_id", term_id);
                Log.d("btn_load",jsonObject.toString());
                try {
                    jsonObject.addProperty("page_number", pagination.getInt("next_page"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adforest_loadMore(jsonObject);

            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemHomeAllCategories.getFilter().filter(searchView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }

    private void adforest_loadMore(JsonObject jsonObject) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            Log.d("info catLoc sendLoad", jsonObject.toString());

            Call<ResponseBody> myCall = restService.getAllLocAndCat(jsonObject, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info LoadMore Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info LoadMore obj", "" + response.getJSONObject("data"));
                                responceData = response.getJSONObject("data");
                                pagination = responceData.getJSONObject("pagination");
                                has_next_page = pagination.getBoolean("has_next_page");
                                next_page = pagination.getInt("next_page");

                                adforest_setLoadMoreLocationAds(responceData.getJSONArray("terms"));

                                if (!has_next_page) {
                                    btn_loadMore.setVisibility(View.GONE);
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
                        Log.d("info LoadMore ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info LoadMore err", String.valueOf(t));
                        Log.d("info LoadMore err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loadData() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            if (!HomeActivity.checkLoading)
                SettingsMain.showDilog(getActivity());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("term_name", "ad_cats");
            jsonObject.addProperty("term_id", term_id);
            jsonObject.addProperty("page_number", next_page);
            Log.d("info catLoc send", jsonObject.toString());

            Call<ResponseBody> myCall = restService.getAllLocAndCat(jsonObject, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info catLoc Resp", "" + responseObj.toString());
                            HomeActivity.checkLoading = false;

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info catLoc obj", "" + response.getJSONObject("data"));
                                responceData = response.getJSONObject("data");
                                getActivity().setTitle(responceData.getString("page_title"));
                                pagination = responceData.getJSONObject("pagination");
                                has_next_page = pagination.getBoolean("has_next_page");
                                searchView.setHint(responceData.getString("search_here"));

                                adforest_setAllCategoriesAds(responceData.getJSONArray("terms"));

                                if (has_next_page) {
                                    btn_loadMore.setVisibility(View.VISIBLE);
                                    btn_loadMore.setText(responceData.getString("load_more"));
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
                        Log.d("info catLoc ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info catLoc err", String.valueOf(t));
                        Log.d("info catLoc err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_setLoadMoreLocationAds(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            try {
                item.setId(jsonArray.getJSONObject(i).getString("term_id"));
                item.setTitle(jsonArray.optJSONObject(i).getString("name"));
                item.setHas_children(jsonArray.getJSONObject(i).getBoolean("has_children"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            homeCatListModels.add(item);
        }
        itemHomeAllCategories.notifyDataSetChanged();
    }

    private void adforest_setAllCategoriesAds(JSONArray jsonArray) {

        homeCatListModels.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            try {
                item.setId(jsonArray.getJSONObject(i).getString("term_id"));
                item.setTitle(jsonArray.optJSONObject(i).getString("name"));
                item.setHas_children(jsonArray.getJSONObject(i).getBoolean("has_children"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            homeCatListModels.add(item);
        }

        itemHomeAllCategories = new ItemHomeAllCategories(context, homeCatListModels);
        categoriesRecycler_view.setAdapter(itemHomeAllCategories);
        itemHomeAllCategories.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(homeCatListModel item) {
                if (item.isHas_children()) {
                    next_page = 1;
                    term_id = item.getId();
                    searchView.setText("");
                    adforest_loadData();

                } else {
                    searchView.setText("");
                    FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
                    Bundle bundle = new Bundle();
                    bundle.putString("ad_cats1", item.getId());

                    fragment_search.setArguments(bundle);
                    replaceFragment(fragment_search, "FragmentCatSubNSearch");
                }
            }
        });

    }

    void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

}
