package com.kathryn.kathryn.profile.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.modelsList.myAdsModel;


public class ItemExpireAdsAdapter extends RecyclerView.Adapter<ItemExpireAdsAdapter.MyViewHolder> {
    com.kathryn.kathryn.utills.Network.RestService restService;
    private ArrayList<myAdsModel> list;
    private com.kathryn.kathryn.helper.MyAdsOnclicklinstener onItemClickListener;
    private Context mContext;
    private com.kathryn.kathryn.utills.SettingsMain settingsMain;
    private ArrayList<String> temp;

    public ItemExpireAdsAdapter(Context context, ArrayList<myAdsModel> Data) {
        this.list = Data;
        this.mContext = context;
        this.settingsMain = new com.kathryn.kathryn.utills.SettingsMain(mContext);
        restService = com.kathryn.kathryn.utills.UrlController.createService(com.kathryn.kathryn.utills.Network.RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), mContext);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemof_user_adds, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final myAdsModel feedItem = list.get(position);

        holder.name.setText(list.get(position).getName());

        if (!TextUtils.isEmpty(feedItem.getImage())) {
            Picasso.with(mContext).load(feedItem.getImage())
                    .resize(270, 270).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
        holder.priceTV.setText(list.get(position).getPrice());

        holder.spinner.setTag(list.get(position).getAdId());
        holder.linearLayout.setTag(list.get(position).getAdId());

        if (list.get(position).getAdType().equals("draft")) {
            holder.spinner.setVisibility(View.VISIBLE);

            holder.delAd.setText(list.get(position).getDelAd());
            holder.editAd.setText(list.get(position).getEditAd());

            holder.layoutDellAd.setTag(list.get(position).getAdId());
            holder.layoutEditAd.setTag(list.get(position).getAdId());

            temp = list.get(position).getSpinerValue();
            holder.statusTV.setText(list.get(position).getAdStatusValue());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, list.get(position).getSpinerData());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setSelection(list.get(position).getSpinerData().indexOf(list.get(position).getAdStatusValue()));

            if (list.get(position).getAdStatus().equals("expired")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#d9534f"));
            } else if (list.get(position).getAdStatus().equals("active")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#4caf50"));
            } else if (list.get(position).getAdStatus().equals("sold")) {
                holder.statusTV.setBackgroundColor(Color.parseColor("#3498db"));
            }

            View.OnClickListener listener2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.delViewOnClick(v, position);
                }
            };
            View.OnClickListener listener3 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.editViewOnClick(v, position);
                }
            };

            holder.removeFavBtn.setVisibility(View.GONE);
            holder.layoutEditAd.setOnClickListener(listener3);
            holder.layoutDellAd.setOnClickListener(listener2);

        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };


        holder.linearLayout.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(com.kathryn.kathryn.helper.MyAdsOnclicklinstener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    private void updateexpire(String adId, String adStatus) {

        if (com.kathryn.kathryn.utills.SettingsMain.isConnectingToInternet(mContext)) {

            com.kathryn.kathryn.utills.SettingsMain.showDilog(mContext);

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adId);
            params.addProperty("ad_status", adStatus);

            Log.d("info Send AdChngStatus", params.toString());
            Call<ResponseBody> myCall = restService.postUpdateAdStatus(params, com.kathryn.kathryn.utills.UrlController.AddHeaders(mContext));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdStatus Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info AdsStatus Change", "" + response.get("message").toString());
                                Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                com.kathryn.kathryn.utills.SettingsMain.reload(mContext, "MyAdsExpire");

                            } else {
                                Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        }
                    } catch (JSONException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    Log.d("info AdStatus error", String.valueOf(t));
                    Log.d("info AdStatus error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
            Toast.makeText(mContext, "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    private void update(String adId, String adStatus) {

        if (com.kathryn.kathryn.utills.SettingsMain.isConnectingToInternet(mContext)) {

            com.kathryn.kathryn.utills.SettingsMain.showDilog(mContext);

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adId);
            params.addProperty("ad_status", adStatus);

            Log.d("info Send AdChngStatus", params.toString());
            Call<ResponseBody> myCall = restService.postUpdateAdStatus(params, com.kathryn.kathryn.utills.UrlController.AddHeaders(mContext));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdStatus Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info AdsStatus Change", "" + response.get("message").toString());
                                Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                com.kathryn.kathryn.utills.SettingsMain.reload(mContext, "MyAdsExpire");

                            } else {
                                Toast.makeText(mContext, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        }
                    } catch (JSONException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    com.kathryn.kathryn.utills.SettingsMain.hideDilog();
                    Log.d("info AdStatus error", String.valueOf(t));
                    Log.d("info AdStatus error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            com.kathryn.kathryn.utills.SettingsMain.hideDilog();
            Toast.makeText(mContext, "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, priceTV, editAd, delAd, statusTV, removeFavBtn,EDitRejected;
        ImageView mainImage;
        RelativeLayout linearLayout, relativeLayoutSpiner, buttonLayout;
        Spinner spinner;
        LinearLayout layoutEditAd, layoutDellAd;
        View EditView;

        boolean spinnerTouched = false;

        MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.text_view_name);
            priceTV = v.findViewById(R.id.prices);
            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));

            statusTV = v.findViewById(R.id.textView4);
            delAd = v.findViewById(R.id.delAdd);
            editAd = v.findViewById(R.id.editAdd);

            spinner = v.findViewById(R.id.spinner);
            mainImage = v.findViewById(R.id.image_view);
            spinner.setVisibility(View.GONE);
            relativeLayoutSpiner = v.findViewById(R.id.rel1);
            linearLayout = v.findViewById(R.id.linear_layout_card_view);
            EDitRejected=v.findViewById(R.id.textViewEDitwla);
            removeFavBtn = v.findViewById(R.id.textView17);
            EditView=v.findViewById(R.id.editView);
            layoutEditAd = v.findViewById(R.id.layoutEditAd);
            layoutDellAd = v.findViewById(R.id.layoutDellAd);
            buttonLayout = v.findViewById(R.id.buttonLayout);


            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println("Real touch felt.");
                    spinnerTouched = true;
                    return false;
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                    if (spinnerTouched) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                        alert.setTitle(settingsMain.getGenericAlertTitle());
                        alert.setCancelable(false);
                        alert.setMessage(settingsMain.getGenericAlertMessage());
                        alert.setPositiveButton(settingsMain.getGenericAlertOkText(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                update(spinner.getTag().toString(), temp.get(i));
//                                updateexpire(spinner.getTag().toString(), temp.get(i));
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton(settingsMain.getGenericAlertCancelText(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                    }
                    spinnerTouched = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

}
