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
import in.gov.pocra.training.R;
import in.gov.pocra.training.event_db.EventDataBase;

class AdaptorDesignationList  extends RecyclerView.Adapter<AdaptorDesignationList.ViewHolder> {

    EventDataBase eDB;
    private Context mContext;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;
    private int count = 0;

    public AdaptorDesignationList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener) {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_gp, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
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
                final String itemId = jsonObject.getString("gp_id");
                final String itemName = jsonObject.getString("gp_name");
                gpTitleTView.setText(title);

                /* For Already Selected VCRMC(GP)*/
                /*String selectedGP = AppSettings.getInstance().getValue(mContext, ApConstants.kS_GP_ARRAY, ApConstants.kS_GP_ARRAY);
                if (!selectedGP.equalsIgnoreCase("kS_GP_ARRAY")) {
                    JSONArray selectedGPArray = new JSONArray(selectedGP);
                    if (isGpIdSelected(selectedGPArray,itemId)){

                    }
                }*/


               /* if (jsonObject.getInt("gp_is_selected") == 1) {
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
//to do
                        /****  This new code without restriction of GP selection*/
                  /*      Intent intent = new Intent(mContext, GPMembersListActivity.class);
                        intent.putExtra("gpId", itemId);
                        intent.putExtra("gpName", itemName);
                        mContext.startActivity(intent);
*/



                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }
}
