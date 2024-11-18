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

import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.VCRMCMemberDetailModel;

public class AdaptorHistEventVCRMCDetail extends RecyclerView.Adapter<AdaptorHistEventVCRMCDetail.ViewHolder> {

    private Context mContext;
    private JSONArray mJSONArray;

    public AdaptorHistEventVCRMCDetail(Context context, JSONArray jsonArray){
        this.mContext = context;
        this.mJSONArray = jsonArray;
    }

    @Override
    public int getItemCount() {
        if (mJSONArray.length() != 0){
            return mJSONArray.length();
        }else {
            return 0;
        }
    }

    @NonNull
    @Override
    public AdaptorHistEventVCRMCDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_hist_detail,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorHistEventVCRMCDetail.ViewHolder viewHolder, int i) {
        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView mobileTextView;
        private final TextView designationTextView;
        private final ImageView presentImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mobileTextView = (TextView) itemView.findViewById(R.id.mobileTextView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            presentImageView = (ImageView) itemView.findViewById(R.id.presentImageView);


        }

        public void onBind(JSONObject jsonObject, int i) {

            try {
                VCRMCMemberDetailModel vcrmcMemberDetailModel = new VCRMCMemberDetailModel(jsonObject);

                String fName = vcrmcMemberDetailModel.getFname();
                String mName = vcrmcMemberDetailModel.getMname();
                String lName = vcrmcMemberDetailModel.getLname();
                String fullName = fName+ " "+mName+ " "+lName;
                String mobile = vcrmcMemberDetailModel.getMobile();
                String gender = vcrmcMemberDetailModel.getGender();
                String socCategory = vcrmcMemberDetailModel.getSocial_category();
                String designation = vcrmcMemberDetailModel.getDesignation();

                nameTextView.setText(fullName);
                mobileTextView.setText(mobile);
                designationTextView.setText(designation);

                if (jsonObject.getInt("attend") == 1) {
                    presentImageView.setVisibility(View.VISIBLE);
                } else {
                    presentImageView.setVisibility(View.INVISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
