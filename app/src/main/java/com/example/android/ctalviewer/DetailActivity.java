package com.example.android.ctalviewer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.ctalviewer.data.CTAViewerPreferences;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView textView = (TextView)findViewById(R.id.tv_train_detail);

        Intent intent = getIntent();

        String text = intent.getStringExtra(CTAViewerPreferences.KEY_TEXT);

        textView.setText(text);
    }
}
