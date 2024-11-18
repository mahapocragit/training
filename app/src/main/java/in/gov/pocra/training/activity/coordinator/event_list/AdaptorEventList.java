package in.gov.pocra.training.activity.coordinator.event_list;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ps_hrd.ps_upcoming_event.PsUpcomingEventDetailActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorEventList extends RecyclerView.Adapter<AdaptorEventList.ViewHolder> {


    private CordOfflineDBase cDBase;
    private String onlineStatus;
    private Context mContext;
    private JSONArray mJsonArray;
    private OnMultiRecyclerItemClickListener mListener;


    public AdaptorEventList(Context context, JSONArray jsonArray,  OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mListener = listener;
        onlineStatus = AppSettings.getInstance().getValue(mContext, ApConstants.kONLINE_STATUS,ApConstants.kONLINE_STATUS);
        cDBase = new CordOfflineDBase(mContext);
    }


    @Override
    public int getItemCount() {
        if (mJsonArray.length() != 0) {
            return mJsonArray.length();
        } else {
            return 0;
        }
    }


    @NonNull
    @Override
    public AdaptorEventList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorEventList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
            viewHolder.eventSyncBtn.setTag(i);
            viewHolder.itemView.setTag(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // For get and Sync Offline event detail
        viewHolder.eventSyncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = (Integer)v.getTag();
                try {

                    JSONObject schJSON = mJsonArray.getJSONObject(index);
                    String schId = schJSON.getString("schedule_id");
                    long memCount = cDBase.getNoOfRecordBySchId(CordOfflineDBase.EVENT_DETAIL_TABLE,schId);
                    // long memCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_DETAIL_TABLE);
                    long memAttendCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
                    long memOtherCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);


                    String btnAction;
                    if (memCount > 0) {
                        if (memAttendCount > 0 || memOtherCount > 0) {
                            if (cDBase.isOthMemDetailSyncedByEId(schId) && cDBase.isImgSyncedByEId(schId) && cDBase.isAttendanceSyncedByEId(schId)){
                                btnAction = "delete";
                            }else {
                                btnAction = "upload";
                            }
                        } else {
                            btnAction = "delete";
                        }
                    } else {
                        btnAction = "download";
                    }

                    if (btnAction.equalsIgnoreCase("upload")){
                        // mListener.onMultiRecyclerViewItemClick(2, schJSON);              // For upload

                        Intent intent = new Intent(mContext, EventDaysActivity.class);
                        intent.putExtra("data",schJSON.toString());
                        mContext.startActivity(intent);

                    }else if (btnAction.equalsIgnoreCase("delete")){
                        mListener.onMultiRecyclerViewItemClick(3, schJSON);              // For Delete
                    }else {
                        mListener.onMultiRecyclerViewItemClick(1, schJSON);              // For Download
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // For taking attendance of event
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = (Integer)v.getTag();
                String is_event_closer;

                try {
                    JSONObject jsonObject = mJsonArray.getJSONObject(index);
                    String schId = jsonObject.getString("schedule_id");
                    long memCount = cDBase.getNoOfRecordBySchId(CordOfflineDBase.EVENT_DETAIL_TABLE,schId);
                    long memAttendCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
                    long memOtherCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);


                    is_event_closer = jsonObject.getString("is_event_closer");
                    if (is_event_closer.equalsIgnoreCase("1")){
                        // viewHolder.scheduleLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                       // UIToastMessage.show(mContext,"Sorry! This event is closed.");
                        Toast.makeText(mContext, "Sorry! This event is closed.", Toast.LENGTH_SHORT).show();
                    }else {

                        Intent intent = new Intent(mContext, EventDaysActivity.class);
                        intent.putExtra("data",jsonObject.toString());
                        mContext.startActivity(intent);

                        /*if (Utility.checkConnection(mContext)){

                            if ( memCount >0 && (memAttendCount>0 || memOtherCount>0)){
                                UIToastMessage.show(mContext,"Please Sync offline detail of event");
                            }else {
                                Intent intent = new Intent(mContext, EventDaysActivity.class);
                                intent.putExtra("data",jsonObject.toString());
                                mContext.startActivity(intent);
                            }
                        }else {
                            Intent intent = new Intent(mContext, EventDaysActivity.class);
                            intent.putExtra("data",jsonObject.toString());
                            mContext.startActivity(intent);
                        }*/

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout scheduleLLayout;
        private final TextView eventSDateTView;
        private final TextView eventEDateTView;
        private final TextView eventVenueTView;
        private final TextView eventTypeTView;
        private final TextView participantTView;
        private final TextView eventTitleTView;
        private final ImageView eventDetailIView;

        private final Button eventSyncBtn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scheduleLLayout = (LinearLayout)itemView.findViewById(R.id.scheduleLLayout);
            eventSDateTView = (TextView) itemView.findViewById(R.id.eventSDateTView);
            eventEDateTView = (TextView) itemView.findViewById(R.id.eventEDateTView);
            eventVenueTView = (TextView) itemView.findViewById(R.id.eventVenueTView);
            eventTypeTView = (TextView) itemView.findViewById(R.id.eventTypeTView);
            participantTView = (TextView) itemView.findViewById(R.id.participantTView);
            eventTitleTView = (TextView) itemView.findViewById(R.id.eventTitleTView);
            eventDetailIView = (ImageView)itemView.findViewById(R.id.eventDetailIView);
            eventSyncBtn = (Button)itemView.findViewById(R.id.eventSyncBtn);

        }



        public void onBind(final JSONObject jsonObject, final int i) {

            try {

                final String schId = jsonObject.getString("schedule_id");
                long memCount = cDBase.getNoOfRecordBySchId(CordOfflineDBase.EVENT_DETAIL_TABLE,schId);
                // long memCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_DETAIL_TABLE);
                long memAttendCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
                long memOtherCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);

                String syncBtnAction;
                if (memCount > 0) {
                    if (memAttendCount > 0 || memOtherCount > 0) {
                        syncBtnAction = "upload";
                    } else {
                        syncBtnAction = "delete";
                    }
                } else {
                    syncBtnAction = "download";
                }

                String is_event_closed = jsonObject.getString("is_event_closer");

                if (is_event_closed.equalsIgnoreCase("1")){
                    // scheduleLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                    scheduleLLayout.setBackgroundResource(R.drawable.selected_list_border_bg);
                }else   // added by mayu 12 Sept 22
                {
                     scheduleLLayout.setBackgroundResource(R.drawable.edit_border_bg);
                }

                if (onlineStatus.equalsIgnoreCase(ApConstants.kOFFLINE)){

                    eventSyncBtn.setVisibility(View.VISIBLE);

                    if (syncBtnAction.equalsIgnoreCase("upload")){
                        eventSyncBtn.setText("Sync attendance");
                        eventSyncBtn.setBackground(mContext.getResources().getDrawable(R.drawable.button_bg));
                    }else if (syncBtnAction.equalsIgnoreCase("delete")){
                        eventSyncBtn.setText("Delete event detail");
                        eventSyncBtn.setBackground(mContext.getResources().getDrawable(R.drawable.button_bg_red));
                    }else {

                        if (Utility.checkConnection(mContext)) {
                            eventSyncBtn.setText("Get event details");
                            eventSyncBtn.setBackground(mContext.getResources().getDrawable(R.drawable.button_bg_blue_gr));
                        }else {
                            eventSyncBtn.setVisibility(View.GONE);
                        }
                    }

                }else if (onlineStatus.equalsIgnoreCase(ApConstants.kONLINE)){

                    if (syncBtnAction.equalsIgnoreCase("upload")){
                        eventSyncBtn.setVisibility(View.VISIBLE);
                        eventSyncBtn.setText("Sync Attendance");
                    }else if (syncBtnAction.equalsIgnoreCase("delete")){
                        eventSyncBtn.setVisibility(View.VISIBLE);
                        eventSyncBtn.setText("Delete Event Detail");
                    }else {
                        eventSyncBtn.setVisibility(View.GONE);
                    }
                }


                String sTime = jsonObject.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                String fromDat = "From: "+stDate;
                String eTime = jsonObject.getString("end_date");
                String enDate = ApUtil.getDateByTimeStamp(eTime);
                String toDate = "To: "+ enDate;

                String other_venue = "";

                if (jsonObject.isNull("other_venue")) {
                    other_venue = "";
                } else {
                    other_venue = jsonObject.getString("other_venue");
                }

                String eVenue = jsonObject.getString("venue");
                String venueDetails= "";


                // String eVenue = jsonObject.getString("venue");
                String eType = jsonObject.getString("type");
                String participants = jsonObject.getString("participints");

                // String rId = AppSettings.getInstance().getValue(mContext, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
                String eTitle = "";

                String subType = jsonObject.getString("event_sub_type_name");
                if (!subType.equalsIgnoreCase("Others")){
                    eTitle = subType;
                }else {
                    eTitle = jsonObject.getString("title");
                }

                /*if (rId.equalsIgnoreCase("15")){
                    String subType = jsonObject.getString("sub_type");
                    if (!subType.equalsIgnoreCase("Others")){
                        eTitle = subType;
                    }else {
                        eTitle = jsonObject.getString("title");
                    }
                }else {
                    eTitle = jsonObject.getString("title");
                }*/


                if (other_venue != null && !other_venue.isEmpty()) {
                    venueDetails = other_venue;
                } else {
                    venueDetails = eVenue;
                }
                int serialno = i+1;
                eventSDateTView.setText(""+serialno+". "+fromDat);

                eventSDateTView.setText(fromDat);
                eventEDateTView.setText(toDate);
                eventVenueTView.setText(venueDetails);
                eventTypeTView.setText(eType);
                participantTView.setText(participants);
                eventTitleTView.setText(eTitle);

                eventDetailIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PsUpcomingEventDetailActivity.class);
                        intent.putExtra("type", "update");
                        intent.putExtra("sch_id", schId);
                        mContext.startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
