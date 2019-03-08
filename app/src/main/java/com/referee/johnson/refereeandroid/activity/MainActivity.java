package com.referee.johnson.refereeandroid.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.referee.johnson.refereeandroid.R;
import com.referee.johnson.refereeandroid.interaction.ClientInteraction;
import com.referee.johnson.refereeandroid.tcp.Client;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_show_score)
    TextView tvShowScore;

    private Client client;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ButterKnife.bind(this);
        client = Client.getInstance();
        client.setClientInteraction(new ClientInteraction() {
            @Override
            public void toDo(String data) {
                if (data.equals(new String("test start"))) {
                    isStart = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShowScore.setText(R.string.max_score);
                        }
                    });
                } else if (data.equals(new String("test end"))) {
                    isStart = false;
                }
            }
        });
    }

    @OnTouch({R.id.ib_start, R.id.ib_deduct_1, R.id.ib_deduct_2, R.id.ib_deduct_3, R.id.ib_deduct_5})
    public boolean onViewTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ImageButton ibDown = (ImageButton) view;
                ibDown.setImageDrawable(getResources().getDrawable(R.drawable.end));
                break;
            case MotionEvent.ACTION_UP:
                ImageButton ibUp = (ImageButton) view;
                ibUp.setImageDrawable(getResources().getDrawable(R.drawable.start));
                break;
        }
        return false;
    }

    @OnClick({R.id.ib_start, R.id.ib_deduct_1, R.id.ib_deduct_2, R.id.ib_deduct_3, R.id.ib_deduct_5})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ib_start:
                if (client.isConnection() && !isStart) {
                    client.send("ready");
                } else  if (!client.isConnection()) {
                    Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case R.id.ib_deduct_1:
                deductScore(1);
                break;
            case R.id.ib_deduct_2:
                deductScore(2);
                break;
            case R.id.ib_deduct_3:
                deductScore(3);
                break;
            case R.id.ib_deduct_5:
                deductScore(5);
                break;
        }
    }

    private void deductScore(int score) {
        if (client.isConnection() && isStart) {
            tvShowScore.setText(
                    String.format("%03d", Integer.valueOf(tvShowScore.getText().toString()) - score));
            client.send("deduction:" + score);
        } else if (!client.isConnection()) {
            Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
