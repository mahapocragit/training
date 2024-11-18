package in.gov.pocra.training.activity.coordinator.attend_image_upload;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;


import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
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
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.co.appinventor.ui.BuildConfig;
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ResponseModel;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.BitmapUtil;
import in.gov.pocra.training.util.GPSTracker;
import in.gov.pocra.training.util.ImagePathUtil;
import in.gov.pocra.training.util.ManagePermission;
import in.gov.pocra.training.web_services.APIRequest;
import in.gov.pocra.training.web_services.APIServices;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class AttendImageUploadActivity extends AppCompatActivity implements ApiCallbackCode, OnMultiRecyclerItemClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageView homeBack;
    private String loginType;
    GPSTracker gps;

    private String roleId;
    private String userID;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_CODE = 22;

    private static final String IMAGE_DIRECTORY = "/PoCRA_TRAINING_IMG";
    private static final int APP_PERMISSION_REQUEST_CODE = 111;
    static final Integer CAMERA = 0x5;
    private File photoFile = null;
    private String selectedImage;
    private boolean isFirstImageSelected = false;

    private String schId = "0";
    private String schDate = "0";
    private String current_lat;
    private String current_long;

    private ImageView attendImgIView;
    private ImageView materialIView;
    private ImageView thirdImgIView;
    private ImageView fourthImgIView;
    private ImageView fiImgIView;
    private ImageView sixImgIView;
    private String imgId = "";
    // private RecyclerView imgRView;
    private JSONArray imgListArray = null;
    private String imgId1 = "";
    private String imgId2 = "";
    private String imgId3 = "";
    private String imgId4 = "";
    private String imgId5 = "";
    private String imgId6 = "";
    private String imgId7 = "";
    private TextView uploadedSheetTV;

    private Button pdfLLayout;
    private static final int PICK_FILE_REQUEST = 2;
    private String selectedFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_image_upload);

        /* ** For actionbar title in center ***/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.attendance_actionbar_layout);
        AppCompatTextView actionTitleTextView = (AppCompatTextView) getSupportActionBar().getCustomView().findViewById(R.id.actionTitleTextView);
        homeBack = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.backImageView);
        // addPersonImageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.addPersonImageView);
        homeBack.setVisibility(View.VISIBLE);
        // addPersonImageView.setVisibility(View.INVISIBLE);
        actionTitleTextView.setText(getResources().getString(R.string.title_img_upload));

        String type = AppSettings.getInstance().getValue(this, ApConstants.kLOGIN_TYPE, ApConstants.kLOGIN_TYPE);
        if (!type.equalsIgnoreCase("kLOGIN_TYPE")) {
            loginType = type;
        }
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

        // To get User Id and Roll Id
        String rId = AppSettings.getInstance().getValue(this, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
        String uId = AppSettings.getInstance().getValue(this, ApConstants.kUSER_ID, ApConstants.kUSER_ID);
        if (!rId.equalsIgnoreCase("kROLE_ID")) {
            roleId = rId;
        }
        if (!uId.equalsIgnoreCase("kUSER_ID")) {
            userID = uId;
        }

        // upImgIView = (ImageView) findViewById(R.id.upImgIView);

        attendImgIView = (ImageView) findViewById(R.id.attendImgIView);
        materialIView = (ImageView) findViewById(R.id.materialIView);
        thirdImgIView = (ImageView) findViewById(R.id.thirdImgIView);
        fourthImgIView = (ImageView) findViewById(R.id.fourthImgIView);
        fiImgIView = (ImageView) findViewById(R.id.fiImgIView);
        sixImgIView = (ImageView) findViewById(R.id.sixImgIView);
        uploadedSheetTV = (TextView) findViewById(R.id.uploadedSheetTV);
        pdfLLayout = (Button) findViewById(R.id.pdfLLayout);

        // imgRView = (RecyclerView) findViewById(R.id.imgRView);
        // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        // imgRView.setLayoutManager(gridLayoutManager);
    }
    private void defaultConfiguration() {

        attendImgIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "1";

                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {
                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });

        pdfLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = "7";
                getTrackerLocation();
                showFileChooser();
            }
        });

        materialIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "2";
                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {

                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });

        thirdImgIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "3";
                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {

                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });


        fourthImgIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "4";
                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {
                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });


        fiImgIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "5";
                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {
                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });

        sixImgIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedImage = "6";
                if (Build.VERSION.SDK_INT < 23) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                } else {
                    if (checkPermissionsIsEnabledOrNot() && statusCheck()) {
                        takeImageFromCameraUri();
                        getTrackerLocation();
                    } else {
                        checkUserPermission();
                    }
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("application/pdf");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }


    @Override
    protected void onResume() {
        super.onResume();

        schId = getIntent().getStringExtra("schId");
        schDate = getIntent().getStringExtra("event_date");
        if (schId.equalsIgnoreCase("")) {
            schId = "0";
        }

        if (schDate.equalsIgnoreCase("")) {
            schDate = "0";
        }

        getImageList();
    }


    @Override
    public void onMultiRecyclerViewItemClick(int i, Object o) {

        imgId = "";
        try {

            JSONObject imgObj = (JSONObject) o;
            imgId = imgObj.getString("img_id");
            if (!imgId.equalsIgnoreCase("")) {
                uploadImageAction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getImageList() {

        try {

            // To get Detail
            JSONObject param = new JSONObject();
            param.put("schedule_event_id", schId);
            param.put("attendance_date", schDate);
            param.put("api_key", ApConstants.kAUTHORITY_KEY);

            RequestBody requestBody = AppUtility.getInstance().getRequestBody(param.toString());
            AppinventorApi api = new AppinventorApi(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();
            APIRequest apiRequest = retrofit.create(APIRequest.class);
            Call<JsonObject> responseCall = apiRequest.getAttendImageListOfDayRequest(requestBody);

            DebugLog.getInstance().d("attend_image_list_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("attend_image_list_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

            api.postRequest(responseCall, this, 1);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void uploadImageAction() {

       /* if (Build.VERSION.SDK_INT < 19) {
            takeImageFromCameraUri();
            getTrackerLocation();
        } else {
            if (checkUserPermission()) {
                takeImageFromCameraUri();
                getTrackerLocation();
            }
        }*/
    }


    @Override
    public void onResponse(JSONObject jsonObject, int i) {

        try {

            if (jsonObject != null) {

                if (i == 1) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        String fUrl = jsonObject.getString("file_url");
                        Log.d("ImageUpload-atd","file_url==" +fUrl);
                        imgListArray = responseModel.getData();

                        if (imgListArray.length() > 0) {

                            for (int img = 0; img < imgListArray.length(); img++) {

                                JSONObject imgJSON = imgListArray.getJSONObject(img);
                                String imgType = imgJSON.getString("img_attend_type");
                                String imgName = imgJSON.getString("image_name");
                                String imgUrl = fUrl + imgName;
                                Log.d("ImageUpload-atd","finalImgUrl==" +fUrl);
                                switch (imgType) {
                                    case "1":
                                        if (!imgUrl.isEmpty()) {
                                            imgId1 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(attendImgIView);
                                        }
                                        break;
                                    case "2":
                                        if (!imgUrl.isEmpty()) {
                                            imgId2 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(materialIView);
                                        }
                                        break;

                                    case "3":
                                        if (!imgUrl.isEmpty()) {
                                            imgId3 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(thirdImgIView);
                                        }
                                        break;

                                    case "4":
                                        if (!imgUrl.isEmpty()) {
                                            imgId4 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(fourthImgIView);
                                        }
                                        break;

                                    case "5":
                                        if (!imgUrl.isEmpty()) {
                                            imgId5 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(fiImgIView);
                                        }
                                        break;

                                    case "6":
                                        if (!imgUrl.isEmpty()) {
                                            imgId6 = imgJSON.getString("img_id");
                                            Picasso.get()
                                                    .load(Uri.parse(imgUrl))
                                                    .into(sixImgIView);
                                        }
                                        break;
                                    case "7":
                                        if (!imgUrl.isEmpty()) {
                                            imgId7 = imgJSON.getString("img_id");
                                            if (!imgName.equalsIgnoreCase("")) {
                                                uploadedSheetTV.setVisibility(View.VISIBLE);
                                                uploadedSheetTV.setText(imgName);
                                            } else {
                                                uploadedSheetTV.setVisibility(View.GONE);
                                            }

                                        }
                                        break;
                                }
                            }

                        }
                    }
                }


                // Upload Image Response
                if (i == 3) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);

                    if (responseModel.isStatus()) {
                        //UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                        getImageList();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }


                if (i == 4) {
                    ResponseModel responseModel = new ResponseModel(jsonObject);
                    if (responseModel.isStatus()) {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
//                        UIToastMessage.show(this, responseModel.getMsg());
                        Toast.makeText(this, ""+responseModel.getMsg(), Toast.LENGTH_SHORT).show();
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


    // FOR CAMERA  & Location

    private boolean checkPermissionsIsEnabledOrNot() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionLocationCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionLocationFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        return permissionCamera == PackageManager.PERMISSION_GRANTED &&
                permissionWrite == PackageManager.PERMISSION_GRANTED &&
                permissionStorage == PackageManager.PERMISSION_GRANTED &&
                permissionLocationCoarse == PackageManager.PERMISSION_GRANTED &&
                permissionLocationFine == PackageManager.PERMISSION_GRANTED;
    }

    /**** For permission with ManagePermissionClass*****/
    private void checkUserPermission() {

        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionLocationCoarcre = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionLocationFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

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

        if (permissionLocationCoarcre != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionLocationFine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), APP_PERMISSION_REQUEST_CODE);

        }
    }


    private boolean requestPermission() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, APP_PERMISSION_REQUEST_CODE);
        } else {
            result = true;
        }
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case APP_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                }else {
                    takeImageFromCameraUri();
                    getTrackerLocation();
                }
                break;
            }
        }
    }

    private void getTrackerLocation() {

        gps = new GPSTracker(this);
        // Check if GPS enabled
        if (gps.canGetLocation()) {

            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            current_lat = String.valueOf(latitude);
            current_long = String.valueOf(longitude);

            /*AppSettings.getInstance().setValue(this, AppConstants.kLATITUDE, register_lat);
            AppSettings.getInstance().setValue(this, AppConstants.kLONGITUDE, register_long);*/

            // getAddress(gps, latitude, longitude);

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            showSettingsAlert();

        }
    }


    public boolean statusCheck() {
        boolean result = false;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }else {
            result = true;
        }
        return result;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    // For Camera

    private void takeImageFromCameraUri() {

        ImagePathUtil imagePathUtil = new ImagePathUtil(this);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath(),IMAGE_DIRECTORY);
           // File photoDirectory = new File(Environment.getExternalStorageDirectory().getPath(),IMAGE_DIRECTORY);
            Log.d("Mayu","photoDirectory=="+photoDirectory);
            try {
                photoFile = imagePathUtil.createImageFile(photoDirectory,selectedImage +"_" + System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null){
                Uri photoURI = null;
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)) {
                    photoURI = FileProvider.getUriForFile(this, "in.gov.pocra.training.android.fileprovider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                PackageManager packageManager = getPackageManager();

                List<ResolveInfo> listCam = packageManager.queryIntentActivities(takePictureIntent, 0);

                ResolveInfo res = listCam.get(0);
                String packageName = res.activityInfo.packageName;

                Intent intent = new Intent(takePictureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);

                startActivityForResult(intent, CAMERA_CODE);

            }
        }
    }


    private static String getFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                onCameraActivityResult();
            } /*else if (resultCode == RESULT_CANCELED) {
                UIToastMessage.show(this, "Camera intent aborted");
                onCameraActivityResult();
            }*/ else {
               // UIToastMessage.show(this, "Failed to capture Image");
                Toast.makeText(this, "Failed to capture Image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onCameraActivityResult() {

        int rotate = 0;
        Bitmap bmp = null;

        if (photoFile != null && photoFile.exists()) {

            bmp = BitmapUtil.decodeSampledBitmapFromPath(this, photoFile, 1050, true); // BitmapFactory.decodeFile(photopath);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            if (selectedImage.equalsIgnoreCase("1")) {    // For Attend type
                //attendImgIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId1);
                    }
                }, 2000);


            } else if (selectedImage.equalsIgnoreCase("2")) {     // For Material type
                //materialIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId2);
                    }
                }, 2000);

            } else if (selectedImage.equalsIgnoreCase("3")) {      // For third event image
                //thirdImgIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId3);
                    }
                }, 2000);
            } else if (selectedImage.equalsIgnoreCase("4")) {      // For fourth event image
                //fourthImgIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId4);
                    }
                }, 2000);
            } else if (selectedImage.equalsIgnoreCase("5")) {      // For fifth event image
                //fiImgIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId5);
                    }
                }, 2000);
            } else if (selectedImage.equalsIgnoreCase("6")) {      // For sixth event image
                //sixImgIView.setImageBitmap(bmp);
                final String imagePath = "file://" + photoFile;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uploadImageOnServer(imagePath, selectedImage, imgId6);
                    }
                }, 2000);
            }

            /*if (photoFile != null){
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
            }*/

        }
    }

    private void uploadImageOnServer(String selectedFilePath, String attendType, String imgId, String displayname) {
        //
        try {

            MultipartBody.Part partBody = null;

//            DebugLog.getInstance().d("imgName=" + imagePath);

            Map<String, RequestBody> params = new HashMap<>();

            // params.put("timestamp", AppinventorApi.toRequestBody(ApUtil.getCurrentTimeStamp()));
            params.put("schedule_event_id", AppinventorApi.toRequestBody(schId));
            params.put("img_id", AppinventorApi.toRequestBody(imgId));
            params.put("attendance_date", AppinventorApi.toRequestBody(schDate));
            params.put("img_attend_type", AppinventorApi.toRequestBody(attendType));
            params.put("role_id", AppinventorApi.toRequestBody(roleId));
            params.put("user_id", AppinventorApi.toRequestBody(userID));
            params.put("lat", AppinventorApi.toRequestBody(current_lat));
            params.put("long", AppinventorApi.toRequestBody(current_long));
            params.put("api_key", AppinventorApi.toRequestBody(ApConstants.kAUTHORITY_KEY));

            //creating a file
            File file = new File(selectedFilePath);
            // File file = new File(imagePath);
//            multipart/form-data;boundary=" + boundary
            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            partBody = MultipartBody.Part.createFormData("image_name", file.getName(), reqFile);

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();

            //creating our api
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            //creating a call and calling the upload image method
            Call<JsonObject> responseCall = apiRequest.uploadAttendImageRequest(partBody, params);
            api.postRequest(responseCall, this, 4);

            DebugLog.getInstance().d("A_Day_Attendance_photo_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("A_Day_Attendance_photo_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void uploadImageOnServer(String imagePath, String attendType, String imgId) {
        try {
            MultipartBody.Part partBody = null;
            DebugLog.getInstance().d("imgName=" + imagePath);
            Map<String, RequestBody> params = new HashMap<>();

            // params.put("timestamp", AppinventorApi.toRequestBody(ApUtil.getCurrentTimeStamp()));
            params.put("schedule_event_id", AppinventorApi.toRequestBody(schId));
            params.put("img_id", AppinventorApi.toRequestBody(imgId));
            params.put("attendance_date", AppinventorApi.toRequestBody(schDate));
            params.put("img_attend_type", AppinventorApi.toRequestBody(attendType));
            params.put("role_id", AppinventorApi.toRequestBody(roleId));
            params.put("user_id", AppinventorApi.toRequestBody(userID));
            params.put("lat", AppinventorApi.toRequestBody(current_lat));
            params.put("long", AppinventorApi.toRequestBody(current_long));
            params.put("api_key", AppinventorApi.toRequestBody(ApConstants.kAUTHORITY_KEY));

            Log.d("schedule_event_id","mmm"+schId);
            Log.d("imgId","mmm"+imgId);
            Log.d("attendance_date","mmm"+schDate);
            Log.d("img_attend_type","mmm"+attendType);
            Log.d("role_id",""+roleId);
            Log.d("user_id",""+userID);
            Log.d("lat",""+AppinventorApi.toRequestBody(current_lat));
            Log.d("long",""+AppinventorApi.toRequestBody(current_long));
            Log.d("api_key",""+AppinventorApi.toRequestBody(ApConstants.kAUTHORITY_KEY));

            //creating a file
            File file = new File(photoFile.getPath());
            // File file = new File(imagePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            partBody = MultipartBody.Part.createFormData("image_name", file.getName(), reqFile);

            AppinventorIncAPI api = new AppinventorIncAPI(this, APIServices.BASE_URL, "", ApConstants.kMSG, true);
            Retrofit retrofit = api.getRetrofitInstance();

            //creating our api
            APIRequest apiRequest = retrofit.create(APIRequest.class);

            //creating a call and calling the upload image method
            Call<JsonObject> responseCall = apiRequest.uploadAttendImageRequest(partBody, params);
            api.postRequest(responseCall, this, 3);

            DebugLog.getInstance().d("A_Day_Attendance_photo_param=" + responseCall.request().toString());
            DebugLog.getInstance().d("param=" + responseCall.request().toString());
            DebugLog.getInstance().d("A_Day_Attendance_photo_param=" + AppUtility.getInstance().bodyToString(responseCall.request()));
            DebugLog.getInstance().d("param=" + AppUtility.getInstance().bodyToString(responseCall.request()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}