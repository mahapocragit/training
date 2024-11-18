package in.gov.pocra.training.activity.coordinator.add_edit_other_member;

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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;


public class AdaptorCordOtherMemList extends RecyclerView.Adapter<AdaptorCordOtherMemList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorCordOtherMemList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public AdaptorCordOtherMemList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_cord_other_member, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorCordOtherMemList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTView;
        private final ImageView editIView;
        private final TextView mobileTView;
        private final TextView genderTView;
        private final TextView designationTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTView = (TextView) itemView.findViewById(R.id.nameTView);
            editIView = (ImageView) itemView.findViewById(R.id.editIView);
            mobileTView = (TextView) itemView.findViewById(R.id.mobileTView);
            genderTView = (TextView) itemView.findViewById(R.id.genderTView);
            designationTView = (TextView) itemView.findViewById(R.id.designationTView);

        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {

                String rId = AppSettings.getInstance().getValue(mContext, ApConstants.kROLE_ID, ApConstants.kROLE_ID);
                if (!rId.equalsIgnoreCase("kROLE_ID")) {
                    String roleId = rId;
                    String added_by_roleId = jsonObject.getString("role_id");
                    if (roleId.equalsIgnoreCase(added_by_roleId)){
                        editIView.setVisibility(View.VISIBLE);
                    }else {
                        editIView.setVisibility(View.INVISIBLE);
                    }

                }

                String fName = jsonObject.getString("first_name").trim();
                String mName = jsonObject.getString("middle_name").trim();
                String lName = jsonObject.getString("last_name").trim();
                String title = fName +" "+mName +" "+lName;
                final String itemId = jsonObject.getString("id");

                final String schId = jsonObject.getString("eventid");

                final String mobile = jsonObject.getString("mobile");
                final String gender = jsonObject.getString("gender");
                final String desig = jsonObject.getString("designation");

                nameTView.setText(title);
                mobileTView.setText(mobile);
                if (gender.equalsIgnoreCase("1")){
                    genderTView.setText("Male");
                }else if (gender.equalsIgnoreCase("2")){
                    genderTView.setText("Female");
                }else if (gender.equalsIgnoreCase("3")){
                    genderTView.setText("Transgander");
                }

                designationTView.setText(desig);


                editIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddEditOtherMemberActivity.class);
                        intent.putExtra("attendType",desig);
                        intent.putExtra("type","update");
                        intent.putExtra("oPerId",itemId);
                        intent.putExtra("schId",schId);
                        mContext.startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
