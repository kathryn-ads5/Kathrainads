package com.kathryn.kathryn.packages.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.adapters.SpinnerAndListAdapter;
import com.kathryn.kathryn.helper.OnItemClickListener;
import com.kathryn.kathryn.helper.OnItemClickListenerPackages;
import com.kathryn.kathryn.modelsList.PackagesModel;
import com.kathryn.kathryn.modelsList.subcatDiloglist;
import com.kathryn.kathryn.utills.SettingsMain;

import static android.view.Gravity.END;
import static android.view.Gravity.RIGHT;

public class ItemPackagesAdapter extends RecyclerView.Adapter<ItemPackagesAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<PackagesModel> feedItemList;
    private OnItemClickListenerPackages onItemClickListener;
    private OnItemClickListener onItemClickListenerPayment;
    private Context mContext;


    public ItemPackagesAdapter(Context context1, List<PackagesModel> feedItemList) {
        this.feedItemList = feedItemList;
        settingsMain = new SettingsMain(context1);
        this.mContext = context1;

    }

    @Override
    public ItemPackagesAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_packages, null);
        return new ItemPackagesAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemPackagesAdapter.CustomViewHolder customViewHolder, int i) {

        final PackagesModel feedItem = feedItemList.get(i);

        if (settingsMain.getAppOpen()) {
            customViewHolder.spinner.setVisibility(View.GONE);
        }
        customViewHolder.name.setText(feedItem.getPlanType());
        customViewHolder.price.setText(feedItem.getPrice());
        customViewHolder.regularPrice.setText(feedItem.getRegularPrice());
        customViewHolder.regularPrice.setPaintFlags(customViewHolder.regularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        customViewHolder.sale.setText(feedItem.getSaleText());
        customViewHolder.validaty.setText(feedItem.getValidaty());
        customViewHolder.ads.setText(feedItem.getFreeAds());
        customViewHolder.featureads.setText(feedItem.getFeatureAds());
        customViewHolder.bumpAds.setText(feedItem.getBumupAds());
        customViewHolder.biddingAds.setText(feedItem.getAllowBidding());
        customViewHolder.noOfImages.setText(feedItem.getNumOfImages());
        customViewHolder.videoUrl.setText(feedItem.getVideoUrl());
        customViewHolder.allowTags.setText(feedItem.getAllowTags());

        if (feedItem.getAllowBidding() == null) {
            customViewHolder.biddingAds.setVisibility(View.GONE);
        } else {
            customViewHolder.biddingAds.setVisibility(View.VISIBLE);
        }
        if (feedItem.getNumOfImages() == null) {
            customViewHolder.noOfImages.setVisibility(View.GONE);
        } else {
            customViewHolder.noOfImages.setVisibility(View.VISIBLE);
        }
        if (feedItem.getVideoUrl() == null) {
            customViewHolder.videoUrl.setVisibility(View.GONE);
        } else {
            customViewHolder.videoUrl.setVisibility(View.VISIBLE);
        }
        if (feedItem.getAllowTags() == null) {
            customViewHolder.allowTags.setVisibility(View.GONE);
        } else {
            customViewHolder.allowTags.setVisibility(View.VISIBLE);
        }

        if (feedItem.getAllowCats() == null) {
            customViewHolder.allowCats.setVisibility(View.GONE);
            customViewHolder.allowCatsValue.setVisibility(View.VISIBLE);
        } else
            customViewHolder.allowCats.setText(feedItem.getAllowCats());

//        customViewHolder.titlePackages.setText(feedItem.getListTitleText());
        if (feedItem.getReadMoreText() == null) {
            customViewHolder.allowCatsValue.setVisibility(View.GONE);
        } else {
            customViewHolder.allowCatsValue.setVisibility(View.VISIBLE);
        }
        if (feedItem.getAllowCatsValue() == null) {
            customViewHolder.allowCatsValue.setVisibility(View.GONE);
        } else {
            customViewHolder.allowCatsValue.setVisibility(View.VISIBLE);

        }
        if (feedItem.getSaleText() == null) {
            customViewHolder.imageView.setVisibility(View.GONE);
            customViewHolder.sale.setVisibility(View.GONE);
        } else {
            customViewHolder.imageView.setVisibility(View.VISIBLE);
            customViewHolder.sale.setVisibility(View.VISIBLE);

        }

        customViewHolder.allowCatsValue.setText(feedItem.getReadMoreText());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, feedItem.getSpinnerData());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customViewHolder.spinner.setAdapter(adapter);
        customViewHolder.spinner.setTag(feedItem.getBtnTag());


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };

        customViewHolder.allowCatsValue.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {


                BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(mContext).inflate(R.layout.bubblelayout, null);
//                PopupWindow popupWindow = BubblePopupHelper.create(mContext, bubbleLayout);
                PopupWindow popupWindow = new PopupWindow(bubbleLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                final Random random = new Random();
                bubbleLayout.setArrowDirection(ArrowDirection.BOTTOM_CENTER);

//
//                popupWindow.showAtLocation(view, Gravity.TOP, 0,
//                        view.getTop()-230);
                popupWindow.showAsDropDown(view, 0, -290);
                TextView textView = bubbleLayout.findViewById(R.id.titlePackages);
                ListView listView = bubbleLayout.findViewById(R.id.listView);
                textView.setText(feedItem.getListTitleText());
                textView.getText().toString();
                final ArrayList<subcatDiloglist> listitems12 = new ArrayList<>();


                for (int j = 0; j < feedItem.getAllowCatsValue().length(); j++) {
                    try {
                        JSONObject jsonObject = feedItem.getAllowCatsValue().getJSONObject(j);
                        subcatDiloglist subDiloglist = new subcatDiloglist();
                        subDiloglist.setName(jsonObject.getString("cat_name"));
                        listitems12.add(subDiloglist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                final SpinnerAndListAdapter spinnerAndListAdapter1 = new SpinnerAndListAdapter((Activity) mContext, listitems12, true);
//                Toast.makeText(mContext, "ehwejkrf", Toast.LENGTH_SHORT).show();
                listView.setAdapter(spinnerAndListAdapter1);
            }
        });
        View.OnTouchListener listener1 = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onItemClickListener.onItemTouch();
                return false;
            }
        };
        customViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.onItemSelected(feedItem, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        customViewHolder.spinner.setOnTouchListener(listener1);


    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void setOnItemClickListener(OnItemClickListenerPackages onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name, validaty, ads, price, regularPrice, sale, featureads, bumpAds, biddingAds, noOfImages, videoUrl, allowTags, allowCats, allowCatsValue, titlePackages;
        Spinner spinner;
        RelativeLayout selectPackageLayout;
        ImageView imageView;
        LinearLayout linearLayout;
        Button ok;
        boolean spinnerTouched = false;

        CustomViewHolder(View view) {
            super(view);
            this.ok = view.findViewById(R.id.dialog_ok_btn);
            this.biddingAds = view.findViewById(R.id.textView28);
            this.noOfImages = view.findViewById(R.id.textView29);
            this.videoUrl = view.findViewById(R.id.textView30);
            this.allowTags = view.findViewById(R.id.textView31);
            this.allowCats = view.findViewById(R.id.textView32);
            this.allowCatsValue = view.findViewById(R.id.textView33);
            this.name = view.findViewById(R.id.textView22);
            this.price = view.findViewById(R.id.textView26);
            this.linearLayout = view.findViewById(R.id.line2);
            this.regularPrice = view.findViewById(R.id.textViewRegularpirce);
            this.sale = view.findViewById(R.id.textViewSale);
            this.imageView = view.findViewById(R.id.myImageView);
            this.validaty = view.findViewById(R.id.textView23);
            this.ads = view.findViewById(R.id.textView24);
            this.featureads = view.findViewById(R.id.textView25);
            this.bumpAds = view.findViewById(R.id.textView27);
//            this.titlePackages = view.findViewById(R.id.textViewtitle);
            spinner = view.findViewById(R.id.selectPlan);
            selectPackageLayout = view.findViewById(R.id.selectPackageLayout);
            allowCatsValue.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            price.setTextColor(Color.parseColor(settingsMain.getMainColor()));
        }
    }

}
