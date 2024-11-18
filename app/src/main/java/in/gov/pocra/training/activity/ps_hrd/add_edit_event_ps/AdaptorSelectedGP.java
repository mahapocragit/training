package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps;

import android.app.Activity;
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
import in.co.appinventor.services_api.listener.AlertListCallbackEventListener;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection.GPMembersListActivity;
import in.gov.pocra.training.event_db.EventDataBase;


public class AdaptorSelectedGP extends RecyclerView.Adapter<AdaptorSelectedGP.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;
    private String mActType;
    private Activity mActivity;
    EventDataBase eDB;


    public AdaptorSelectedGP(Context context, JSONArray jsonArray, String actType, OnMultiRecyclerItemClickListener listener, Activity activity) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mListener = listener;
        this.mActType = actType;
        this.mActivity = activity;
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
    public AdaptorSelectedGP.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_selected, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorSelectedGP.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout sledLLayout;
        private final TextView titleTView;
        private final ImageView cancelImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sledLLayout = (LinearLayout)itemView.findViewById(R.id.sledLLayout);
            titleTView = (TextView) itemView.findViewById(R.id.titleTView);
            cancelImageView = (ImageView) itemView.findViewById(R.id.cancelImageView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                final String gpId = jsonObject.getString("gp_id").trim();
                final String title = jsonObject.getString("gp_name").trim();

                JSONArray memArray = eDB.getGpMemberListByGpId(gpId);
                int numOfMem = 0;
                for (int m = 0; memArray.length()>m;m++){
                    JSONObject gpMEM = memArray.getJSONObject(m);
                    int isSelected = gpMEM.getInt("mem_is_selected");
                    if (isSelected == 1){
                        numOfMem++;
                    }
                }
                titleTView.setText(title +" ("+numOfMem+")");

                if (mActType.equalsIgnoreCase("create")){

                    sledLLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(mContext, GPMembersListActivity.class);
                            intent.putExtra("gpId",gpId);
                            intent.putExtra("gpName",title);
                            mContext.startActivity(intent);
                        }
                    });

                    cancelImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                            notifyDataSetChanged();
                        }
                    });
                }else {
                    cancelImageView.setVisibility(View.GONE);
                    final JSONArray gpMemArray = eDB.getGpMemberListByGpId(gpId);
                    sledLLayout.setBackgroundResource(R.drawable.edit_border_bg);

                    sledLLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            JSONArray sledGPMEM = new JSONArray();
                            for (int i = 0; i<gpMemArray.length();i++){

                                JSONObject memJ = new JSONObject();
                                try {

                                    JSONObject gpMEM = gpMemArray.getJSONObject(i);
                                    int isSelected = gpMEM.getInt("mem_is_selected");
                                    if (isSelected == 1){

                                        String fName = gpMEM.getString("mem_first_name");
                                        String mName = gpMEM.getString("mem_middle_name");
                                        String lName = gpMEM.getString("mem_last_name");
                                        String designation = gpMEM.getString("mem_designation_name");
                                        String name = fName +" "+mName+ " "+lName +"\n("+designation+")\n";
                                        String id = gpMEM.getString("mem_id");
                                        memJ.put("id",id);
                                        memJ.put("name",name);

                                        sledGPMEM.put(memJ);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                            AppUtility.getInstance().showListDialog(sledGPMEM,title,"name","id",mActivity, (AlertListCallbackEventListener) mContext);

                            /*if (gpMemArray.length()>0){
                                // ApUtil.showCustomListPicker(coordinatorTextView, desigJSONArray, "Select designation", "name", "id", AddEditEventPsHrdActivity.this, AddEditEventPsHrdActivity.this);
                                AppUtility.getInstance().showListDialog(gpMemArray,"Selected Member","name","id",mActivity,mActivity);
                            }else {
                                UIToastMessage.show(mContext,"Member not found");
                            }*/

                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        private void updateSelection(int position) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");
                if (getTotalCount() > 3 && isSelected == 0){
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext,"Only 4 can be selected");
                } else {
                    if (jsonObject.getInt("is_selected") == 0) {
                        jsonObject.put("is_selected", 1);
                    } else {
                        jsonObject.put("is_selected", 0);
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
            JSONArray  sJsonArray = new JSONArray();

            try {

                if (mJSONArray != null) {

                    for (int j = 0; j< mJSONArray.length(); j++) {
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
