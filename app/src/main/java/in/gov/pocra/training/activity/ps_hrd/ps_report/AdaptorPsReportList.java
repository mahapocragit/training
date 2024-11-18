package in.gov.pocra.training.activity.ps_hrd.ps_report;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.gov.pocra.training.R;
import in.gov.pocra.training.model.offline.OffPsReportListModel;


public class AdaptorPsReportList extends RecyclerView.Adapter<AdaptorPsReportList.ViewHolder> {

    ArrayList<OffPsReportListModel> mValues;
    Context mContext;
    RecyclerViewClickListener mListener;

    public AdaptorPsReportList(PsReportListActivity context, ArrayList<OffPsReportListModel> list, AdaptorPsReportList.RecyclerViewClickListener itemListener) {
        mValues = list;
        mContext = context;
        mListener = itemListener;
    }

    public interface RecyclerViewClickListener extends ViewHolder.RecyclerViewClickListener {
       void onClick(View view,int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName;
        OffPsReportListModel item;
        private RecyclerViewClickListener mListener;

        public ViewHolder(@NonNull View v,RecyclerViewClickListener listener) {
            super(v);
            v.setOnClickListener(this);
            tvName = (TextView)v.findViewById(R.id.report_ps_list);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null){
                mListener.onClick(view,getAdapterPosition());
            }
        }

        public interface RecyclerViewClickListener {
            void onClick(View view, int position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(mContext).inflate(R.layout.list_ps_report_list,parent,false);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder Vholder, int position) {
        OffPsReportListModel modal = mValues.get(position);
        Vholder.tvName.setText(modal.getName());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }




}
