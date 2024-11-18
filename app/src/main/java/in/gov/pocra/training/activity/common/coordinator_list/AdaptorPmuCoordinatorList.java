package in.gov.pocra.training.activity.common.coordinator_list;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.settings.AppSettings;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ca.add_edit_event_ca.AddEditEventCaActivity;
import in.gov.pocra.training.util.ApConstants;


public class AdaptorPmuCoordinatorList extends RecyclerView.Adapter<AdaptorPmuCoordinatorList.ViewHolder> {

    private String userLaval;
    private Context mContext;
    public JSONArray mJSONArray;
    public JSONArray mFJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;
    private int memCount = 0;

    public AdaptorPmuCoordinatorList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mFJSONArray = jsonArray;
        this.mListener = listener;

        String rLaval = AppSettings.getInstance().getValue(mContext, ApConstants.kUSER_LEVEL, ApConstants.kUSER_LEVEL);
        if (!rLaval.equalsIgnoreCase("kUSER_LEVEL")) {
            userLaval = rLaval;
        }
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
    public AdaptorPmuCoordinatorList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_pmu_coordinator, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorPmuCoordinatorList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout rootLLayout;
        private final TextView nameTView;
        private final TextView memPostTView;
        // private final TextView genderTView;
        private final TextView locationTView;
        private final ImageView checkedIView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLLayout = (LinearLayout) itemView.findViewById(R.id.rootLLayout);
            nameTView = (TextView) itemView.findViewById(R.id.nameTView);
            memPostTView = (TextView) itemView.findViewById(R.id.memPostTView);
            // genderTView = (TextView) itemView.findViewById(R.id.genderTView);
            locationTView = (TextView) itemView.findViewById(R.id.locationTView);
            checkedIView = (ImageView) itemView.findViewById(R.id.checkedIView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {

                String fName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "first_name");
                String mName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "middle_name");
                String lName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "last_name");
                final  String name = fName +" "+ mName +" "+ lName;
                nameTView.setText(name);
                String memPost = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "post");
                String memLocation = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "location");
                // String memGender = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "gender");

                memPostTView.setText("Post : "+memPost);
                // genderTView.setText(memGender);
                if (memLocation.equalsIgnoreCase("")){
                    locationTView.setVisibility(View.GONE);
                }else {
                    locationTView.setText("Location : "+memLocation);
                }



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
                            String memLevel;
                            if (!sledCoordinator.equalsIgnoreCase("kS_COORDINATOR")) {

                                sledCordArray = new JSONArray(sledCoordinator);
                                Log.d("MAYUUU","itemView click userLaval==="+userLaval);

                                if (userLaval.equalsIgnoreCase("District")){
                                    memLevel = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "level");
                                    Log.d("MAYUUU","itemView click memLevel==="+memLevel);
                                    if (!memLevel.equalsIgnoreCase("pmu")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }else {
//                                        UIToastMessage.show(mContext,"A Ps-HRD/Ps-AB user cannot select a PMU user as a Coordinator");
                                        Toast.makeText(mContext, "A Ps-HRD/Ps-AB user cannot select a PMU user as a Coordinator", Toast.LENGTH_LONG);
                                    }

                                }else if (userLaval.equalsIgnoreCase("Subdivision")){
                                    memLevel = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "level");
                                    Log.d("MAYUUU","itemView click memLevel==="+memLevel);
                                    if (memLevel.equalsIgnoreCase("Subdivision")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }
                                    else if (memLevel.equalsIgnoreCase("Village")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }
                                    else {
//                                        UIToastMessage.show(mContext,"A CA user cannot select a PMU user as a Coordinator");
                                        Toast.makeText(mContext, "A CA user cannot select a PMU user as a Coordinator", Toast.LENGTH_LONG);
                                    }

                                }else {
                                    updateSelection(position, sledCordArray);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }

                                /*updateSelection(position, sledCordArray);
                                mListener.onMultiRecyclerViewItemClick(1, jsonObject);*/

                            } else {

                                if (userLaval.equalsIgnoreCase("District")){
                                    memLevel = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "level");
                                    if (!memLevel.equalsIgnoreCase("pmu")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }else {
//                                        UIToastMessage.show(mContext,"A Ps-HRD/Ps-AB user cannot select a PMU user as a Coordinator");
                                        Toast.makeText(mContext, "A Ps-HRD/Ps-AB user cannot select a PMU user as a Coordinator", Toast.LENGTH_LONG);
                                    }
                                }else if (userLaval.equalsIgnoreCase("Subdivision")){
                                    memLevel = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "level");
                                    if (memLevel.equalsIgnoreCase("Subdivision")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }
                                    else if (memLevel.equalsIgnoreCase("Village")){
                                        updateSelection(position, sledCordArray);
                                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                    }
                                    else {
//                                        UIToastMessage.show(mContext,"A CA user cannot select a PMU user as a Coordinator");
                                        Toast.makeText(mContext, "A CA user cannot select a PMU user as a Coordinator", Toast.LENGTH_LONG);
                                    }
                                }else {
                                    updateSelection(position, sledCordArray);
                                    mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                                }

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
    }

}
