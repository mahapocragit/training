package in.gov.pocra.training.model.offline;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;


public class OffSchDetailModel {


    private String event_id;
    private String event_type;
    private String event_type_name;
    private String event_title;
    private String event_participints;
    private String event_start_date;
    private String event_end_date;
    private String event_venue;
    private JSONArray event_gp;
    private JSONArray event_village;
    private JSONArray event_resource_person;
    private JSONArray event_coordinators;


    private JSONObject jsonObject;

    public OffSchDetailModel(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getEvent_id() {
        return event_id = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "schedule_id");
    }

    public String getEvent_type() {
        return event_type = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_type");
    }

    public String getEvent_type_name() {
        return event_type_name = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "event_type_name");
    }

    public String getEvent_title() {
        return event_title = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "title");
    }

    public String getEvent_participints() {
        return event_participints = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "participints");
    }

    public String getEvent_start_date() {
        return event_start_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "start_date");
    }

    public String getEvent_end_date() {
        return event_end_date = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "end_date");
    }

    public String getEvent_venue() {
        return event_venue = AppUtility.getInstance().sanitizeJSONObj(this.jsonObject, "venue");
    }

    public JSONArray getEvent_gp() {
        return event_gp = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject,"gp");
    }

    public JSONArray getEvent_village() {
        return event_village = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject,"village");
    }

    public JSONArray getEvent_resource_person() {
        return event_resource_person = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject,"resource_person");
    }

    public JSONArray getEvent_coordinators() {
        return event_coordinators = AppUtility.getInstance().sanitizeArrayJSONObj(this.jsonObject,"coordinators");
    }

}
