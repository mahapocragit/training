package in.gov.pocra.training.activity.ca.person_list;

import android.content.Context;
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
import in.gov.pocra.training.util.ApConstants;

public class AdaptorCaPerson extends RecyclerView.Adapter<AdaptorCaPerson.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    public JSONArray mPJSONArray;
    public String mMemType;
    private String mAction;
    private OnMultiRecyclerItemClickListener mListener;


    public AdaptorCaPerson(Context context, JSONArray jsonArray, String action, String memType,OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mAction = action;
        this.mListener = listener;
        this.mPJSONArray = jsonArray;
        this.mMemType = memType;
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
    public AdaptorCaPerson.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_ca_person, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorCaPerson.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView,txt;
        private final TextView mobileTextView;
        private final TextView designationTextView;
        private final ImageView editImageView;
        private final LinearLayout attendRLLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mobileTextView = (TextView) itemView.findViewById(R.id.mobileTextView);
            designationTextView = (TextView) itemView.findViewById(R.id.designationTextView);
            editImageView = (ImageView) itemView.findViewById(R.id.editImageView);
            attendRLLayout = (LinearLayout) itemView.findViewById(R.id.attendRLLayout);
            txt = (TextView) itemView.findViewById(R.id.txt);

        }

        public void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {
            // Resource Person
            // {"person_name":"ramesh kumar kushwaha","mobile":8004725591,"gender":1,"profession":"testing","is_active":1}
            // Labour
            // {"person_name":"f f f","mobile":7058548899,"gender":1,"social_category_name":"OBC","village_name":"Borgaon","is_active":1}

            try {

                String fullName = jsonObject.getString("person_name");
                String mobile = jsonObject.getString("mobile");
                String designation = "";
                if (mMemType.equalsIgnoreCase("labour")){
                    txt.setText("Social Category:");
                    designation = jsonObject.getString("social_category_name");
                }else if (mMemType.equalsIgnoreCase("resource")){
                    txt.setText("Designation:");

                    designation = jsonObject.getString("profession");
                }

                if (mAction.equalsIgnoreCase("get")){
                    if (jsonObject.getInt("is_selected") == 1) {
                        attendRLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected_item_color));
                    } else {
                        attendRLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_white));
                    }
                }


                nameTextView.setText(fullName);
                mobileTextView.setText(mobile);
                designationTextView.setText(designation);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAction.equalsIgnoreCase("get")){
                            updateSelection(position);
                            mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                        }


                        /*String sledPerson = AppSettings.getInstance().getValue(mContext, ApConstants.kS_CA_RES_PERSON_ARRAY, ApConstants.kS_CA_RES_PERSON_ARRAY);
                        JSONArray sledPersonArray = new JSONArray();
                        try {
                            if (!sledPerson.equalsIgnoreCase("kS_CA_RES_PERSON_ARRAY")) {
                                sledPersonArray = new JSONArray(sledPerson);
                                if (sledPersonArray.length() > 0) {
                                     updateSelection(position);
                                     mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                } else {
                                    updateSelection(position);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }
                            } else {
                                 updateSelection(position);
                                 mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void updateSelection(int position) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (isSelected == 0) {
                    jsonObject.put("is_selected", 1);
                } else {
                    jsonObject.put("is_selected", 0);
                }
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
                    String fullName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "person_name").trim();
                    String mNumber = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();

                    String data = fullName + " " + mNumber ;

                    if (data.toLowerCase().contains(text.toLowerCase())) {
                        temp.put(mJSONArray.getJSONObject(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mJSONArray = temp;
            notifyDataSetChanged();
        }
    }
}
