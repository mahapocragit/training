package in.gov.pocra.training.activity.ca.add_edit_event_ca.ca_farmer_filter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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


public class AdaptoFPCgroupList extends RecyclerView.Adapter<AdaptoFPCgroupList.ViewHolder> {

    EventDataBase eDB;
    private final Context mContext;
    public JSONArray mJSONArray;
    private final String mVillageActId;
    private final String mVillageActName;
    private final OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptoFPCgroupList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener, String villageActId, String villageActName) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mListener = listener;
        this.mVillageActId = villageActId;
        this.mVillageActName = villageActName;
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
    public AdaptoFPCgroupList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_shg_group, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptoFPCgroupList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout farmerLLayout;
        private final TextView farNameTView;
        private final TextView mobileTView;
        private final TextView proNameTView,flagtxt;
        private final TextView trCountTView;
        private final ImageView selectedImageView;
        private final ImageView detailIView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            farmerLLayout = itemView.findViewById(R.id.farmerLLayout);
            farNameTView = itemView.findViewById(R.id.farNameTView);
            mobileTView = itemView.findViewById(R.id.mobileTView);
             trCountTView = itemView.findViewById(R.id.trCountTView);
            selectedImageView = itemView.findViewById(R.id.selectedImageView);
            detailIView = itemView.findViewById(R.id.detailIView);
            proNameTView = itemView.findViewById(R.id.proNameTView);
            flagtxt = itemView.findViewById(R.id.flagtxt);

        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                final String farmerId = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "id");
                String farmerName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "name");
                String fMobile = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_no");
                String flagstr = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "group_flag");
                String proname = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "contact_person");
                String is_selected = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "is_selected");


                detailIView.setVisibility(View.GONE);

                farNameTView.setText(farmerName);
                mobileTView.setText(fMobile);
                flagtxt.setText(flagstr);
                proNameTView.setText(proname);


                if (jsonObject.getInt("is_selected") == 1) { // is_selected is replaced by MobileNumber for testing
                    selectedImageView.setVisibility(View.VISIBLE);
                    farmerLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg_green));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.bg_green));
                    count++;
                } else {
                    selectedImageView.setVisibility(View.INVISIBLE);
                    farmerLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.black_bg));
                    if (count != 0) {
                        count--;
                    }
                }


                detailIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MemAttendanceDetailActivity.class);
                        intent.putExtra("title",mVillageActName);
                        intent.putExtra("mem_id",farmerId);
                        intent.putExtra("group_id",mVillageActId);
                        intent.putExtra("group_type","fActivity");
                        mContext.startActivity(intent);
                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sledFarmer = AppSettings.getInstance().getValue(mContext, ApConstants.kS_FPC_PARTICIPANTS_ARRAY, ApConstants.kS_FPC_PARTICIPANTS_ARRAY);
                        JSONArray sledFarArray = new JSONArray();
                        try {
                            if (!sledFarmer.equalsIgnoreCase("kS_fpc_PARTICIPANTS_ARRAY")) {

                                sledFarArray = new JSONArray(sledFarmer);

                                if (sledFarArray.length() > 0) {
                                    updateSelection(position,sledFarArray,farmerId);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                } else {
                                    updateSelection(position,sledFarArray,farmerId);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }
                            } else {
                                updateSelection(position,sledFarArray,farmerId);
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




        private void updateSelection(int position,JSONArray selectedGPArray, String farId) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                String fId = jsonObject.getString("id");
                String fName = jsonObject.getString("name");
                int count = eDB.sledFarmerCount(fId);

                if (jsonObject.getInt("is_selected") == 0 ) {
                    if (count >= 2){
                        UIToastMessage.show(mContext, fName + " not allowed in more then 2 Activity");
                    }else {
                        jsonObject.put("is_selected", 1);
                        eDB.updateFpcIsSelected(farId,"1");
                    }
                } else {
                    jsonObject.put("is_selected", 0);
                    eDB.updateFpcIsSelected(farId,"0");
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
