package com.kathryn.kathryn.public_profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kathryn.kathryn.R;

/**
 * Created by apple on 12/29/17.
 */

public class social_icons {

    public static void adforest_setViewsForCustom(JSONObject jsonObjec, LinearLayout linearLayout, final Context context) {

        try {
            Log.d("info Custom data ===== ", jsonObjec.getJSONArray("social_icons").toString());
            JSONArray customOptnList = jsonObjec.getJSONArray("social_icons");

            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(40, 40);
                params1.setMarginStart(20);
                final JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);
                if (!eachData.getString("value").equals("")) {
                    linearLayout.setVisibility(View.VISIBLE);
                    ImageView et = new ImageView(context);
                    et.setLayoutParams(params1);
                    if (eachData.getString("key").equals("Facebook")) {
                        adforest_loadSocialIcons(R.drawable.ic_facebook, et, context);
                    }
                    if (eachData.getString("key").equals("Twitter")) {
                        adforest_loadSocialIcons(R.drawable.ic_twitter, et, context);
                    }
                    if (eachData.getString("key").equals("Linkedin")) {
                        adforest_loadSocialIcons(R.drawable.ic_linkedin, et, context);
                    }
                    if (eachData.getString("key").equals("Google+")) {
                        adforest_loadSocialIcons(R.drawable.ic_google_plus, et, context);
                    }
                    et.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                try {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(eachData.getString("value")));
                                    context.startActivity(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return true;
                        }
                    });
                    linearLayout.addView(et);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void adforest_loadSocialIcons(int icon, ImageView imageView, Context context) {
        imageView.setBackgroundResource(icon);
    }
}
