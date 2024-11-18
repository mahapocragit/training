package in.gov.pocra.training.activity.coordinator.attend_image_upload;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.appinventor.services_api.listener.OnMultiRecyclerItemClickListener;
import in.gov.pocra.training.R;

public class AdaptorAttendImage extends RecyclerView.Adapter<AdaptorAttendImage.ViewHolder> {

    private Context mContext;
    private JSONArray mJsonArray;
    private OnMultiRecyclerItemClickListener mListener;
    private String mfileUrl;

    public AdaptorAttendImage(Context context, JSONArray jsonArray, OnMultiRecyclerItemClickListener listener, String fUrl) {
        this.mContext = context;
        this.mJsonArray = jsonArray;
        this.mListener = listener;
        this.mfileUrl = fUrl;

    }


    @Override
    public int getItemCount() {
        if (mJsonArray.length() != 0) {
            return mJsonArray.length();
        } else {
            return 0;
        }
    }


    @NonNull
    @Override
    public AdaptorAttendImage.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_attend_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptorAttendImage.ViewHolder viewHolder, int i) {

        try {
            viewHolder.onBind(mJsonArray.getJSONObject(i), i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
       //  private final LinearLayout imgLLayout;
        private final ImageView attendIView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           //  imgLLayout = (LinearLayout) itemView.findViewById(R.id.imgLLayout);
            attendIView = (ImageView) itemView.findViewById(R.id.attendIView);
        }


        public void onBind(final JSONObject jsonObject, final int i) {

            try {
                String imgName = jsonObject.getString("image_name");
                String imgUrl = mfileUrl + imgName;

                if (!imgUrl.isEmpty()) {

                    Picasso.get()
                            .load(Uri.parse(imgUrl))
                            .into(attendIView);
                }


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onMultiRecyclerViewItemClick(4, jsonObject);

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
