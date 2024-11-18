package in.gov.pocra.training.activity.common.participantsList;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;


public class AdaptorParticipantsList extends RecyclerView.Adapter<AdaptorParticipantsList.ViewHolder> {


    private Context mContext;
    private JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private String mActionType,msledMemType;

    @Override
    public int getItemCount() {
        if (mJSONArray.length() != 0){
            return mJSONArray.length();
        }else {
            return 0;
        }
    }

    public AdaptorParticipantsList(Context context, JSONArray jsonArray,String sledMemType,String actionType,OnMultiRecyclerItemClickListener listener){
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mListener = listener;
        this.mActionType = actionType;
        this.msledMemType = sledMemType;
    }

    @NonNull
    @Override
    public AdaptorParticipantsList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_participants,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorParticipantsList.ViewHolder viewHolder, int i) {



        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView pNameTView;
        private final TextView pDesigTView;
        private final TextView pMobileTView;
        private final ImageView cancelImageView;
        String name;
                String desig;
        String mob;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pNameTView = (TextView)itemView.findViewById(R.id.pNameTView);
            pDesigTView = (TextView)itemView.findViewById(R.id.pDesigTView);
            pMobileTView = (TextView)itemView.findViewById(R.id.pMobileTView);
            cancelImageView = (ImageView)itemView.findViewById(R.id.cancelImageView);

        }



        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            if (mActionType.equalsIgnoreCase("update")){
                cancelImageView.setVisibility(View.GONE);
            }
            else if (mActionType.equalsIgnoreCase("View")){
                cancelImageView.setVisibility(View.GONE);
            }else {
                cancelImageView.setVisibility(View.VISIBLE);
            }
            if (msledMemType.equalsIgnoreCase("SHG")){
                  name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "name");
                  desig = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "chief_promoter_president");
                  mob = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile");
            }
            else if (msledMemType.equalsIgnoreCase("FPC")){
                name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "name");
                desig = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_person");
                mob = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_no");
            } else if (msledMemType.equalsIgnoreCase("FG")){
                name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "group_name");
                desig = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_person");
                mob = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_number");
            }else {
                String first_name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name");
                String middle_name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name");
                String last_name = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name");
                  name = first_name + " " + "" + " " + "";
                  desig = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "designation");
                  mob = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile");

            }
            pNameTView.setText(name);
            pDesigTView.setText(desig);
            pMobileTView.setText(mob);


            cancelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
