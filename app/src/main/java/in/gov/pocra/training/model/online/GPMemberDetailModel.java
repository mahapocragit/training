package in.gov.pocra.training.model.online;

import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class GPMemberDetailModel {



    private String mem_id;
    private String mem_name;
    private String mem_first_name;
    private String mem_middle_name;
    private String mem_last_name;
    private String mem_mobile;
    private String mem_mobile2;
    private String mem_designation_id;
    private String mem_designation_name;
    private String mem_gender_id;
    private String mem_gender_name;
    private String mem_social_category_id;
    private String mem_social_category_name;
    private String mem_land_hold_category;
    private String mem_is_selected;

    private String chief_promoter_president;
    private String flagstr;



    private JSONObject jsonObject;

    public GPMemberDetailModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public String getMem_id() {
        return mem_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "id");
    }

    public String getMem_name() {
        return mem_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "name");
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

    public String getMem_mobile2() {
        return mem_mobile2 = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "mobile2");
    }

    public String getMem_designation_id() {
        return mem_designation_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation_id");
    }

    public String getMem_designation_name() {
        return mem_designation_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "designation_name");
    }

    public String getMem_gender_id() {
        return mem_gender_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender_id");
    }

    public String getMem_gender_name() {
        return mem_gender_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "gender_name");
    }

    public String getMem_social_category_id() {
        return mem_social_category_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "social_category_id");
    }

    public String getMem_social_category_name() {
        return mem_social_category_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "social_category_name");
    }

    public String getMem_land_hold_category() {
        return mem_land_hold_category = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "land_hold_category");
    }

    public String getMem_is_selected() {
        return mem_is_selected = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "is_selected");
    }

    public String getChief_promoter_president() {
        return mem_land_hold_category = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "chief_promoter_president");
    }

    public String getFlagstr() {
        return mem_is_selected = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "group_flag");
    }
}
