package org.t15.chrisnewton;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.t15.chrisnewton.webviewtester.R;

public class WebViewTesterActivity extends Activity {
	
	private WebView webview;
	private EditText addressText;
	private ProgressBar loader;
	private String urlToLoad = "https://wallet.hth.world/";

	@TargetApi(19)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.issue_viewer_nav);						
		WebView.setWebContentsDebuggingEnabled(true);
		
		loader = (ProgressBar) findViewById(R.id.loader);
		webview = (WebView) findViewById(R.id.webView1);
		addressText = (EditText) findViewById(R.id.remoteUrlEditText);
		addressText.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
		addressText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				urlToLoad = addressText.getText().toString();
				webview.loadUrl(urlToLoad);	
				// hide virtual keyboard
		        InputMethodManager imm = (InputMethodManager) WebViewTesterActivity.this.getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(addressText.getWindowToken(), 0);
				return true;
			}
		});

		webview.getSettings().setJavaScriptEnabled(true);
		// enables viewport meta tag
		webview.getSettings().setUseWideViewPort(true);
		//webview.getSettings().setBuiltInZoomControls(true);
		//webview.getSettings().setLoadWithOverviewMode(true);
		//webview.setInitialScale(200);
				
		webview.setWebChromeClient(new WebChromeClient(){
			private int mOriginalOrientation;
			private FrameLayout mFullscreenContainer;		
			private CustomViewCallback mCustomViewCallback;
			
			@Override
	    	public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
	    	    //super.onShowCustomView(view, callback);
				//System.out.println("onShowCustomView");
	    	    
	    		mOriginalOrientation = getRequestedOrientation();
	    		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
	    		mFullscreenContainer = new FrameLayout(getBaseContext());
	    		// Add Black Background to video
	    		View bgColour = new View(getBaseContext());
	    		bgColour.setBackgroundColor(0xFF000000);
	    		mFullscreenContainer.addView(bgColour, ViewGroup.LayoutParams.MATCH_PARENT);
	    		mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
	    		decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
	    		//fullScreenVideoView = view;
	    		mCustomViewCallback = callback;
	    		setRequestedOrientation(getRequestedOrientation());
	    	}
			
			@Override
			public void onHideCustomView() {
				//System.out.println("onHideCustomView");
				//super.onHideCustomView();
				
				FrameLayout decor = (FrameLayout) getWindow().getDecorView();
				decor.removeView(mFullscreenContainer);
				mFullscreenContainer = null;
				//fullScreenVideoView = null;
				mCustomViewCallback.onCustomViewHidden();
				// Show the content view
				setRequestedOrientation(mOriginalOrientation);
			}
		});
		
		
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//return super.shouldOverrideUrlLoading(view, url);
				return false;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				
				System.out.println("onPageFinished NOW");
				loader.setVisibility(View.GONE);
				
			}
		});
		
		float scale = getResources().getDisplayMetrics().density;
		float width = getResources().getDisplayMetrics().widthPixels;
		float height = getResources().getDisplayMetrics().heightPixels;
		
		//TextView info = (TextView) findViewById(R.id.infoPanel);
		//info.setText("scale = "+scale+"  width = "+width+"  height = "+height);
		
		
		
		webview.loadUrl(urlToLoad);
		
		
		
		ImageButton reload = (ImageButton) findViewById(R.id.reloadWebviewBut);		
		reload.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	
				urlToLoad = addressText.getText().toString();
				webview.invalidate();
				webview.clearCache(true);
				loader.setVisibility(View.VISIBLE);
				webview.loadUrl(urlToLoad);								
			}
		});
		
		ImageButton launchQr = (ImageButton) findViewById(R.id.qrButton);		
		launchQr.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				IntentIntegrator integrator = new IntentIntegrator( WebViewTesterActivity.this );
				integrator.initiateScan();				
			}
		});
		
		
		
	}	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
		    // handle scan result
			  System.out.println("scan result = ");
			  //System.out.println(scanResult.getContents());
			  urlToLoad = scanResult.getContents();
			  webview.invalidate();
			  webview.clearCache(true);
			  loader.setVisibility(View.VISIBLE);
			  webview.loadUrl(urlToLoad);
			  addressText.setText(urlToLoad);
		  }
		  // else continue with any other code you need in the method
		 
		}
	
}
