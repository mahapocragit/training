/*
 * Copyright (c) 2019. Runtime Solutions Pvt Ltd. All right reserved.
 * Web URL  http://runtime-solutions.com
 * Author Name: Vinod Vishwakarma
 * Linked In: https://www.linkedin.com/in/vvishwakarma
 * Official Email ID : vinod@runtime-solutions.com
 * Email ID: vish.vino@gmail.com
 * Last Modified : 25/2/19 2:34 PM
 */

package in.gov.pocra.training.activity.common.additionalCharge.adapter;

import android.content.Context;
import android.util.Log;
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
import in.gov.pocra.training.R;
import in.gov.pocra.training.model.online.ProfileModel;

public class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder> {


    private OnMultiRecyclerItemClickListener listener;
    private Context mContext;
    private JSONArray mDataArray;

    public RoleListAdapter(Context mContext, OnMultiRecyclerItemClickListener listener, JSONArray jsonArray) {
        this.mContext = mContext;
        this.listener = listener;
        this.mDataArray = jsonArray;
    }


    @Override
    public int getItemCount() {
        if (mDataArray != null) {
            return mDataArray.length();
        } else {
            return 0;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View base = LayoutInflater.from(mContext).inflate(R.layout.recycler_role_list, parent, false);
        return new ViewHolder(base);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.onBind(mDataArray.getJSONObject(position), listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView statusImageView;
        private TextView nameTextView;
        private LinearLayout mainLinearLayout;
        private androidx.cardview.widget.CardView mainLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            statusImageView = itemView.findViewById(R.id.statusImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            mainLinearLayout = itemView.findViewById(R.id.mainLinearLayout);
           // mainLayout = itemView.findViewById(R.id.mainLayout);


        }

        private void onBind(final JSONObject jsonObject, final OnMultiRecyclerItemClickListener listener) {
            Log.d("nfdgfgfdfdfdf", jsonObject.toString());
            ProfileModel model = new ProfileModel(jsonObject);


            String name = model.getDesignation();
            String role_Id = String.valueOf(model.getRole_id());
            Log.d("printdata", name +""+role_Id);
            nameTextView.setText(name);
//            if (role_Id == "15" || role_Id == "14" || role_Id == "pmu" || role_Id == "8" || role_Id == "Global") {
//
////                mainLinearLayout.setVisibility(View.VISIBLE);
////                nameTextView.setVisibility(View.VISIBLE);
////                statusImageView.setVisibility(View.VISIBLE);
//
//            } else {
//                mainLinearLayout.setVisibility(View.GONE);
//            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMultiRecyclerViewItemClick(1, jsonObject);
                }
            });

        }

    }


}
