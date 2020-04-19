package com.kathryn.kathryn.helper;

import android.view.View;

import com.kathryn.kathryn.modelsList.myAdsModel;

public interface MyAdsOnclicklinstener {

    void onItemClick(myAdsModel item);

    void delViewOnClick(View v, int position);

    void editViewOnClick(View v, int position);

}
