package in.gov.pocra.training.activity.common.coming_closed_dist_list;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;


public class AdaptorComingDistrictList extends RecyclerView.Adapter<AdaptorComingDistrictList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;

    public AdaptorComingDistrictList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mListener = listener;
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
    public AdaptorComingDistrictList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_coming_district, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorComingDistrictList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout rootLLayout;
        private final TextView titleTView;
        private final TextView eventCountTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLLayout = (LinearLayout) itemView.findViewById(R.id.rootLLayout);
            titleTView = (TextView) itemView.findViewById(R.id.titleTView);
            eventCountTView = (TextView)itemView.findViewById(R.id.eventCountTView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            String name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "name").trim();
            String eventCoun = "0";
            String upcomingEventCount = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "count_upcomming_event").trim();
            String closeEventCount = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "count_closed_event").trim();
            if (!upcomingEventCount.equalsIgnoreCase("")){
                eventCoun = upcomingEventCount;
            }else if (!closeEventCount.equalsIgnoreCase("")){
                eventCoun = closeEventCount;
            }
            titleTView.setText(name);
            eventCountTView.setText(eventCoun);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                }
            });
        }

    }

}
