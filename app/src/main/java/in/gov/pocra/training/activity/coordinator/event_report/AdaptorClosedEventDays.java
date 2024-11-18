package in.gov.pocra.training.activity.coordinator.event_report;

import android.content.Context;
import android.content.Intent;
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

import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.pmu.pmu_report.PmuReportListActivity;

public class AdaptorClosedEventDays extends RecyclerView.Adapter<AdaptorClosedEventDays.ViewHolder> {

    private Context mContext;
    private JSONArray mJsonArray;
    private String mSchId;

    public AdaptorClosedEventDays(Context context, JSONArray jsonArray, String schId) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mSchId = schId;
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
    public AdaptorClosedEventDays.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_closed_event_days, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorClosedEventDays.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout eventDayLLayout;
        private final ImageView moreIView;
        private final TextView dayTitleTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventDayLLayout = (LinearLayout) itemView.findViewById(R.id.eventDayLLayout);
            moreIView = (ImageView) itemView.findViewById(R.id.moreIView);
            dayTitleTView = (TextView) itemView.findViewById(R.id.dayTitleTView);
        }


        public void onBind(final JSONObject jsonObject, final int i) {

            try {
                String day = jsonObject.getString("day");
                String date = jsonObject.getString("date");
                final String title = day+" : "+date;
                dayTitleTView.setText(title);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            String date = jsonObject.getString("date");

                            /*Intent intent = new Intent(mContext, ViewOrDownloadPdfActivity.class);
                            intent.putExtra("schId",mSchId);
                            intent.putExtra("date",date);
                            mContext.startActivity(intent);*/

                            Intent intent = new Intent(mContext, PmuReportListActivity.class);
                            intent.putExtra("closed_event_list",mJsonArray.toString());
                            intent.putExtra("position",i);
                            mContext.startActivity(intent);


                            // UIToastMessage.show(mContext,"Can't take attendance for "+date+" date");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
