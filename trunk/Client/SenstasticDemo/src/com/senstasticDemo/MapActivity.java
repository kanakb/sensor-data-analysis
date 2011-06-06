package com.senstasticDemo;

import com.senstasticDemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MapActivity extends Activity implements OnClickListener
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        // Get the URLs passed in through the intent.
        Bundle extras = getIntent().getExtras(); 
        if(extras == null)
        	return;  
        final String dataRefreshUrl = extras.getString("dataRefreshUrl");
        final String mapUrl = extras.getString("mapUrl");

        // Set up the web view.
        WebView webView = (WebView)findViewById(R.id.mapViewWebView);
        
        // Enable JavaScript.
        webView.getSettings().setJavaScriptEnabled(true);
        
        // Do not cache pages.
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        // Remove the scroll bars.
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);   
        
        // Set up the back button.
        Button backButton = (Button)findViewById(R.id.mapViewBackButton);
        backButton.setOnClickListener(this);
        
        // If there is a data refresh URL, refresh the data for the map by visiting the URL.
        if (dataRefreshUrl != null)
        {
        	// Refresh the data first.
        	webView.loadUrl(dataRefreshUrl);
        
        	// When the refreshing is done, load the map.
	        webView.setWebViewClient(new WebViewClient() 
	        {
	        	public void onPageFinished(WebView webView, String url)
	        	{
	                webView.loadUrl(mapUrl);
	        	}
	        });
        }
        // Otherwise, just load the map.
        else
        {
        	webView.loadUrl(mapUrl);
        }
    }
    
    public void onClick(View view) 
    {
        finish();
    }
}
