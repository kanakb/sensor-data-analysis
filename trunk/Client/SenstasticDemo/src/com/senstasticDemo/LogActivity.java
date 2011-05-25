package com.senstasticDemo;

import com.senstastic.Logger;
import com.senstasticDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LogActivity extends Activity implements OnClickListener
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_view);
        
        // Set up the back button.
        Button backButton = (Button)findViewById(R.id.logViewBackButton);
        backButton.setOnClickListener(this);
        
        // Set up the text view.
        TextView textView = (TextView)findViewById(R.id.logViewTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(Logger.getLog());
    }
    
    public void onClick(View view) 
    {
        finish();
    }
}
