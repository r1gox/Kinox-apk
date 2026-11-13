from PIL import Image, ImageDraw
import os, math

OUT = "./resources/android/res"
C1 = (124, 58, 255)   # violeta
C2 = (214, 41, 118)   # rosa

def gradient(size):
    # degradado diagonal (arriba-izq -> abajo-der)
    img = Image.new("RGB", (size, size))
    px = img.load()
    for y in range(size):
        for x in range(size):
            t = (x + y) / (2 * (size - 1))
            px[x, y] = tuple(int(C1[i] + (C2[i] - C1[i]) * t) for i in range(3))
    return img

def play_layer(size, scale):
    """Triángulo play blanco con esquinas redondeadas, centrado, transparente."""
    S = size * 4
    layer = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    h = S * scale
    w = h * 0.88
    cx, cy = S / 2 + w * 0.06, S / 2          # compensación óptica
    pts = [(cx - w / 2, cy - h / 2), (cx - w / 2, cy + h / 2), (cx + w / 2, cy)]
    d.polygon(pts, fill=(255, 255, 255, 255))
    # redondear: dilatar con círculos en los vértices + aristas gruesas
    r = S * 0.035
    for (x, y) in pts:
        d.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255, 255))
    for i in range(3):
        a, b = pts[i], pts[(i + 1) % 3]
        d.line([a, b], fill=(255, 255, 255, 255), width=int(r * 2))
    return layer.resize((size, size), Image.LANCZOS)

def rounded_mask(size, radius_ratio):
    S = size * 4
    m = Image.new("L", (S, S), 0)
    ImageDraw.Draw(m).rounded_rectangle([0, 0, S - 1, S - 1], radius=int(S * radius_ratio), fill=255)
    return m.resize((size, size), Image.LANCZOS)

def circle_mask(size):
    S = size * 4
    m = Image.new("L", (S, S), 0)
    ImageDraw.Draw(m).ellipse([0, 0, S - 1, S - 1], fill=255)
    return m.resize((size, size), Image.LANCZOS)

def legacy(size, mask_fn):
    bg = gradient(size).convert("RGBA")
    bg.alpha_composite(play_layer(size, 0.40))
    bg.putalpha(mask_fn(size))
    return bg

dens_legacy = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
dens_adapt  = {"mdpi": 108, "hdpi": 162, "xhdpi": 216, "xxhdpi": 324, "xxxhdpi": 432}

for dn, sz in dens_legacy.items():
    d = f"{OUT}/mipmap-{dn}"; os.makedirs(d, exist_ok=True)
    legacy(sz, lambda s: rounded_mask(s, 0.22)).save(f"{d}/ic_launcher.png")
    legacy(sz, circle_mask).save(f"{d}/ic_launcher_round.png")

for dn, sz in dens_adapt.items():
    d = f"{OUT}/mipmap-{dn}"; os.makedirs(d, exist_ok=True)
    # zona segura adaptativa = 66% central; el play ocupa ~30% del lienzo
    play_layer(sz, 0.30).save(f"{d}/ic_launcher_foreground.png")
    gradient(sz).save(f"{d}/kinox_bg.png")

d = f"{OUT}/mipmap-anydpi-v26"; os.makedirs(d, exist_ok=True)
xml = '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@mipmap/kinox_bg"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
'''
for n in ("ic_launcher.xml", "ic_launcher_round.xml"):
    open(f"{d}/{n}", "w").write(xml)

# Splash: fondo oscuro + logo centrado (sin deformarse en ninguna pantalla)
dn = f"{OUT}/drawable-nodpi"; os.makedirs(dn, exist_ok=True)
legacy(288, lambda s: rounded_mask(s, 0.22)).save(f"{dn}/kinox_splash_logo.png")
os.makedirs(f"{OUT}/drawable", exist_ok=True)
open(f"{OUT}/drawable/splash.xml", "w").write('''<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#0A0611"/>
        </shape>
    </item>
    <item>
        <bitmap android:src="@drawable/kinox_splash_logo" android:gravity="center"/>
    </item>
</layer-list>
''')

# Ícono maestro 512 (Play Store / repo)
legacy(512, lambda s: rounded_mask(s, 0.22)).save("./assets/icon-512.png")
print("ok")
