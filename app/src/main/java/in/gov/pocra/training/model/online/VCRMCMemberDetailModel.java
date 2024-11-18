package in.gov.pocra.training.model.online;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class VCRMCMemberDetailModel {

    private String id;
    private String fname;
    private String mobile;
    private String designation;
    private String gender;
    private String social_category;
    private String attend;
    private String type;
    private String mname;
    private String lname;




    private JSONObject jsonObject;
    public VCRMCMemberDetailModel(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }


    public String getId() {
        return id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");
    }

    public String getFname() {
        return fname = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "fname");
    }

    public String getMobile() {
        return mobile = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile");
    }

    public String getDesignation() {
        return designation = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation");
    }

    public String getGender() {
        return gender = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender");
    }

    public String getSocial_category() {
        return social_category = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "social_category");
    }

    public String getAttend() {
        return attend = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "attend");
    }

    public String getType() {
        return type = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "type");
    }

    public String getMname() {
        return mname = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mname");
    }

    public String getLname() {
        return lname = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "lname");
    }
}
