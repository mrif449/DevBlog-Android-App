package com.example.devblog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout; // For pull-to-refresh
    private long lastBackPressTime = 0; // For double-tap to exit

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set status bar to black
        makeStatusBarBlack();

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload the WebView when user pulls down
            webView.reload();
        });

        // Initialize WebView
        webView = findViewById(R.id.webViewID);
        WebSettings webSettings = webView.getSettings();

        // Enable necessary settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Enable DOM storage
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Enable caching

        // Set clients
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Disable the refresh indicator once the page finishes loading
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Handle errors
                view.loadUrl("about:blank");
                // You can show a custom error page here
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        webView.setWebChromeClient(new WebChromeClient()); // For better JS support

        // Enable debugging (optional)
        WebView.setWebContentsDebuggingEnabled(true);

        // Load the URL
        webView.loadUrl("https://fardins-diary.vercel.app/");
    }

    // Handle back button press
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            // If WebView can go back, go back
            webView.goBack();
        } else {
            // Show a toast message before exiting
            if (System.currentTimeMillis() - lastBackPressTime < 2000) { // 2 seconds
                super.onBackPressed(); // Exit the app
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                lastBackPressTime = System.currentTimeMillis();
            }
        }
    }

    // Make the status bar black
    private void makeStatusBarBlack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(15, 23, 42)); // Set status bar color to black
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}

