package com.spate.in.activities;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.spate.in.R;
import com.spate.in.utils.Gen;
import com.skyfishjy.library.RippleBackground;

public class RingingActivity extends AppCompatActivity implements View.OnClickListener {

    private Ringtone ringtone;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        wakeLock.acquire();

        ImageButton acceptCallButton = (ImageButton) findViewById(R.id.accept);
        ImageButton rejectCallButton = (ImageButton) findViewById(R.id.decline);

        acceptCallButton.setOnClickListener(this);
        rejectCallButton.setOnClickListener(this);

        ImageView imageView = (ImageView) findViewById(R.id.ringing_image_dp);
        String gender = getIntent().getStringExtra("gender");

        if (gender.equals("male"))
            imageView.setImageResource(R.mipmap.male);
        else
            imageView.setImageResource(R.mipmap.female);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.ringing_image_background);
        rippleBackground.startRippleAnimation();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ringtone.play();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        // Skip pressed back button
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ringtone.stop();
        wakeLock.release();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.accept){
            Bundle bundle = getIntent().getExtras();

            Intent intent = new Intent(this, CallActivity.class);
            intent.putExtra(CallStatusActivity.CHAT_END_TIME_KEY, bundle.getString(CallStatusActivity.CHAT_END_TIME_KEY));
            intent.putExtra(CallStatusActivity.CURRENT_TIME, bundle.getString(CallStatusActivity.CURRENT_TIME));
            intent.putExtra(CallStatusActivity.GENDER, bundle.getString(CallStatusActivity.GENDER));
            Gen.startActivity(intent, true);

        }else if (v.getId() == R.id.decline){
            finish();
        }
    }
}
