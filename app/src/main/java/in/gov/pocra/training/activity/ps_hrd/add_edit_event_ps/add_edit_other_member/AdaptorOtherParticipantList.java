package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.add_edit_other_member;

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
import in.gov.pocra.training.util.ApConstants;

public class AdaptorOtherParticipantList extends RecyclerView.Adapter<AdaptorOtherParticipantList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;


    public AdaptorOtherParticipantList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public AdaptorOtherParticipantList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_other_participants, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorOtherParticipantList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout otherParticipantsLLayout;
        private final TextView memNameTView;
        private final TextView genderTView;
        private final TextView trCountTView;
        private final ImageView checkedIView;
        private final TextView memDesignationTView;
        private final ImageView detailIView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            otherParticipantsLLayout = (LinearLayout) itemView.findViewById(R.id.otherParticipantsLLayout);
            memNameTView = (TextView) itemView.findViewById(R.id.memNameTView);
            genderTView = (TextView) itemView.findViewById(R.id.genderTView);
            trCountTView = (TextView) itemView.findViewById(R.id.trCountTView);
            checkedIView = (ImageView) itemView.findViewById(R.id.checkedIView);
            detailIView = (ImageView) itemView.findViewById(R.id.detailIView);
            memDesignationTView = (TextView) itemView.findViewById(R.id.memDesignationTView);

        }

        public void onBind(final JSONObject jsonObject, final int position) {

            // {"id":1,"name":"s m ss","first_name":"s","middle_name":"m","last_name":"ss","mobile":"9966552233","gender":2,"designation":null,"is_selected":0}

            try {
                //final String groupId = jsonObject.getString("role_id");
                final String memId = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "id");
                String fName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name");
                String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name");
                String lName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name");
                final String name = fName + " " + mName + " " + lName;
                String designation = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "designation");
                String mNumber = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile");
                String gender = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "gender");


                memNameTView.setText(name);
                if (gender.equalsIgnoreCase("1")) {
                    genderTView.setText("Male");
                } else if (gender.equalsIgnoreCase("2")) {
                    genderTView.setText("Female");
                } else if (gender.equalsIgnoreCase("3")) {
                    genderTView.setText("Transgender");
                }

                memDesignationTView.setText(designation);
                // trCountTView.setText();

                if (jsonObject.getInt("is_selected") == 1) {
                    checkedIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check));
                    otherParticipantsLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg_green));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.bg_green));
                    count++;
                } else {
                    checkedIView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.edit));
                    checkedIView.setColorFilter(mContext.getResources().getColor(R.color.black_bg));
                    detailIView.setColorFilter(mContext.getResources().getColor(R.color.black_bg));
                    otherParticipantsLLayout.setBackground(mContext.getResources().getDrawable(R.drawable.parti_list_border_bg));
                    if (count != 0) {
                        count--;
                    }
                }


               /* detailIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MemAttendanceDetailActivity.class);
                        intent.putExtra("title",name);
                        intent.putExtra("mem_id",memId);
                        intent.putExtra("group_id",groupId);
                        intent.putExtra("group_type","facilitator");
                        mContext.startActivity(intent);
                    }
                });*/


                // If item not selected go for edit
                checkedIView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            if (jsonObject.getInt("is_selected") == 0) {
                                Intent intent = new Intent(mContext, AddEditPsOtherParticipantsActivity.class);
                                intent.putExtra("type", "update");
                                intent.putExtra("memberType", "other");
                                intent.putExtra("memberData", jsonObject.toString());
                                mContext.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                otherParticipantsLLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sledOthParticipants = AppSettings.getInstance().getValue(mContext, ApConstants.kS_OTH_PARTICIPANTS_ARRAY, ApConstants.kS_OTH_PARTICIPANTS_ARRAY);
                        JSONArray sledOthPartiArray = new JSONArray();

                        try {
                            if (!sledOthParticipants.equalsIgnoreCase("kS_OTH_PARTICIPANTS_ARRAY")) {
                                sledOthPartiArray = new JSONArray(sledOthParticipants);
                                updateSelection(position, sledOthPartiArray);
                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);

                            } else {
                                updateSelection(position, sledOthPartiArray);
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


        private void updateSelection(int position, JSONArray selectedGPArray) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (selectedGPArray.length() >= 100 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 100 can be selected, \nto select new remove selected");
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


    }
}
