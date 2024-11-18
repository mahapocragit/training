package in.gov.pocra.training.activity.pmu.add_event_pmu.pmu_mem_filter_list;

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
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.common.mem_attend_detail.MemAttendanceDetailActivity;
import in.gov.pocra.training.event_db.EventDataBase;
import in.gov.pocra.training.util.ApConstants;


public class AdaptorPmuParticipantList extends RecyclerView.Adapter<AdaptorPmuParticipantList.ViewHolder> {


    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    public JSONArray mPJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorPmuParticipantList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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


    @NonNull
    @Override
    public AdaptorPmuParticipantList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pmu_participant, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorPmuParticipantList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ffsMemLLayout;
        private final TextView memNameTView;
        private final TextView genderTView;
        private final TextView trCountTView;
        private final TextView memMobileTView;
        private final View checkedIView;
        private final TextView memDesignationTView;
        private final TextView locationTView;
        private final ImageView detailIView;
        private final TextView villageNameTView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ffsMemLLayout = (LinearLayout) itemView.findViewById(R.id.ffsMemLLayout);
            memNameTView = (TextView) itemView.findViewById(R.id.memNameTView);
            genderTView = (TextView) itemView.findViewById(R.id.genderTView);
            memMobileTView = (TextView) itemView.findViewById(R.id.memMobileTView);
            trCountTView = (TextView) itemView.findViewById(R.id.trCountTView);
            checkedIView = (ImageView) itemView.findViewById(R.id.checkedIView);
            detailIView = (ImageView) itemView.findViewById(R.id.detailIView);
            memDesignationTView = (TextView) itemView.findViewById(R.id.memDesignationTView);
            locationTView = (TextView) itemView.findViewById(R.id.locationTView);
            villageNameTView = (TextView) itemView.findViewById(R.id.Village_name);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {

                final String groupId = jsonObject.getString("role_id");
                final String memId = jsonObject.getString("id");

                String fName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name");
                String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name");
                String lName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name");
                final String name = fName + " " + mName + " " + lName;
                String designation = jsonObject.getString("post");
                String mNumber = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile");
                String gender = jsonObject.getString("gender");
                String villageName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "village_name");
                String mLocation = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "location");

                memNameTView.setText(name);
                genderTView.setText(gender);
                memDesignationTView.setText(designation);
                memMobileTView.setText(mNumber);
                villageNameTView.setText(villageName);
                locationTView.setText(mLocation);

                if (jsonObject.getInt("is_selected") == 1) {
                    checkedIView.setVisibility(View.VISIBLE);
                    ffsMemLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg_green));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.bg_green));
                    count++;
                } else {
                    checkedIView.setVisibility(View.INVISIBLE);
                    ffsMemLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg));
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
                        intent.putExtra("group_id", "999999999");
                        intent.putExtra("group_type", "pop");
                        mContext.startActivity(intent);
                    }
                });


                ffsMemLLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sledPOMem = AppSettings.getInstance().getValue(mContext, ApConstants.kS_FACILITATOR_ARRAY, ApConstants.kS_FACILITATOR_ARRAY);
                        JSONArray sledPOMemberArray = new JSONArray();

                        try {
                            if (!sledPOMem.equalsIgnoreCase("kS_FACILITATOR_ARRAY")) {
                                sledPOMemberArray = new JSONArray(sledPOMem);
                                updateSelection(position, sledPOMemberArray, memId);
                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);

                            } else {
                                updateSelection(position, sledPOMemberArray, memId);
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


        private void updateSelection(int position, JSONArray selectedGPArray, String memID) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (isSelected == 0) {
                    jsonObject.put("is_selected", 1);
                } else {
                    jsonObject.put("is_selected", 0);
                }


               /* if (selectedGPArray.length() >= 100 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 100 can be selected, \nto select new remove selected");
                } else {
                    if (jsonObject.getInt("is_selected") == 0) {
                        jsonObject.put("is_selected", 1);
                        // eDB.updatePOMemSelectionDetail(memID,"1");
                    } else {
                        jsonObject.put("is_selected", 0);
                       // eDB.updatePOMemSelectionDetail(memID,"0");
                    }
                }*/

                mJSONArray.put(position, jsonObject);
                notifyItemChanged(position);
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    void filter(String text) {

        if (text.isEmpty()) {

            mJSONArray = mPJSONArray;
            notifyDataSetChanged();

        } else {
            mJSONArray = mPJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i < mJSONArray.length(); i++) {

                try {
                    JSONObject jsonObject = mJSONArray.getJSONObject(i);
                    String fName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name").trim();
                    String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name").trim();
                    String lName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name").trim();
                    String mNumber = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();
                    String vName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "village_name").trim();
                    String location = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "location").trim();

                    String data = fName + " " + mName + " " + lName + " " + mNumber + "" + vName + "" + location;

                    if (data.toLowerCase().contains(text.toLowerCase())) {
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

}
