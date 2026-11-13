# Kinox APK

App Android (Capacitor) que abre tu sitio desplegado en Vercel, con ícono propio y
bloqueo de anuncios nativo.

## Compilar sin instalar nada (GitHub Actions)
1. Sube TODO el contenido de esta carpeta a tu repo (incluidas `.github`, `native` y `resources`).
2. Pestaña **Actions → Build Kinox APK → Run workflow**.
3. Al terminar (~5 min), descarga el artefacto **Kinox-Android-APK** → `Kinox.apk`.
4. Instálalo en el móvil (permite "orígenes desconocidos").

## Qué incluye
- **Nombre:** Kinox (`com.kinox.app`).
- **Ícono:** `resources/android/res/` (adaptativo + redondo + pantalla de arranque).
  El maestro está en `assets/icon-512.png`. Se genera con `make_icons.py`.
- **Bloqueo de anuncios:** `native/java/`
  - Bloquea peticiones a dominios de publicidad/rastreo (scripts, iframes, imágenes).
  - Cancela redirecciones hacia dominios de anuncios.
  - Rechaza ventanas emergentes (pop-ups).
  - Oculta contenedores de anuncios comunes en la página principal.
  - La lista de dominios se descarga al compilar (StevenBlack/hosts) y hay una lista mínima integrada de respaldo.

## Límites del bloqueador
- No es el Brave Browser: usa el mismo enfoque (filtrar en la capa de red del WebView), pero no su motor `adblock-rust`.
- No puede ver dentro de iframes de otros dominios (reproductores), así que no oculta anuncios que el propio reproductor dibuja con su JS. Sí bloquea sus peticiones a redes publicitarias.
- Si algo del sitio deja de funcionar, añade su dominio a `ALLOWED` en `native/java/AdBlocker.java`.

## Importante
- La URL de tu sitio está en `capacitor.config.json` (`server.url`).
- El sitio debe estar desplegado (usa el proxy `/api/tmdb` y Supabase).
