package com.example.nemojsoup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.net.URL;

public class askWebView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askwebview);
        WebView webView=(WebView)findViewById(R.id.askweb);


        String address="";
        String homepage="";
        Intent intent=getIntent();
        address=intent.getStringExtra("address");
         homepage=intent.getStringExtra("homepage");

        WebSettings webSettings=webView.getSettings();
        webSettings.setAppCacheEnabled(false);

        if(address ==null) {
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(homepage);
        }
        else
        {
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("http:/.daum.net/?q="+address);
        }




    }
}
