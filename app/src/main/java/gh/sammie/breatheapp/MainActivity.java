package gh.sammie.breatheapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import java.text.MessageFormat;

import gh.sammie.breatheapp.Utils.Prefs;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private ImageView imageView;
    private TextView breathsTxt, timeTxt, sessionTxt, guideTxt;
    private Button startButton;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init values
        initView();
        prefs = new Prefs(this);
        sessionTxt.setText(MessageFormat.format("{0} min today", prefs.getSessions()));
        breathsTxt.setText(MessageFormat.format("{0} Breaths", prefs.getBreaths()));
        timeTxt.setText(prefs.getDate());


        startIntroAnimation();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation();
            }
        });


    }

    private void initView() {
        imageView = findViewById(R.id.lotusImage);
        breathsTxt = findViewById(R.id.breathsTakenTxt);
        timeTxt = findViewById(R.id.lastBreathTxt);
        sessionTxt = findViewById(R.id.todayMinutesTxt);
        guideTxt = findViewById(R.id.guideTxt);
        startButton = findViewById(R.id.startButton);
    }

    private void startIntroAnimation() {
        ViewAnimator
                .animate(guideTxt)
                .scale(0, 1)
                .duration(1500)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        guideTxt.setText("Start Breathing");
                    }
                })
                .start();

    }

    private void startAnimation() {

        ViewAnimator
                .animate(imageView)
                .alpha(0, 1)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        guideTxt.setText("Inhale... Exhale");
                        startButton.setVisibility(View.GONE);
                    }
                })
                .decelerate()
                .duration(1000)
                .thenAnimate(imageView)
                .scale(0.02f, 1.5f, 0.02f)
                .rotation(360)
                .repeatCount(5)
                .accelerate()
                .duration(5000)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        guideTxt.setText("Good Job");
                        startButton.setText("Start");
                        imageView.setScaleX(1.0f);
                        imageView.setScaleY(1.0f);

                        //since start os 0 we add 1 to kow is second time
                        prefs.setSessions(prefs.getSessions() + 1);
                        prefs.setBreaths(prefs.getBreaths() + 1);
                        prefs.setDate(System.currentTimeMillis());

                        //refresh activity to resume
                        new CountDownTimer(3000, 500) {

                            @Override
                            public void onTick(long l) {
                                startButton.setVisibility(View.VISIBLE);

                                long sec = l / 1000 + 1;
                                Log.d(TAG, "onTick: " + sec);
                               Toast message = Toast.makeText(MainActivity.this, "refreshed", Toast.LENGTH_SHORT);
                               message.setGravity(Gravity.TOP,message.getXOffset(),message.getYOffset());
                               message.show();


                            }

                            @Override
                            public void onFinish() {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }.start();


                    }
                })
                .start();
    }


}
