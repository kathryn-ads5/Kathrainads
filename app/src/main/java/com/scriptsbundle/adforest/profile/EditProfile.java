package com.kathryn.kathryn.profile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.login.LoginManager;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonObject;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.kathryn.kathryn.R;
import com.kathryn.kathryn.SplashScreen;
import com.kathryn.kathryn.adapters.SpinnerAndListAdapter;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.modelsList.subcatDiloglist;
import com.kathryn.kathryn.public_profile.social_icons;
import com.kathryn.kathryn.signinorup.MainActivity;
import com.kathryn.kathryn.utills.AnalyticsTrackers;
import com.kathryn.kathryn.utills.CustomBorderDrawable;
import com.kathryn.kathryn.utills.Network.RestService;
import com.kathryn.kathryn.utills.RuntimePermissionHelper;
import com.kathryn.kathryn.utills.SettingsMain;
import com.kathryn.kathryn.utills.UrlController;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class EditProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, RuntimePermissionHelper.permissionInterface, AdapterView.OnItemClickListener {

    SettingsMain settingsMain;
    TextView verifyBtn, textViewRateNo, textViewUserName, textViewLastLogin;
    TextView textViewAdsSold, textViewTotalList, textViewInactiveAds,textViewExppiry;
    TextView textViewName, textViewPhoneNumber, textViewLocation, textViewMainTitle, textViewImage,
            textViewAccType, textViewIntroduction;
    TextView btnCancel, btnSend, btnChangePwd, btnDeleteAccount;
    ImageView btnSeletImage;
    EditText editTextName, editTextPhone, editTextIntroduction;
    List<View> allViewInstanceforCustom = new ArrayList<>();
    List<View> fieldsValidationforCustom = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextViewLocation;
    RatingBar ratingBar;
    ImageView imageViewProfile;
    JSONObject jsonObjectforCustom, extraText;
    Spinner spinnerACCType;
    LinearLayout viewSocialIconsLayout;
    RestService restService;
    boolean checkValidation = true;
    LinearLayout publicProfileCustomIcons;
    RuntimePermissionHelper runtimePermissionHelper;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private JSONObject jsonObject;
    private JSONObject jsonObjectChnge, jsonObject_select_pic, deleteAccountJsonObject;
    private PlacesClient placesClient;
    EditText placesContainer;
    String address_by_mapbox;
    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        settingsMain = new SettingsMain(getActivity());

        publicProfileCustomIcons = view.findViewById(R.id.publicProfileCustomIcons);
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);
        publicProfileCustomIcons.setVisibility(View.GONE);
