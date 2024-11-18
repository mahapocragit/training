package in.gov.pocra.training.activity.common.profile;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.co.appinventor.services_api.api.AppinventorApi;
import in.co.appinventor.services_api.api.AppinventorIncAPI;
import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;
import in.co.appinventor.services_api.listener.ApiCallbackCode;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.BuildConfig;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ProfileModel;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;
import in.gov.pocra.training.util.BitmapUtil;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MyProfileActivity extends AppCompatActivity implements ApiCallbackCode {

    private ImageView homeBack;
    private ImageView profileIView;

    // For Camera
    private static final String IMAGE_DIRECTORY = "/PoCRA_TRAINING";
    private ManagePermission managePermissions;
    private static final int APP_PERMISSION_REQUEST_CODE = 111;
    private File photoFile = null;

    private String selectedImage = "profile";
    private Transformation transformation;
    private EditText lNameEditText;
    private EditText fNameEditText;
    private EditText mNameEditText;
    private EditText emailEditText;
    private EditText mobileEditText;
    private EditText designationEditText;
    private Button profSubmitButton;
    private String rollId;
    private String userID;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        homeBack.setVisibility(View.VISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_profile));

        initialization();
        defaultConfiguration();

    }
    private void initialization() {

        // For Action Bar
        homeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileIView = (ImageView) findViewById(R.id.profileIView);
        lNameEditText = (EditText) findViewById(R.id.lNameEditText);
        fNameEditText = (EditText) findViewById(R.id.fNameEditText);
        mNameEditText = (EditText) findViewById(R.id.mNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        mobileEditText = (EditText) findViewById(R.id.mobileEditText);
        designationEditText = (EditText) findViewById(R.id.designationEditText);

        profSubmitButton = (Button) findViewById(R.id.profSubButton);

    }

    private void defaultConfiguration() {
        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        String uToken = AppSettings.getInstance().getValue(this, ApConstants.kUSER_TOKEN, ApConstants.kUSER_TOKEN);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            rollId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }
        if (!uToken.equalsIgnoreCase("userToken")) {
            userToken = uToken;
        }

        transformation = new RoundedTransformationBuilder()
                .borderColor(getResources().getColor(R.color.colorPrimaryDark))
                .borderWidthDp(1)
                .cornerRadiusDp(40)
                .oval(false)
                .build();

        Picasso.get()
                //.load("")
                .load(R.mipmap.ic_profile)
                .transform(transformation)
                .resize(150, 150)
                .centerCrop()
                .into(profileIView);


        // For Camera
        profileIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "first";
                if (Build.VERSION.SDK_INT < 19) {
                    takeImageFromCameraUri();
                } else {
                    if (checkUserPermission()) {
                        takeImageFromCameraUri();
                    }
                }
            }
        });

        profSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profSubmitButtonAction();
            }
        });

        /*
         // For Validation
        venueLocationEditText.setError(null);
        participantsEditText.setError("Input number of participants");
        participantsEditText.requestFocus();*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        setProfileData();
    }

    private void setProfileData() {
        String user_data = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_DATA, ApConstants.kLOGIN_DATA);
        String desgtn = AppSettings.getInstance().getValue(this, ApConstants.kDesignation, ApConstants.kDesignation);
        if (!user_data.equalsIgnoreCase("kLOGIN_DATA")) {

            try {
                JSONObject userJson = new JSONObject(user_data);
                ProfileModel pModel = new ProfileModel(userJson);
                String lName = pModel.getLast_name();
                String fName = pModel.getFirst_name();
                String mName = pModel.getMiddle_name();
                String email = pModel.getEmail();
                String mobile = pModel.getMobile();
                String designation = pModel.getDesignation();
                String profileImage = pModel.getProfile_pic();

                lNameEditText.setText(lName);
                fNameEditText.setText(fName);
                mNameEditText.setText(mName);
                emailEditText.setText(email);
                mobileEditText.setText(mobile);
                designationEditText.setText(desgtn);

                if (!profileImage.isEmpty()) {

                    Picasso.get()
                            .load(Uri.parse(profileImage))
                            .transform(transformation)
                            .resize(150, 150)
                            .centerCrop()
                            .into(profileIView);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void profSubmitButtonAction() {
        String lastName = lNameEditText.getText().toString().toString().trim();
        String firstName = fNameEditText.getText().toString().toString().trim();
        String middleName = mNameEditText.getText().toString().toString().trim();
        String emailId = emailEditText.getText().toString().toString().trim();
        // String mobileNumber = mobileEditText.getText().toString().toString().trim();
        // String designation = designationEditText.getText().toString().toString().trim();

        if (lastName.equalsIgnoreCase("")) {
            lNameEditText.setError("Input last name");
            lNameEditText.requestFocus();
        } else if (firstName.equalsIgnoreCase("")) {
            lNameEditText.setError(null);
            fNameEditText.setError("Input first name");
            fNameEditText.requestFocus();
      /*  }
        else if (middleName.equalsIgnoreCase("")) {
            fNameEditText.setError(null);
            mNameEditText.setError("Input middle name");
            mNameEditText.requestFocus();*/
            // code commented by shashank on 12-dec-2020 reported by Tejas.
        } else if (emailId.equalsIgnoreCase("")) {
            mNameEditText.setError(null);
            emailEditText.setError("Input email-id");
            emailEditText.requestFocus();
        } else if (!AppUtility.getInstance().isValidEmail(emailId)) {
            emailEditText.setError(null);
            emailEditText.setError("Input valid email-id");
            emailEditText.requestFocus();
        } /*else if (mobileNumber.equalsIgnoreCase("")) {
            emailEditText.setError(null);
            mobileEditText.setError("Input mobile number");
            mobileEditText.requestFocus();
        } else if (AppUtility.getInstance().isValidCallingNumber(mobileNumber)) {
            mobileEditText.setError(null);
            mobileEditText.setError("Input valid mobile number");
            mobileEditText.requestFocus();
        } else if (designation.equalsIgnoreCase("")) {
            mobileEditText.setError(null);
            designationEditText.setError("Input designation");
            designationEditText.requestFocus();
        }*/ else {
            emailEditText.setError(null);


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", userID);
                jsonObject.put("role_id", rollId);
                jsonObject.put("first_name", firstName);
                jsonObject.put("middle_name", middleName);
                jsonObject.put("last_name", lastName);
                jsonObject.put("email", emailId);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(jsonObject.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.updateUserProfileRequest(requestBody);

            DebugLog.getInstance().d("Profile_update_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Profile_update_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);

        }
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {
                // District Response
                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        JSONObject login_Data = jsonObject.getJSONObject("data");
                        AppSettings.getInstance().setValue(this, ApConstants.kLOGIN_DATA, login_Data.toString());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setProfileData();
                            }
                        }, 2000);

                    } else {
                        UIToastMessage.show(this, responseModel.getMsg());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onFailure(Object o, Throwable throwable, int i) {

    }


    // FOR CAMERA

    /**** For permission with ManagePermissionClass*****/
    private boolean checkUserPermission() {

        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            managePermissions = new ManagePermission(this, listPermissionsNeeded, APP_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), APP_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case APP_PERMISSION_REQUEST_CODE: {
                boolean isPermissionsGranted = managePermissions.processPermissionsResult(requestCode, permissions, grantResults);
                if (isPermissionsGranted) {
                    takeImageFromCameraUri();
                } else {
                    // toast("Permissions denied.")
                    managePermissions.checkPermission();
                }
            }
        }
    }


    private void takeImageFromCameraUri() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(takePictureIntent, 0);

        ResolveInfo res = listCam.get(0);
        final String packageName = res.activityInfo.packageName;
        final Intent intent = new Intent(takePictureIntent);
        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        Log.d("Camera Package Name", packageName);
        intent.setPackage(packageName);

        if (intent.resolveActivity(getPackageManager()) != null) {

            File photoDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
            if (!photoDirectory.exists()) {
                photoDirectory.mkdirs();
            }
            photoFile = new File(photoDirectory, selectedImage + Calendar.getInstance().getTimeInMillis() + ".jpg");

            Uri photoURI = null;
            if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".android.fileprovider", photoFile);
            } else {
                photoURI = Uri.fromFile(photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setClipData(ClipData.newRawUri("", photoURI));
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            startActivityForResult(intent, CAMERA);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                onCameraActivityResult();
            }
        }
    }

    public void onCameraActivityResult() {

        int rotate = 0;
        Bitmap bmp = null;

        if (photoFile.exists()) {

            bmp = BitmapUtil.decodeSampledBitmapFromPath(this, photoFile, 1050, true); // BitmapFactory.decodeFile(photopath);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            //profileIView.setImageBitmap(bmp);
            //Uri uri = Uri.fromFile(photoFile);

            final String imagePath = "file://" + photoFile;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.get()
                            //.load("")
                            // .load(photoFile)
                            .load(imagePath)
                            .transform(transformation)
                            .resize(150, 150)
                            .centerCrop()
                            .into(profileIView);

                    uploadImageOnServer(imagePath);
                }
            }, 2000);


            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(photoFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImageOnServer(String imagePath) {
        try {

            MultipartBody.Part partBody = null;

            DebugLog.getInstance().d("imgName=" + imagePath);

            Map<String, RequestBody> params = new HashMap<>();
            String userId = String.valueOf(userID);
            String roleId = String.valueOf(rollId);

            params.put("timestamp", AppinventorApi.toRequestBody(ApUtil.getCurrentTimeStamp()));
            params.put("user_id", AppinventorApi.toRequestBody(userId));
            params.put("role_id", AppinventorApi.toRequestBody(roleId));
            params.put("lat_lng", AppinventorApi.toRequestBody("00"));
            params.put("token", AppinventorApi.toRequestBody(userToken));

            //creating a file
            File file = new File(photoFile.getPath());
            // File file = new File(imagePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            partBody = MultipartBody.Part.createFormData("fileToUpload", file.getName(), reqFile);


            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.API_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();

            //creating our api
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            //creating a call and calling the upload image method
            Call<JsonObject> responseCall = apiRequest.uploadProfileImagesRequest(partBody, params);
            api.postRequest(responseCall, this, 1);


            DebugLog.getInstance().d("Profile_photo_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("Profile_photo_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
