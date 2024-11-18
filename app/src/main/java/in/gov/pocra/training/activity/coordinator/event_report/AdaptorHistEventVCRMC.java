package in.gov.pocra.training.activity.coordinator.event_report;

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


public class AdaptorHistEventVCRMC extends RecyclerView.Adapter<AdaptorHistEventVCRMC.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;


    public AdaptorHistEventVCRMC(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public AdaptorHistEventVCRMC.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_day_gru, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorHistEventVCRMC.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView vcrmcTitleTView;
        private final ImageView completedImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            vcrmcTitleTView = (TextView) itemView.findViewById(R.id.memGrpTitleTView);
            completedImageView = (ImageView) itemView.findViewById(R.id.completedImageView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                final String title = jsonObject.getString("name").trim();
                vcrmcTitleTView.setText(title);

                if (jsonObject.getInt("attend_upd") == 1){
                    completedImageView.setVisibility(View.VISIBLE);
                }else {
                    completedImageView.setVisibility(View.INVISIBLE);
                }


                // To check is attendance done VCRMC
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
