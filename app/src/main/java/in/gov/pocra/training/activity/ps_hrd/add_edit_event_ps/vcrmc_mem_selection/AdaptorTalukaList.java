package in.gov.pocra.training.activity.ps_hrd.add_edit_event_ps.vcrmc_mem_selection;

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

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.co.appinventor.services_api.widget.UIToastMessage;
import in.gov.pocra.training.R;


public class AdaptorTalukaList extends RecyclerView.Adapter<AdaptorTalukaList.ViewHolder> {

    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorTalukaList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public AdaptorTalukaList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_gp, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorTalukaList.ViewHolder viewHolder, int i) {

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

                gpTitleTView.setText(title);

                /*if (jsonObject.getInt("is_selected") == 1) {
                    // selectedImageView.setVisibility(View.VISIBLE);
                    selectedImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.check));
                    selectedImageView.setColorFilter(mContext.getResources().getColor(R.color.bg_green));
                    gpLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected_item_color));
                    count++;
                } else {
                    // selectedImageView.setVisibility(View.INVISIBLE);
                    selectedImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.right_arrow));
                    selectedImageView.setColorFilter(mContext.getResources().getColor(R.color.bg_blue));
                    gpLLayout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_white));
                    if (count != 0) {
                        count--;
                    }
                }*/


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mListener.onMultiRecyclerViewItemClick(1, jsonObject);

                       /* String eventMemType = AppSettings.getInstance().getValue(mContext, ApConstants.kEVENT_MEM_TYPE,ApConstants.kEVENT_MEM_TYPE);

                        if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase("vcrmc")){
                            Intent intent = new Intent(mContext, GPListActivity.class);
                            intent.putExtra("talukaId",talukaId);
                            intent.putExtra("talukaName",talukaName);
                            mContext.startActivity(intent);
                        }else if (!eventMemType.equalsIgnoreCase("") && eventMemType.equalsIgnoreCase("farmer")){
                            Intent intent = new Intent(mContext, VillageListActivity.class);
                            intent.putExtra("talukaId",talukaId);
                            intent.putExtra("talukaName",talukaName);
                            mContext.startActivity(intent);
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




        private void updateSelection(int position,JSONArray selectedGPArray) {
            try {
                JSONObject jsonObject = mJSONArray.getJSONObject(position);
                int isSelected = jsonObject.getInt("is_selected");

                if (selectedGPArray.length() >= 5 && isSelected == 0) {
                    // UIAlertView.getOurInstance().show(mContext,"Only 4 can be selected");
                    UIToastMessage.show(mContext, "Only 5 can be selected, \nto select new remove selected VCRMC(GP)");
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
