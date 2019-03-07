package deo.com.signaldemo;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IntroActivity extends AppCompatActivity {
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // delete title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        handler = new Handler();
        // 3sec later move to another activity
        handler.postDelayed(mrun, 3000);
    }

    // animation effect
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        ImageView imgView = (ImageView) findViewById(R.id.iv_anim);
        imgView.setVisibility(ImageView.VISIBLE);
        imgView.setBackgroundResource(R.drawable.anim_list);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgView.getBackground();
        frameAnimation.start();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
        imgView.startAnimation(animation);

        super.onWindowFocusChanged(hasFocus);
    }

    Runnable mrun = new Runnable() {
        @Override
        public void run() {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            startActivity(new Intent(IntroActivity.this, SigninActivity.class));
//            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(mrun);
    }
}
