package in.gov.pocra.training.activity.common.coordinator_list;

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
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.util.ApConstants;


public class AdaptorCoordinatorList extends RecyclerView.Adapter<AdaptorCoordinatorList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    public JSONArray mFJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;
    private int memCount = 0;

    public AdaptorCoordinatorList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mFJSONArray = jsonArray;
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
    public AdaptorCoordinatorList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_coordinator, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorCoordinatorList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout rootLLayout;
        private final TextView titleTView;
        private final TextView mobileTView;
        private final ImageView checkedIView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLLayout = (LinearLayout) itemView.findViewById(R.id.rootLLayout);
            titleTView = (TextView) itemView.findViewById(R.id.titleTView);
            mobileTView = (TextView) itemView.findViewById(R.id.mobileTView);
            checkedIView = (ImageView) itemView.findViewById(R.id.checkedIView);

        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {


                String fName  = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name").trim();
                String mName  = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name").trim();
                String lName  = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name").trim();
                String mobile  = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "mobile").trim();

                final String name = fName+" "+mName+" "+lName;
                final String itemId = jsonObject.getString("id");

                titleTView.setText(name);
                mobileTView.setText(mobile);

                if (jsonObject.getInt("is_selected") == 1 ) {
                    checkedIView.setVisibility(View.VISIBLE);
                    rootLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected_item_color));
                    count++;
                    memCount = 1;
                } else {
                    checkedIView.setVisibility(View.INVISIBLE);
                    rootLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_white));
                    if (count != 0) {
                        count--;
                    }
                }


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String sledCoordinator = AppSettings.getInstance().getValue(mContext, ApConstants.kS_COORDINATOR, ApConstants.kS_COORDINATOR);
                        JSONArray sledCordArray = new JSONArray();
                        try {
                            if (!sledCoordinator.equalsIgnoreCase("kS_COORDINATOR")) {

                                sledCordArray = new JSONArray(sledCoordinator);

                                /*if (isCordIdSelected(sledCordArray, itemId)) {
                                    UIToastMessage.show(mContext, name + " is already selected");
                                } else {
                                    updateSelection(position, sledCordArray);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }*/

                                updateSelection(position, sledCordArray);
                                mListener.onMultiRecyclerViewItemClick(4, jsonObject);

                               /* if (sledCordArray.length() > 0) {

                                    if (isCordIdSelected(sledCordArray, itemId)) {
                                        UIToastMessage.show(mContext, name + " is already selected");
                                    } else {
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }

                                } else {

                                    updateSelection(position, sledCordArray);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);

                                }*/

                            } else {
                                updateSelection(position, sledCordArray);
                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);

//                                updateSelection(position, sledCordArray);
//                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);
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

        private boolean isCordIdSelected(JSONArray jsonArray, String itemId) {

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


        private void updateSelection(int position, JSONArray selectedGPArray) {
            try {
                AppSettings.getInstance().setValue(mContext, ApConstants.kS_CO_COORDINATOR, ApConstants.kS_CO_COORDINATOR);
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (memCount >= 1 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 1 can be selected, \nto select new remove selected");
                } else {
                    if (jsonObject.getInt("is_selected") == 0) {
                        jsonObject.put("is_selected", 1);
                        memCount = memCount+1;
                    } else {
                        jsonObject.put("is_selected", 0);
                        memCount = memCount-1;
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


    void filter(String text){

        if (text.isEmpty()){

            mJSONArray = mFJSONArray;
            notifyDataSetChanged();

        }else {
            mJSONArray = mFJSONArray;
            JSONArray temp = new JSONArray();
            for (int i = 0; i< mJSONArray.length(); i++){
                try {

                    String fName = mJSONArray.getJSONObject(i).getString("first_name").toLowerCase();
                    String mName = mJSONArray.getJSONObject(i).getString("middle_name").toLowerCase();
                    String lName = mJSONArray.getJSONObject(i).getString("last_name").toLowerCase();
                    String mobile = mJSONArray.getJSONObject(i).getString("mobile").toLowerCase(); // added for the mobile number search
                    String data = fName+" "+mName+" "+lName+" "+mobile;

                    if (data.contains(text.toLowerCase())){
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
