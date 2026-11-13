# MovieZone APK

Envoltorio Android (Capacitor) que abre tu sitio desplegado en Vercel.

## Compilar sin instalar nada (GitHub Actions)
1. Crea un repo en GitHub y sube TODO el contenido de esta carpeta (incluida `.github`).
2. Pestaña **Actions → Build APK → Run workflow**.
3. Al terminar (~5 min), descarga el artefacto **MovieZone-apk** → `app-debug.apk`.
4. Instálalo en el móvil (permite "orígenes desconocidos").

## Compilar en tu PC (Node 20 + JDK 17 + Android SDK)
```
npm install
npx cap add android
npx cap sync android
cd android && ./gradlew assembleDebug
```
APK en `android/app/build/outputs/apk/debug/app-debug.apk`.

## Importante
- La URL de tu sitio está en `capacitor.config.json` (`server.url`). Cámbiala por tu dominio real de Vercel.
- Necesita el sitio desplegado, porque usa el proxy `/api/tmdb` y Supabase.
