package com.kathryn.kathryn.adapters;

import android.content.Context;
import android.graphics.Point;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.ItemLanguageOnclicklinstener;
import com.kathryn.kathryn.home.helper.chooseLanguageModel;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ItemLanguagueAdapter extends RecyclerView.Adapter<ItemLanguagueAdapter.CustomViewHolder> {

    private List<chooseLanguageModel> productlist;
    private Context mContext;
    Animation animation;

    private ItemLanguageOnclicklinstener itemLanguageOnclicklinstener;
    private boolean isMultiLine = false;

    public boolean isMultiLine() {
        return isMultiLine;
    }

    public void setMultiLine(boolean multiLine) {
        isMultiLine = multiLine;
    }

    public ItemLanguagueAdapter(Context context, List<chooseLanguageModel> productlist) {
        this.productlist = productlist;
        this.mContext = context;
    }

    @Override
    public ItemLanguagueAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_layout, null);

        return new ItemLanguagueAdapter.CustomViewHolder(view);
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    @Override
    public void onBindViewHolder(ItemLanguagueAdapter.CustomViewHolder customViewHolder, int i) {
        chooseLanguageModel chooseLanguageModel = productlist.get(i);
//        AnimationUtils.slideUp(customViewHolder.itemView);
//        animation = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tap);
//        customViewHolder.cardView.animate();
////        setScaleAnimation(customViewHolder.itemView);
//        animation = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
//                customViewHolder.cardView.setAnimation(animation);
        animation = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        customViewHolder.title.setAnimation(animation);
        animation = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dialog_leave_to_right);
        customViewHolder.imageView.setAnimation(animation);
//        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent= new Intent(mContext, HomeActivity.class);
//                intent.putExtra("Image",i);
//                   mContext.startActivity(intent);


//                   customViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//                       @Override
//                       public void onClick(View v) {
//                            setFadeAnimation(customViewHolder.itemView);
//// animation = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
////                customViewHolder.cardView.setAnimation(animation);
////                           new AnimationUtils().ItemTap(customViewHolder.cardView);
//                       }
//                   });
        // }
        //   });
        customViewHolder.title.setText(productlist.get(i).getTitle());
//        customViewHolder.title2.setText(productlist.get(i).getTitle2());

//        customViewHolder.langaugeCode.setText(productlist.get(i).getLanguageCode());
//        customViewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(chooseLanguageModel.getImage())));

        Picasso.with(mContext).load(chooseLanguageModel.getImage())
//                .resize(250, 250).centerCrop()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(customViewHolder.imageView);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemLanguageOnclicklinstener.onItemClick(chooseLanguageModel);
            }
        };
        customViewHolder.itemView.setOnClickListener(listener);

    }

    private final static int FADE_DURATION = 500; //FADE_DURATION in milliseconds


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
//        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return productlist.size();
    }


    public ItemLanguageOnclicklinstener getOnItemClickListener() {
        return itemLanguageOnclicklinstener;
    }

    public void setItemLanguageOnclicklinstener(ItemLanguageOnclicklinstener itemLanguageOnclicklinstener) {
        this.itemLanguageOnclicklinstener = itemLanguageOnclicklinstener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,title2, langaugeCode;

        CardView cardView;
        CustomViewHolder(View view) {
            super(view);
//this.title2=view.findViewById(R.id.txt_translated_name);
            this.imageView = view.findViewById(R.id.img_job_logo);
            this.title = view.findViewById(R.id.txt_jobs_include);

//            this.langaugeCode=view.findViewById(R.id.language_code);
            this.cardView=view.findViewById(R.id.card_language);
        }
    }
}