package in.gov.pocra.training.activity.coordinator.event_session;

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


public class AdaptorSessionList extends RecyclerView.Adapter<AdaptorSessionList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorSessionList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public AdaptorSessionList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_session, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorSessionList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView startTView;
        private final TextView endTView;
        private final TextView sessionDescTView;
        private final TextView resPerNameTView;
        private final TextView resPerMobileTView;
        private final ImageView removeIView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            startTView = (TextView) itemView.findViewById(R.id.startTView);
            endTView = (TextView) itemView.findViewById(R.id.endTView);
            sessionDescTView = (TextView) itemView.findViewById(R.id.sessionDescTView);
            resPerNameTView = (TextView) itemView.findViewById(R.id.resPerNameTView);
            resPerMobileTView = (TextView) itemView.findViewById(R.id.resPerMobileTView);
            removeIView = (ImageView)itemView.findViewById(R.id.removeIView);

        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {

                String startTime = jsonObject.getString("start_time");
                String sTime = ApUtil.getTimeByTimeStamp(startTime);
                String endTime = jsonObject.getString("end_time");
                String eTime = ApUtil.getTimeByTimeStamp(endTime);
                String session_desc = jsonObject.getString("session_desc");
                String resPerName = jsonObject.getString("resource_person");
                String resPerMobile = jsonObject.getString("mobile");


                startTView.setText("Start : "+sTime);
                endTView.setText("End "+ eTime);
                sessionDescTView.setText("Description : "+session_desc);
                resPerNameTView.setText("Trainer Name : "+ resPerName);
                resPerMobileTView.setText("Trainer Mobile : "+resPerMobile);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            removeIView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMultiRecyclerViewItemClick(1,jsonObject);
                }
            });
        }


    }
}
