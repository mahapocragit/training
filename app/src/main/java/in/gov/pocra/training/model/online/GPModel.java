package in.gov.pocra.training.model.online;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class GPModel {


    private String id;
    private String name;
    private String code;
    private String email;
    private String is_selected;
    private JSONArray vcrmc_member;
    private JSONObject jsonObject;




    public GPModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getId() {
        return id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");

    }


    public String getName() {
        return name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "name");
    }

    public String getCode() {
        return code = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "code");
    }

    public String getEmail() {
        return email = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "email");
    }

    public String getIs_selected() {
        return is_selected = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "is_selected");
    }


    public JSONArray getVcrmc_member() {
        return vcrmc_member = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject, "vcrmc_member");
    }
}
