package com.xianrui.hencoder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TrimmingView mTrimmingView;
    TextView mValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTrimmingView = (TrimmingView) findViewById(R.id.trimming_view);
        mValueTextView = (TextView) findViewById(R.id.value_text);
        mTrimmingView.setValueRange(100);
        mTrimmingView.setEnableLoop(true);
        mTrimmingView.setOnValueChangeListener(new TrimmingView.OnValueChangeListener() {
            @Override
            public void onScroll(float distance) {

            }

            @Override
            public void onProgressChanged(TrimmingView trimmingView, float progress, boolean fromUser) {
                mValueTextView.setText(String.valueOf((int) progress / 10f));
            }

            @Override
            public void onStartTrackingTouch(TrimmingView trimmingView) {

            }

            @Override
            public void onStopTrackingTouch(TrimmingView trimmingView) {
                mValueTextView.setText(String.valueOf((int) trimmingView.getValue() / 10f));
            }
        });

        mTrimmingView.setTickTextAdapter(new TrimmingView.TickTextAdapter() {
            @Override
            public String getTickText(int index) {
                return String.valueOf(index / mTrimmingView.getHighlight_interval());
            }
        });
    }
}
