package lk.futureshipping.staff;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * FCS native shell: loads ONLY your HTTPS panel (login + features).
 * Not a browser shortcut — own app ID, full-screen, locked to your domain.
 */
public class MainActivity extends AppCompatActivity {
    private static final int REQ_LOCATION = 42;
    private WebView webView;
    private ProgressBar progress;
    private String allowedHost;
    private String startUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String base = getString(R.string.server_base).replaceAll("/$", "");
        String path = getString(R.string.start_path);
        if (!path.startsWith("/")) path = "/" + path;
        startUrl = base + path;
        try {
            allowedHost = android.net.Uri.parse(base).getHost();
        } catch (Exception e) {
            allowedHost = "";
        }

        if (base.contains("YOUR-DOMAIN")) {
            Toast.makeText(this, "Set server_base in res/values/strings.xml to your HTTPS domain", Toast.LENGTH_LONG).show();
        }

        progress = findViewById(R.id.progress);
        webView = findViewById(R.id.webview);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setGeolocationEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        // Security: no file access from web content
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String host = request.getUrl().getHost();
                // Stay inside your domain only (open app, not random sites)
                if (host != null && allowedHost != null && host.equalsIgnoreCase(allowedHost)) {
                    return false;
                }
                // Block navigation away from your system
                Toast.makeText(MainActivity.this, "External links blocked in app", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                ensureLocationPermission();
                // Only grant geolocation to your own origin
                boolean ok = allowedHost != null && origin != null && origin.contains(allowedHost);
                callback.invoke(origin, ok, false);
            }
        });

        ensureLocationPermission();
        webView.loadUrl(startUrl);
    }

    private void ensureLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
