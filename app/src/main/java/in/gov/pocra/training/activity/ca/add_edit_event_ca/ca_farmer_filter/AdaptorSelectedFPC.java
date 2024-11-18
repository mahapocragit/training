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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;
import in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.ps_farmer_filter.PsFarmerListActivity;


public class AdaptorSelectedFPC extends RecyclerView.Adapter<AdaptorSelectedFPC.ViewHolder> {


    private Context mContext;
    public JSONArray mJSONArray;
    private String mActType;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorSelectedFPC(Context context, JSONArray jsonArray, String actType, OnMultiRecyclerItemClickListener listener) {
        this.mContext = context;
        this.mJSONArray = jsonArray;
        this.mActType = actType;
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
    public AdaptorSelectedFPC.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_selected, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorSelectedFPC.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout sledLLayout;
        private final TextView gpTitleTView;
        private final ImageView cancelImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sledLLayout = (LinearLayout)itemView.findViewById(R.id.sledLLayout);
            gpTitleTView = (TextView) itemView.findViewById(R.id.titleTView);
            cancelImageView = (ImageView) itemView.findViewById(R.id.cancelImageView);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            try {
                String title = jsonObject.getString("name").trim();
                final String villageId = jsonObject.getString("taluka_id");
                final String villageName = jsonObject.getString("contact_person");
                gpTitleTView.setText(title);


                if (mActType.equalsIgnoreCase("create")){

                    sledLLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(mContext, PsFarmerListActivity.class);
                            intent.putExtra("viId",villageId);
                            intent.putExtra("viName",villageName);
                            intent.putExtra("vData","");
                            mContext.startActivity(intent);
                        }
                    });


                    cancelImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onMultiRecyclerViewItemClick(2, jsonObject);
                            notifyDataSetChanged();
                        }
                    });


                }else {
                    cancelImageView.setVisibility(View.GONE);
                    sledLLayout.setBackgroundResource(R.drawable.edit_border_bg);
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
