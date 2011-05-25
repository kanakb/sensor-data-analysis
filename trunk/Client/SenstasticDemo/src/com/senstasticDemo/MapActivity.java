package com.senstasticDemo;

import com.senstasticDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class MapActivity extends Activity implements OnClickListener
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        // Get the URL passed in through the intent.
        Bundle extras = getIntent().getExtras(); 
        if(extras == null)
        	return;        
        String url = extras.getString("url");

        // Set up the web view.
        WebView webView = (WebView)findViewById(R.id.mapViewWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        
        // Set up the back button.
        Button next = (Button)findViewById(R.id.mapViewBackButton);
        next.setOnClickListener(this);
    }
    
    public void onClick(View view) 
    {
        finish();
    }
}
