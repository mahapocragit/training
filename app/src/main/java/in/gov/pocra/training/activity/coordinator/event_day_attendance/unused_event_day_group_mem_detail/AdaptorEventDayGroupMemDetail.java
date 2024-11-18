package in.gov.pocra.training.activity.coordinator.event_day_attendance.unused_event_day_group_mem_detail;

import android.content.Context;
import android.content.Intent;
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
import in.gov.pocra.training.activity.coordinator.add_edit_other_member.AddEditOtherMemberActivity;
import in.gov.pocra.training.model.online.VCRMCMemberDetailModel;

public class AdaptorEventDayGroupMemDetail extends RecyclerView.Adapter<AdaptorEventDayGroupMemDetail.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private String mAttendType;
    private String mVCRMC_GP_Id;
    private String mSchId;


    @Override
    public int getItemCount() {
        if (mJSONArray.length() != 0) {
            return mJSONArray.length();
        } else {
            return 0;
        }
    }

    public AdaptorEventDayGroupMemDetail(Context context, JSONArray jsonArray, String attendType, String vcrmcId, String schId) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mAttendType = attendType;
        this.mVCRMC_GP_Id = vcrmcId;
        this.mSchId = schId;
    }

    @NonNull
    @Override
    public AdaptorEventDayGroupMemDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_event_day_group_mem_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorEventDayGroupMemDetail.ViewHolder viewHolder, int i) {

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
        private final ImageView editImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mobileTextView = (TextView) itemView.findViewById(R.id.mobileTextView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            editImageView = (ImageView) itemView.findViewById(R.id.editImageView);

        }

        public void onBind(final JSONObject jsonObject, final int position) {

            VCRMCMemberDetailModel vcrmcMemberDetailModel = new VCRMCMemberDetailModel(jsonObject);

            String fName = vcrmcMemberDetailModel.getFname();
            String mName = vcrmcMemberDetailModel.getMname();
            String lName = vcrmcMemberDetailModel.getLname();
            String fullName = fName + " " + mName + " " + lName;
            String mobile = vcrmcMemberDetailModel.getMobile();
            String designation = vcrmcMemberDetailModel.getDesignation();

            nameTextView.setText(fullName);
            mobileTextView.setText(mobile);
            designationTextView.setText(designation);


            if (mAttendType.equalsIgnoreCase("officer")) {
                editImageView.setVisibility(View.INVISIBLE);
            } else {
                editImageView.setVisibility(View.VISIBLE);
            }


            // For edit Member detail
            editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddEditOtherMemberActivity.class);
                    intent.putExtra("gp_id", mVCRMC_GP_Id);
                    intent.putExtra("schId", mSchId);
                    intent.putExtra("type", "update");
                    intent.putExtra("memberType", "vcrmc");
                    intent.putExtra("memberData", jsonObject.toString());
                    mContext.startActivity(intent);
                }
            });

        }

    }
}
