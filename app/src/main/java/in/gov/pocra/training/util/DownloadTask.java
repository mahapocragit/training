package in.gov.pocra.training.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import in.co.appinventor.services_api.widget.UIToastMessage;


public class DownloadTask {

        private static final String TAG = "Download Task";
        private Context context;
        private File mFile;
        private String downloadUrl = "", downloadFileName = "";
        private ProgressDialog progressDialog;

        public DownloadTask(Context context, String downloadUrl, File file) {

            this.context = context;
            this.mFile = file;
            this.downloadUrl = downloadUrl;

            if (downloadUrl != null && !downloadUrl.equalsIgnoreCase("")){
                int lastIndex = downloadUrl.length();
                downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'), lastIndex) ;//Create file name by picking download file name from URL
                Log.e(TAG, downloadFileName);

                //Start Downloading Task
                new DownloadingTask().execute();
            }else {
                UIToastMessage.show(context,"Report PDF link not found");
                // Toast.makeText(context, "Report PDF link not found", Toast.LENGTH_SHORT).show();
            }
        }

        private class DownloadingTask extends AsyncTask<Void, Void, Void> {

            File apkStorage = null;
            File outputFile = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Downloading...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Void result) {
                try {
                    if (outputFile != null) {

                        progressDialog.dismiss();
                        Toast.makeText(context, "Downloaded Successfully,\n Please check in '/PoCRA_TRAINING/PoCRA_TRAINING_REPORT'", Toast.LENGTH_SHORT).show();

                    } else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);

                        Log.e(TAG, "Download Failed");
                        Toast.makeText(context, "Downloaded Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    //Change button text if exception occurs

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 3000);
                    Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
                    Toast.makeText(context, "Downloaded Failed", Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(result);
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {
                    URL url = new URL(downloadUrl);//Create Download URl
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                    c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                    c.connect();//connect the URL Connection

                    //If Connection response is not OK then show Logs
                    if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                                + " " + c.getResponseMessage());
                    }


                    //Get File if SD card is present
                    if (ApUtil.isSDCardPresent()) {
                        apkStorage = mFile;
                    } else
                        Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                    //If File is not present create directory
                    if (!apkStorage.exists()) {
                        apkStorage.mkdir();
                        Log.e(TAG, "Directory Created.");
                    }

                    outputFile = new File(apkStorage, downloadFileName);//Create Output file in Main File

                    //Create New File if not present
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                        Log.e(TAG, "File Created");
                    }

                    FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                    InputStream is = c.getInputStream();//Get InputStream for connection

                    byte[] buffer = new byte[1024];//Set buffer type
                    int len1 = 0;//init length
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);//Write new file
                    }

                    //Close all connection after doing task
                    fos.close();
                    is.close();

                } catch (Exception e) {

                    //Read exception if something went wrong
                    e.printStackTrace();
                    outputFile = null;
                    Log.e(TAG, "Download Error Exception " + e.getMessage());
                }

                return null;
            }
        }
}
