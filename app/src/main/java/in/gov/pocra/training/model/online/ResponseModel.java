package in.gov.pocra.training.model.online;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class ResponseModel {

    private boolean is_success;
    private String msg;
    private JSONObject jsonObject;
    private JSONArray dataArray,dataArray1;
    private String response;
    private String refreshToken;

    public ResponseModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public JSONArray getData() {
        try {
            return this.jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isStatus() {
        JSONObject jsonObject = null;
        jsonObject = this.jsonObject;
        if (AppUtility.getInstance().sanitizeJSONObj(jsonObject, "status").equalsIgnoreCase("200")) {
            is_success = true;
        } else {
            is_success = false;
        }

        /*try {
            jsonObject = this.jsonObject.getJSONObject("meta");
            if (AppUtility.getInstance().sanitizeJSONObj(jsonObject, "status").equalsIgnoreCase("200")) {
                is_success = true;
            } else {
                is_success = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return is_success;
    }

    public String getMsg() {
        JSONObject jsonObject = null;
        jsonObject = this.jsonObject;
        msg = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "response");

       /* try {
            jsonObject = this.jsonObject.getJSONObject("meta");
            msg = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "response");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return msg;
    }
    public String getRefreshToken() {

        refreshToken = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "refresh_token");
        return refreshToken;
    }

    public JSONArray getDataArray() {
        dataArray = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "data");
        return dataArray;
    }
    public String getResponse() {

        response = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "response");
        return response;
    }


}
