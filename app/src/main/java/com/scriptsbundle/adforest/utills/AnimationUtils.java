package com.kathryn.kathryn.utills;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.Animation;

import com.kathryn.kathryn.R;

import static com.kathryn.kathryn.home.HomeActivity.activity;

public final class AnimationUtils {
    public void ItemTap(View a){
//        Animation metuapik = AnimationUtils.loadAnimation(activity, R.anim.tap);
        Animation metuapik= android.view.animation.AnimationUtils.loadAnimation(activity,R.anim.tap);
        metuapik.setDuration(40);
        a.startAnimation(metuapik);
    }
    public static void slideDown(final View view) {
        view.setTranslationY(0);
        view.animate()
                .translationY(-view.getHeight())
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // superfluous restoration
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);

        if (view.getHeight() > 0) {
            slideUpNow(view);
        } else {
            // wait till height is measured
            view.post(() -> slideUpNow(view));
        }
    }

    private static void slideUpNow(final View view) {
        view.setTranslationY(-view.getHeight());
        view.animate()
                .translationY(0)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
    }

}