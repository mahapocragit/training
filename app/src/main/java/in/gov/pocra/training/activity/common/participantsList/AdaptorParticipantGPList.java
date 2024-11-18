package in.gov.pocra.training.activity.common.participantsList;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.EventDataBase;


public class AdaptorParticipantGPList extends RecyclerView.Adapter<AdaptorParticipantGPList.ViewHolder> {

    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private String mActionType;
    private String mGroupType;
    private int count = 0;


    public AdaptorParticipantGPList(Context context, JSONArray jsonArray,String actionType,String groupType, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mListener = listener;
        this.mActionType = actionType;
        this.mGroupType = groupType;
        eDB = new EventDataBase(mContext);
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
    public AdaptorParticipantGPList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_gp, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorParticipantGPList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
            viewHolder.gpLLayout.setTag(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout gpLLayout;
        private final TextView gpTitleTView;
        private final ImageView selectedImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gpLLayout = (LinearLayout) itemView.findViewById(R.id.gpLLayout);
            gpTitleTView = (TextView) itemView.findViewById(R.id.titleTView);
            selectedImageView = (ImageView) itemView.findViewById(R.id.selectedImageView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                if (mGroupType.equalsIgnoreCase("VCRMC(GP)")){
                    String title = jsonObject.getString("gp_name").trim();
                    gpTitleTView.setText(title);
                } else {
                    String title = jsonObject.getString("vil_act_name").trim();
                    gpTitleTView.setText(title);
                }

                gpLLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int index = (Integer) gpLLayout.getTag();

                        if (mGroupType.equalsIgnoreCase("VCRMC(GP)")){

                            try {
                                JSONObject gpJSON = mJSONArray.getJSONObject(index);
                                String itemId = gpJSON.getString("gp_id");
                                String itemName = gpJSON.getString("gp_name");

                                /****  This new code without restriction of GP selection*/
                                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                                intent.putExtra("sledMemType","VCRMC(GP)");
                                intent.putExtra("sledMemArray","");
                                intent.putExtra("actionType",mActionType);
                                intent.putExtra("gpId", itemId);
                                intent.putExtra("gpName", itemName);
                                mContext.startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else {

                            try {
                                JSONObject actJson = mJSONArray.getJSONObject(index);

                                String itemId = actJson.getString("vil_act_id");
                                String itemName = actJson.getString("vil_act_name");

                                Intent intent = new Intent(mContext, ParticipantsListActivity.class);
                                intent.putExtra("sledMemType","Farmer");
                                intent.putExtra("sledMemArray","");
                                intent.putExtra("actionType",mActionType);
                                intent.putExtra("actId", itemId);
                                intent.putExtra("actName", itemName);
                                mContext.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
