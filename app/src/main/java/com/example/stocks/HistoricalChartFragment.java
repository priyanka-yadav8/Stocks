package com.example.stocks;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class HistoricalChartFragment extends Fragment {

   private WebView webView;
   String ticker;

    public HistoricalChartFragment(String ticker) {
        // Required empty public constructor
        this.ticker=ticker;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historical_chart, container, false);
        webView = (WebView) view.findViewById(R.id.historicalChartWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/HistoricalChart.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.evaluateJavascript("javascript:loadChartData('" + ticker + "')", null);
            }
        });


        WebSettings objWeb = webView.getSettings();
        objWeb.setAllowUniversalAccessFromFileURLs(true);
        return view;
    }
}