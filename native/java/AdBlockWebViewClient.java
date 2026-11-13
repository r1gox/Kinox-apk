package com.kinox.app;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeWebViewClient;

/**
 * WebViewClient de Capacitor + filtro de anuncios.
 * Cancela cualquier petición (scripts, iframes, imágenes, XHR) hacia dominios de
 * publicidad/rastreo y las navegaciones hacia ellos (redirecciones de anuncios).
 */
public class AdBlockWebViewClient extends BridgeWebViewClient {

    public AdBlockWebViewClient(Bridge bridge) {
        super(bridge);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        if (uri != null && AdBlocker.isBlocked(uri.getHost())) {
            return AdBlocker.emptyResponse();
        }
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        if (uri != null && AdBlocker.isBlocked(uri.getHost())) {
            return true; // cancela la navegación
        }
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        view.evaluateJavascript(AdBlocker.COSMETIC_JS, null);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.evaluateJavascript(AdBlocker.COSMETIC_JS, null);
    }
}
