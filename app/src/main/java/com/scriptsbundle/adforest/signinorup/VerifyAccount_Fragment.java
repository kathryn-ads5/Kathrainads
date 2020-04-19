package com.kathryn.kathryn.signinorup;


import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyAccount_Fragment extends Fragment implements View.OnClickListener {
    Activity activity;
    SettingsMain settingsMain;
    LinearLayout linearLayoutLogo;
    RestService restService;
    private View view;
    private EditText verifyCode;
    private TextView submit, back, headingText;
    private ImageView imageViewLogo;

    public VerifyAccount_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_verify_account, container, false);
        activity = getActivity();
        settingsMain = new SettingsMain(activity);
        restService = UrlController.createService(RestService.class);
        adforest_initViews();
        adforest_setDataToViews();
        setListeners();
        return view;
    }

    // Initialize the views
    @SuppressWarnings("ResourceType")
    private void adforest_initViews() {

        verifyCode = view.findViewById(R.id.et_verify_code);
        submit = view.findViewById(R.id.verify_button);
        back = view.findViewById(R.id.backToLoginBtn);

        imageViewLogo = view.findViewById(R.id.logoimage);
        headingText = view.findViewById(R.id.heading);
        linearLayoutLogo = view.findViewById(R.id.logo);

        linearLayoutLogo.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));


        // Setting text selector over textviews
        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            //noinspection deprecation
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            back.setTextColor(csl);
            submit.setTextColor(csl);

        } catch (Exception e) {
            Log.d("err", e.toString());
        }

    }

    void adforest_setDataToViews() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getVerifyAccountViewDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info verify account", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info verify account obj", "" + response.getJSONObject("data"));
                                if (!response.getJSONObject("data").getString("logo").equals(""))
                                    Picasso.with(getContext()).load(response.getJSONObject("data").getString("logo"))
                                            .error(R.drawable.logo)
                                            .placeholder(R.drawable.logo)
                                            .into(imageViewLogo);

                                headingText.setText(response.getJSONObject("data").getString("text"));
                                verifyCode.setHint(response.getJSONObject("data").getString("confirm_placeholder"));
                                submit.setText(response.getJSONObject("data").getString("submit_text"));
                                back.setText(response.getJSONObject("data").getString("back_text"));

                                SettingsMain.hideDilog();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info ForGotpass error", String.valueOf(t));
                    Log.d("info ForGotpass error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }

    }

    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:
                // Replace Login Fragment on Back Presses
                new MainActivity().adforest_replaceLoginFragment();
                break;
            case R.id.verify_button:
                // Call Submit button task
                String getverifyCode = verifyCode.getText().toString();
                //verfiy code field empty or not
                if (getverifyCode.equals("")) {
                    verifyCode.requestFocus();
                    verifyCode.setError("!");
                } else adforest_submitButtonTask();
                break;
        }
    }

    private void adforest_submitButtonTask() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("confirm_code", verifyCode.getText().toString());
            if (!Utils.user_id.isEmpty())
                params.addProperty("user_id", Utils.user_id);

            RestService restService =
                    UrlController.createService(RestService.class);
            Call<ResponseBody> myCall = restService.postConfirmAccount(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Verfy account ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                                SettingsMain.hideDilog();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new MainActivity().adforest_replaceLoginFragment();
                                    }
                                }, 1000);

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
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info LoginPost error", String.valueOf(t));
                    Log.d("info LoginPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }


    }
}
