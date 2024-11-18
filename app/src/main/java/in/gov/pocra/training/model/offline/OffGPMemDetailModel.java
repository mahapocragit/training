package in.gov.pocra.training.model.offline;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class OffGPMemDetailModel {


    private String gp_mem_id;
    private String gp_id;
    private String gp_name;
    private String gp_mem_first_name;
    private String gp_mem_middle_name;
    private String gp_mem_last_name;
    private String gp_mem_gender;
    private String gp_mem_designation;
    private String gp_mem_mobile;


    private JSONObject jsonObject;

    public OffGPMemDetailModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getGp_mem_id() {
        return gp_mem_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "member_id");
    }

    public String getGp_id() {
        return gp_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gp_id");
    }

    public String getGp_name() {
        return gp_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "group_name");
    }


    public String getGp_mem_first_name() {
        return gp_mem_first_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "first_name");
    }

    public String getGp_mem_middle_name() {
        return gp_mem_middle_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "middle_name");
    }

    public String getGp_mem_last_name() {
        return gp_mem_last_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "last_name");
    }

    public String getGp_mem_gender() {
        return gp_mem_gender = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender");
    }

    public String getGp_mem_designation() {
        return gp_mem_designation = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation");
    }

    public String getGp_mem_mobile() {
        return gp_mem_mobile = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile");
    }



}
