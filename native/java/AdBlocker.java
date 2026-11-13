package com.kinox.app;

import android.content.Context;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Motor de bloqueo por dominio (estilo Brave Shields, nivel de red).
 * Lista = dominios integrados + assets/adblock/hosts.txt (descargado al compilar).
 */
final class AdBlocker {

    private AdBlocker() {}

    /** Dominios que NUNCA se bloquean (tu sitio y servicios que necesita). */
    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList(
        "vercel.app", "supabase.co", "supabase.in",
        "themoviedb.org", "tmdb.org", "gstatic.com", "googleapis.com",
        "youtube.com", "youtube-nocookie.com", "ytimg.com", "github.com"
    ));

    /** Respaldo mínimo si no se pudo descargar la lista completa. */
    private static final String[] BUILTIN = {
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adservice.google.com", "pagead2.googlesyndication.com", "adnxs.com",
        "taboola.com", "outbrain.com", "popads.net", "popcash.net", "propellerads.com",
        "propellerclick.com", "exoclick.com", "exosrv.com", "juicyads.com", "trafficjunky.net",
        "adsterra.com", "highperformanceformat.com", "profitablecpmrate.com",
        "profitablegatecpm.com", "clickadu.com", "hilltopads.net", "mgid.com",
        "revcontent.com", "onclickads.net", "onclkds.com", "ad-maven.com", "admaven.com",
        "a-ads.com", "adcash.com", "bidvertiser.com", "yllix.com", "zeroredirect.com",
        "tsyndicate.com", "richpush.co", "pushwhy.com", "monetag.com", "acscdn.com",
        "amazon-adsystem.com", "adform.net", "criteo.com", "rubiconproject.com",
        "pubmatic.com", "openx.net", "smartadserver.com", "scorecardresearch.com",
        "hotjar.com", "mc.yandex.ru", "an.yandex.ru", "adriver.ru"
    };

    private static volatile Set<String> blocked = new HashSet<>(Arrays.asList(BUILTIN));

    static void init(final Context context) {
        final Context app = context.getApplicationContext();
        new Thread(() -> {
            Set<String> set = new HashSet<>(Arrays.asList(BUILTIN));
            try (InputStream in = app.getAssets().open("adblock/hosts.txt");
                 BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    int hash = line.indexOf('#');
                    if (hash >= 0) line = line.substring(0, hash);
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] p = line.split("\\s+");
                    if (p.length >= 2 && (p[0].equals("0.0.0.0") || p[0].equals("127.0.0.1"))) {
                        String d = p[1].toLowerCase(Locale.ROOT);
                        if (d.indexOf('.') > 0 && !d.equals("localhost")) set.add(d);
                    }
                }
            } catch (Exception ignored) {
                // sin lista descargada: se usa solo la integrada
            }
            blocked = set;
        }, "kinox-adblock-init").start();
    }

    /** true si el host (o un dominio padre) está en la lista de bloqueo. */
    static boolean isBlocked(String host) {
        if (host == null || host.isEmpty()) return false;
        String h = host.toLowerCase(Locale.ROOT);

        String c = h;
        while (c.indexOf('.') > 0) {
            if (ALLOWED.contains(c)) return false;
            c = c.substring(c.indexOf('.') + 1);
        }

        Set<String> list = blocked;
        c = h;
        while (c.indexOf('.') > 0) {
            if (list.contains(c)) return true;
            c = c.substring(c.indexOf('.') + 1);
        }
        return false;
    }

    static WebResourceResponse emptyResponse() {
        return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
    }

    /** Filtrado cosmético: oculta contenedores de anuncios habituales en la página principal. */
    static final String COSMETIC_JS =
        "(function(){try{" +
        "if(window.__kinoxCosmetic)return;window.__kinoxCosmetic=true;" +
        "var css='ins.adsbygoogle,.adsbygoogle,[id^=\"google_ads\"],[id^=\"div-gpt-ad\"]," +
        "iframe[src*=\"doubleclick\"],iframe[src*=\"googlesyndication\"],iframe[src*=\"adsterra\"]," +
        "iframe[src*=\"popads\"],a[href*=\"doubleclick.net\"],[class*=\"ad-banner\"]," +
        "[class*=\"adsbox\"],[id*=\"ad-container\"],[class*=\"popunder\"]{display:none!important}';" +
        "var s=document.createElement('style');s.textContent=css;" +
        "(document.head||document.documentElement).appendChild(s);" +
        "}catch(e){}})();";
}
