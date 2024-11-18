package in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_mem_attendance;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.util.ApConstants;

public class AdaptorGruMemAttendance extends RecyclerView.Adapter<AdaptorGruMemAttendance.ViewHolder> {


    private String onlineStatus;
    private CordOfflineDBase cDB;
    private Context mContext;
    public JSONArray mJSONArray;
    public JSONArray mPJSONArray;
    private String mAttendType;
    private String mVCRMC_GP_Id;
    private String mSchId;
    private OnMultiRecyclerItemClickListener mListener;

    @Override
    public int getItemCount() {
        if (mJSONArray.length() != 0) {
            return mJSONArray.length();
        } else {
            return 0;
        }
    }


    public AdaptorGruMemAttendance(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener, String attendType, String vcrmcId, String schId) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mPJSONArray = jsonArray;
        this.mListener = listener;
        this.mAttendType = attendType;
        this.mVCRMC_GP_Id = vcrmcId;
        this.mSchId = schId;

        cDB = new CordOfflineDBase(mContext);
        onlineStatus = AppSettings.getInstance().getValue(mContext, ApConstants.kONLINE_STATUS, ApConstants.kONLINE_STATUS);
    }

    @NonNull
    @Override
    public AdaptorGruMemAttendance.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_day_group_mem_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorGruMemAttendance.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView mobileTextView;
        private final TextView designationTextView;
        //private final TextView memLocTextView;
        private final ImageView presentImageView;
        private final LinearLayout attendRLLayout;
        private final TextView genderTView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mobileTextView = (TextView) itemView.findViewById(R.id.mobileTextView);
            genderTView = (TextView)itemView.findViewById(R.id.genderTView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            //memLocTextView = (TextView) itemView.findViewById(R.id.memLocTextView);
            presentImageView = (ImageView) itemView.findViewById(R.id.presentImageView);
            attendRLLayout = (LinearLayout) itemView.findViewById(R.id.attendRLLayout);

        }

        public void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {

                final String memId = jsonObject.getString("id");
                String mamName = jsonObject.getString("name");
                String mobile = jsonObject.getString("mobile");
                String gender = jsonObject.getString("gender");
                String designation = jsonObject.getString("designation");
                // String memLocation = jsonObject.getString("location_name");  TODO
                // String memLocation = jsonObject.getString("designation");

                nameTextView.setText(mamName);
                mobileTextView.setText(mobile);

                if (gender.equalsIgnoreCase("1")){
                    gender = "Male";
                }else if (gender.equalsIgnoreCase("2")){
                    gender = "Female";
                }else if (gender.equalsIgnoreCase("3")){
                    gender = "Transgender";
                }

                genderTView.setText(gender);
               //  designationTextView.setText(designation + " ("+memLocation+")");
                designationTextView.setText(designation);
               // memLocTextView.setText(memLocation);

                if (jsonObject.getInt("is_selected") == 1) {
                    presentImageView.setVisibility(View.VISIBLE);
                    //attendRLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected_item_color));
                    attendRLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg_green));
                } else {
                    presentImageView.setVisibility(View.INVISIBLE);
                    attendRLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_white));
                }


                // For Make attendance -- attendRLLayout
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (memId.equalsIgnoreCase("")) {
                            UIToastMessage.show(mContext, "Member detail not found");
                        } else {
                            updateSelection(position);
                        }
                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void updateSelection(int position) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);

                if (jsonObject.getInt("is_selected") == 0) {
                    jsonObject.put("is_selected", 1);

                    // For Offline
                    if (onlineStatus.equalsIgnoreCase(ApConstants.kOFFLINE)){
                        String memId = jsonObject.getString("id");
                        if (cDB.isOtherMemExistbyId(memId)){
                            cDB.updateOtherMemAttendanceById(memId,"1");
                        }else {
                            cDB.updateEventMemAttendanceById(memId,"1");
                        }
                    }


                } else {
                    jsonObject.put("is_selected", 0);

                    // For Offline
                    if (onlineStatus.equalsIgnoreCase(ApConstants.kOFFLINE)){
                        String memId = jsonObject.getString("id");
                        if (cDB.isOtherMemExistbyId(memId)){
                            cDB.updateOtherMemAttendanceById(memId,"0");
                        }else {
                            cDB.updateEventMemAttendanceById(memId,"0");
                        }
                    }
                }

                mJSONArray.put(position, jsonObject);
                notifyItemChanged(position);
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    void filterByMob(String text){

        if (text.isEmpty()){

            mJSONArray = mPJSONArray;
            notifyDataSetChanged();

        }else {
            mJSONArray = mPJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i< mJSONArray.length(); i++){

                try {
                    JSONObject jsonObject = mJSONArray.getJSONObject(i);
                    String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();

                    if (mName.toLowerCase().contains(text.toLowerCase())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }
                    /*if (data.contains(text.toLowerCase().trim())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mJSONArray = temp;
            notifyDataSetChanged();
        }
    }

    void filter(String text){

        if (text.isEmpty()){

            mJSONArray = mPJSONArray;
            notifyDataSetChanged();

        }else {
            mJSONArray = mPJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i< mJSONArray.length(); i++){

                try {
                    JSONObject jsonObject = mJSONArray.getJSONObject(i);
                    String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "name").trim();
                    String mMob = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();

                    if (mName.toLowerCase().contains(text.toLowerCase())|| mMob.toLowerCase().contains(text.toLowerCase())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }
                    /*if (data.contains(text.toLowerCase().trim())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mJSONArray = temp;
            notifyDataSetChanged();
        }
    }
}
