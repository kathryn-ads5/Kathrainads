package com.kathryn.kathryn.utills;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.kathryn.kathryn.R;

/**
 * Created by Glixen Technologies on 18/01/2018.
 */

public class Adforest_PopupManager {

    private Context context;
    private AdForest_PopupModel popupModel;

    public interface ConfirmInterface{
     void onConfirmClick(Dialog dialog);
    }
    public interface NoInternetInterface{
        void onButtonClick(DialogInterface dialog);
        void onNoClick(DialogInterface dialog);
    }
    private ConfirmInterface confirmInterface;
    public Adforest_PopupManager(Context context, ConfirmInterface confirmInterface) {
        this.context = context;
        this.confirmInterface = confirmInterface;
    }



        //requires only exit text
    private void showCustomTitleDialog(String title){
        popupModel = SettingsMain.getPopupSettings(context);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.popup_delete);
        //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView deleteJobTextView = dialog.findViewById(R.id.txt_delete_job);
        deleteJobTextView.setText(title);
        Button confirmButton = dialog.findViewById(R.id.btn_confirm);
        confirmButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        final Button closeButton = dialog.findViewById(R.id.btn_close );

        confirmButton.setText("COnfirm");
        deleteJobTextView.setText("exit");
         closeButton.setText("cancel");

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmInterface!=null)
                confirmInterface.onConfirmClick(dialog);
                //dialog.dismiss();
            }
        });
        dialog.show();

        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, (int) confirmButton.getResources().getDimension(R.dimen.saved_jobs_popup_height));}

        public void nokri_showPopupWithCustomMessage(String message){
                showCustomTitleDialog(message);
        }


    public static void nokri_showNoInternetAlert(Context context, final NoInternetInterface noInternetInterface){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Connection Lost!")
                .setMessage("Close App?").setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                            noInternetInterface.onButtonClick(dialog);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noInternetInterface.onNoClick(dialog);
            }
        })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



}

