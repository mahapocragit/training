package in.gov.pocra.training.activity.pmu.pmu_upcoming_event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.add_event_pmu.AddEventPmuActivity;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorPmuComingEvent extends RecyclerView.Adapter<AdaptorPmuComingEvent.ViewHolder> {

    private Context mContext;
    private JSONArray mJsonArray;
    public JSONArray mPJSONArray;
    public String mLevel;
    private Boolean mOptToShow;
    private OnMultiRecyclerItemClickListener mListener;

    public AdaptorPmuComingEvent(Context context, JSONArray jsonArray, String level,boolean optionToShow, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mPJSONArray = jsonArray;
        this.mLevel = level;
        this.mOptToShow = optionToShow;
        this.mListener = listener;
    }


    @Override
    public int getItemCount() {
        if (mJsonArray != null && mJsonArray.length() != 0) {
            return mJsonArray.length();
        } else {
            return 0;
        }
    }


    @NonNull
    @Override
    public AdaptorPmuComingEvent.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pmu_coming_event, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdaptorPmuComingEvent.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);

            JSONObject dataJson = mJsonArray.getJSONObject(i);
            try {
                // Schedule Start Date
                String sTime = dataJson.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(stDate);

                // Schedule End Date
                String eTime = dataJson.getString("end_date");
                String etDate = ApUtil.getDateByTimeStamp(eTime);
                Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(etDate);

                // Current Date
                // String curDateTime = ApUtil.getCurrentTimeStamp();
                String curDateTime = dataJson.getString("current_date");
                String currentDate = ApUtil.getDateByTimeStamp(curDateTime);
                Date curDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);

                if (mOptToShow){

                    /*if ((curDate.equals(startDate) || curDate.after(startDate) && (curDate.equals(endDate) || curDate.before(endDate)))){
                        viewHolder.optMoreImageView.setVisibility(View.GONE);
                    }else {
                        viewHolder.optMoreImageView.setVisibility(View.VISIBLE);
                    }*/

                    if (curDate.after(startDate) && (curDate.equals(endDate) || curDate.before(endDate))){
                        viewHolder.optMoreImageView.setVisibility(View.GONE);
                    }else {
                        viewHolder.optMoreImageView.setVisibility(View.VISIBLE);
                    }


                }else {
                    viewHolder.optMoreImageView.setVisibility(View.GONE);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout scheduleLLayout;
        private final TextView eventSDateTView;
        private final TextView eventEDateTView;
        private final TextView eventVenueTView;
        private final TextView eventTypeTView;
        private final TextView participantTView;
        private final TextView eventTitleTView;
        private final ImageView optMoreImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scheduleLLayout = (LinearLayout) itemView.findViewById(R.id.scheduleLLayout);
            eventSDateTView = (TextView) itemView.findViewById(R.id.eventSDateTView);
            eventEDateTView = (TextView) itemView.findViewById(R.id.eventEDateTView);
            eventVenueTView = (TextView) itemView.findViewById(R.id.eventVenueTView);
            eventTypeTView = (TextView) itemView.findViewById(R.id.eventTypeTView);
            participantTView = (TextView) itemView.findViewById(R.id.participantTView);
            eventTitleTView = (TextView) itemView.findViewById(R.id.eventTitleTView);
            optMoreImageView = (ImageView) itemView.findViewById(R.id.optMoreImageView);

        }

        public void onBind(final JSONObject jsonObject, final int i) {

            try {

                String sTime = jsonObject.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                String fromDat = "From:"+stDate;
                String eTime = jsonObject.getString("end_date");
                String enDate = ApUtil.getDateByTimeStamp(eTime);
                String toDate = "To:"+ enDate;

                String other_venue = "";

                if (jsonObject.isNull("other_venue")) {
                    other_venue = "";
                } else {
                    other_venue = jsonObject.getString("other_venue");
                }

                String eVenue = jsonObject.getString("venue");
                String venueDetails= "";
                String eType = jsonObject.getString("type");
                String participants = jsonObject.getString("participints");

                String eTitle = "";
                String subType = jsonObject.getString("sub_type");
                if (!subType.equalsIgnoreCase("Others")){
                    if (jsonObject.isNull("sub_type")) {
                        eTitle = jsonObject.getString("title");
                    } else {
                        eTitle = subType;
                    }
                }else {
                    eTitle = jsonObject.getString("title");
                }


                String is_event_closed = jsonObject.getString("is_event_closer");
                if (is_event_closed.equalsIgnoreCase("1")){
                    scheduleLLayout.setBackgroundResource(R.drawable.selected_list_border_bg);
                }

                if (other_venue != null && !other_venue.isEmpty()) {
                    venueDetails = other_venue;
                } else {
                    venueDetails = eVenue;
                }
                int serialno = i+1;
                eventSDateTView.setText(""+serialno+". "+fromDat);

//                eventSDateTView.setText(fromDat);
                eventEDateTView.setText(toDate);
                eventVenueTView.setText(venueDetails);
                eventTypeTView.setText(eType);
                participantTView.setText(participants);
                eventTitleTView.setText(eTitle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject eJSONObject = mJsonArray.getJSONObject(i);
                        final String sch_id = eJSONObject.getString("id");

                        Intent intent = new Intent(mContext, PmuUpcomingEventDetailActivity.class);
                        intent.putExtra("event_level", mLevel);
                        intent.putExtra("type", "update");
                        intent.putExtra("sch_id", sch_id);
                        mContext.startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            optMoreImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {
                        final JSONObject eJSONObject = mJsonArray.getJSONObject(i);
                        final String sch_id = eJSONObject.getString("id");
                        final String eType = jsonObject.getString("type");
                        final String is_sch_closed = eJSONObject.getString("is_event_closer");

                        String sTime = eJSONObject.getString("start_date");
                        String stDate = ApUtil.getDateByTimeStamp(sTime);
                        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(stDate);

                        // Current Date
                        String curDateTime = eJSONObject.getString("current_date");
                        String currentDate = ApUtil.getDateByTimeStamp(curDateTime);
                        Date curDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);

                        if (is_sch_closed.equalsIgnoreCase("0")){
                            PopupMenu popup = new PopupMenu(mContext, v);

                            if (startDate.equals(curDate)){
                                popup.inflate(R.menu.cancel_menu);
                                //adding click listener
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.action_cancel:
                                                askUserPermission(eJSONObject);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                            }else {
                                popup.inflate(R.menu.edit_cancel_menu);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.action_update:
                                                Intent intent = new Intent(mContext, AddEventPmuActivity.class);
                                                intent.putExtra("type", "update");
                                                intent.putExtra("sch_id", sch_id);
                                                mContext.startActivity(intent);
                                                break;
                                            case R.id.action_cancel:
                                                askUserPermission(eJSONObject);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                            }

                            //displaying the popup
                            popup.show();
                        }else {
                            UIToastMessage.show(mContext,"Sorry! This Event is closed.");
                        }


                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }



    private void askUserPermission(final JSONObject eJSONObject) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Are you sure you want to cancel the scheduled event?");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onMultiRecyclerViewItemClick(1, eJSONObject);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void filter(String text) {

        if (text.isEmpty()) {

            mJsonArray = mPJSONArray;
            notifyDataSetChanged();

        } else {
            mJsonArray = mPJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i < mJsonArray.length(); i++) {

                try {
                    JSONObject jsonObject = mJsonArray.getJSONObject(i);
                    String fullName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "person_name").trim();
                    String mNumber = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();

                    String data = fullName + " " + mNumber ;

                    if (data.toLowerCase().contains(text.toLowerCase())) {
                        temp.put(mJsonArray.getJSONObject(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mJsonArray = temp;
            notifyDataSetChanged();
        }
    }
}
