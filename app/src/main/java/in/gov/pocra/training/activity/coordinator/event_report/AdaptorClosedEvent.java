package in.gov.pocra.training.activity.coordinator.event_report;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ps_hrd.ps_report.EventReportActivity;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorClosedEvent extends RecyclerView.Adapter<AdaptorClosedEvent.ViewHolder> {

    private Context mContext;
    private JSONArray mJSONArray;

    public AdaptorClosedEvent(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public int getItemCount() {
        if (mJSONArray.length() != 0) {
            return mJSONArray.length();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public AdaptorClosedEvent.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_closed_event, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorClosedEvent.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView eventSDateTView;
        private final TextView eventEDateTView;
        private final TextView eventVenueTView;
        private final TextView eventTypeTView;
        private final TextView participantTView;
        private final TextView eventTitleTView;
        private final ImageView moreIView;
        private final TextView eventSerialnoTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventSerialnoTView = (TextView) itemView.findViewById(R.id.tv_serialno);

            eventSDateTView = (TextView) itemView.findViewById(R.id.eventSDateTView);
            eventEDateTView = (TextView) itemView.findViewById(R.id.eventEDateTView);
            eventVenueTView = (TextView) itemView.findViewById(R.id.eventVenueTView);
            eventTypeTView = (TextView) itemView.findViewById(R.id.eventTypeTView);
            participantTView = (TextView) itemView.findViewById(R.id.participantTView);
            eventTitleTView = (TextView) itemView.findViewById(R.id.eventTitleTView);
            moreIView = (ImageView) itemView.findViewById(R.id.moreIView);
        }

        public void onBind(final JSONObject jsonObject, int i) {

            try {


                String sTime = jsonObject.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                String fromDat = "From: "+stDate;
                String eTime = jsonObject.getString("end_date");
                String enDate = ApUtil.getDateByTimeStamp(eTime);
                String toDate = "To: "+ enDate;
                String eType = jsonObject.getString("type");
                String participants = jsonObject.getString("participints");


                // For venue
                String other_venue = "";

                if (jsonObject.isNull("other_venue")) {
                    other_venue = "";
                } else {
                    other_venue = jsonObject.getString("other_venue");
                }

                String eVenue = jsonObject.getString("venue");
                String venueDetails= "";

                if (other_venue != null && !other_venue.isEmpty()) {
                    venueDetails = other_venue;
                } else {
                    venueDetails = eVenue;
                }


                // For title
                String eTitle = "";
                String subType = jsonObject.getString("event_sub_type_name");
                if (!subType.equalsIgnoreCase("Others")){
                    eTitle = subType;
                }else {
                    eTitle = jsonObject.getString("title");
                }
                int serialno = i+1;
//                eventSerialnoTView.setText(""+serialno);
                eventSDateTView.setText(""+serialno+". "+fromDat);

//                eventSDateTView.setText(fromDat);
                eventEDateTView.setText(toDate);
                eventVenueTView.setText(venueDetails);
                eventTypeTView.setText(eType);
                participantTView.setText(participants);
                eventTitleTView.setText(eTitle);

                /*if (attendance.equalsIgnoreCase("0")){
                    scheduleLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.layout_border_bg_red));
                    schTopLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.layout_top_bg_red));
                    scheduleDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    scheduleVenueTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    tr1DetailTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    tr2DetailTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));

                }else if (attendance.equalsIgnoreCase("1")){
                    scheduleLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.layout_border_bg_green));
                    schTopLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.layout_top_bg_green));
                    scheduleDateTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    scheduleVenueTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    tr1DetailTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                    tr2DetailTextView.setTextColor(mContext.getResources().getColor(R.color.text_white));
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ClosedEventDaysActivity.class);
                    intent.putExtra("data",jsonObject.toString());
                    mContext.startActivity(intent);
                }
            });*/


            moreIView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {

                        final String sch_id = jsonObject.getString("schedule_id");
                        String sch_closed = jsonObject.getString("end_date");
                        final  String closedDate = ApUtil.getDateByTimeStamp(sch_closed);

                        PopupMenu popup = new PopupMenu(mContext, v);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.ps_report_opt_menu);
                        //adding click listener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_date:
                                        Intent intent = new Intent(mContext, ClosedEventDaysActivity.class);
                                        intent.putExtra("data", jsonObject.toString());
                                        mContext.startActivity(intent);
                                        break;
                                    case R.id.action_consi:
                                        Intent cIntent = new Intent(mContext, EventReportActivity.class);
                                        cIntent.putExtra("schId", sch_id);
                                        cIntent.putExtra("closerDate", closedDate);
                                        cIntent.putExtra("type","cons");
                                        mContext.startActivity(cIntent);
                                        break;
                                }
                                return false;
                            }
                        });
                        //displaying the popup
                        popup.show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
}
