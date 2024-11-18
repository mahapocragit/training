package in.gov.pocra.training.event_db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.debug.DebugLog;

public class CordOfflineDBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CordOfflineDatabase.db";


    // Event Lis Table
    public static final String EVENT_LIST_TABLE = "event_list_table";
    public static final String EL_U_ID = "u_id";
    public static final String EL_ID = "schedule_id";
    public static final String EL_CORD_ID = "coordinator_id";
    public static final String EL_CORD_ROLL_ID = "coordinator_role_id";
    public static final String EL_TYPE = "type";
    public static final String EL_TITLE = "title";
    public static final String EL_S_DATE = "start_date";
    public static final String EL_E_DATE = "end_date";
    public static final String EL_VENUE = "venue";
    public static final String EL_PARTICIPANT_NUM = "participints";
    public static final String EL_CLOSER = "is_event_closer";


    // Event detail Table
    public static final String EVENT_DETAIL_TABLE = "event_detail_table";
    public static final String ID = "e_u_id";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_USER_ID = "user_id";
    public static final String EVENT_ROLL_ID = "roll_id";
    public static final String EVENT_S_DATE = "event_start_date";
    public static final String EVENT_E_DATE = "event_end_date";
    public static final String EVENT_AT = "event_venue";
    public static final String EVENT_TYPE_ID = "event_type";
    public static final String EVENT_TYPE_NAME = "event_type_name";
    public static final String EVENT_TITLE = "event_title";
    public static final String EVENT_PARTICIPANT_NUM = "event_participant_num";
    public static final String EVENT_GROUP_TYPE = "event_group_type";
    public static final String EVENT_GROUP_ID = "event_group_id";
    public static final String EVENT_GROUP_NAME = "event_group_name";
    public static final String EVENT_ATTEND_DATE = "event_attend_date";
    public static final String EVENT_GROUP_IS_ATTEND = "event_group_is_attend";
    public static final String EVENT_GROUP_MEM_ID = "event_group_mem_id";
    public static final String EVENT_GROUP_MEM_F_NAME = "event_group_mem_f_name";
    public static final String EVENT_GROUP_MEM_M_NAME = "event_group_mem_m_name";
    public static final String EVENT_GROUP_MEM_L_NAME = "event_group_mem_l_name";
    public static final String EVENT_GROUP_MEM_GENDER = "event_group_mem_gender";
    public static final String EVENT_GROUP_MEM_MOB = "event_group_mem_mobile";
    public static final String EVENT_GROUP_MEM_DESIG = "event_group_mem_desig";
    public static final String EVENT_GROUP_MEM_IS_ATTEND = "event_group_mem_is_attend";


    // Cord Image upload Table
    public static final String EVENT_IMAGE_TABLE = "event_image_table";
    public static final String E_IMG_ID = "i_u_id";
    public static final String E_ID = "event_id";
    public static final String E_IMG_USER_ID = "event_img_user_id";
    public static final String E_IMG_ROLL_ID = "event_img_roll_id";
    public static final String E_IMG_DATE = "event_img_date";
    public static final String E_IMG_FILE = "event_img_file";
    public static final String E_IMG_NAME = "event_img_name";
    public static final String E_IMG_LAT = "event_img_lat";
    public static final String E_IMG_LANG = "event_img_lang";
    public static final String IS_E_IMG_SYNC = "event_img_sync";
    public static final String E_IMG_UP_TIME = "event_img_up_time";


    // Other Member table
    public static final String EVENT_OTHER_MEM_TABLE = "event_other_mem_table";
    public static final String OTH_MEM_ID = "o_m_id";
    public static final String OTH_E_ID = "event_id";
    public static final String OTH_USER_ID = "o_user_id";
    public static final String OTH_ROLL_ID = "o_roll_id";
    public static final String OTH_E_DATE = "o_event_date";
    public static final String OTH_E_MEM_F_NAME = "o_mem_f_name";
    public static final String OTH_E_MEM_M_NAME = "o_mem_m_name";
    public static final String OTH_E_MEM_L_NAME = "o_mem_l_name";
    public static final String OTH_E_MEM_GENDER_ID = "o_mem_gender";
    public static final String OTH_E_MEM_MOB = "o_mem_mobile";
    public static final String OTH_E_MEM_DSGN = "o_mem_dsgn";
    public static final String OTH_E_MEM_IS_ATTEND = "o_mem_is_attend";
    public static final String IS_OTH_E_MEM_DETAIL_SYNC = "is_mem_detail_sysc";
    public static final String OTH_E_DATE_TIME = "o_date_time";


    // Attendance Table
    public static final String EVENT_MEM_ATTEND_TABLE = "event_mem_attend_table";
    public static final String ATT_ID = "a_u_id";
    public static final String ATT_E_ID = "event_id";
    public static final String ATT_GROUP_ID = "att_group_id";
    public static final String ATT_GROUP_TYPE = "att_group_type";
    public static final String ATT_DATE = "att_date";
    public static final String ATT_USER_ID = "att_user_id";
    public static final String ATT_ROLE_ID = "att_role_id";
    public static final String ATT_MEM_ID = "att_mem_id";
    public static final String ATT_MEM_MOB = "att_mem_mob";
    public static final String IS_ATT_SYNC = "att_is_sysc";
    public static final String ATT_DATE_TIME = "att_date_time";




    private static CordOfflineDBase cordOfflineDBase = null;

    public static CordOfflineDBase getInstance(Context context) {
        if (cordOfflineDBase == null) {
            cordOfflineDBase = new CordOfflineDBase(context);
        }
        return cordOfflineDBase;
    }

    public CordOfflineDBase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase cDb) {

        try {

            // EVENT LIST TABLE
            cDb.execSQL("CREATE TABLE " + EVENT_LIST_TABLE +
                    "(" + EL_U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + EL_ID + " TEXT ,"
                    + EL_CORD_ID + " TEXT ,"
                    + EL_CORD_ROLL_ID + " TEXT ,"
                    + EL_TYPE + " TEXT ,"
                    + EL_TITLE + " TEXT ,"
                    + EL_S_DATE + " TEXT ,"
                    + EL_E_DATE + " TEXT ,"
                    + EL_VENUE + " TEXT ,"
                    + EL_PARTICIPANT_NUM + " TEXT ,"
                    + EL_CLOSER + " INTIGER DEFAULT 0 "
                    + ")");


            // EVENT DETAIL TABLE
            cDb.execSQL("CREATE TABLE " + EVENT_DETAIL_TABLE +
                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + EVENT_ID + " TEXT ,"
                    + EVENT_USER_ID + " TEXT ,"
                    + EVENT_ROLL_ID + " TEXT ,"
                    + EVENT_S_DATE + " TEXT ,"
                    + EVENT_E_DATE + " TEXT ,"
                    + EVENT_AT + " TEXT ,"
                    + EVENT_TYPE_ID + " TEXT ,"
                    + EVENT_TYPE_NAME + " TEXT ,"
                    + EVENT_TITLE + " TEXT ,"
                    + EVENT_PARTICIPANT_NUM + " TEXT ,"
                    + EVENT_GROUP_ID + " TEXT ,"
                    + EVENT_GROUP_NAME + " TEXT ,"
                    + EVENT_GROUP_TYPE + " TEXT ,"
                    + EVENT_ATTEND_DATE + " TEXT ,"
                    + EVENT_GROUP_IS_ATTEND + " INTIGER DEFAULT 0 ,"
                    + EVENT_GROUP_MEM_ID + " TEXT ,"
                    + EVENT_GROUP_MEM_F_NAME + " TEXT ,"
                    + EVENT_GROUP_MEM_M_NAME + " TEXT ,"
                    + EVENT_GROUP_MEM_L_NAME + " TEXT ,"
                    + EVENT_GROUP_MEM_GENDER + " TEXT ,"
                    + EVENT_GROUP_MEM_MOB + " TEXT ,"
                    + EVENT_GROUP_MEM_DESIG + " TEXT ,"
                    + EVENT_GROUP_MEM_IS_ATTEND + " INTIGER DEFAULT 0 "
                    + ")");


            // IMAGE TABLE
            cDb.execSQL("CREATE TABLE " + EVENT_IMAGE_TABLE +
                    "(" + E_IMG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + E_ID + " TEXT ,"
                    + E_IMG_DATE + " TEXT ,"
                    + E_IMG_USER_ID + " TEXT ,"
                    + E_IMG_ROLL_ID + " TEXT ,"
                    + E_IMG_FILE + " BLOB ,"
                    + E_IMG_NAME + " TEXT ,"
                    + E_IMG_LAT + " TEXT ,"
                    + E_IMG_LANG + " TEXT ,"
                    + IS_E_IMG_SYNC + " INTIGER DEFAULT 0 ,"
                    + E_IMG_UP_TIME + " TEXT "
                    + ")");


            // OTHER MEM DETAIL TABLE
            cDb.execSQL("CREATE TABLE " + EVENT_OTHER_MEM_TABLE +
                    "(" + OTH_MEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + OTH_E_ID + " TEXT ,"
                    + OTH_ROLL_ID + " TEXT ,"
                    + OTH_USER_ID + " TEXT ,"
                    + OTH_E_DATE + " TEXT ,"
                    + OTH_E_MEM_F_NAME + " TEXT ,"
                    + OTH_E_MEM_M_NAME + " TEXT ,"
                    + OTH_E_MEM_L_NAME + " TEXT ,"
                    + OTH_E_MEM_GENDER_ID + " TEXT ,"
                    + OTH_E_MEM_MOB + " TEXT ,"
                    + OTH_E_MEM_DSGN + " TEXT ,"
                    + OTH_E_MEM_IS_ATTEND + " INTIGER DEFAULT 0 ,"
                    + IS_OTH_E_MEM_DETAIL_SYNC + " INTIGER DEFAULT 0 ,"
                    + OTH_E_DATE_TIME + " TEXT "
                    + ")");


            // MEM ATTENDANCE TABLE
            cDb.execSQL("CREATE TABLE " + EVENT_MEM_ATTEND_TABLE +
                    "(" + ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + ATT_E_ID + " TEXT ,"
                    + ATT_GROUP_ID + " TEXT ,"
                    + ATT_GROUP_TYPE + " TEXT ,"
                    + ATT_DATE + " TEXT ,"
                    + ATT_USER_ID + " TEXT ,"
                    + ATT_ROLE_ID + " TEXT ,"
                    + ATT_MEM_ID + " TEXT ,"
                    + ATT_MEM_MOB + " TEXT ,"
                    + IS_ATT_SYNC + " INTIGER DEFAULT 0 ,"
                    + ATT_DATE_TIME + " TEXT "
                    + ")");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase cDb, int oldVersion, int newVersion) {

        try {

            if (oldVersion != newVersion) {

                // For GP MEMBER TABLE
                cDb.execSQL(" DROP TABLE IF EXISTS " + EVENT_LIST_TABLE);
                onCreate(cDb);

                // For GP MEMBER TABLE
                cDb.execSQL(" DROP TABLE IF EXISTS " + EVENT_DETAIL_TABLE);
                onCreate(cDb);

                // For GP MEMBER TABLE
                cDb.execSQL(" DROP TABLE IF EXISTS " + EVENT_IMAGE_TABLE);
                onCreate(cDb);

                // For GP MEMBER TABLE
                cDb.execSQL(" DROP TABLE IF EXISTS " + EVENT_OTHER_MEM_TABLE);
                onCreate(cDb);

                // For GP MEMBER TABLE
                cDb.execSQL(" DROP TABLE IF EXISTS " + EVENT_MEM_ATTEND_TABLE);
                onCreate(cDb);


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // COMMON Methods

    // for getting last inserted id
    public long lastId(String TABLE_NAME) {
        long Id = 0;
        SQLiteDatabase cDb = this.getReadableDatabase();
        String query = "SELECT ROWID from " + TABLE_NAME + " order by ROWID DESC limit 1";
        Cursor c = cDb.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            Id = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return Id;
    }


    // for getting first inserted id
    public long firstId(String TABLE_NAME) {
        long Id = 0;
        SQLiteDatabase cDb = this.getReadableDatabase();
        String query = "SELECT ROWID from " + TABLE_NAME + " order by ROWID ASC limit 1";
        Cursor c = cDb.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            Id = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return Id;
    }


    // for getting number of record in table
    public long getNoOfRecord(String TABLE_NAME) {
        long count = 0;
        SQLiteDatabase cDb = this.getReadableDatabase();
        count = DatabaseUtils.longForQuery(cDb, " SELECT COUNT(*) FROM " + TABLE_NAME, null);
        return count;
    }

    // for for deleting selected table row
    public boolean deleteRow(String TABLE_NAME, String rowId) {
        SQLiteDatabase cDb = this.getReadableDatabase();
        return cDb.delete(TABLE_NAME, " id = " + rowId, null) > 0;
    }

    // for for deleting all table data from database
    public void deleteAllData() {
        SQLiteDatabase cDb = this.getReadableDatabase();
        // db.delete(GP_TABLE, null, null);
        cDb.delete(EVENT_LIST_TABLE, null, null);
        cDb.delete(EVENT_DETAIL_TABLE, null, null);
        cDb.delete(EVENT_IMAGE_TABLE, null, null);
        cDb.delete(EVENT_OTHER_MEM_TABLE, null, null);
        cDb.delete(EVENT_MEM_ATTEND_TABLE, null, null);

    }


    // for deleting a table data from a database
    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase cDb = this.getReadableDatabase();
        cDb.execSQL("delete from " + TABLE_NAME);
    }



    // for getting number of record in table
    public long getNoOfRecordBySchId(String TABLE_NAME, String SchId) {
        long count = 0;
        SQLiteDatabase cDb = this.getReadableDatabase();
        count = DatabaseUtils.longForQuery(cDb, " SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE event_id = "+SchId, null);
        return count;
    }


    /* INSERT FUNCTION*/


    // INSERT EVENT LIST TABLE

    public Boolean insertEventList(String schId, String cord_id, String cord_roll_id, String event_type, String event_title,
                                   String start_date, String endDate, String venue, String num_of_participants,
                                   String is_event_closer) {

        long result = -1;
        try {
            SQLiteDatabase cDb = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(EL_ID, schId);
            contentValues.put(EL_CORD_ID, cord_id);
            contentValues.put(EL_CORD_ROLL_ID, cord_roll_id);
            contentValues.put(EL_TYPE, event_type);
            contentValues.put(EL_TITLE, event_title);
            contentValues.put(EL_S_DATE, start_date);
            contentValues.put(EL_E_DATE, endDate);
            contentValues.put(EL_VENUE, venue);
            contentValues.put(EL_PARTICIPANT_NUM, num_of_participants);
            contentValues.put(EL_CLOSER, is_event_closer);

            result = cDb.insert(EVENT_LIST_TABLE, null, contentValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }




    // UPDATE GP_MEM_TABLE TABLE DETAIL
    public Boolean updateEventListDetail(String schId, String cord_id, String cord_roll_id, String event_type, String event_title,
                                         String start_date, String endDate, String venue, String num_of_participants,
                                         String is_event_closer) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(EL_ID, schId);
            contentValues.put(EL_CORD_ID, cord_id);
            contentValues.put(EL_CORD_ROLL_ID, cord_roll_id);
            contentValues.put(EL_TYPE, event_type);
            contentValues.put(EL_TITLE, event_title);
            contentValues.put(EL_S_DATE, start_date);
            contentValues.put(EL_E_DATE, endDate);
            contentValues.put(EL_VENUE, venue);
            contentValues.put(EL_PARTICIPANT_NUM, num_of_participants);
            contentValues.put(EL_CLOSER, is_event_closer);

            result = db.update(EVENT_LIST_TABLE, contentValues, " schedule_id = " + schId, null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    /* To get Event list*/
    public JSONArray getEventListByUserId(String userId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_list_table WHERE coordinator_id = " + userId + " GROUP By schedule_id ORDER BY schedule_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                String eUId = cursor.getString(cursor.getColumnIndex("u_id"));
                String eId = cursor.getString(cursor.getColumnIndex("schedule_id"));
                String cordId = cursor.getString(cursor.getColumnIndex("coordinator_id"));
                String cordRollId = cursor.getString(cursor.getColumnIndex("coordinator_role_id"));
                String eType = cursor.getString(cursor.getColumnIndex("type"));
                String eTitle = cursor.getString(cursor.getColumnIndex("title"));
                String eSDate = cursor.getString(cursor.getColumnIndex("start_date"));
                String eEdate = cursor.getString(cursor.getColumnIndex("end_date"));
                String eVenue = cursor.getString(cursor.getColumnIndex("venue"));
                String eNumParticipant = cursor.getString(cursor.getColumnIndex("participints"));
                String eCloser = cursor.getString(cursor.getColumnIndex("is_event_closer"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", eUId);
                    jsonObject1.put("schedule_id", eId);
                    jsonObject1.put("coordinator_id", cordId);
                    jsonObject1.put("coordinator_role_id", cordRollId);
                    jsonObject1.put("type", eType);
                    jsonObject1.put("title", eTitle);
                    jsonObject1.put("start_date", eSDate);
                    jsonObject1.put("end_date", eEdate);
                    jsonObject1.put("venue", eVenue);
                    jsonObject1.put("participints", eNumParticipant);
                    jsonObject1.put("is_event_closer", eCloser);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Event Member JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    /* To get Event list*/
    public JSONArray getCloserEventListByUserId(String userId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_list_table WHERE coordinator_id = " + userId + " AND is_event_closer = 1 GROUP By schedule_id ORDER BY schedule_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                String eUId = cursor.getString(cursor.getColumnIndex("u_id"));
                String eId = cursor.getString(cursor.getColumnIndex("schedule_id"));
                String cordId = cursor.getString(cursor.getColumnIndex("coordinator_id"));
                String cordRollId = cursor.getString(cursor.getColumnIndex("coordinator_role_id"));
                String eType = cursor.getString(cursor.getColumnIndex("type"));
                String eTitle = cursor.getString(cursor.getColumnIndex("title"));
                String eSDate = cursor.getString(cursor.getColumnIndex("start_date"));
                String eEdate = cursor.getString(cursor.getColumnIndex("end_date"));
                String eVenue = cursor.getString(cursor.getColumnIndex("venue"));
                String eNumParticipant = cursor.getString(cursor.getColumnIndex("participints"));
                String eCloser = cursor.getString(cursor.getColumnIndex("is_event_closer"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", eUId);
                    jsonObject1.put("schedule_id", eId);
                    jsonObject1.put("coordinator_id", cordId);
                    jsonObject1.put("coordinator_role_id", cordRollId);
                    jsonObject1.put("type", eType);
                    jsonObject1.put("title", eTitle);
                    jsonObject1.put("start_date", eSDate);
                    jsonObject1.put("end_date", eEdate);
                    jsonObject1.put("venue", eVenue);
                    jsonObject1.put("participints", eNumParticipant);
                    jsonObject1.put("is_event_closer", eCloser);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Event Member JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }




    // Insert event detail
    public Boolean insertEventDetail(String eventId, String eUserId, String eRollId, String eSDate, String eEDate,
                                     String eVenue, String eTypeId, String eTypeName,String eTitle, String eParticipants, String eGroupType,
                                     String eGroupId, String eGroupName, String mem_id,String mem_f_name, String mem_m_name,
                                     String mem_l_name, String mem_mobile,String mem_gender, String mem_designation) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();


            contentValues.put(EVENT_ID, eventId);
            contentValues.put(EVENT_USER_ID, eUserId);
            contentValues.put(EVENT_ROLL_ID, eRollId);
            contentValues.put(EVENT_S_DATE, eSDate);
            contentValues.put(EVENT_E_DATE, eEDate);
            contentValues.put(EVENT_AT, eVenue);
            contentValues.put(EVENT_TYPE_ID, eTypeId);
            contentValues.put(EVENT_TYPE_NAME, eTypeName);
            contentValues.put(EVENT_TITLE, eTitle);
            contentValues.put(EVENT_PARTICIPANT_NUM, eParticipants);
            contentValues.put(EVENT_GROUP_TYPE, eGroupType);
            contentValues.put(EVENT_GROUP_ID, eGroupId);
            contentValues.put(EVENT_GROUP_NAME, eGroupName);
            contentValues.put(EVENT_GROUP_MEM_ID, mem_id);
            contentValues.put(EVENT_GROUP_MEM_F_NAME, mem_f_name);
            contentValues.put(EVENT_GROUP_MEM_M_NAME, mem_m_name);
            contentValues.put(EVENT_GROUP_MEM_L_NAME, mem_l_name);
            contentValues.put(EVENT_GROUP_MEM_GENDER, mem_gender);
            contentValues.put(EVENT_GROUP_MEM_MOB, mem_mobile);
            contentValues.put(EVENT_GROUP_MEM_DESIG, mem_designation);

            result = db.insert(EVENT_DETAIL_TABLE, null, contentValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // Update Other member by member id
    public Boolean updateEventMemAttendanceById(String memId, String isMemAttedn) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(EVENT_GROUP_MEM_IS_ATTEND, isMemAttedn);

            result = db.update(EVENT_DETAIL_TABLE,contentValues," event_group_mem_id = " + memId ,null );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    /* To get group list*/

    public JSONArray getGroupListByEventID(String eventId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_detail_table WHERE event_id = " + eventId + " GROUP By event_group_id ORDER BY event_group_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String grpId = cursor.getString(cursor.getColumnIndex("event_group_id"));
                String grpName = cursor.getString(cursor.getColumnIndex("event_group_name"));
                String grpType = cursor.getString(cursor.getColumnIndex("event_group_type"));
                String grpIsSelected = cursor.getString(cursor.getColumnIndex("event_group_is_attend"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("id", grpId);
                    jsonObject1.put("name", grpName);
                    jsonObject1.put("attendance_type", grpType);
                    jsonObject1.put("is_selected", grpIsSelected);

                    jsonArray.put(jsonObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Event group JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }



    /* To get group member list by group id*/
    public JSONArray getGrpMemListByGrpId(String grp_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_detail_table WHERE event_group_id = " + grp_id + " ORDER BY event_group_mem_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String gpMemId = cursor.getString(cursor.getColumnIndex("event_group_mem_id"));
                String gpMemFName = cursor.getString(cursor.getColumnIndex("event_group_mem_f_name"));
                String gpMemMName = cursor.getString(cursor.getColumnIndex("event_group_mem_m_name"));
                String gpMemLName = cursor.getString(cursor.getColumnIndex("event_group_mem_l_name"));
                String gpMemMobile = cursor.getString(cursor.getColumnIndex("event_group_mem_mobile"));
                String gpMemDesigName = cursor.getString(cursor.getColumnIndex("event_group_mem_desig"));
                String gpMemGenderId = cursor.getString(cursor.getColumnIndex("event_group_mem_gender"));
                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("event_group_mem_is_attend"));


                try {
                    JSONObject jsonObject1 = new JSONObject();

                    jsonObject1.put("id", gpMemId);
                    jsonObject1.put("name", gpMemFName +" "+ gpMemMName +" "+gpMemLName);
                    if (gpMemGenderId.equalsIgnoreCase("1")){
                        jsonObject1.put("gender", "Male");
                    }else if (gpMemGenderId.equalsIgnoreCase("2")){
                        jsonObject1.put("gender", "Female");
                    }else {
                        jsonObject1.put("gender", "Transgender");
                    }
                    jsonObject1.put("designation", gpMemDesigName);
                    jsonObject1.put("mobile", gpMemMobile);
                    jsonObject1.put("is_selected", gpMemIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("GP Member JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    // Insert other member detail by event id
    public Boolean insertOtherMemByEventId(String eventId, String eUserId, String eRollId, String eDate,
                                           String mem_f_name, String mem_m_name, String mem_l_name, String mem_gender_id,
                                           String mem_mobile,String mem_designation,String time) {


        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();


            contentValues.put(OTH_E_ID, eventId);
            contentValues.put(OTH_USER_ID, eUserId);
            contentValues.put(OTH_ROLL_ID, eRollId);
            contentValues.put(OTH_E_DATE, eDate);
            contentValues.put(OTH_E_MEM_F_NAME, mem_f_name);
            contentValues.put(OTH_E_MEM_M_NAME, mem_m_name);
            contentValues.put(OTH_E_MEM_L_NAME, mem_l_name);
            contentValues.put(OTH_E_MEM_GENDER_ID, mem_gender_id);
            contentValues.put(OTH_E_MEM_MOB, mem_mobile);
            contentValues.put(OTH_E_MEM_DSGN, mem_designation);
            contentValues.put(OTH_E_DATE_TIME, time);

            result = db.insert(EVENT_OTHER_MEM_TABLE, null, contentValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // Update Other member by member id
    public Boolean updateOtherMemById(String memId, String eventId, String eUserId, String eRollId, String eDate,
                                      String mem_f_name, String mem_m_name, String mem_l_name, String mem_gender_id,
                                      String mem_mobile, String mem_designation, String time) {


        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(OTH_E_ID, eventId);
            contentValues.put(OTH_USER_ID, eUserId);
            contentValues.put(OTH_ROLL_ID, eRollId);
            contentValues.put(OTH_E_DATE, eDate);
            contentValues.put(OTH_E_MEM_F_NAME, mem_f_name);
            contentValues.put(OTH_E_MEM_M_NAME, mem_m_name);
            contentValues.put(OTH_E_MEM_L_NAME, mem_l_name);
            contentValues.put(OTH_E_MEM_GENDER_ID, mem_gender_id);
            contentValues.put(OTH_E_MEM_MOB, mem_mobile);
            contentValues.put(OTH_E_MEM_DSGN, mem_designation);
            contentValues.put(OTH_E_DATE_TIME, time);

            result = db.update(EVENT_OTHER_MEM_TABLE,contentValues," o_m_id = " + memId ,null );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // is other member exist in other mem table
    public Boolean isOtherMemExistbyId(String memID) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_other_mem_table WHERE o_m_id = " + memID;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String memId = cursor.getString(cursor.getColumnIndex("o_m_id"));

                if (!memId.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }




    // Update Other member by member id
    public Boolean updateOtherMemAttendanceById(String memId, String isMemAttedn) {


        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(OTH_E_MEM_IS_ATTEND, isMemAttedn);

            result = db.update(EVENT_OTHER_MEM_TABLE,contentValues," o_m_id = " + memId ,null );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // To Check mobile number exist in other mem table
    public Boolean isMobileExistInOther(String mob) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_other_mem_table WHERE o_mem_mobile = " + mob;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String mobile = cursor.getString(cursor.getColumnIndex("o_mem_mobile"));

                if (!mobile.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // To Check mobile exist in Event Detail table
    public Boolean isMobileExistInEvent(String mob) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_detail_table WHERE event_group_mem_mobile = " + mob;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String mobile = cursor.getString(cursor.getColumnIndex("event_group_mem_mobile"));

                if (!mobile.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // Get other member list by event id
    public JSONArray getOtherMemByEventId(String eventId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_other_mem_table WHERE event_id = "+ eventId +" ORDER BY o_m_id ASC " ;


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String eId = cursor.getString(cursor.getColumnIndex("event_id"));
                String MemId = cursor.getString(cursor.getColumnIndex("o_m_id"));
                String MemFName = cursor.getString(cursor.getColumnIndex("o_mem_f_name"));
                String MemMName = cursor.getString(cursor.getColumnIndex("o_mem_m_name"));
                String MemLName = cursor.getString(cursor.getColumnIndex("o_mem_l_name"));
                String MemMobile = cursor.getString(cursor.getColumnIndex("o_mem_mobile"));
                String MemGenderId = cursor.getString(cursor.getColumnIndex("o_mem_gender"));
                String roleId = cursor.getString(cursor.getColumnIndex("o_roll_id"));
                String userId = cursor.getString(cursor.getColumnIndex("o_user_id"));
                String memDesig = cursor.getString(cursor.getColumnIndex("o_mem_dsgn"));
                String MemIsSelected = cursor.getString(cursor.getColumnIndex("o_mem_is_attend"));


                try {
                    JSONObject jsonObject1 = new JSONObject();

                    jsonObject1.put("id", MemId);
                    jsonObject1.put("schedule_event_id", eId);
                    jsonObject1.put("name", MemFName +" "+ MemMName +" "+MemLName);
                    jsonObject1.put("first_name", MemFName);
                    jsonObject1.put("middle_name", MemMName);
                    jsonObject1.put("last_name", MemLName);
                    jsonObject1.put("mobile", MemMobile);
                    jsonObject1.put("gender", MemGenderId);
                    jsonObject1.put("role_id", roleId);
                    jsonObject1.put("created_by", userId);
                    jsonObject1.put("designation", memDesig);
                    jsonObject1.put("is_selected", MemIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Other member detail" + jsonArray.toString());
        }
        return jsonArray;
    }


    // Get other member list by event id
    public JSONArray getOtherMemListByEventIdDate(String eventId, String date) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_other_mem_table WHERE event_id = '"+ eventId +"' AND o_event_date = '"+ date +"' ORDER BY o_m_id ASC " ;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String eId = cursor.getString(cursor.getColumnIndex("event_id"));
                String MemId = cursor.getString(cursor.getColumnIndex("o_m_id"));
                String MemFName = cursor.getString(cursor.getColumnIndex("o_mem_f_name"));
                String MemMName = cursor.getString(cursor.getColumnIndex("o_mem_m_name"));
                String MemLName = cursor.getString(cursor.getColumnIndex("o_mem_l_name"));
                String MemMobile = cursor.getString(cursor.getColumnIndex("o_mem_mobile"));
                String MemGenderId = cursor.getString(cursor.getColumnIndex("o_mem_gender"));
                String roleId = cursor.getString(cursor.getColumnIndex("o_roll_id"));
                String userId = cursor.getString(cursor.getColumnIndex("o_user_id"));
                String memDesig = cursor.getString(cursor.getColumnIndex("o_mem_dsgn"));

                try {
                    JSONObject jsonObject1 = new JSONObject();

                    jsonObject1.put("id", MemId);
                    jsonObject1.put("schedule_event_id", eId);
                    jsonObject1.put("name", MemFName +" "+ MemMName +" "+MemLName);
                    jsonObject1.put("first_name", MemFName);
                    jsonObject1.put("middle_name", MemMName);
                    jsonObject1.put("last_name", MemLName);
                    jsonObject1.put("mobile", MemMobile);
                    jsonObject1.put("gender", MemGenderId);
                    jsonObject1.put("role_id", roleId);
                    jsonObject1.put("created_by", userId);
                    jsonObject1.put("designation", memDesig);


                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Other member detail" + jsonArray.toString());
        }
        return jsonArray;
    }



    // Get other member detail by member id
    public JSONArray getOtherMemDetailById(String othMemId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_other_mem_table WHERE o_m_id = "+ othMemId ;


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String eventId = cursor.getString(cursor.getColumnIndex("event_id"));
                String gpMemId = cursor.getString(cursor.getColumnIndex("o_m_id"));
                String gpMemFName = cursor.getString(cursor.getColumnIndex("o_mem_f_name"));
                String gpMemMName = cursor.getString(cursor.getColumnIndex("o_mem_m_name"));
                String gpMemLName = cursor.getString(cursor.getColumnIndex("o_mem_l_name"));
                String gpMemMobile = cursor.getString(cursor.getColumnIndex("o_mem_mobile"));
                String gpMemGenderId = cursor.getString(cursor.getColumnIndex("o_mem_gender"));
                String roleId = cursor.getString(cursor.getColumnIndex("o_roll_id"));
                String userId = cursor.getString(cursor.getColumnIndex("o_user_id"));
                String memDesig = cursor.getString(cursor.getColumnIndex("o_mem_dsgn"));
                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("o_mem_is_attend"));


                try {
                    JSONObject jsonObject1 = new JSONObject();

                    jsonObject1.put("id", gpMemId);
                    jsonObject1.put("schedule_event_id", eventId);
                    jsonObject1.put("first_name", gpMemFName);
                    jsonObject1.put("middle_name", gpMemMName);
                    jsonObject1.put("last_name", gpMemLName);
                    jsonObject1.put("mobile", gpMemMobile);
                    jsonObject1.put("gender_id", gpMemGenderId);

                    if (gpMemGenderId.equalsIgnoreCase("1")){
                        jsonObject1.put("gender_name", "Male");
                    }else if (gpMemGenderId.equalsIgnoreCase("2")){
                        jsonObject1.put("gender_name", "Female");
                    }else {
                        jsonObject1.put("gender_name", "Transgender");
                    }
                    jsonObject1.put("role_id", roleId);
                    jsonObject1.put("created_by", userId);
                    jsonObject1.put("designation", memDesig);
                    jsonObject1.put("is_selected", gpMemIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Other member detail" + jsonArray.toString());
        }
        return jsonArray;
    }



    // is other member detail sync done by date
    public Boolean isOthMemDetailSyncedByDate(String eventId, String eventDate) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_other_mem_table WHERE event_id = "+ eventId+" AND o_event_date = '"+eventDate +"' AND is_mem_detail_sysc = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("is_mem_detail_sysc"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // is image sync by date
    public Boolean isImgSyncedByDate(String eventId, String eventDate) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_image_table WHERE event_id = "+ eventId+" AND event_img_date = '"+eventDate +"' AND event_img_sync = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("event_img_sync"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // is Attendance synced by date
    public Boolean isAttendanceSyncedByDate(String eventId, String eventDate) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_mem_attend_table WHERE event_id = '"+ eventId+ "' AND att_date = '"+eventDate +"' AND att_is_sysc = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("att_is_sysc"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // To Check is attendance done by Date
    public Boolean isAnyAttendanceDoneByDate(String eventId, String eventDate) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_mem_attend_table WHERE event_id = "+ eventId+" AND att_date = '"+ eventDate +"' ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isMemPresent = cursor.getString(cursor.getColumnIndex("att_mem_id"));

                if (!isMemPresent.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // is other member detail sync done
    public Boolean isOthMemDetailSyncedByEId(String eventId) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_other_mem_table WHERE event_id = "+ eventId+" AND is_mem_detail_sysc = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("is_mem_detail_sysc"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // is Image sync done
    public Boolean isImgSyncedByEId(String eventId) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_image_table WHERE event_id = "+ eventId+" AND event_img_sync = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("event_img_sync"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // is attendance detail sync done
    public Boolean isAttendanceSyncedByEId(String eventId) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_mem_attend_table WHERE event_id = "+ eventId+ " AND att_is_sysc = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String isSync = cursor.getString(cursor.getColumnIndex("att_is_sysc"));

                if (!isSync.equalsIgnoreCase("0")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



        // For Attendance

    // Insert member Attendance detail
    public Boolean insertAttendanceDetail(String eventId, String grp_id,String grp_type, String eDate,String eUserId, String eRollId,
                                          String mem_id, String mem_mobile,String time) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(ATT_E_ID, eventId);
            contentValues.put(ATT_GROUP_ID, grp_id);
            contentValues.put(ATT_GROUP_TYPE, grp_type);
            contentValues.put(ATT_DATE, eDate);
            contentValues.put(ATT_USER_ID, eUserId);
            contentValues.put(ATT_ROLE_ID, eRollId);
            contentValues.put(ATT_MEM_ID, mem_id);
            contentValues.put(ATT_MEM_MOB, mem_mobile);
            contentValues.put(ATT_DATE_TIME, time);

            result = db.insert(EVENT_MEM_ATTEND_TABLE, null, contentValues);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // To Check Group Attendance exist
    public Boolean isGroupAttendanceExist(String grpID, String date) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM event_mem_attend_table WHERE att_date = '" + date + "' AND att_group_id = "+ grpID + " GROUP BY  att_group_id ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String grp_id = cursor.getString(cursor.getColumnIndex("att_group_id"));

                if (!grp_id.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }




    // Update member Attendance detail
    public Boolean updateAttendanceDetail(String eventId, String grp_id,String grp_type, String eDate,String eUserId, String eRollId,
                                          String mem_id, String mem_mobile,String time) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(ATT_E_ID, eventId);
            contentValues.put(ATT_GROUP_ID, grp_id);
            contentValues.put(ATT_GROUP_TYPE, grp_type);
            contentValues.put(ATT_DATE, eDate);
            contentValues.put(ATT_USER_ID, eUserId);
            contentValues.put(ATT_ROLE_ID, eRollId);
            contentValues.put(ATT_MEM_ID, mem_id);
            contentValues.put(ATT_MEM_MOB, mem_mobile);
            contentValues.put(ATT_DATE_TIME, time);

            result = db.update(EVENT_MEM_ATTEND_TABLE,contentValues," att_mem_id = " + mem_id ,null );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // To Remove Attendance if exist against schId, date & groupID
    public Boolean removeAttendanceBySchIdDateGrpId(String date,String grpID) {

        boolean result = false;

        SQLiteDatabase db = this.getReadableDatabase();
        // String whereClose = " att_date = "+ date +" AND att_group_id = "+grpID;
        result = db.delete(EVENT_MEM_ATTEND_TABLE, " att_date = '"+ date +"' AND att_group_id = "+grpID , null) > 0;

        if (result){
            return true;
        }else {
            return false;
        }

    }


    // Get Present member list by group id and date
    public long getNumPresentMemByDate(String date) {

        long count = 0;
        SQLiteDatabase cDb = this.getReadableDatabase();

        count = DatabaseUtils.longForQuery(cDb, " SELECT COUNT(*) FROM event_mem_attend_table WHERE att_date = '" + date + "'", null);
        return count;
    }



    // Get Present member list by group id and date
    public JSONArray getPresentMemByDateGroup(String date, String groupType) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM event_mem_attend_table WHERE att_group_type = '"+ groupType +"' AND att_date = '" + date + "' ORDER BY  att_mem_id ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String eId = cursor.getString(cursor.getColumnIndex("event_id"));
                String grpID = cursor.getString(cursor.getColumnIndex("att_group_id"));
                String grpType = cursor.getString(cursor.getColumnIndex("att_group_type"));
                String attendDate = cursor.getString(cursor.getColumnIndex("att_date"));
                String userId = cursor.getString(cursor.getColumnIndex("att_user_id"));
                String roleId = cursor.getString(cursor.getColumnIndex("att_role_id"));
                String memberId = cursor.getString(cursor.getColumnIndex("att_mem_id"));
                String memMobNum = cursor.getString(cursor.getColumnIndex("att_mem_mob"));
                String time = cursor.getString(cursor.getColumnIndex("att_date_time"));


                try {
                    JSONObject jsonObject1 = new JSONObject();


                    jsonObject1.put("schedule_event_id", eId);
                    jsonObject1.put("gp_id", grpID);
                    jsonObject1.put("groupType", grpType);
                    jsonObject1.put("attendance_date", attendDate);
                    jsonObject1.put("created_by", userId);
                    jsonObject1.put("role_id", roleId);
                    jsonObject1.put("member_id", memberId);
                    jsonObject1.put("memMob", memMobNum);
                    jsonObject1.put("created_at", time);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Attended member list" + jsonArray.toString());
        }
        return jsonArray;
    }





}
