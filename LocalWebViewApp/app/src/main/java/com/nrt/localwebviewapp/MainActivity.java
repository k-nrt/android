package com.nrt.localwebviewapp;

import android.app.*;
import android.os.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		WebView  myWebView = (WebView)findViewById(R.id.MainWebView);

        //リンクをタップしたときに標準ブラウザを起動させない
        myWebView.setWebViewClient(new WebViewClient());

        //最初にgoogleのページを表示する。
        //myWebView.loadUrl("file:///storage/emulated/0/webmsx/webmsx/index.html");
        myWebView.loadUrl("file:///android_asset/index.html");
		
        //jacascriptを許可する
        myWebView.getSettings().setJavaScriptEnabled(true);
    }
}
