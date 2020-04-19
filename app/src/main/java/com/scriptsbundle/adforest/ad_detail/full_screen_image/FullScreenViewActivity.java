package com.kathryn.kathryn.ad_detail.full_screen_image;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.rd.PageIndicatorView;

import java.util.ArrayList;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.ad_detail.full_screen_image.photoview.HackyViewPager;
import com.kathryn.kathryn.ad_detail.full_screen_image.photoview.view.PhotoView;

public class FullScreenViewActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private PageIndicatorView pageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_full_screen_view);
        viewPager = (HackyViewPager) findViewById(R.id.viewPager);
        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        ArrayList<String> imageUurls = i.getStringArrayListExtra("imageUrls");
        Log.d("info Image Urls", imageUurls.toString());
        viewPager.setAdapter(new SamplePagerAdapter(imageUurls));

        // displaying selected image first
        viewPager.setCurrentItem(position);
        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(viewPager);
    }

    private boolean isViewPagerActive() {
        return (viewPager != null && viewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean("isLocked", ((HackyViewPager) viewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    static class SamplePagerAdapter extends PagerAdapter {
        /* Here I'm adding the demo pics, but you can add your Item related pics , just get your pics based on itemID (use asynctask) and
         fill the urls in arraylist*/
        private static ArrayList<String> sDrawables = null;

        public SamplePagerAdapter(ArrayList<String> strings) {
            sDrawables = strings;
        }

        @Override
        public int getCount() {
            return sDrawables.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageUri(sDrawables.get(position));

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
