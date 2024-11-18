package in.gov.pocra.training.activity.coordinator.event_list;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.util.Utility;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.coordinator.event_day_attendance.event_day_group_list.EventDGruListActivity;
import in.gov.pocra.training.activity.ps_hrd.ps_report.EventReportActivity;
import in.gov.pocra.training.event_db.CordOfflineDBase;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorEventDays extends RecyclerView.Adapter<AdaptorEventDays.ViewHolder> {

    private String onlineStatus;
    private CordOfflineDBase cDBase;
    private Context mContext;
    private JSONArray mJsonArray;
    private String mSchId;
    private OnMultiRecyclerItemClickListener mListener;
    private String title = "";

    public AdaptorEventDays(Context context, JSONArray jsonArray, String schId,OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mSchId = schId;
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
    public AdaptorEventDays.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_days, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorEventDays.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
            viewHolder.moreIView.setTag(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.moreIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (Integer)v.getTag();
                try {
                    final JSONObject dateJSON = mJsonArray.getJSONObject(index);
                    String date = dateJSON.getString("date");

//                    long memAttendCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
//                    long memOtherCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);

                    long memCount = cDBase.getNoOfRecordBySchId(CordOfflineDBase.EVENT_DETAIL_TABLE,mSchId);
                    long memAttendCountByDate = cDBase.getNumPresentMemByDate(date);
                    JSONArray otherMemArrayByDate = cDBase.getOtherMemListByEventIdDate(mSchId,date);


                    if (memCount > 0) {

                        if (memAttendCountByDate > 0 || otherMemArrayByDate.length() > 0) {
                            if (otherMemArrayByDate.length() > 0 || !cDBase.isImgSyncedByDate(mSchId,date) || !cDBase.isAttendanceSyncedByDate(mSchId,date)){
                                mListener.onMultiRecyclerViewItemClick(1, dateJSON); // For Sync Offline Data
                            }
                        }else {

                            PopupMenu popup = new PopupMenu(mContext, v);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.sesson_menu);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.add_session:
                                            mListener.onMultiRecyclerViewItemClick(2, dateJSON);
                                            break;
                                        /*case R.id.take_attendance:
                                           takeAttendanceAction(dateJSON);
                                            break;*/
                                    }
                                    return false;
                                }
                            });
                            //displaying the popup
                            popup.show();
                        }
                    }else {
                        PopupMenu popup = new PopupMenu(mContext, v);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.sesson_menu);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.add_session:
                                        mListener.onMultiRecyclerViewItemClick(2, dateJSON);
                                        AppSettings.getInstance().setValue(mContext, ApConstants.Session_data_list,mJsonArray.toString());
                                        break;
                                    /*case R.id.take_attendance:
                                        takeAttendanceAction(dateJSON);
                                        break;*/
                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout eventDayLLayout;
        private final ImageView moreIView;
        private final TextView dayTitleTView;
        private ImageView downloadIView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventDayLLayout = (LinearLayout) itemView.findViewById(R.id.eventDayLLayout);
            moreIView = (ImageView) itemView.findViewById(R.id.moreIView);
            dayTitleTView = (TextView) itemView.findViewById(R.id.dayTitleTView);
            downloadIView = (ImageView)itemView.findViewById(R.id.downloadIView);
        }


        public void onBind(final JSONObject jsonObject, final int i) {

            try {
                // String day = jsonObject.getString("day");
                final String currentDate = ApUtil.getDateByTimeStamp(ApUtil.getCurrentTimeStamp());
                final String date = jsonObject.getString("date");

                if (mJsonArray.length()>1){
                    title = jsonObject.getString("day")+" : "+jsonObject.getString("date");
                }else {
                    title = jsonObject.getString("date");
                }
                dayTitleTView.setText(title);

//                long memAttendCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_MEM_ATTEND_TABLE);
//                long memOtherCount = cDBase.getNoOfRecord(CordOfflineDBase.EVENT_OTHER_MEM_TABLE);

                long memCount = cDBase.getNoOfRecordBySchId(CordOfflineDBase.EVENT_DETAIL_TABLE,mSchId);
                long memAttendCountByDate = cDBase.getNumPresentMemByDate(date);
                JSONArray otherMemArrayByDate = cDBase.getOtherMemListByEventIdDate(mSchId,date);

                if (date.equalsIgnoreCase(currentDate)) {
                    eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected_item_color));

                    if (memCount > 0) {

                        if (memAttendCountByDate > 0 || otherMemArrayByDate.length() > 0) {

                            if (!cDBase.isOthMemDetailSyncedByDate(mSchId,date) && !cDBase.isImgSyncedByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sync));
                            }else if (!cDBase.isAttendanceSyncedByDate(mSchId,date) && cDBase.isAnyAttendanceDoneByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sync));
                            }else if (cDBase.isOthMemDetailSyncedByDate(mSchId,date) && cDBase.isImgSyncedByDate(mSchId,date) && cDBase.isAttendanceSyncedByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check));
                            }

                        }else {
                            moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_more));
                            moreIView.setVisibility(View.VISIBLE);
                        }

                    }else {
                        moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_more));
                        moreIView.setVisibility(View.VISIBLE);
                    }


                } else if(new SimpleDateFormat("dd-MM-yyyy").parse(date).before(new Date())){
                    eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));

                    if (memCount > 0) {

                        if (memAttendCountByDate > 0 || otherMemArrayByDate.length() > 0) {

                            if (!cDBase.isOthMemDetailSyncedByDate(mSchId,date) && !cDBase.isImgSyncedByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sync));
                            }else if (!cDBase.isAttendanceSyncedByDate(mSchId,date) && cDBase.isAnyAttendanceDoneByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sync));
                            }else if (cDBase.isOthMemDetailSyncedByDate(mSchId,date) && cDBase.isImgSyncedByDate(mSchId,date) && cDBase.isAttendanceSyncedByDate(mSchId,date)){
                                moreIView.setVisibility(View.VISIBLE);
                                eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));
                                moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check));
                            }

                        }else {
                            eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));
                            moreIView.setVisibility(View.INVISIBLE);
                            downloadIView.setVisibility(View.VISIBLE);

                        }

                    }else {
                        eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.stroke_color_gray));
                        moreIView.setVisibility(View.INVISIBLE);
                        downloadIView.setVisibility(View.VISIBLE);
                    }

                }else {
                    eventDayLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_white));
                    moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_more));
                    moreIView.setVisibility(View.VISIBLE);
                }

                downloadIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Toast.makeText(mContext, "Download pdf", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, EventReportActivity.class);
                        intent.putExtra("schId",mSchId);
                        intent.putExtra("closerDate",date);
                        intent.putExtra("type","day");
                        mContext.startActivity(intent);

                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {

                            String date = jsonObject.getString("date");
                            String dTitle = "";
                            if (mJsonArray.length()>1){
                                dTitle = jsonObject.getString("day")+" : "+jsonObject.getString("date");
                            }else {
                                dTitle = jsonObject.getString("date");
                            }


                            if (date.equalsIgnoreCase(currentDate)) {

                                long memAttendCountByDate = cDBase.getNumPresentMemByDate(date);
                                JSONArray otherMemArrayByDate = cDBase.getOtherMemListByEventIdDate(mSchId,date);

                                if (Utility.checkConnection(mContext)){
                                    if (memAttendCountByDate > 0 || otherMemArrayByDate.length() > 0) {
                                        UIToastMessage.show(mContext,"Please sync offline data");
                                    }else {
                                        Intent intent = new Intent(mContext, EventDGruListActivity.class);
                                        intent.putExtra("title",dTitle);
                                        intent.putExtra("eventDate",date);
                                        intent.putExtra("schId",mSchId);
                                        mContext.startActivity(intent);
                                    }
                                }else {

                                    Intent intent = new Intent(mContext, EventDGruListActivity.class);
                                    intent.putExtra("title",dTitle);
                                    intent.putExtra("eventDate",date);
                                    intent.putExtra("schId",mSchId);
                                    mContext.startActivity(intent);
                                }

                            }else {
//                                UIToastMessage.show(mContext,"Can't take attendance for "+date+" date");
                                Toast.makeText(mContext, "Can't take attendance for "+date+" date", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }


    private void takeAttendanceAction(JSONObject jsonObject) {

        try {
            String currentDate = ApUtil.getDateByTimeStamp(ApUtil.getCurrentTimeStamp());
            String date = jsonObject.getString("date");
            String title = jsonObject.getString("day")+" : "+jsonObject.getString("date");
            if (date.equalsIgnoreCase(currentDate)) {

                long memAttendCountByDate = cDBase.getNumPresentMemByDate(date);
                JSONArray otherMemArrayByDate = cDBase.getOtherMemListByEventIdDate(mSchId,date);

                if (Utility.checkConnection(mContext)){
                    if (memAttendCountByDate > 0 || otherMemArrayByDate.length() > 0) {
                        UIToastMessage.show(mContext,"Please sync offline data");
                    }else {
                        Intent intent = new Intent(mContext, EventDGruListActivity.class);
                        intent.putExtra("title",title);
                        intent.putExtra("eventDate",date);
                        intent.putExtra("schId",mSchId);
                        mContext.startActivity(intent);
                    }
                }else {

                    Intent intent = new Intent(mContext, EventDGruListActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("eventDate",date);
                    intent.putExtra("schId",mSchId);
                    mContext.startActivity(intent);
                }

            }else {
                UIToastMessage.show(mContext,"Can't take attendance for "+date+" date");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




}
