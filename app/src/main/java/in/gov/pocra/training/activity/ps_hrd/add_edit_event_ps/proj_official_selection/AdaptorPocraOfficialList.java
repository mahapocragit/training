package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.proj_official_selection;

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


public class AdaptorPocraOfficialList extends RecyclerView.Adapter<AdaptorPocraOfficialList.ViewHolder> {


    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorPocraOfficialList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
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
    public AdaptorPocraOfficialList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_ffs_facilitator, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorPocraOfficialList.ViewHolder viewHolder, int i) {

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
        private final TextView memMobTView;
        private final View checkedIView;
        private final TextView memDesignationTView;
        private final ImageView detailIView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ffsMemLLayout = (LinearLayout) itemView.findViewById(R.id.ffsMemLLayout);
            memNameTView = (TextView) itemView.findViewById(R.id.memNameTView);
            genderTView = (TextView)itemView.findViewById(R.id.genderTView);
            trCountTView = (TextView)itemView.findViewById(R.id.trCountTView);
            checkedIView = (ImageView)itemView.findViewById(R.id.checkedIView);
            detailIView = (ImageView)itemView.findViewById(R.id.detailIView);
            memMobTView = (TextView)itemView.findViewById(R.id.memMobTView);
            memDesignationTView = (TextView)itemView.findViewById(R.id.memDesignationTView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {


                //final String uID = jsonObject.getString("id");
                final String groupId = jsonObject.getString("role_id");
                final String memId = jsonObject.getString("id");
                //String fName = jsonObject.getString("first_name");
                String fName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name");
                // String mName = jsonObject.getString("middle_name");
                String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name");
                // String lName = jsonObject.getString("last_name");
                String lName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name");
                final  String name = fName +" "+ mName +" "+ lName;
                String designation = jsonObject.getString("post");
                String mNumber = jsonObject.getString("mobile");
                String gender = jsonObject.getString("gender");



                memNameTView.setText(name);
                // mobileTView.setText(mNumber);
                genderTView.setText(gender);
                memDesignationTView.setText(designation);
                // trCountTView.setText();
                memMobTView.setText(mNumber);

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
                        intent.putExtra("title",name);
                        intent.putExtra("mem_id",memId);
                        intent.putExtra("group_id","999999999");
                        intent.putExtra("group_type","pop");
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


        private void updateSelection(int position,JSONArray selectedGPArray, String memID) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (isSelected == 0) {
                    jsonObject.put("is_selected", 1);
                } else {
                    jsonObject.put("is_selected", 0);
                }

                /*if (selectedGPArray.length() >= 10 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 10 can be selected, \nto select new remove selected");
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
