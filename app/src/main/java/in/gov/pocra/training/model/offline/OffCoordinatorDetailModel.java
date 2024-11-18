package in.gov.pocra.training.model.offline;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class OffCoordinatorDetailModel {

    private String mem_id;
    private String group_id;
    private String group_name;
    private String mem_first_name;
    private String mem_middle_name;
    private String mem_last_name;
    private String mem_mobile;
    private String mem_designation;
    private String mem_gender;
    private String mem_role_id;



    private JSONObject jsonObject;

    public OffCoordinatorDetailModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public String getMem_id() {
        return mem_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "member_id");
    }

    public String getGroup_id() {
        return group_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gp_id");
    }

    public String getGroup_name() {
        return group_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "group_name");
    }

    public String getMem_first_name() {
        return mem_first_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "first_name");
    }

    public String getMem_middle_name() {
        return mem_middle_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "middle_name");
    }

    public String getMem_last_name() {
        return mem_last_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "last_name");
    }

    public String getMem_mobile() {
        return mem_mobile = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile");
    }

    public String getMem_designation() {
        return mem_designation = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation");
    }

    public String getMem_gender() {
        return mem_gender = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender");
    }

    public String getMem_role_id() {
        return mem_role_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "role_id");
    }

}
