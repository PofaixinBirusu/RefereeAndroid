package com.referee.johnson.refereeandroid.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.referee.johnson.refereeandroid.R;
import com.referee.johnson.refereeandroid.tcp.Client;
import com.referee.johnson.refereeandroid.utils.RefereeApplication;
import com.referee.johnson.refereeandroid.utils.RefereeUtils;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputIpActivity extends AppCompatActivity {

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_input_ip);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ButterKnife.bind(this);
        client = Client.getInstance();
        init();
    }

    private void init() {
        String ip = RefereeUtils.getString(RefereeApplication.getContext(), "ip", "");
        int port = RefereeUtils.getInt(RefereeApplication.getContext(), "port", -1);
        if (!StringUtils.isBlank(ip) && port > 0) {
            etIp.setText(ip);
            etPort.setText(String.valueOf(port));
        }
    }

    @OnClick(R.id.bt_connection)
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bt_connection:
                final String ip = etIp.getText().toString();
                final String port = etPort.getText().toString();
                pbLoading.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (client.connect(ip, Integer.valueOf(port))) {
                            RefereeUtils.putString(RefereeApplication.getContext(), "ip", ip);
                            RefereeUtils.putInt(RefereeApplication.getContext(), "port", Integer.valueOf(port));
                            Intent intent = new Intent(InputIpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbLoading.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
                break;
        }
    }
}
