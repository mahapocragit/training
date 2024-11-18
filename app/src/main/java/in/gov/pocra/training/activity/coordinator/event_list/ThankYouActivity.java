package in.gov.pocra.training.activity.coordinator.event_list;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import in.gov.pocra.training.R;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);


       /* String imgToShare = getIntent().getStringExtra("imgToShare");
        if (!imgToShare.equalsIgnoreCase("")){
            ApUtil.shareItem(imgToShare,this);
        }*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ThankYouActivity.this, CoEventListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        },1000);


    }

}