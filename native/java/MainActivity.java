package com.kinox.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carga las listas de bloqueo en segundo plano
        AdBlocker.init(this);

        WebView webView = getBridge().getWebView();

        // Bloqueo de peticiones (anuncios, rastreadores) y redirecciones
        webView.setWebViewClient(new AdBlockWebViewClient(getBridge()));

        // Bloqueo de ventanas emergentes (pop-ups / pop-unders)
        webView.setWebChromeClient(new AdBlockChromeClient(getBridge()));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(true); // necesario para que onCreateWindow nos avise y lo rechacemos
    }
}
