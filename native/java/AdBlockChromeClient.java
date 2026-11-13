package com.kinox.app;

import android.os.Message;
import android.webkit.WebView;

import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeWebChromeClient;

/** Rechaza todas las ventanas emergentes que abren las páginas (típico de anuncios). */
public class AdBlockChromeClient extends BridgeWebChromeClient {

    public AdBlockChromeClient(Bridge bridge) {
        super(bridge);
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false; // no se crea la ventana
    }
}
