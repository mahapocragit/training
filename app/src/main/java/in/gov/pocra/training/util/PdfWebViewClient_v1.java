package in.gov.pocra.training.util;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.co.appinventor.services_api.widget.UIToastMessage;

public class PdfWebViewClient_v1 extends WebViewClient {

    private static final String TAG = "PdfWebViewClient";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String PDF_VIEWER_URL = "http://docs.google.com/gview?embedded=true&url=";

    private Context mContext;
    private WebView mWebView;
    private ProgressDialog mProgressDialog;
    private boolean isLoadingPdfUrl;
    private boolean loadingFinished;
    private String pdfUrl;

    public PdfWebViewClient_v1(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;

        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
       // webSettings.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
        {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        mWebView.setWebViewClient(new MyBrowser());
        mWebView.clearCache(true);
        mWebView.clearHistory();

        // mWebView.setWebViewClient(this);
    }

    public void loadPdfUrl(String url) {
        mWebView.stopLoading();

        if (!TextUtils.isEmpty(url)) {
            isLoadingPdfUrl = isPdfUrl(url);
            pdfUrl = PDF_VIEWER_URL+url;
            if (isLoadingPdfUrl) {
                mWebView.clearHistory();
            }
            // showProgressDialog();
        }

        loadUrl(0);
    }

    private void loadUrl(int errorCode) {
        if (checkInternetConnection() && errorCode == 0) {
            if (!pdfUrl.equalsIgnoreCase("")) {
               // Log.d("pdf_url",pdfUrl);
                // mWebView.loadUrl(pdfUrl);
                mWebView.loadUrl(pdfUrl);
            }

            mWebView.setWebViewClient(new MyBrowser() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                   // Log.d("pdf_url",url);
                    view.loadUrl(url);
                    return true;
                }
            });

        } else {
//            connectionLostLLayout.setVisibility(View.VISIBLE);
//            webView.setVisibility(View.GONE);
            UIToastMessage.show(mContext,"Unable to load, Please Try again!");
        }

    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            // loadUrl(0);
        }

        Dialog loadingDialog = new Dialog(mContext);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            loadingFinished = false;
            if (loadingDialog == null || !loadingDialog.isShowing())
                loadingDialog = ProgressDialog.show(mContext, "",
                        "Loading... Please Wait..!", true, true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // do something
                            }
                        });

            loadingDialog.setCancelable(false);
            loadingDialog.show();

            // super.onPageStarted(view, url, favicon);
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            int errorCode = error.getErrorCode();

            if (errorCode != 0) {
//                connectionLostLLayout.setVisibility(View.VISIBLE);
//                webView.setVisibility(View.GONE);
                UIToastMessage.show(mContext,"Unable to load, Please Try again!");
            } else {
                loadUrl(errorCode);
            }
            // super.onReceivedError(view, request, error);
        }

        int tryCount = 0;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            final int status = errorResponse.getStatusCode();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (status != 0 && tryCount > 1) {
//                        connectionLostLLayout.setVisibility(View.VISIBLE);
//                        webView.setVisibility(View.GONE);
                        UIToastMessage.show(mContext,"Unable to load, Please Try again!");
                    } else {
                        if (tryCount>1){
                            tryCount++;
                            loadUrl(status);
                        }else {
                            tryCount++;
                            loadUrl(0);
                        }
                    }
                }
            },500);

            //  super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            if(view.getContentHeight() == 0){
                view.reload();
            } else {
                if (!loadingFinished) {
                    if (null != loadingDialog) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                }
            }

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return shouldOverrideUrlLoading(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        handleError(errorCode, description.toString(), failingUrl);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        return shouldOverrideUrlLoading(webView, uri.toString());
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onReceivedError(final WebView webView, final WebResourceRequest request, final WebResourceError error) {
        final Uri uri = request.getUrl();
        handleError(error.getErrorCode(), error.getDescription().toString(), uri.toString());
    }

    @Override
    public void onPageFinished(final WebView view, final String url) {
        Log.i(TAG, "Finished loading. URL : " + url);
        dismissProgressDialog();
    }

    private boolean shouldOverrideUrlLoading(final String url) {
        Log.i(TAG, "shouldOverrideUrlLoading() URL : " + url);

        if (!isLoadingPdfUrl && isPdfUrl(url)) {
            mWebView.stopLoading();

            final String pdfUrl = PDF_VIEWER_URL + url;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadPdfUrl(pdfUrl);
                }
            }, 300);

            return true;
        }

        return false; // Load url in the webView itself
    }

    private void handleError(final int errorCode, final String description, final String failingUrl) {
        Log.e(TAG, "Error : " + errorCode + ", " + description + " URL : " + failingUrl);
    }

    private void showProgressDialog() {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(mContext, "", "Loading...");
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private boolean isPdfUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            url = url.trim();
            int lastIndex = url.toLowerCase().lastIndexOf(PDF_EXTENSION);
            if (lastIndex != -1) {
                return url.substring(lastIndex).equalsIgnoreCase(PDF_EXTENSION);
            }
        }
        return false;
    }

    // Common Functions
    public boolean checkInternetConnection() {
        ConnectivityManager con_manager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }

    public String getFileNameFromUrl(String url){

        String fileName = url.substring(url.lastIndexOf('/')+1, url.length());
        String fileNameWithoutExtension = "";
        String fileExtension = "";
        if (!fileName.equalsIgnoreCase("")){
            fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            fileExtension = url.substring(url.lastIndexOf("."));
        }
        return fileNameWithoutExtension;
    }
}
