package in.gov.pocra.training.activity.common.session_detail;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApUtil;

public class AdaptorEventDate extends RecyclerView.Adapter<AdaptorEventDate.ViewHolder> {



    private Context mContext;
    private JSONArray mJsonArray;
    private String mSessionData;
    private OnMultiRecyclerItemClickListener mListener;
    private int dataCount;
    private String title = "";

    public AdaptorEventDate(Context context, JSONArray jsonArray,String sessonData, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mListener = listener;
        this.mSessionData = sessonData;
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
    public AdaptorEventDate.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_days, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorEventDate.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
            viewHolder.moreIView.setTag(i);
            dataCount = 0;

            String date = mJsonArray.getJSONObject(i).getString("date");
            JSONArray SessionArray = new JSONArray(mSessionData);
            for (int j =0; SessionArray.length()>j;j++){
                JSONObject sessionJson = SessionArray.getJSONObject(j);
                String startTime = sessionJson.getString("start_time");
                String sessDate = ApUtil.getDateByTimeStamp(startTime);
                if (sessDate.equalsIgnoreCase(date)){
                    dataCount++;
                }
            }

            if (dataCount>0){
                viewHolder.moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_right));
            }else {
                viewHolder.moreIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right_arrow));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView moreIView;
        private final TextView dayTitleTView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            moreIView = (ImageView) itemView.findViewById(R.id.moreIView);
            dayTitleTView = (TextView) itemView.findViewById(R.id.dayTitleTView);
        }


        public void onBind(final JSONObject jsonObject, final int i) {

            try {

                if (mJsonArray.length()>1){
                    title = jsonObject.getString("day")+" : "+jsonObject.getString("date");
                }else {
                    title = jsonObject.getString("date");
                }
                dayTitleTView.setText(title);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onMultiRecyclerViewItemClick(1,jsonObject);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
