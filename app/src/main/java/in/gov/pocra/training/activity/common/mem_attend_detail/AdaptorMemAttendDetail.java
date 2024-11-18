package in.gov.pocra.training.activity.common.mem_attend_detail;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorMemAttendDetail extends RecyclerView.Adapter<AdaptorMemAttendDetail.ViewHolder> {


    private Context mContext;
    private JSONArray mJsonArray;


    public AdaptorMemAttendDetail(Context context, JSONArray jsonArray) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
    }


    @Override
    public int getItemCount() {
        if (mJsonArray.length() != 0) {
            return mJsonArray.length();
        } else {
            return 1;
        }
    }


    @NonNull
    @Override
    public AdaptorMemAttendDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_mem_attend_detail, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdaptorMemAttendDetail.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView eSDateTView;
        private final TextView eEDateTView;
        private final TextView eVenueTView;
        private final TextView eTypeTView;
        private final TextView eNumOfDaysTView;
        private final TextView preTView;
        private final TextView absTView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            eSDateTView = (TextView) itemView.findViewById(R.id.eSDateTView);
            eEDateTView = (TextView) itemView.findViewById(R.id.eEDateTView);
            eVenueTView = (TextView) itemView.findViewById(R.id.eVenueTView);
            eTypeTView = (TextView) itemView.findViewById(R.id.eTypeTView);
            eNumOfDaysTView = (TextView)itemView.findViewById(R.id.eNumOfDaysTView);
            preTView = (TextView)itemView.findViewById(R.id.preTView);
            absTView = (TextView)itemView.findViewById(R.id.absTView);

        }

        public void onBind(final JSONObject jsonObject, final int i) {

            try {

                String sTime = jsonObject.getString("start_date");
                String stDate = ApUtil.getDateByTimeStamp(sTime);
                // String stDate = ApUtil.getDateInDDMMYYYY(sDate);
                String fromDat = "From:"+stDate;
                String eTime = jsonObject.getString("end_date");
                String enDate = ApUtil.getDateByTimeStamp(eTime);
                // String enDate = ApUtil.getDateInDDMMYYYY(eDate);
                String toDate = "to:"+ enDate;
                // String dispTraDate = ApUtil.getDateByTimeStamp(sDate);
                JSONArray datesList = ApUtil.getDateBetweenTwoDate(stDate,enDate);
                int numOfEventDays = datesList.length();
                String eVenue = jsonObject.getString("venue");
                String eType = jsonObject.getString("event_type_name");

                eSDateTView.setText(fromDat);
                eEDateTView.setText(toDate);
                eVenueTView.setText(eVenue);
                eTypeTView.setText(eType);
                eNumOfDaysTView.setText(String.valueOf(numOfEventDays));
                preTView.setText("");
                absTView.setText("");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



}
