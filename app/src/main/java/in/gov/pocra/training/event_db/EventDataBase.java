package in.gov.pocra.training.event_db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.debug.DebugLog;

public class EventDataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EventDatabase.db";

    // VCRMC or GP List with Gp member detail

    public static final String GP_MEM_TABLE = "gp_mem_table";
    public static final String ID = "u_id";
    public static final String TALUKA_ID = "taluka_id";
    public static final String GP_ID = "gp_id";
    public static final String GP_NAME = "gp_name";
    public static final String GP_CODE = "gp_code";
    public static final String GP_IS_SELECTED = "gp_is_selected";
    public static final String GP_MEM_ID = "mem_id";
    public static final String GP_MEM_NAME = "mem_name";
    public static final String GP_MEM_F_NAME = "mem_first_name";
    public static final String GP_MEM_M_NAME = "mem_middle_name";
    public static final String GP_MEM_L_NAME = "mem_last_name";
    public static final String GP_MEM_MOBILE = "mem_mobile";
    public static final String GP_MEM_MOBILE2 = "mem_mobile2";
    public static final String GP_MEM_DESIG_ID = "mem_designation_id";
    public static final String GP_MEM_DESIG_NAME = "mem_designation_name";
    public static final String GP_MEM_GENDER_ID = "mem_gender_id";
    public static final String GP_MEM_GENDER_NAME = "mem_gender_name";
    public static final String GP_MEM_SOC_CAT_ID = "mem_soc_cat_id";
    public static final String GP_MEM_SOC_CAT_NAME = "mem_soc_cat_name";
    public static final String GP_MEM_LAND_HAND_CAT = "gp_mem_land_hold_cat";
    public static final String GP_MEM_IS_SELECTED = "mem_is_selected";


    // Village and farmer detail table
    public static final String VIL_FARMER_TABLE = "vil_farmer_table";
    public static final String VIL_ID = "u_id";
    public static final String VILLAGE_ID = "village_id";
    public static final String VILLAGE_NAME = "village_name";
    public static final String VILLAGE_CODE = "village_code";
    public static final String VIL_IS_SELECTED = "vil_is_selected";
    public static final String VIL_ACT_ID = "vil_act_id";
    public static final String VIL_ACT_NAME = "vil_act_name";
    public static final String VIL_ACT_IS_SELECTED = "vil_act_is_selected";
    public static final String VIL_FAR_ID = "farmer_id";
    public static final String VIL_FAR_NAME = "farmer_name";
    public static final String VIL_FAR_MOBILE = "farmer_mobile";
    public static final String VIL_FAR_GEN_ID = "farmer_gen_id";
    public static final String VIL_FAR_GEN_NAME = "farmer_gen_name";
    public static final String VIL_FAR_DESIG_ID = "farmer_desig_id";
    public static final String VIL_FAR_DESIG_NAME = "farmer_desig_name";
    public static final String VIL_FAR_IS_SELECTED = "far_is_selected";

    // Village and farmer detail table
    public static final String PO_MEM_TABLE = "pom_member_table";
    public static final String PO_ID = "u_id";
    public static final String POM_ROLE_ID = "pom_role_id";
    public static final String POM_F_NAME = "pom_first_name";
    public static final String POM_M_NAME = "pom_middle_name";
    public static final String POM_L_NAME = "pom_last_name";
    public static final String POM_ID = "pom_id";
    public static final String POM_MOBILE = "pom_mobile";
    public static final String POM_GEN_ID = "pom_gender";
    public static final String POM_GEN_NAME = "pom_get_name";
    public static final String POM_DESIGNATION = "pom_designation";
    public static final String POM_TALUKA = "pom_taluka_id";
    public static final String POM_IS_SELECTED = "pom_is_selected";


    ///shgtable
    public static final String SHG_TABLE = "shg_table";
    public static final String SName = "name";
    public static final String SVillage_code = "village_code";
    public static final String Smobile = "mobile";
    public static final String Sflag = "flag";
    public static final String Sproname = "proname";
    public static final String Sshg_is_selected = "shg_is_selected";


    // Fpc table
    public static final String FPC_TABLE = "fpc_table";
    public static final String FPC_TALUKA_ID = "taluka_id";
    public static final String FPC_NAME = "name";
    public static final String FPC_CONTACT_NO = "contact_no";
    public static final String FPC_GROUP_FLAG = "group_flag";
    public static final String FPC_CONTACT_PERSON = "contact_person";
    public static final String FPC_IS_SELECTED = "is_selected";
    public static final String FPC_CIN = "cin";


    // Farmer Group table
    public static final String FG_TABLE = "farmer_group_table";
    public static final String FG_VILLAGE_NAME = "village";
    public static final String FG_VILLAGE_CODE = "village_census_code";
    public static final String FG_GROUP_NAME = "group_name";
    public static final String FG_CONTACT_PERSON = "contact_person";
    public static final String FG_CONTACT_NUMBER = "contact_number";
    public static final String FG_FLAG = "group_flag";
    public static final String FG_IS_SELECTED = "is_selected";


    private static EventDataBase eventDataBase = null;

    public static EventDataBase getInstance(Context context) {
        if (eventDataBase == null) {
            eventDataBase = new EventDataBase(context);
        }
        return eventDataBase;
    }

    public EventDataBase(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            //SHG TABLE
            db.execSQL("CREATE TABLE " + SHG_TABLE +
                    "(" + VIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + SVillage_code + " TEXT ,"
                    + Sflag + " TEXT ,"
                    + SName + " TEXT ,"
                    + Sproname + " TEXT ,"
                    + Sshg_is_selected + " INTIGER DEFAULT 0 ,"
                    + Smobile + " TEXT ,"
                    + VIL_FAR_ID + " TEXT "
                    + ")");


            //FPC TABLE
            db.execSQL("CREATE TABLE " + FPC_TABLE +
                    "(" + VIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + FPC_TALUKA_ID + " TEXT ,"
                    + FPC_NAME + " TEXT ,"
                    + FPC_CONTACT_NO + " TEXT ,"
                    + FPC_GROUP_FLAG + " TEXT ,"
                    + FPC_IS_SELECTED + " INTIGER DEFAULT 0 ,"
                    + FPC_CONTACT_PERSON + " TEXT ,"
                    + FPC_CIN + " TEXT ,"
                    + VIL_FAR_ID + " TEXT "
                    + ")");


            //Farmer Group TABLE
            db.execSQL("CREATE TABLE " + FG_TABLE +
                    "(" + VIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + FG_VILLAGE_NAME + " TEXT ,"
                    + FG_VILLAGE_CODE  + " TEXT ,"
                    + FG_GROUP_NAME + " TEXT ,"
                    + FG_CONTACT_PERSON  + " TEXT ,"
                    + FG_CONTACT_NUMBER  + " INTIGER DEFAULT 0 ,"
                    + FG_FLAG  + " TEXT ,"
                    + FG_IS_SELECTED  + " TEXT ,"
                    + VIL_FAR_ID + " TEXT "
                    + ")");


            // GP MEMBER TABLE
            db.execSQL("CREATE TABLE " + GP_MEM_TABLE +
                    "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + TALUKA_ID + " TEXT ,"
                    + GP_ID + " TEXT ,"
                    + GP_NAME + " TEXT ,"
                    + GP_CODE + " TEXT ,"
                    + GP_IS_SELECTED + " TEXT ,"
                    + GP_MEM_ID + " TEXT ,"
                    + GP_MEM_NAME + " TEXT ,"
                    + GP_MEM_F_NAME + " TEXT ,"
                    + GP_MEM_M_NAME + " TEXT ,"
                    + GP_MEM_L_NAME + " TEXT ,"
                    + GP_MEM_MOBILE + " TEXT ,"
                    + GP_MEM_MOBILE2 + " TEXT ,"
                    + GP_MEM_DESIG_ID + " TEXT ,"
                    + GP_MEM_DESIG_NAME + " TEXT ,"
                    + GP_MEM_GENDER_ID + " TEXT ,"
                    + GP_MEM_GENDER_NAME + " TEXT ,"
                    + GP_MEM_SOC_CAT_ID + " TEXT ,"
                    + GP_MEM_SOC_CAT_NAME + " TEXT ,"
                    + GP_MEM_LAND_HAND_CAT + " TEXT ,"
                    + GP_MEM_IS_SELECTED + " INTIGER DEFAULT 0 "
                    + ")");


            // VILLAGE FARMER TABLE
            db.execSQL("CREATE TABLE " + VIL_FARMER_TABLE +
                    "(" + VIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + VILLAGE_ID + " TEXT ,"
                    + VILLAGE_NAME + " TEXT ,"
                    + VILLAGE_CODE + " TEXT ,"
                    + VIL_IS_SELECTED + " TEXT ,"
                    + VIL_ACT_ID + " TEXT ,"
                    + VIL_ACT_NAME + " TEXT ,"
                    + VIL_ACT_IS_SELECTED + " TEXT ,"
                    + VIL_FAR_ID + " TEXT ,"
                    + VIL_FAR_NAME + " TEXT ,"
                    + VIL_FAR_MOBILE + " TEXT ,"
                    + VIL_FAR_GEN_ID + " TEXT ,"
                    + VIL_FAR_GEN_NAME + " TEXT ,"
                    + VIL_FAR_DESIG_ID + " TEXT ,"
                    + VIL_FAR_DESIG_NAME + " TEXT ,"
                    + VIL_FAR_IS_SELECTED + " INTIGER DEFAULT 0 "
                    + ")");


            // FFS FACILITATOR TABLE
            db.execSQL("CREATE TABLE " + PO_MEM_TABLE +
                    "(" + PO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    + POM_ROLE_ID + " TEXT ,"
                    + POM_F_NAME + " TEXT ,"
                    + POM_M_NAME + " TEXT ,"
                    + POM_L_NAME + " TEXT ,"
                    + POM_ID + " TEXT ,"
                    + POM_MOBILE + " TEXT ,"
                    + POM_GEN_ID + " TEXT ,"
                    + POM_GEN_NAME + " TEXT ,"
                    + POM_DESIGNATION + " TEXT ,"
                    + POM_TALUKA + " TEXT ,"
                    + POM_IS_SELECTED + " INTIGER DEFAULT 0 "
                    + ")");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        try {

            if (oldVersion != newVersion) {

                // For GP MEMBER TABLE
                db.execSQL(" DROP TABLE IF EXISTS " + GP_MEM_TABLE);
                onCreate(db);

                //for SHG
                db.execSQL(" DROP TABLE IF EXISTS " + SHG_TABLE);
                onCreate(db);

                //for FPC
                db.execSQL(" DROP TABLE IF EXISTS " + FPC_TABLE);
                onCreate(db);

                //for Farmer Group   FG
                db.execSQL(" DROP TABLE IF EXISTS " + FG_TABLE);
                onCreate(db);

                // For VILLAGE FARMAR TABLE
                db.execSQL(" DROP TABLE IF EXISTS " + VIL_FARMER_TABLE);
                onCreate(db);

                // For FFS FACILITATOR TABLE
                db.execSQL(" DROP TABLE IF EXISTS " + PO_MEM_TABLE);
                onCreate(db);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // COMMON Methods

    // for getting last inserted id
    public long lastId(String TABLE_NAME) {
        long Id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ROWID from " + TABLE_NAME + " order by ROWID DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            Id = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return Id;
    }

    // for getting first inserted id
    public long firstId(String TABLE_NAME) {
        long Id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT ROWID from " + TABLE_NAME + " order by ROWID ASC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            Id = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        return Id;
    }


    // for getting number of record in table
    public long getNoOfRecord(String TABLE_NAME) {
        long count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        count = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_NAME, null);
        return count;
    }

    // for for deleting selected table row
    public boolean deleteRow(String TABLE_NAME, String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_NAME, " id = " + rowId, null) > 0;
    }

    // for for deleting all table data from database
    public void deleteAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        // db.delete(GP_TABLE, null, null);
        db.delete(GP_MEM_TABLE, null, null);
        db.delete(VIL_FARMER_TABLE, null, null);
        db.delete(PO_MEM_TABLE, null, null);
        db.delete(SHG_TABLE, null, null);
        db.delete(FPC_TABLE, null, null);
        db.delete(FG_TABLE, null, null);

    }

    // for deleting a table data from a database
    public void deleteTable(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }


    // INSERT SHG_TABLE TABLE DETAIL
    public Boolean insertshg(String vcode, String flag, String s_name, String sproname, String shg_is_selected,
                             String smobile, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SVillage_code, vcode);
            contentValues.put(Sflag, flag);
            contentValues.put(SName, s_name);
            contentValues.put(Sproname, sproname);
            contentValues.put(Sshg_is_selected, shg_is_selected);
            contentValues.put(Smobile, smobile);
            contentValues.put(VIL_FAR_ID, farmer_id);
            result = db.insert(SHG_TABLE, null, contentValues);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    // UPDATE SHG_TABLE TABLE DETAIL
    public Boolean updateShg(String vcode, String flag, String s_name, String sproname, String shg_is_selected,
                             String smobile, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SVillage_code, vcode);
            contentValues.put(Sflag, flag);
            contentValues.put(SName, s_name);
            contentValues.put(Sproname, sproname);
            contentValues.put(Sshg_is_selected, shg_is_selected);
            contentValues.put(Smobile, smobile);
            result = db.update(SHG_TABLE, contentValues, " farmer_id = " + farmer_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // INSERT FPC_TABLE TABLE DETAIL
    public Boolean insertFpc(String taluka_id, String fpc_name, String fpc_contact_no, String fpc_flag, String fpc_contact_person,
                             String fpc_is_selected, String cin, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FPC_TALUKA_ID, taluka_id);
            contentValues.put(FPC_NAME, fpc_name);
            contentValues.put(FPC_CONTACT_NO, fpc_contact_no);
            contentValues.put(FPC_GROUP_FLAG, fpc_flag);
            contentValues.put(FPC_CONTACT_PERSON, fpc_contact_person);
            contentValues.put(FPC_IS_SELECTED, fpc_is_selected);
            contentValues.put(FPC_CIN, cin);
            contentValues.put(VIL_FAR_ID, farmer_id);
            result = db.insert(FPC_TABLE, null, contentValues);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // UPDATE INTO FPC_TABLE DETAIL
    public Boolean updateFpc(String taluka_id, String fpc_name, String fpc_contact_no, String fpc_flag, String fpc_contact_person,
                             String fpc_is_selected, String cin, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FPC_TALUKA_ID, taluka_id);
            contentValues.put(FPC_NAME, fpc_name);
            contentValues.put(FPC_CONTACT_NO, fpc_contact_no);
            contentValues.put(FPC_GROUP_FLAG, fpc_flag);
            contentValues.put(FPC_CONTACT_PERSON, fpc_contact_person);
            contentValues.put(FPC_IS_SELECTED, fpc_is_selected);
            contentValues.put(FPC_CIN, cin);
            result = db.update(FPC_TABLE, contentValues, " farmer_id = " + farmer_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // INSERT FG_TABLE TABLE DETAIL
    public Boolean insertFarmerGroup(String village_name, String village_cocde, String group_name, String contact_person, String contact_number,
                             String flag, String id_selected, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FG_VILLAGE_NAME , village_name);
            contentValues.put(FG_VILLAGE_CODE  , village_cocde);
            contentValues.put(FG_GROUP_NAME , group_name);
            contentValues.put(FG_CONTACT_PERSON , contact_person);
            contentValues.put(FG_CONTACT_NUMBER , contact_number);
            contentValues.put(FG_FLAG , flag);
            contentValues.put(FG_IS_SELECTED , id_selected);
            contentValues.put(VIL_FAR_ID  , farmer_id);
            result = db.insert(FG_TABLE, null, contentValues);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // UPDATE FG_TABLE TABLE DETAIL
    public Boolean updateFarmerGroup(String village_name, String village_cocde, String group_name, String contact_person, String contact_number,
                                     String flag, String id_selected, String farmer_id) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FG_VILLAGE_NAME , village_name);
            contentValues.put(FG_VILLAGE_CODE  , village_cocde);
            contentValues.put(FG_GROUP_NAME , group_name);
            contentValues.put(FG_CONTACT_PERSON , contact_person);
            contentValues.put(FG_CONTACT_NUMBER , contact_number);
            contentValues.put(FG_FLAG , flag);
            contentValues.put(FG_IS_SELECTED , id_selected);
            contentValues.put(VIL_FAR_ID  , farmer_id);
            result = db.update(FG_TABLE, contentValues, " farmer_id = " + farmer_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }







    /* FOR VERMC(GP)  */
    // INSERT GP_MEM_TABLE TABLE DETAIL
    public Boolean insertGPWithMemDetail(String taluka_id, String gp_id, String gp_name, String gp_code, String gp_is_selected,
                                         String mem_id, String mem_name, String mem_first_name, String mem_middle_name,
                                         String mem_last_name, String mem_mobile, String mem_mobile2, String mem_designation_id,
                                         String mem_designation_name, String mem_gender_id, String mem_gender_name,
                                         String mem_soc_cat_id, String mem_soc_cat_name, String gp_mem_land_hold_cat,
                                         String mem_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TALUKA_ID, taluka_id);
            contentValues.put(GP_ID, gp_id);
            contentValues.put(GP_NAME, gp_name);
            contentValues.put(GP_CODE, gp_code);
            contentValues.put(GP_IS_SELECTED, gp_is_selected);
            contentValues.put(GP_MEM_ID, mem_id);
            contentValues.put(GP_MEM_NAME, mem_name);
            contentValues.put(GP_MEM_F_NAME, mem_first_name);
            contentValues.put(GP_MEM_M_NAME, mem_middle_name);
            contentValues.put(GP_MEM_L_NAME, mem_last_name);
            contentValues.put(GP_MEM_MOBILE, mem_mobile);
            contentValues.put(GP_MEM_MOBILE2, mem_mobile2);
            contentValues.put(GP_MEM_DESIG_ID, mem_designation_id);
            contentValues.put(GP_MEM_DESIG_NAME, mem_designation_name);
            contentValues.put(GP_MEM_GENDER_ID, mem_gender_id);
            contentValues.put(GP_MEM_GENDER_NAME, mem_gender_name);
            contentValues.put(GP_MEM_SOC_CAT_ID, mem_soc_cat_id);
            contentValues.put(GP_MEM_SOC_CAT_NAME, mem_soc_cat_name);
            contentValues.put(GP_MEM_LAND_HAND_CAT, gp_mem_land_hold_cat);
            contentValues.put(GP_MEM_IS_SELECTED, mem_is_selected);
Log.d("test1q2132113",contentValues.toString());
            result = db.insert(GP_MEM_TABLE, null, contentValues);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean insertGPByCaWithMemDetail(String ca_id, String gp_id, String gp_name, String gp_code, String gp_is_selected,
                                             String mem_id, String mem_name, String mem_first_name, String mem_middle_name,
                                             String mem_last_name, String mem_mobile, String mem_mobile2, String mem_designation_id,
                                             String mem_designation_name, String mem_gender_id, String mem_gender_name,
                                             String mem_soc_cat_id, String mem_soc_cat_name, String gp_mem_land_hold_cat,
                                             String mem_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TALUKA_ID, ca_id);
            contentValues.put(GP_ID, gp_id);
            contentValues.put(GP_NAME, gp_name);
            contentValues.put(GP_CODE, gp_code);
            contentValues.put(GP_IS_SELECTED, gp_is_selected);
            contentValues.put(GP_MEM_ID, mem_id);
            contentValues.put(GP_MEM_NAME, mem_name);
            contentValues.put(GP_MEM_F_NAME, mem_first_name);
            contentValues.put(GP_MEM_M_NAME, mem_middle_name);
            contentValues.put(GP_MEM_L_NAME, mem_last_name);
            contentValues.put(GP_MEM_MOBILE, mem_mobile);
            contentValues.put(GP_MEM_MOBILE2, mem_mobile2);
            contentValues.put(GP_MEM_DESIG_ID, mem_designation_id);
            contentValues.put(GP_MEM_DESIG_NAME, mem_designation_name);
            contentValues.put(GP_MEM_GENDER_ID, mem_gender_id);
            contentValues.put(GP_MEM_GENDER_NAME, mem_gender_name);
            contentValues.put(GP_MEM_SOC_CAT_ID, mem_soc_cat_id);
            contentValues.put(GP_MEM_SOC_CAT_NAME, mem_soc_cat_name);
            contentValues.put(GP_MEM_LAND_HAND_CAT, gp_mem_land_hold_cat);
            contentValues.put(GP_MEM_IS_SELECTED, mem_is_selected);

            result = db.insert(GP_MEM_TABLE, null, contentValues);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    /* FOR VERMC(GP)  */
    // UPDATE GP_MEM_TABLE TABLE DETAIL
    public Boolean updateGPWithMemDetailByMemId(String taluka_id, String gp_id, String gp_name, String gp_code, String gp_is_selected,
                                                String mem_id, String mem_name, String mem_first_name, String mem_middle_name,
                                                String mem_last_name, String mem_mobile, String mem_mobile2, String mem_designation_id,
                                                String mem_designation_name, String mem_gender_id, String mem_gender_name,
                                                String mem_soc_cat_id, String mem_soc_cat_name, String gp_mem_land_hold_cat,
                                                String mem_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(TALUKA_ID, taluka_id);
            contentValues.put(GP_ID, gp_id);
            contentValues.put(GP_NAME, gp_name);
            contentValues.put(GP_CODE, gp_code);
            contentValues.put(GP_IS_SELECTED, gp_is_selected);
            contentValues.put(GP_MEM_ID, mem_id);
            contentValues.put(GP_MEM_NAME, mem_name);
            contentValues.put(GP_MEM_F_NAME, mem_first_name);
            contentValues.put(GP_MEM_M_NAME, mem_middle_name);
            contentValues.put(GP_MEM_L_NAME, mem_last_name);
            contentValues.put(GP_MEM_MOBILE, mem_mobile);
            contentValues.put(GP_MEM_MOBILE2, mem_mobile2);
            contentValues.put(GP_MEM_DESIG_ID, mem_designation_id);
            contentValues.put(GP_MEM_DESIG_NAME, mem_designation_name);
            contentValues.put(GP_MEM_GENDER_ID, mem_gender_id);
            contentValues.put(GP_MEM_GENDER_NAME, mem_gender_name);
            contentValues.put(GP_MEM_SOC_CAT_ID, mem_soc_cat_id);
            contentValues.put(GP_MEM_SOC_CAT_NAME, mem_soc_cat_name);
            contentValues.put(GP_MEM_LAND_HAND_CAT, gp_mem_land_hold_cat);
            contentValues.put(GP_MEM_IS_SELECTED, mem_is_selected);

            result = db.update(GP_MEM_TABLE, contentValues, " mem_id = " + mem_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }





    /* TO GET DETAIL */

    /* FOR VERMC(GP)  */
    /* To get GP list*/
    public JSONArray getGpList(String talukaId) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE taluka_id = " + talukaId + " GROUP By gp_id ORDER BY u_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String id = cursor.getString(cursor.getColumnIndex("u_id"));
                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));
                String gpName = cursor.getString(cursor.getColumnIndex("gp_name"));
                String gpIsSelected = cursor.getString(cursor.getColumnIndex("gp_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", id);
                    jsonObject1.put("gp_id", gpId);
                    jsonObject1.put("gp_name", gpName);
                    jsonObject1.put("gp_is_selected", gpIsSelected);

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

    // For CA
    public JSONArray getGpListByCA(String caID) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE taluka_id = " + caID + " GROUP By gp_id ORDER BY u_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String id = cursor.getString(cursor.getColumnIndex("u_id"));
                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));
                String gpName = cursor.getString(cursor.getColumnIndex("gp_name"));
                String gpIsSelected = cursor.getString(cursor.getColumnIndex("gp_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", id);
                    jsonObject1.put("gp_id", gpId);
                    jsonObject1.put("gp_name", gpName);
                    jsonObject1.put("gp_is_selected", gpIsSelected);

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


    public JSONArray getGpMemberListByGpId(String gp_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE gp_id = " + gp_id + " ORDER BY u_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String id = cursor.getString(cursor.getColumnIndex("u_id"));
                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));
                String gpName = cursor.getString(cursor.getColumnIndex("gp_name"));

                String gpIsSelected = cursor.getString(cursor.getColumnIndex("gp_is_selected"));
                String gpMemId = cursor.getString(cursor.getColumnIndex("mem_id"));
                String gpMemName = cursor.getString(cursor.getColumnIndex("mem_name"));
                String gpMemFName = cursor.getString(cursor.getColumnIndex("mem_first_name"));
                String gpMemMName = cursor.getString(cursor.getColumnIndex("mem_middle_name"));
                String gpMemLName = cursor.getString(cursor.getColumnIndex("mem_last_name"));
                String gpMemMobile = cursor.getString(cursor.getColumnIndex("mem_mobile"));
                String gpMemMobile2 = cursor.getString(cursor.getColumnIndex("mem_mobile2"));
                String gpMemDesigId = cursor.getString(cursor.getColumnIndex("mem_designation_id"));
                String gpMemDesigName = cursor.getString(cursor.getColumnIndex("mem_designation_name"));
                String gpMemGenderId = cursor.getString(cursor.getColumnIndex("mem_gender_id"));
                String gpMemGenderName = cursor.getString(cursor.getColumnIndex("mem_gender_name"));
                String gpMemSocCatId = cursor.getString(cursor.getColumnIndex("mem_soc_cat_id"));
                String gpMemSocCatName = cursor.getString(cursor.getColumnIndex("mem_soc_cat_name"));
                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("mem_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", id);
                    jsonObject1.put("gp_id", gpId);
                    jsonObject1.put("gp_name", gpName);
                    jsonObject1.put("gp_is_selected", gpIsSelected);
                    jsonObject1.put("mem_id", gpMemId);
                    jsonObject1.put("mem_name", gpMemName);
                    jsonObject1.put("mem_first_name", gpMemFName);
                    jsonObject1.put("mem_middle_name", gpMemMName);
                    jsonObject1.put("mem_last_name", gpMemLName);
                    jsonObject1.put("mem_mobile", gpMemMobile);
                    jsonObject1.put("mem_mobile2", gpMemMobile2);
                    jsonObject1.put("mem_designation_id", gpMemDesigId);
                    jsonObject1.put("mem_designation_name", gpMemDesigName);
                    jsonObject1.put("mem_gender_id", gpMemGenderId);
                    jsonObject1.put("mem_gender_name", gpMemGenderName);
                    jsonObject1.put("mem_soc_cat_id", gpMemSocCatId);
                    jsonObject1.put("mem_soc_cat_name", gpMemSocCatName);
                    jsonObject1.put("mem_is_selected", gpMemIsSelected);

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


    public JSONArray getGpMemberListBydesignationId(String mem_designation_id, String taluka_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE mem_designation_id = '" + mem_designation_id + "' AND taluka_id = '" + taluka_id + "' ORDER BY u_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String id = cursor.getString(cursor.getColumnIndex("u_id"));
                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));
                String gpName = cursor.getString(cursor.getColumnIndex("gp_name"));
                String gpIsSelected = cursor.getString(cursor.getColumnIndex("gp_is_selected"));
                String gpMemId = cursor.getString(cursor.getColumnIndex("mem_id"));
                String gpMemName = cursor.getString(cursor.getColumnIndex("mem_name"));
                String gpMemFName = cursor.getString(cursor.getColumnIndex("mem_first_name"));
                String gpMemMName = cursor.getString(cursor.getColumnIndex("mem_middle_name"));
                String gpMemLName = cursor.getString(cursor.getColumnIndex("mem_last_name"));
                String gpMemMobile = cursor.getString(cursor.getColumnIndex("mem_mobile"));
                String gpMemMobile2 = cursor.getString(cursor.getColumnIndex("mem_mobile2"));
                String gpMemDesigId = cursor.getString(cursor.getColumnIndex("mem_designation_id"));
                String gpMemDesigName = cursor.getString(cursor.getColumnIndex("mem_designation_name"));
                String gpMemGenderId = cursor.getString(cursor.getColumnIndex("mem_gender_id"));
                String gpMemGenderName = cursor.getString(cursor.getColumnIndex("mem_gender_name"));
                String gpMemSocCatId = cursor.getString(cursor.getColumnIndex("mem_soc_cat_id"));
                String gpMemSocCatName = cursor.getString(cursor.getColumnIndex("mem_soc_cat_name"));
                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("mem_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", id);
                    jsonObject1.put("gp_id", gpId);
                    jsonObject1.put("gp_name", gpName);
                    jsonObject1.put("gp_is_selected", gpIsSelected);
                    jsonObject1.put("mem_id", gpMemId);
                    jsonObject1.put("mem_name", gpMemName);
                    jsonObject1.put("mem_first_name", gpMemFName);
                    jsonObject1.put("mem_middle_name", gpMemMName);
                    jsonObject1.put("mem_last_name", gpMemLName);
                    jsonObject1.put("mem_mobile", gpMemMobile);
                    jsonObject1.put("mem_mobile2", gpMemMobile2);
                    jsonObject1.put("mem_designation_id", gpMemDesigId);
                    jsonObject1.put("mem_designation_name", gpMemDesigName);
                    jsonObject1.put("mem_gender_id", gpMemGenderId);
                    jsonObject1.put("mem_gender_name", gpMemGenderName);
                    jsonObject1.put("mem_soc_cat_id", gpMemSocCatId);
                    jsonObject1.put("mem_soc_cat_name", gpMemSocCatName);
                    jsonObject1.put("mem_is_selected", gpMemIsSelected);

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


    public JSONArray getSelectedGpList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE mem_is_selected = 1 GROUP by gp_id ORDER BY gp_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String talukaId = cursor.getString(cursor.getColumnIndex("taluka_id"));
                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));
                String gpName = cursor.getString(cursor.getColumnIndex("gp_name"));
                String gpIsSelected = cursor.getString(cursor.getColumnIndex("gp_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("taluka_id", talukaId);
                    jsonObject1.put("gp_id", gpId);
                    jsonObject1.put("gp_name", gpName);
                    jsonObject1.put("gp_is_selected", gpIsSelected);

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


    public JSONArray getSledGpMemIdListByGpId(String gp_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE gp_id = " + gp_id + "  AND mem_is_selected = 1 ORDER BY mem_id ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String mem_id = cursor.getString(cursor.getColumnIndex("mem_id"));
                String memIsSelected = cursor.getString(cursor.getColumnIndex("mem_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("mem_id", mem_id);
                    jsonObject1.put("mem_is_selected", memIsSelected);

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


    // To Check Taluka already present
    public Boolean isTalukaPresentById(String talukaID) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE taluka_id = " + talukaID;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String taluka_id = cursor.getString(cursor.getColumnIndex("taluka_id"));

                if (!taluka_id.equalsIgnoreCase("")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("GP Member JsonArray" + jsonArray.toString());
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // To Check GP already present
    public Boolean isGPExist(String gp_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE gp_id = " + gp_id;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String gpId = cursor.getString(cursor.getColumnIndex("gp_id"));

                if (!gpId.equalsIgnoreCase("")) {
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


    // To Check GP already exist
    public Boolean isGpMemExist(String gp_mem_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE mem_id = " + gp_mem_id;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);
        try {

            if (cursor.moveToFirst()) {
                do {

                    String gpId = cursor.getString(cursor.getColumnIndex("mem_id"));

                    if (!gpId.equalsIgnoreCase("")) {
                        result = 1;
                    } else {
                        result = -1;
                    }

                } while (cursor.moveToNext());

                db.close();
            }
        } finally {
            cursor.close();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Boolean isGPMemSelectedByGpId(String gp_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE gp_id = " + gp_id + " AND mem_is_selected = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("mem_is_selected"));

                if (gpMemIsSelected.equalsIgnoreCase("1")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("GP Member JsonArray" + jsonArray.toString());
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean isGPMemSelectedByDesignationId(String designation_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM gp_mem_table WHERE mem_designation_id = " + designation_id + " AND mem_is_selected = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String gpMemIsSelected = cursor.getString(cursor.getColumnIndex("mem_is_selected"));

                if (gpMemIsSelected.equalsIgnoreCase("1")) {
                    result = 1;
                } else {
                    result = -1;
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("GP Member JsonArray" + jsonArray.toString());
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    /* UPDATE FUNCTION*/


    // UPDATE Selection of GP MEM Detail
    public Boolean updateGpIsSelected(String gpId, String gp_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(GP_IS_SELECTED, gp_is_selected);
            result = db.update(GP_MEM_TABLE, contentValues, " gp_id = " + gpId, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public Boolean updatedesignationIsSelected(String mem_designation_id, String gp_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(GP_IS_SELECTED, gp_is_selected);
            result = db.update(GP_MEM_TABLE, contentValues, " mem_designation_id = " + mem_designation_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    // UPDATE Selection of GP MEM Detail
    public Boolean updateGpMemIsSelected(String mem_id, String mem_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(GP_MEM_IS_SELECTED, mem_is_selected);

            result = db.update(GP_MEM_TABLE, contentValues, " mem_id = " + mem_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }



    /* INSERT FUNCTION*/


    /* Village and farmer */
    // INSERT Vil_FARMER_TABLE TABLE DETAIL
    public Boolean insertFarmerDetail(String village_id, String village_name, String village_code, String village_is_selected,
                                      String vil_act_id, String vil_act_name, String act_is_selected,
                                      String farmer_id, String farmer_name, String farmer_mobile, String farmer_gender_id,
                                      String farmer_gender_name, String farmer_designation_id,
                                      String farmer_designation_name, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(VILLAGE_ID, village_id);
            contentValues.put(VILLAGE_NAME, village_name);
            contentValues.put(VILLAGE_CODE, village_code);
            contentValues.put(VIL_IS_SELECTED, village_is_selected);
            contentValues.put(VIL_ACT_ID, vil_act_id);
            contentValues.put(VIL_ACT_NAME, vil_act_name);
            contentValues.put(VIL_ACT_IS_SELECTED, act_is_selected);
            contentValues.put(VIL_FAR_ID, farmer_id);
            contentValues.put(VIL_FAR_NAME, farmer_name);
            contentValues.put(VIL_FAR_MOBILE, farmer_mobile);
            contentValues.put(VIL_FAR_GEN_ID, farmer_gender_id);
            contentValues.put(VIL_FAR_GEN_NAME, farmer_gender_name);
            contentValues.put(VIL_FAR_MOBILE, farmer_mobile);
            contentValues.put(VIL_FAR_DESIG_ID, farmer_designation_id);
            contentValues.put(VIL_FAR_DESIG_NAME, farmer_designation_name);
            contentValues.put(VIL_FAR_IS_SELECTED, farmer_is_selected);

            result = db.insert(VIL_FARMER_TABLE, null, contentValues);
            db.close();
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
    public Boolean updateFarmerDetail(String village_id, String village_name, String village_code, String village_is_selected,
                                      String vil_act_id, String vil_act_name, String act_is_selected,
                                      String farmer_id, String farmer_name, String farmer_mobile, String farmer_gender_id,
                                      String farmer_gender_name, String farmer_designation_id,
                                      String farmer_designation_name, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(VILLAGE_ID, village_id);
            contentValues.put(VILLAGE_NAME, village_name);
            contentValues.put(VILLAGE_CODE, village_code);
            contentValues.put(VIL_IS_SELECTED, village_is_selected);
            // contentValues.put(VIL_ACT_ID, vil_act_id);
            // contentValues.put(VIL_ACT_NAME, vil_act_name);
            // contentValues.put(VIL_ACT_IS_SELECTED, act_is_selected);
            contentValues.put(VIL_FAR_ID, farmer_id);
            contentValues.put(VIL_FAR_NAME, farmer_name);
            contentValues.put(VIL_FAR_MOBILE, farmer_mobile);
            contentValues.put(VIL_FAR_GEN_ID, farmer_gender_id);
            contentValues.put(VIL_FAR_GEN_NAME, farmer_gender_name);
            contentValues.put(VIL_FAR_DESIG_ID, farmer_designation_id);
            contentValues.put(VIL_FAR_DESIG_NAME, farmer_designation_name);
            contentValues.put(VIL_FAR_IS_SELECTED, farmer_is_selected);

            result = db.update(VIL_FARMER_TABLE, contentValues, " farmer_id = " + farmer_id, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    public JSONArray getSledVillageList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE vil_is_selected = " + 1 + " GROUP by village_id ORDER BY village_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String villageId = cursor.getString(cursor.getColumnIndex("village_id"));
                String villageName = cursor.getString(cursor.getColumnIndex("village_name"));
                String villageIsSelected = cursor.getString(cursor.getColumnIndex("vil_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village_id", villageId);
                    jsonObject1.put("village_name", villageName);
                    jsonObject1.put("vil_is_selected", villageIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Village JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getSledFarmerListByVillage(String village_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE village_id = " + village_id + "  AND far_is_selected = 1 ORDER BY far_is_selected ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String farmer_id = cursor.getString(cursor.getColumnIndex("farmer_id"));
                String farmerIsSelected = cursor.getString(cursor.getColumnIndex("far_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("farmer_id", farmer_id);
                    jsonObject1.put("far_is_selected", farmerIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Farmer JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getSledActivityList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE vil_act_is_selected = 1 GROUP by vil_act_id ORDER BY vil_act_id ASC ";


        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String vilActId = cursor.getString(cursor.getColumnIndex("vil_act_id"));
                String vilActName = cursor.getString(cursor.getColumnIndex("vil_act_name"));
                String vilCode = cursor.getString(cursor.getColumnIndex("village_code"));
                String vilActIsSelected = cursor.getString(cursor.getColumnIndex("vil_act_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("vil_act_id", vilActId);
                    jsonObject1.put("vil_act_name", vilActName);
                    jsonObject1.put("vil_code", vilCode);
                    jsonObject1.put("vil_act_is_selected", vilActIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("activity JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getSledFarmerListByActivity(String activity_id) {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE vil_act_id = " + activity_id + "  AND far_is_selected = 1 ORDER BY farmer_id ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String villageId = cursor.getString(cursor.getColumnIndex("village_id"));
                String villageCode = cursor.getString(cursor.getColumnIndex("village_code"));
                String villageName = cursor.getString(cursor.getColumnIndex("village_name"));
                String actId = cursor.getString(cursor.getColumnIndex("vil_act_id"));
                String actName = cursor.getString(cursor.getColumnIndex("vil_act_name"));
                String farmerId = cursor.getString(cursor.getColumnIndex("farmer_id"));
                String farmerName = cursor.getString(cursor.getColumnIndex("farmer_name"));
                String fMobile = cursor.getString(cursor.getColumnIndex("farmer_mobile"));
                String fGender = cursor.getString(cursor.getColumnIndex("farmer_gen_name"));
                String fDesignation = cursor.getString(cursor.getColumnIndex("farmer_desig_name"));
                String farmerIsSelected = cursor.getString(cursor.getColumnIndex("far_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village_id", villageId);
                    jsonObject1.put("village_code", villageCode);
                    jsonObject1.put("village_name", villageName);
                    jsonObject1.put("vil_act_id", actId);
                    jsonObject1.put("vil_act_name", actName);
                    jsonObject1.put("id", farmerId);
                    jsonObject1.put("name", farmerName);
                    jsonObject1.put("mobile", fMobile);
                    jsonObject1.put("gender", fGender);
                    jsonObject1.put("designation", fDesignation);
                    jsonObject1.put("is_selected", farmerIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Farmer JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getSledFarmerList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE far_is_selected = 1 ORDER BY farmer_id ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String villageId = cursor.getString(cursor.getColumnIndex("village_id"));
                String villageCode = cursor.getString(cursor.getColumnIndex("village_code"));
                String villageName = cursor.getString(cursor.getColumnIndex("village_name"));
                String actId = cursor.getString(cursor.getColumnIndex("vil_act_id"));
                String actName = cursor.getString(cursor.getColumnIndex("vil_act_name"));
                String farmerId = cursor.getString(cursor.getColumnIndex("farmer_id"));
                String farmerName = cursor.getString(cursor.getColumnIndex("farmer_name"));
                String fMobile = cursor.getString(cursor.getColumnIndex("farmer_mobile"));
                String fGender = cursor.getString(cursor.getColumnIndex("farmer_gen_name"));
                String fDesignation = cursor.getString(cursor.getColumnIndex("farmer_desig_name"));
                String farmerIsSelected = cursor.getString(cursor.getColumnIndex("far_is_selected"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village_id", villageId);
                    jsonObject1.put("village_code", villageCode);
                    jsonObject1.put("village_name", villageName);
                    jsonObject1.put("vil_act_id", actId);
                    jsonObject1.put("vil_act_name", actName);
                    jsonObject1.put("id", farmerId);
                    jsonObject1.put("name", farmerName);
                    jsonObject1.put("mobile", fMobile);
                    jsonObject1.put("gender", fGender);
                    jsonObject1.put("designation", fDesignation);
                    jsonObject1.put("is_selected", farmerIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Farmer JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }

    public JSONArray getShglist() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT DISTINCT village_code, name, mobile, proname, shg_is_selected, farmer_id  FROM shg_table WHERE shg_is_selected = 1 ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                String villageCode = cursor.getString(cursor.getColumnIndex("village_code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String Mobile = cursor.getString(cursor.getColumnIndex("mobile"));
                String proname = cursor.getString(cursor.getColumnIndex("proname"));
                String shgIsSelected = cursor.getString(cursor.getColumnIndex("shg_is_selected"));
                String id = cursor.getString(cursor.getColumnIndex("farmer_id"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village_census_code", villageCode);
                    jsonObject1.put("name", name);
                    jsonObject1.put("mobile", Mobile);
                    jsonObject1.put("chief_promoter_president", proname);
                    jsonObject1.put("is_selected", shgIsSelected);
                    jsonObject1.put("id", Integer.parseInt(id));
                    jsonObject1.put("date_of_letter_deliver", "");
                    jsonObject1.put("added_by", "");
                    jsonObject1.put("group_flag", "SHG");

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("ShgJsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getFpcList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT DISTINCT taluka_id , name, contact_no, contact_person, is_selected, farmer_id  FROM fpc_table WHERE is_selected = 1 ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                String taluka_id = cursor.getString(cursor.getColumnIndex("taluka_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String contact_no = cursor.getString(cursor.getColumnIndex("contact_no"));
                String contact_person = cursor.getString(cursor.getColumnIndex("contact_person"));
                String fpc_IsSelected = cursor.getString(cursor.getColumnIndex("is_selected"));
                String farmer_id = cursor.getString(cursor.getColumnIndex("farmer_id"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("taluka_id", Integer.parseInt(taluka_id));
                    jsonObject1.put("name", name);
                    jsonObject1.put("contact_no", contact_no);
                    jsonObject1.put("contact_person", contact_person);
                    jsonObject1.put("is_selected", fpc_IsSelected);
                    jsonObject1.put("id", farmer_id);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("FpcJsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getFarmerGrouplist() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT DISTINCT village, village_census_code, group_name, contact_person, contact_number, is_selected, farmer_id  FROM farmer_group_table WHERE is_selected = 1 ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {


                String village_name = cursor.getString(cursor.getColumnIndex("village"));
                String village_census_code = cursor.getString(cursor.getColumnIndex("village_census_code"));
                String group_name = cursor.getString(cursor.getColumnIndex("group_name"));
                String contact_person = cursor.getString(cursor.getColumnIndex("contact_person"));
                String contact_number = cursor.getString(cursor.getColumnIndex("contact_number"));
                String FG_is_selected = cursor.getString(cursor.getColumnIndex("is_selected"));
                String farmer_id = cursor.getString(cursor.getColumnIndex("farmer_id"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village", village_name);
                    jsonObject1.put("village_census_code", village_census_code);
                    jsonObject1.put("group_name", group_name);
                    jsonObject1.put("contact_person", contact_person);
                    jsonObject1.put("contact_number", contact_number);
                    jsonObject1.put("is_selected", FG_is_selected);
                    jsonObject1.put("id", farmer_id);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("FGJsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }



    // UPDATE Selection of GP MEM Detail
    public Boolean updateSledVillage(String villageId, String village_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VIL_IS_SELECTED, village_is_selected);
            result = db.update(VIL_FARMER_TABLE, contentValues, " village_id = " + villageId, null);
            db.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    // UPDATE Selection of Village Activity Detail
    public Boolean updateSledVillageActivity(String villageActId, String act_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VIL_ACT_IS_SELECTED, act_is_selected);
            result = db.update(VIL_FARMER_TABLE, contentValues, " vil_act_id = " + villageActId, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    // UPDATE Selection of GP MEM Detail
    public Boolean updateFarmerIsSelected(String farmer_id, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(VIL_FAR_IS_SELECTED, farmer_is_selected);
            contentValues.put(VIL_ACT_IS_SELECTED, farmer_is_selected);

            result = db.update(VIL_FARMER_TABLE, contentValues, " farmer_id = " + farmer_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    // UPDATE Selection of SHG Group Detail
    public Boolean updateShgIsSelected(String farmer_id, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Sshg_is_selected, farmer_is_selected);

            result = db.update(SHG_TABLE, contentValues, " farmer_id = " + farmer_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // UPDATE Selection of FPC Group Detail
    public Boolean updateFpcIsSelected(String farmer_id, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(FPC_IS_SELECTED, farmer_is_selected);

            result = db.update(FPC_TABLE, contentValues, " farmer_id = " + farmer_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    // UPDATE Selection of Farmer Group Detail
    public Boolean updateFGroupIsSelected(String farmer_id, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(FG_IS_SELECTED , farmer_is_selected);

            result = db.update(FG_TABLE, contentValues, " farmer_id = " + farmer_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }





    // UPDATE Selection of GP MEM Detail
    public Boolean updateFarmerIsSelectedWithActId(String farmer_id, String actId, String farmer_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(VIL_FAR_IS_SELECTED, farmer_is_selected);
            contentValues.put(VIL_ACT_IS_SELECTED, farmer_is_selected);

            result = db.update(VIL_FARMER_TABLE, contentValues, " farmer_id = " + farmer_id + " AND vil_act_id = " + actId, null);

            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    // To Check Village Selected
    public Boolean isVillageSled(String Village_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE village_id = " + Village_id + " AND vil_is_selected = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String villId = cursor.getString(cursor.getColumnIndex("village_id"));

                if (!villId.equalsIgnoreCase("")) {
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


    // To Check Farmer is Selected
    public Boolean isFarmerSled(String farmerId) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = " + farmerId + " AND far_is_selected = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String farId = cursor.getString(cursor.getColumnIndex("farmer_id"));

                if (!farId.equalsIgnoreCase("")) {
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


    // To Check Farmer is Selected
    public int sledFarmerCount(String farmerId) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = " + farmerId + " AND far_is_selected = 1";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String farId = cursor.getString(cursor.getColumnIndex("farmer_id"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("farmer_id", farId);
                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }
        return jsonArray.length();

    }


    // To Check Farmer and activity is Selected
    public Boolean isFarmerAndActSled(String farmerId, String actId) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = " + farmerId + " AND far_is_selected = 1";
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String farId = cursor.getString(cursor.getColumnIndex("farmer_id"));
                String sledActId = cursor.getString(cursor.getColumnIndex("vil_act_id"));

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("farmer_id", farId);
                    jsonObject1.put("vil_act_id", sledActId);
                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    int count = 0;
                    String fId = "";
                    String aId = "";
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObJ = jsonArray.getJSONObject(i);
                            fId = jObJ.getString("farmer_id");
                            aId = jObJ.getString("vil_act_id");
                            if (fId.equalsIgnoreCase(farmerId) && aId.equalsIgnoreCase(actId)) {
                                result = 1;
                            } else if (fId.equalsIgnoreCase(farmerId) && !aId.equalsIgnoreCase(actId) && count < 2) {
                                count++;
                                result = -1;
                            } else if (fId.equalsIgnoreCase(farmerId) && !aId.equalsIgnoreCase(actId) && count >= 2) {
                                result = 1;
                            } else {
                                result = -1;
                            }
                        }

                    } else {
                        result = -1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

    //SHG check data
    public Boolean isshgseld(String SHGfarmer_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM shg_table WHERE farmer_id = " + SHGfarmer_id + " AND shg_is_selected = 1";
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String vid = cursor.getString(cursor.getColumnIndex("village_code"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("village_code", vid);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    int count = 0;
                    String fId = "";
                    String aId = "";
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObJ = jsonArray.getJSONObject(i);
                            fId = jObJ.getString("village_code");

                            if (fId.equalsIgnoreCase(vid)) {
                                result = 1;

                            } else {
                                result = -1;
                            }
                        }

                    } else {
                        result = -1;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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


    // FPC check data
    public Boolean isFpcseld(String FPCfarmer_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM fpc_table WHERE farmer_id = " + FPCfarmer_id + " AND is_selected = 1";
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String taluka_id = cursor.getString(cursor.getColumnIndex("taluka_id"));
                result = 1;

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



    // Farmer Group check data
    public Boolean isFGseld(String FGfarmer_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
       try {
           String query = " SELECT * FROM farmer_group_table WHERE farmer_id = " + FGfarmer_id + " AND is_selected = 1";
           Cursor cursor = null;
           cursor = db.rawQuery(query, null);

           if (cursor.moveToFirst()) {
               do {
                   //String village_census_code = cursor.getString(cursor.getColumnIndex("village_census_code"));
                   result = 1;

               } while (cursor.moveToNext());
               cursor.close();
               db.close();
           }
       }catch (Exception e)
       {
           Log.e("FG",e.toString());
       }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }



    // To Check Village already present
    public Boolean isVillageExist(String village_id) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE village_id = " + village_id;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String vId = cursor.getString(cursor.getColumnIndex("village_id"));

                if (!vId.equalsIgnoreCase("")) {
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


    // To Check Activity already present
    public Boolean isActivityAndVillageExist(String activityId, String villageCode) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE vil_act_id = " + activityId + " AND village_code = " + villageCode;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String vAId = cursor.getString(cursor.getColumnIndex("vil_act_id"));

                if (!vAId.equalsIgnoreCase("")) {
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


    // To Check Farmer already present
    public Boolean isFarmerExist(String farmerId) {

        long result = -1;
        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = " + farmerId;

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String fId = cursor.getString(cursor.getColumnIndex("farmer_id"));

                if (!fId.equalsIgnoreCase("")) {
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

    // To Check Farmer already present
    public Boolean isFarmerWithActExist(String farmerId, String actId) {

        long result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = '" + farmerId + "' AND vil_act_id = '" + actId + "'";
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String fId = cursor.getString(cursor.getColumnIndex("farmer_id"));

                if (!fId.equalsIgnoreCase("")) {
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

    // To Check shg already present
    public Boolean isSgWithActExist(String farmerId, String actId) {

        long result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM vil_farmer_table WHERE farmer_id = '" + farmerId + "' AND vil_act_id = '" + actId + "'";
        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String fId = cursor.getString(cursor.getColumnIndex("farmer_id"));

                if (!fId.equalsIgnoreCase("")) {
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
    // FFS FACILITATOR

    // INSERT FACILITATOR TABLE DETAIL
    public Boolean insertFfsParticipantsDetail(String facilitator_id, String facilitator_role_id, String facilitator_f_Name,
                                               String facilitator_m_name, String facilitator_l_name, String facilitator_mobile,
                                               String facilitator_gender_name, String facilitator_designation,
                                               String taluka_id, String facilitator_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(POM_ID, facilitator_id);
            contentValues.put(POM_ROLE_ID, facilitator_role_id);
            contentValues.put(POM_F_NAME, facilitator_f_Name);
            contentValues.put(POM_M_NAME, facilitator_m_name);
            contentValues.put(POM_L_NAME, facilitator_l_name);
            contentValues.put(POM_MOBILE, facilitator_mobile);
            contentValues.put(POM_GEN_NAME, facilitator_gender_name);
            contentValues.put(POM_DESIGNATION, facilitator_designation);
            contentValues.put(POM_TALUKA, taluka_id);
            contentValues.put(POM_IS_SELECTED, facilitator_is_selected);

            result = db.insert(PO_MEM_TABLE, null, contentValues);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // UPDATE FACILITATOR TABLE DETAIL
    public Boolean updateFfsParticipantsTableDetail(String facilitator_id, String facilitator_role_id, String facilitator_f_Name,
                                                    String facilitator_m_name, String facilitator_l_name, String facilitator_mobile,
                                                    String facilitator_gender_name, String facilitator_designation,
                                                    String taluka_id, String facilitator_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(POM_ROLE_ID, facilitator_role_id);
            contentValues.put(POM_F_NAME, facilitator_f_Name);
            contentValues.put(POM_M_NAME, facilitator_m_name);
            contentValues.put(POM_L_NAME, facilitator_l_name);
            contentValues.put(POM_MOBILE, facilitator_mobile);
            contentValues.put(POM_GEN_NAME, facilitator_gender_name);
            contentValues.put(POM_DESIGNATION, facilitator_designation);
            contentValues.put(POM_TALUKA, taluka_id);
            contentValues.put(POM_IS_SELECTED, facilitator_is_selected);

            result = db.update(PO_MEM_TABLE, contentValues, " pom_id = " + facilitator_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    // UPDATE FACILITATOR TABLE DETAIL
    public Boolean updatePOMemSelectionDetail(String facilitator_id, String facilitator_is_selected) {

        long result = -1;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(POM_ID, facilitator_id);
            contentValues.put(POM_IS_SELECTED, facilitator_is_selected);

            result = db.update(PO_MEM_TABLE, contentValues, " pom_id = " + facilitator_id, null);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public JSONArray getFfsFacilitatorList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM pom_member_table ORDER BY u_id ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String ffsId = cursor.getString(cursor.getColumnIndex("u_id"));
                String facilitatorId = cursor.getString(cursor.getColumnIndex("pom_id"));
                String fasiRoleId = cursor.getString(cursor.getColumnIndex("pom_role_id"));
                String fasiFName = cursor.getString(cursor.getColumnIndex("pom_first_name"));
                String fasiMName = cursor.getString(cursor.getColumnIndex("pom_middle_name"));
                String fasiLName = cursor.getString(cursor.getColumnIndex("pom_last_name"));
                String fasiMobile = cursor.getString(cursor.getColumnIndex("pom_mobile"));
                String fasiGenderId = cursor.getString(cursor.getColumnIndex("pom_gender"));
                String fasiGenderName = cursor.getString(cursor.getColumnIndex("pom_get_name"));
                String fasiDesignation = cursor.getString(cursor.getColumnIndex("pom_designation"));
                String talukaId = cursor.getString(cursor.getColumnIndex("pom_taluka_id"));
                String fasiIsSelected = cursor.getString(cursor.getColumnIndex("pom_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", ffsId);
                    jsonObject1.put("id", facilitatorId);
                    jsonObject1.put("role_id", fasiRoleId);
                    jsonObject1.put("first_name", fasiFName);
                    jsonObject1.put("middle_name", fasiMName);
                    jsonObject1.put("last_name", fasiLName);
                    jsonObject1.put("mobile", fasiMobile);
                    jsonObject1.put("gender", fasiGenderId);
                    jsonObject1.put("gender_name", fasiGenderName);
                    jsonObject1.put("designation", fasiDesignation);
                    jsonObject1.put("talika_id", talukaId);
                    jsonObject1.put("is_selected", fasiIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("FFS Facilitator JsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    public JSONArray getSledPOMemberList() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM pom_member_table WHERE pom_is_selected = 1 ORDER BY u_id ASC ";

        Cursor cursor = null;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                String ffsId = cursor.getString(cursor.getColumnIndex("u_id"));
                String facilitatorId = cursor.getString(cursor.getColumnIndex("pom_id"));
                String fasiRoleId = cursor.getString(cursor.getColumnIndex("pom_role_id"));
                String fasiFName = cursor.getString(cursor.getColumnIndex("pom_first_name"));
                String fasiMName = cursor.getString(cursor.getColumnIndex("pom_middle_name"));
                String fasiLName = cursor.getString(cursor.getColumnIndex("pom_last_name"));
                String fasiMobile = cursor.getString(cursor.getColumnIndex("pom_mobile"));
                String fasiGenderId = cursor.getString(cursor.getColumnIndex("pom_gender"));
                String fasiGenderName = cursor.getString(cursor.getColumnIndex("pom_get_name"));
                String fasiDesignation = cursor.getString(cursor.getColumnIndex("pom_designation"));
                String fasiIsSelected = cursor.getString(cursor.getColumnIndex("pom_is_selected"));


                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("u_id", ffsId);
                    jsonObject1.put("id", facilitatorId);
                    jsonObject1.put("role_id", fasiRoleId);
                    jsonObject1.put("first_name", fasiFName);
                    jsonObject1.put("middle_name", fasiMName);
                    jsonObject1.put("last_name", fasiLName);
                    jsonObject1.put("mobile", fasiMobile);
                    jsonObject1.put("gender", fasiGenderId);
                    jsonObject1.put("gender_name", fasiGenderName);
                    jsonObject1.put("designation", fasiDesignation);
                    jsonObject1.put("is_selected", fasiIsSelected);

                    jsonArray.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();
            DebugLog.getInstance().d("Pocra_official_member_jsonArray" + jsonArray.toString());
        }
        return jsonArray;
    }


    // To Check GP already exist
    public Boolean isFacilitatorExist(String facilitator_id) {

        long result = -1;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM pom_member_table WHERE pom_id = " + facilitator_id;

        try {

            Cursor cursor = null;
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    String facilitatorId = cursor.getString(cursor.getColumnIndex("pom_id"));

                    if (!facilitatorId.equalsIgnoreCase("")) {
                        result = 1;
                    } else {
                        result = -1;
                    }

                } while (cursor.moveToNext());

                cursor.close();
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}

