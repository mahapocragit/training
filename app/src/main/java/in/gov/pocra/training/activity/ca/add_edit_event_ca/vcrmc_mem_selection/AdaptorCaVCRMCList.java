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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.EventDataBase;

public class AdaptorCaVCRMCList extends RecyclerView.Adapter<AdaptorCaVCRMCList.ViewHolder> {

    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    private String userID;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorCaVCRMCList(Context context, JSONArray jsonArray, String userID, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.userID = userID;
        this.mListener = listener;
//        eDB = new EventDataBase(mContext);
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_gp, viewGroup, false);
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

        private final LinearLayout gpLLayout;
        private final TextView gpTitleTView;
        private final ImageView selectedImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gpLLayout = (LinearLayout) itemView.findViewById(R.id.gpLLayout);
            gpTitleTView = (TextView) itemView.findViewById(R.id.titleTView);
            selectedImageView = (ImageView) itemView.findViewById(R.id.selectedImageView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                String title = jsonObject.getString("name").trim();
                final String itemId = jsonObject.getString("id");
                final String itemName = jsonObject.getString("name");
                gpTitleTView.setText(title);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /****  This new code without restriction of GP selection*/
                        Intent intent = new Intent(mContext, GPMembersListCaActivity.class);
                        intent.putExtra("gpId", itemId);
                        intent.putExtra("gpName", itemName);
                        intent.putExtra("userID", userID);
                        intent.putExtra("type","designation");
                        mContext.startActivity(intent);

                        // String selectedGP = AppSettings.getInstance().getValue(mContext, ApConstants.kS_GP_ARRAY, ApConstants.kS_GP_ARRAY);
                      //  JSONArray selectedGPArray = eDB.getSelectedGpList();
                        //  try {
                        // if (!selectedGP.equalsIgnoreCase("kS_GP_ARRAY")) {


                        /****Live Code commented by santosh as per requirement On 8-26-2019 For new Version upload*/

                        /*if (selectedGPArray.length() > 0) {

                            if (isGpIdSelected(selectedGPArray, itemId)) {
                                Intent intent = new Intent(mContext, GPMembersListActivity.class);
                                intent.putExtra("gpId", itemId);
                                intent.putExtra("gpName", itemName);
                                mContext.startActivity(intent);

                            } else {

                                if (getTotalVCRMCS() < 4) {
                                    Intent intent = new Intent(mContext, GPMembersListActivity.class);
                                    intent.putExtra("gpId", itemId);
                                    intent.putExtra("gpName", itemName);
                                    mContext.startActivity(intent);
                                } else {

                                    UIAlertView.getOurInstance().show(mContext, "Selecting more than 4 VCRMC(GP) is not allowed");
                                }

                                // updateSelection(position,selectedGPArray);
                                // mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                            }


                        } else {

                            if (getTotalVCRMCS() < 4) {
                                Intent intent = new Intent(mContext, GPMembersListActivity.class);
                                intent.putExtra("gpId", itemId);
                                intent.putExtra("gpName", itemName);
                                mContext.startActivity(intent);
                            } else {
                                UIAlertView.getOurInstance().show(mContext, "For new selection please deselect previous selected VCRMC(GP) on previous screen");
                            }

                            // updateSelection(position,selectedGPArray);
                            // mListener.onMultiRecyclerViewItemClick(1, jsonObject);
                        }*/


                        /*} catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private boolean isGpIdSelected(JSONArray jsonArray, String itemId) {

            Boolean result = false;
            try {
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(k);
                    String preItemId = jsonObject.getString("gp_id");
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
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("gp_is_selected");

                if (selectedGPArray.length() >= 5 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 5 can be selected, \nto select new remove selected VCRMC(GP)");
                } else {
                    if (jsonObject.getInt("gp_is_selected") == 0) {
                        jsonObject.put("gp_is_selected", 1);
                    } else {
                        jsonObject.put("gp_is_selected", 0);
                    }
                }

                /* Old Condition*/
               /* if (getTotalCount() > 4 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 5 can be selected");
                } else {
                    if (jsonObject.getInt("is_selected") == 0) {
                        jsonObject.put("is_selected", 1);
                    } else {
                        jsonObject.put("is_selected", 0);
                    }
                }*/

                mJSONArray.put(position, jsonObject);
                notifyItemChanged(position);
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private int getTotalVCRMCS() {
            JSONArray sJsonArray = new JSONArray();
            try {
                if (mJSONArray != null) {

                    for (int j = 0; j < mJSONArray.length(); j++) {
                        JSONObject jsonObject = mJSONArray.getJSONObject(j);
                        if (jsonObject.getInt("gp_is_selected") == 1) {
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

