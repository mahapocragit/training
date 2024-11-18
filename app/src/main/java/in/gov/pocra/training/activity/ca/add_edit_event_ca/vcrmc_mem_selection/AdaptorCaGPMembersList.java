package in.gov.pocra.training.activity.ca.add_edit_event_ca.vcrmc_mem_selection;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.mem_attend_detail.MemAttendanceDetailActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.util.ApConstants;
import in.gov.pocra.training.util.ApUtil;


public class AdaptorCaGPMembersList extends RecyclerView.Adapter<AdaptorCaGPMembersList.ViewHolder> {

    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;
    private JSONArray mPJSONArray;

    public AdaptorCaGPMembersList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mPJSONArray = jsonArray;
        this.mListener = listener;
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

    public void filter(String text){

        if (text.isEmpty()){

            mJSONArray = mPJSONArray;
            notifyDataSetChanged();

        }else {
            mJSONArray = mPJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i< mJSONArray.length(); i++){

                try {
                    JSONObject jsonObject = mJSONArray.getJSONObject(i);
                    String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mem_first_name").trim();
                    String mGPName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "gp_name").trim();

                    if (mName.toLowerCase().contains(text.toLowerCase())|| mGPName.toLowerCase().contains(text.toLowerCase())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }
                    /*if (data.contains(text.toLowerCase().trim())){
                        temp.put(mJSONArray.getJSONObject(i));
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mJSONArray = temp;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_gp_members, viewGroup, false);
        ApUtil.hideKeybord(view,mContext);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout gpMemLLayout;
        private final TextView memNameTView;
        // private final TextView mobileTView;
        private final TextView genderTView;
        private final TextView trCountTView;
        // private final View selectedView;
        private final View checkedIView;
        private final TextView memDesignationTView;
        private final ImageView detailIView;
        private final TextView gpvalTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gpMemLLayout = (LinearLayout) itemView.findViewById(R.id.gpMemLLayout);
            memNameTView = (TextView) itemView.findViewById(R.id.memNameTView);
            // mobileTView = (TextView)itemView.findViewById(R.id.mobileTView);
            genderTView = (TextView) itemView.findViewById(R.id.genderTView);
            trCountTView = (TextView) itemView.findViewById(R.id.trCountTView);
            // selectedView = (View) itemView.findViewById(R.id.selectedView);
            checkedIView = (ImageView) itemView.findViewById(R.id.checkedIView);
            detailIView = (ImageView) itemView.findViewById(R.id.detailIView);
            memDesignationTView = (TextView) itemView.findViewById(R.id.memDesignationTView);
            gpvalTView = (TextView) itemView.findViewById(R.id.gpvalTView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {


                final String uID = jsonObject.getString("u_id");
                final String groupId = jsonObject.getString("gp_id");
                final String groupType = jsonObject.getString("gp_id");
                final String memId = jsonObject.getString("mem_id");
                String memberName = jsonObject.getString("mem_name");
                String fName = jsonObject.getString("mem_first_name");
                String mName = jsonObject.getString("mem_middle_name");
                String lName = jsonObject.getString("mem_last_name");
                final String name = fName + " " + mName + " " + lName;
                String designation = jsonObject.getString("mem_designation_name");
                String mNumber = jsonObject.getString("mem_mobile");
                String gender = jsonObject.getString("mem_gender_name");
                String gpName = jsonObject.getString("gp_name");


                memNameTView.setText(name);
                // mobileTView.setText(mNumber);
                genderTView.setText(gender);
                memDesignationTView.setText(designation);
                 gpvalTView.setText(gpName);

                if (jsonObject.getInt("mem_is_selected") == 1) {
                    checkedIView.setVisibility(View.VISIBLE);
                    gpMemLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg_green));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.bg_green));
                    count++;
                } else {
                    checkedIView.setVisibility(View.INVISIBLE);
                    gpMemLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.black_bg));
                    if (count != 0) {
                        count--;
                    }
                }


                detailIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MemAttendanceDetailActivity.class);
                        intent.putExtra("title", name);
                        intent.putExtra("mem_id", memId);
                        intent.putExtra("group_id", groupId);
                        intent.putExtra("group_type", "gp");
                        mContext.startActivity(intent);
                    }
                });


                gpMemLLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sledGPMem = AppSettings.getInstance().getValue(mContext, ApConstants.kS_GP_MEM_ARRAY, ApConstants.kS_GP_MEM_ARRAY);
                        JSONArray sledGPMemArray = new JSONArray();
                        try {
                            if (!sledGPMem.equalsIgnoreCase("kS_GP_MEM_ARRAY")) {

                                sledGPMemArray = new JSONArray(sledGPMem);

                                if (sledGPMemArray.length() > 0) {

                                    if (isGpMemIdSelected(sledGPMemArray, memId)) {
                                        UIToastMessage.show(mContext, name + " is already selected");
                                    } else {
                                        updateSelection(position, sledGPMemArray, memId);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }

                                } else {
                                    updateSelection(position, sledGPMemArray, memId);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }
                            } else {
                                updateSelection(position, sledGPMemArray, memId);
                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private boolean isGpMemIdSelected(JSONArray jsonArray, String itemId) {

            Boolean result = false;
            try {
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);
                    String preItemId = jsonObject.getString("id");
                    if (itemId.equalsIgnoreCase(preItemId)) {
                        result = true;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        private void updateSelection(int position, JSONArray selectedGPArray, String mem_id) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("mem_is_selected");

                if (selectedGPArray.length() >= 5 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 5 can be selected, \nto select new remove selected VCRMC(GP)");
                } else {
                    if (jsonObject.getString("mem_is_selected").equalsIgnoreCase("0")) {
                        jsonObject.put("mem_is_selected", "1");
                        eDB.updateGpMemIsSelected(mem_id, "1");
                    } else {
                        jsonObject.put("mem_is_selected", "0");
                        eDB.updateGpMemIsSelected(mem_id, "0");
                    }
                }

                mJSONArray.put(position, jsonObject);
                notifyItemChanged(position);
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private int getTotalCount() {
            JSONArray sJsonArray = new JSONArray();

            try {

                if (mJSONArray != null) {

                    for (int j = 0; j < mJSONArray.length(); j++) {
                        JSONObject jsonObject = mJSONArray.getJSONObject(j);
                        if (jsonObject.getInt("is_selected") == 1) {
                            sJsonArray.put(jsonObject);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return sJsonArray.length();
        }
    }
}