//        publicProfileCustomIcons.setBackgroundResource(R.drawable.socialicons);

        textViewName = view.findViewById(R.id.textViewName);
        textViewPhoneNumber = view.findViewById(R.id.textViewPhone);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        textViewAccType = view.findViewById(R.id.textViewAccount_type);
        textViewMainTitle = view.findViewById(R.id.textView);
        textViewIntroduction = view.findViewById(R.id.textViewIntroduction);
        textViewImage = view.findViewById(R.id.textViewSetImage);
        btnCancel = view.findViewById(R.id.textViewCancel);
        btnSeletImage = view.findViewById(R.id.imageSelected);
        btnSend = view.findViewById(R.id.textViewSend);
        btnChangePwd = view.findViewById(R.id.textViewChangePwd);
        btnDeleteAccount = view.findViewById(R.id.deleteAccount);
        textViewLastLogin = view.findViewById(R.id.loginTime);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);
        spinnerACCType = view.findViewById(R.id.spinner);

        imageViewProfile = view.findViewById(R.id.image_view);
        ratingBar = view.findViewById(R.id.ratingBar);
        viewSocialIconsLayout = view.findViewById(R.id.editProfileCustomLayout);
        viewSocialIconsLayout.setVisibility(View.INVISIBLE);

        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        textViewAdsSold = view.findViewById(R.id.share);
        textViewTotalList = view.findViewById(R.id.addfav);
        textViewInactiveAds = view.findViewById(R.id.report);
        textViewExppiry = view.findViewById(R.id.expired);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);

        autoCompleteTextViewLocation = view.findViewById(R.id.editTexLocation);
        placesContainer=view.findViewById(R.id.et_location_mapBox);

        btnSeletImage.setOnClickListener(view1 -> runtimePermissionHelper.requestStorageCameraPermission(1));
        btnSend.setOnClickListener(view12 -> adforest_sendData());

        btnCancel.setOnClickListener(view13 -> getActivity().onBackPressed());
        adforest_setAllViewsText();

        ((HomeActivity) getActivity()).changeImage();

        btnChangePwd.setTextColor(Color.parseColor(settingsMain.getMainColor()));
        btnChangePwd.setOnClickListener(view14 -> adforest_showDilogChangePassword());

        ratingBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                RatingFragment fragment = new RatingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", settingsMain.getUserLogin());
                bundle.putBoolean("isprofile", true);
                fragment.setArguments(bundle);

                replaceFragment(fragment, "RatingFragment");
            }
            return true;
        });
        editTextIntroduction = (EditText) view.findViewById(R.id.textArea_information);

        editTextIntroduction.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });
        btnDeleteAccount.setOnClickListener(v -> adforest_showDeteleDialog());

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        placesClient = com.google.android.libraries.places.api.Places.createClient(getContext());
        placesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new PlaceAutocomplete.IntentBuilder()
                            .accessToken(getString(R.string.access_token))
                            .placeOptions(PlaceOptions.builder().backgroundColor(Color.parseColor("#EEEEEE")).limit(10).build(PlaceOptions.MODE_CARDS))
                            .build(getActivity());
                    startActivityForResult(intent,35);
                }
            });
        autoCompleteTextViewLocation.setOnItemClickListener(this);

        autoCompleteTextViewLocation.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                manageAutoComplete(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

        if (SplashScreen.gmap_has_countries) {
            request.setCountry(SplashScreen.gmap_countries);
        }
        if (settingsMain.getAlertDialogMessage("location_type").equals("regions")) {
            request.setTypeFilter(TypeFilter.ADDRESS);
        } else {
            request
                    .setTypeFilter(TypeFilter.REGIONS);
        }
        request.setSessionToken(token)
                .setQuery(query);


        placesClient.findAutocompletePredictions(request.build()).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]); // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
            autoCompleteTextViewLocation.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String placeId = ids.get(position);
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
// Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            com.google.android.libraries.places.api.model.Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });


    }

    private void adforest_sendData() {
        checkValidation = true;

        if (SettingsMain.isConnectingToInternet(getActivity())) {


            subcatDiloglist subDiloglist = (subcatDiloglist) spinnerACCType.getSelectedView().getTag();
            JsonObject params = new JsonObject();
            if (jsonObjectforCustom != null && adforest_getDataFromDynamicViewsForCustom() != null) {
                params.add("social_icons", adforest_getDataFromDynamicViewsForCustom());
                if (fieldsValidationforCustom.size() > 0) {

                    for (int i = 0; i < fieldsValidationforCustom.size(); i++) {

                        if (fieldsValidationforCustom.get(i) instanceof EditText) {
                            EditText editText = (EditText) fieldsValidationforCustom.get(i);
                            if (!URLUtil.isValidUrl(editText.getText().toString()) && !editText.getText().toString().equals("")) {
                                editText.setError("!");
                                checkValidation = false;
                            }
                        }
                    }
                }
            }
            params.addProperty(editTextName.getTag().toString(), editTextName.getText().toString());
            try {
                String phoneNumber = editTextPhone.getText().toString();
                if (extraText.getBoolean("is_verification_on")) {
                    if (phoneNumber.contains("+"))
                        params.addProperty(editTextPhone.getTag().toString(), phoneNumber);
                    else
                        params.addProperty(editTextPhone.getTag().toString(), "+" + phoneNumber);
                } else
                    params.addProperty(editTextPhone.getTag().toString(), phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.addProperty(autoCompleteTextViewLocation.getTag().toString(), autoCompleteTextViewLocation.getText().toString());
            params.addProperty(spinnerACCType.getTag().toString(), subDiloglist.getId());
            params.addProperty(editTextIntroduction.getTag().toString(), editTextIntroduction.getText().toString());

            Log.d("info Send UpdatePofile", "" + params.toString());
            if (!checkValidation) {
                Toast.makeText(getContext(), "Invalid URL specified", Toast.LENGTH_SHORT).show();
            } else {
                SettingsMain.showDilog(getActivity());
                Call<ResponseBody> req = restService.postUpdateProfile(params, UrlController.UploadImageAddHeaders(getActivity()));
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseobj) {
                        try {
                            if (responseobj.isSuccessful()) {
                                Log.d("info UpdateProfile Resp", "" + responseobj.toString());

                                JSONObject response = new JSONObject(responseobj.body().string());
                                if (response.getBoolean("success")) {
                                    Log.d("info UpdateProfile obj", "" + response.toString());
                                    settingsMain.setUserName(response.getJSONObject("data").getString("display_name"));
                                    settingsMain.setUserPhone(response.getJSONObject("data").getString("phone"));
                                    settingsMain.setUserImage(response.getJSONObject("data").getString("profile_img"));
                                    ((HomeActivity) getActivity()).changeImage();

                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    SettingsMain.reload(getActivity(), "EditProfile");
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
                        SettingsMain.hideDilog();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (t instanceof TimeoutException) {
                            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                            Log.d("info UpdateProfile", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info UpdateProfile err", String.valueOf(t));
                            Log.d("info UpdateProfile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            }

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {

        final CharSequence[] items;
        try {
            items = new CharSequence[]{jsonObject_select_pic.getString("camera"), jsonObject_select_pic.getString("library")
                    , jsonObject_select_pic.getString("cancel")};

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(jsonObject_select_pic.getString("title"));
            builder.setItems(items, (dialog, item) -> {
                if (item == 0) {
                    {
                        userChoosenTask = "Take Photo";
                        cameraIntent();
                    }
                } else if (item == 1) {
                    {
                        userChoosenTask = "Choose from Library";
                        galleryIntent();
                    }
                } else if (item == 3) {
                    dialog.dismiss();
                }

            });
            builder.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (requestCode == 35) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                CarmenFeature feature = PlaceAutocomplete.getPlace(data);
                Point point = feature.center();
//                latitude = point.latitude();
//                longitude = point.longitude();

                address_by_mapbox = feature.placeName();
                placesContainer.setText(address_by_mapbox);


            }
        }

    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        Uri tempUri = SettingsMain.getImageUri(getActivity(), thumbnail);
        File finalFile = new File(SettingsMain.getRealPathFromURI(getActivity(), tempUri));
        galleryImageUpload(tempUri);


        btnSeletImage.setImageURI(tempUri);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Uri tempUri = SettingsMain.getImageUri(getActivity(), bm);
                File finalFile = new File(SettingsMain.getRealPathFromURI(getActivity(), tempUri));

                Log.d("imageval", "" + finalFile.length());
                Log.d("imageval", "" + tempUri.toString());

                galleryImageUpload(tempUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        btnSeletImage.setImageBitmap(bm);
    }

    private void galleryImageUpload(final Uri absolutePath) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            final File finalFile = new File(SettingsMain.getRealPathFromURI(getActivity(), absolutePath));
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContext().getContentResolver().getType(absolutePath)),
                            finalFile
                    );
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("profile_img", finalFile.getName(), requestFile);

            Call<ResponseBody> req = restService.postUploadProfileImage(body, UrlController.UploadImageAddHeaders(getActivity()));
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {
                        Log.v("info Upload Responce", response.toString());
                        JSONObject responseobj = null;
                        try {
                            responseobj = new JSONObject(response.body().string());
                            if (responseobj.getBoolean("success")) {
                                try {
                                    Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    Log.d("info data object", "" + responseobj.getJSONObject("data"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //noinspection ResultOfMethodCallIgnored
                                finalFile.delete();
                            } else {
                                Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            SettingsMain.hideDilog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            SettingsMain.hideDilog();
                        }

                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Upload profile", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Upload profile err", String.valueOf(t));
                        Log.d("info Upload profile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }


    }

    private void cameraImageUpload(final String absolutePath) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            final File file = new File(absolutePath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profile_img", file.getName(), requestBody);

            Call<ResponseBody> req = restService.postUploadProfileImage(fileToUpload, UrlController.UploadImageAddHeaders(getActivity()));
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {
                        Log.v("info Upload Responce", response.toString());
                        JSONObject responseobj = null;
                        try {
                            responseobj = new JSONObject(response.body().string());
                            if (responseobj.getBoolean("success")) {
                                try {
                                    Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    Log.d("info data object", "" + responseobj.getJSONObject("data"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //noinspection ResultOfMethodCallIgnored
                                file.delete();
                            } else {
                                Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            SettingsMain.hideDilog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            SettingsMain.hideDilog();
                        }

                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Upload profile", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Upload profile err", String.valueOf(t));
                        Log.d("info Upload profile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }


    }

    private void adforest_setAllViewsText() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), getActivity());
            Call<ResponseBody> myCall = restService.getEditProfileDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Edit Profile ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info Edit ProfileGet", "" + response.getJSONObject("data"));

                                jsonObject = response.getJSONObject("data");
                                extraText = response.getJSONObject("extra_text");

                                textViewLastLogin.setText(jsonObject.getJSONObject("profile_extra").getString("last_login"));
                                textViewUserName.setText(jsonObject.getJSONObject("profile_extra").getString("display_name"));
                                getActivity().setTitle(response.getJSONObject("data").getString("page_title_edit"));

                                settingsMain.setUserName(jsonObject.getJSONObject("profile_extra").getString("display_name"));
                                settingsMain.setUserImage(jsonObject.getJSONObject("profile_extra").getString("profile_img"));

                                ((HomeActivity) getActivity()).changeImage();

                                Picasso.with(getContext()).load(jsonObject.getJSONObject("profile_extra").getString("profile_img"))
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(imageViewProfile);

                                Picasso.with(getContext()).load(jsonObject.getJSONObject("profile_extra").getString("profile_img"))
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(btnSeletImage);
                                verifyBtn.setText(jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("text"));
                                verifyBtn.setBackground(CustomBorderDrawable.customButton(0, 0, 0, 0,
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"),
                                        jsonObject.getJSONObject("profile_extra").getJSONObject("verify_buton").getString("color"), 3));

                                textViewAdsSold.setText(jsonObject.getJSONObject("profile_extra").getString("ads_sold"));
                                textViewTotalList.setText(jsonObject.getJSONObject("profile_extra").getString("ads_total"));
                                textViewInactiveAds.setText(jsonObject.getJSONObject("profile_extra").getString("ads_inactive"));
                            textViewExppiry.setText(jsonObject.getJSONObject("profile_extra").getString("ads_expired"));

                                ratingBar.setNumStars(5);
                                ratingBar.setRating(Float.parseFloat(jsonObject.getJSONObject("profile_extra").getJSONObject("rate_bar").getString("number")));
                                textViewRateNo.setText(jsonObject.getJSONObject("profile_extra").getJSONObject("rate_bar").getString("text"));

                                textViewMainTitle.setText(response.getJSONObject("extra_text").getString("profile_edit_title"));
                                btnSend.setText(response.getJSONObject("extra_text").getString("save_btn"));
                                btnCancel.setText(response.getJSONObject("extra_text").getString("cancel_btn"));

                                btnSeletImage.setContentDescription(response.getJSONObject("extra_text").getString("select_image"));

                                jsonObjectChnge = response.getJSONObject("extra_text").getJSONObject("change_pass");
                                jsonObject_select_pic = response.getJSONObject("extra_text").getJSONObject("select_pic");

                                btnChangePwd.setText(jsonObjectChnge.getString("title"));
                                textViewName.setText(jsonObject.getJSONObject("display_name").getString("key"));
                                editTextName.setText(jsonObject.getJSONObject("display_name").getString("value"));
                                editTextName.setTag(jsonObject.getJSONObject("display_name").getString("field_name"));
                                textViewAccType.setText(jsonObject.getJSONObject("account_type").getString("key"));
                                spinnerACCType.setTag(jsonObject.getJSONObject("account_type").getString("field_name"));
                                textViewPhoneNumber.setText(jsonObject.getJSONObject("phone").getString("key"));
                                editTextPhone.setText(jsonObject.getJSONObject("phone").getString("value"));
                                editTextPhone.setTag(jsonObject.getJSONObject("phone").getString("field_name"));
                                textViewImage.setText(jsonObject.getJSONObject("profile_img").getString("key"));
                                textViewLocation.setText(jsonObject.getJSONObject("location").getString("key"));

                                textViewIntroduction.setText(jsonObject.getJSONObject("introduction").getString("key"));
                                editTextIntroduction.setText(jsonObject.getJSONObject("introduction").getString("value"));
                                editTextIntroduction.setTag(jsonObject.getJSONObject("introduction").getString("field_name"));


                                autoCompleteTextViewLocation.setText(jsonObject.getJSONObject("location").getString("value"));
                                autoCompleteTextViewLocation.setTag(jsonObject.getJSONObject("location").getString("field_name"));

                                if (response.getJSONObject("data").getBoolean("is_show_social")) {
                                    Log.d("Info custom", "====Add all===");
                                    social_icons.adforest_setViewsForCustom(response.getJSONObject("data"), publicProfileCustomIcons, getContext());
                                }
                                final JSONArray dropDownJSONOpt = jsonObject.getJSONArray("account_type_select");
                                final ArrayList<subcatDiloglist> SpinnerOptions;
                                SpinnerOptions = new ArrayList<>();
                                for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                                    subcatDiloglist subDiloglist = new subcatDiloglist();
                                    subDiloglist.setId(dropDownJSONOpt.getJSONObject(j).getString("key"));
                                    subDiloglist.setName(dropDownJSONOpt.getJSONObject(j).getString("value"));

                                    SpinnerOptions.add(subDiloglist);
                                }

                                final SpinnerAndListAdapter spinnerAndListAdapter;
                                spinnerAndListAdapter = new SpinnerAndListAdapter(getActivity(), SpinnerOptions, true);

                                spinnerACCType.setAdapter(spinnerAndListAdapter);
                                if (response.getJSONObject("data").getBoolean("is_show_social")) {
                                    Log.d("Info custom", "====Add all===");
                                    adforest_setViewsForCustom(jsonObject);
                                }

                                if (jsonObject.getBoolean("can_delete_account")) {
                                    Log.d("info in check", "asdasd");
                                    btnDeleteAccount.setVisibility(View.VISIBLE);
                                    deleteAccountJsonObject = jsonObject.getJSONObject("delete_account");
                                    btnDeleteAccount.setText(deleteAccountJsonObject.getString("text"));
                                }
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
                    Log.d("info Edit Profile error", String.valueOf(t));
                    Log.d("info Edit Profile error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_setViewsForCustom(JSONObject jsonObjec) {

        allViewInstanceforCustom.clear();
        fieldsValidationforCustom.clear();
        try {
            jsonObjectforCustom = jsonObjec;
            Log.d("info Custom data ===== ", jsonObjectforCustom.getJSONArray("social_icons").toString());
            JSONArray customOptnList = jsonObjectforCustom.getJSONArray("social_icons");

            viewSocialIconsLayout.setVisibility(View.VISIBLE);
            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {
                CardView cardView = new CardView(getActivity());
                cardView.setCardElevation(2);
                cardView.setUseCompatPadding(true);
                cardView.setRadius(0);
                cardView.setPaddingRelative(10, 10, 10, 10);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.topMargin = 10;
                params1.bottomMargin = 10;
                cardView.setLayoutParams(params1);

                JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);

                TextInputLayout til = new TextInputLayout(getActivity());
                til.setHint(eachData.getString("key"));
                EditText et = new EditText(getActivity());
                et.setTextSize(14);
                if (!eachData.getString("value").equals("")) {
                    et.setText(eachData.getString("value"));
                }

                et.getBackground().clearColorFilter();
                allViewInstanceforCustom.add(et);
                fieldsValidationforCustom.add(et);
                til.addView(et);
                cardView.addView(til);

                viewSocialIconsLayout.addView(cardView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JsonObject adforest_getDataFromDynamicViewsForCustom() {
        JsonObject optionsObj = null;
        try {
            JSONArray customOptnList = jsonObjectforCustom.getJSONArray("social_icons");
            optionsObj = new JsonObject();
            for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {
                JSONObject eachData = customOptnList.getJSONObject(noOfViews);

                TextView textView = (TextView) allViewInstanceforCustom.get(noOfViews);
                if (!textView.getText().toString().equalsIgnoreCase(""))
                    optionsObj.addProperty(eachData.getString("field_name"), textView.getText().toString());
                else
                    optionsObj.addProperty(eachData.getString("field_name"), textView.getText().toString());
                Log.d("variant_name", textView.getText().toString() + "");
            }

//            hideSoftKeyboard();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("array us custom", (optionsObj != null ? optionsObj.toString() : null) + " ==== size====  " + allViewInstanceforCustom.size());

        return optionsObj;
    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    void adforest_showDilogChangePassword() {

        final Dialog dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        final EditText editTextOld = dialog.findViewById(R.id.editText);
        final EditText editTextNew = dialog.findViewById(R.id.editText2);
        final EditText editTextConfirm = dialog.findViewById(R.id.editText3);

        try {
            Send.setText(btnSend.getText());
            Cancel.setText(btnCancel.getText());

            editTextOld.setHint(jsonObjectChnge.getString("old_pass"));
            editTextNew.setHint(jsonObjectChnge.getString("new_pass"));
            editTextConfirm.setHint(jsonObjectChnge.getString("new_pass_con"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(editTextOld.getText().toString()) &&
                        !TextUtils.isEmpty(editTextNew.getText().toString()) &&
                        !TextUtils.isEmpty(editTextConfirm.getText().toString()))
                    if (editTextNew.getText().toString().equals(editTextConfirm.getText().toString())) {

                        if (SettingsMain.isConnectingToInternet(getActivity())) {

                            SettingsMain.showDilog(getActivity());

                            JsonObject params = new JsonObject();
                            params.addProperty("old_pass", editTextOld.getText().toString());
                            params.addProperty("new_pass", editTextNew.getText().toString());
                            params.addProperty("new_pass_con", editTextConfirm.getText().toString());

                            Log.d("info sendChange Passwrd", params.toString());
                            Call<ResponseBody> myCall = restService.postChangePasswordEditProfile(params, UrlController.AddHeaders(getActivity()));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                        if (responseObj.isSuccessful()) {
                                            Log.d("info ChangePassword Res", "" + responseObj.toString());

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                dialog.dismiss();
                                                settingsMain.setUserPassword(editTextNew.getText().toString());
                                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                                    if (t instanceof TimeoutException) {
                                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                                        SettingsMain.hideDilog();
                                    }
                                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                                        SettingsMain.hideDilog();
                                    }
                                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                                        Log.d("info ChangePassword ", "NullPointert Exception" + t.getLocalizedMessage());
                                        SettingsMain.hideDilog();
                                    } else {
                                        SettingsMain.hideDilog();
                                        Log.d("info ChangePassword err", String.valueOf(t));
                                        Log.d("info ChangePassword err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                    }
                                }
                            });
                        } else {
                            SettingsMain.hideDilog();
                            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Toast.makeText(getActivity(), "" + jsonObjectChnge.getString("err_pass"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void adforest_showDeteleDialog() {
        try {

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(deleteAccountJsonObject.getString("text"));
            alert.setCancelable(false);
            alert.setMessage(deleteAccountJsonObject.getString("popuptext"));
            alert.setPositiveButton(deleteAccountJsonObject.getString("btn_submit"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    adforest_deleteAccount();
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton(deleteAccountJsonObject.getString("btn_cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void adforest_deleteAccount() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", settingsMain.getUserLogin());
            Log.d("info Send terms id =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postDeleteAccount(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info terms responce ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                settingsMain.setUserLogin("0");
                                settingsMain.setFireBaseId("");
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "false");
                                editor.apply();
                                LoginManager.getInstance().logOut();
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                if (settingsMain.getCheckOpen()) {
                                    settingsMain.isAppOpen(true);
                                }

                            } else {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info CustomPages ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info CustomPages err", String.valueOf(t));
                        Log.d("info CustomPages err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        try {
            if (settingsMain.getAnalyticsShow() && !settingsMain.getAnalyticsId().equals(""))
                AnalyticsTrackers.getInstance().trackScreenView("Edit Profile");
            super.onResume();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccessPermission(int code) {
        selectImage();
    }
}
