package in.gov.pocra.training.activity.common.coming_closed_dist_list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.app_util.AppUtility;
import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;


public class AdaptorComingSubdivisionWithDistrictList extends RecyclerView.Adapter<AdaptorComingSubdivisionWithDistrictList.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    public JSONArray mJSONArray;
    private OnMultiRecyclerItemClickListener mListener;

    public AdaptorComingSubdivisionWithDistrictList(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
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
    public AdaptorComingSubdivisionWithDistrictList.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_coming_subdivision_district, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdaptorComingSubdivisionWithDistrictList.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJSONArray.getJSONObject(i), i, mListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView distTView;
        private final RecyclerView districtRView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            distTView = (TextView) itemView.findViewById(R.id.distTView);
            districtRView = (RecyclerView) itemView.findViewById(R.id.districtRView);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,RecyclerView.VERTICAL,false);
            districtRView.setLayoutManager(linearLayoutManager);
        }


        private void onBind(final JSONObject jsonObject, final int position, OnMultiRecyclerItemClickListener listener) {

            String districtName = AppUtility.getInstance().sanitizeJSONObj(jsonObject, "district").trim();
            distTView.setText(districtName);
            JSONArray subDivArray = AppUtility.getInstance().sanitizeArrayJSONObj(jsonObject, "subdivision");
            AdaptorComingSubdivisionByDistrictList adaptorComingSubdivisionByDistrictList = new AdaptorComingSubdivisionByDistrictList(mContext, subDivArray, listener);
            districtRView.setAdapter(adaptorComingSubdivisionByDistrictList);

        }

    }

}
