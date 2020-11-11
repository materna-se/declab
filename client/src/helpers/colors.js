export default {
    pleasingRandomColor() {
        const golden_ratio_conjugate = 0.618033988749895;
        let r = Math.random();

        r += golden_ratio_conjugate;
        r %= 1;
        return this.HSVtoRGBtoHEX(r, 0.5, 0.95);
    },

    HSVtoRGBtoHEX(h, s, v) {
        var r, g, b, i, f, p, q, t;
        if (arguments.length === 1) {
            s = h.s, v = h.v, h = h.h;
        }
        i = Math.floor(h * 6);
        f = h * 6 - i;
        p = v * (1 - s);
        q = v * (1 - f * s);
        t = v * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0: r = v, g = t, b = p; break;
            case 1: r = q, g = v, b = p; break;
            case 2: r = p, g = v, b = t; break;
            case 3: r = p, g = q, b = v; break;
            case 4: r = t, g = p, b = v; break;
            case 5: r = v, g = p, b = q; break;
        }

        r = Math.round(r * 255);
        g = Math.round(g * 255);
        b = Math.round(b * 255);

        return this.RGBtoHEX(r, g, b);
    },

    RGBtoHEX(r, g, b) {
        var rH = r.toString(16);
        rH = (rH.length == 1 ? "0" + rH : rH);

        var gH = g.toString(16);
        gH = (gH.length == 1 ? "0" + gH : gH);

        var bH = b.toString(16);
        bH = (bH.length == 1 ? "0" + bH : bH);

        return "#" + rH + gH + bH;
    }
}