package com.gbbtbb.lightscontrol;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	
	static final String PREFS_NAME = "com.gbbtbb.lightscontrol";
	final Activity activity = this;
	WebView webView;
	float zoomLevel;
	 
    @SuppressLint("SetJavaScriptEnabled") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);     
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        zoomLevel = settings.getFloat("zoomLevel", 1.0f);
        Log.v("lightscontrol", "Restoring webview scale=" + zoomLevel);
        webView.setInitialScale((int)(100*zoomLevel));
        
  
        // Manage progress bar
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);
  
                if(progress == 100)
                    activity.setTitle(R.string.app_name);
            }
        });
  
        // Manage in-app web links handling
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                // Handle the error
            }
  
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
            	zoomLevel = newScale;
            	super.onScaleChanged(view, oldScale, newScale);
            }
        });  
        
        // Load the main page
        webView.loadUrl("http://192.168.0.13:8083/expert");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.menu_zoomin:
    		webView.zoomIn();
    		return super.onOptionsItemSelected(item);
    	case R.id.menu_zoomout:
    		webView.zoomOut();
    		return super.onOptionsItemSelected(item);
    	case android.R.id.home:
    		onBackPressed();
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
	@Override
	protected void onStop() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat("zoomLevel",zoomLevel);
		Log.v("lightscontrol", "Saving webview scale=" + zoomLevel);
		editor.commit();
		
		super.onStop();
	}       
}
