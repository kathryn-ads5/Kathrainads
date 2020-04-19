package com.kathryn.kathryn.messages.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.ChatMessage;
import com.kathryn.kathryn.utills.SettingsMain;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    SettingsMain settingsMain;
    Context context;
    private ArrayList<ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        context = activity;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingsMain = new SettingsMain(context);

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatMessage message = chatMessageList.get(position);
        View vi = convertView;
        if (message.isMine())
            vi = inflater.inflate(R.layout.item_chat_layout, null);
        else
            vi = inflater.inflate(R.layout.item_chat_received_layout, null);

//        HtmlTextView msg = vi.findViewById(R.id.message_text);
        TextView tv_message = vi.findViewById(R.id.message);
        TextView tv_date = vi.findViewById(R.id.tv_date);
        ImageView imageView = vi.findViewById(R.id.profile_image);
        LinearLayout bubbleLayout = vi.findViewById(R.id.chat_bubble);
//        if (message.isMine()) {
//            msg.setHtml("<font color=\"#969696\">" + message.getBody() + "<br><br>"
//                    + "<small><font color=\"#949494\">" + message.getDate());
        tv_message.setText(message.getBody());
        tv_date.setText(message.getDate());
//        } else {
//            msg.setHtml("<font color=\"#FFFFFF\">" + message.getBody() + "<br><br>"
//                    + "<small><font color=\"#FFFFFF\">" + message.getDate());
//        }
        RelativeLayout parent_layout = vi
                .findViewById(R.id.bubble_layout_parent);

        Picasso.with(context).load(message.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(imageView);
        // if message is mine then align to right
        if (settingsMain.getRTL()) {
            if (message.isMine()) {
                tv_message.setPaddingRelative(10, 5, 30, 0);
                tv_date.setPaddingRelative(10, 10, 30, 0);
                bubbleLayout.setBackgroundResource(R.drawable.ic_rtl_send_message);

//                bubbleLayout.setBackgroundResource(mDrawable);
//                parent_layout.setGravity(Gravity.END);
//                parent_layout.removeView(imageView);
//                parent_layout.addView(imageView, 1);
            }
            // If not mine then align to left
            else {
                bubbleLayout.setBackgroundResource(R.drawable.ic_rtl_received_message);
                tv_message.setPaddingRelative(30, 5, 10, 0);
                tv_date.setPaddingRelative(30, 10, 10, 0);
            }
        }

        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}