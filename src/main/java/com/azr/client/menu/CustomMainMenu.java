package com.azr.client.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4667;
import net.minecraft.class_500;
import net.minecraft.class_526;
import net.minecraft.class_5250;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomMainMenu extends class_437 {

    private final int buttonWidth  = 120;
    private final int buttonHeight = 30;
    private final int buttonSpacing = 8;
    private final int buttonsYOffset = 60;

    private final long startTime;
    private volatile ArrayList<String> changelogEntries;

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static volatile ArrayList<String> commitBuffer = new ArrayList<>();
    private static volatile long commitBufferLife = 0L;
    private static final long commitBufferTTL = 5 * 60 * 1000L;

    public CustomMainMenu() {
        super((class_5250) class_2561.method_43470("Azr Client Menu"));
        startTime = System.currentTimeMillis();
    }

    // ── init ─────────────────────────────────────────────────────────────────

    @Override
    protected void method_25426() {
        long now = System.currentTimeMillis();

        if (!commitBuffer.isEmpty() && now - commitBufferLife < commitBufferTTL) {
            changelogEntries = new ArrayList<>(commitBuffer);
            super.method_25426();
            return;
        }

        changelogEntries = new ArrayList<>();
        changelogEntries.add("loading...");

        new Thread(() -> {
            try {
                ArrayList<String> entries = fetchLatestCommitMessages("x0lumie", "Simp", 4);
                commitBuffer = new ArrayList<>(entries);
                commitBufferLife = System.currentTimeMillis();
                changelogEntries = entries;          // volatile write — safe
            } catch (IOException e) {
                ArrayList<String> fallback = new ArrayList<>();
                fallback.add("HTTP_403".equals(e.getMessage()) ? "403: ratelimited" : "failed to load");
                changelogEntries = fallback;
            } catch (Exception e) {
                ArrayList<String> fallback = new ArrayList<>();
                fallback.add("unexpected error");
                changelogEntries = fallback;
            }
        }).start();

        super.method_25426();
    }

    // ── render ────────────────────────────────────────────────────────────────

    @Override
    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        int w = this.field_22789;
        int h = this.field_22790;
        class_327 tr = this.field_22793;

        // Arka plan: koyu gradyan
        context.method_25294(0, 0, w, h / 2, argb(15, 15, 25, 255));
        context.method_25294(0, h / 2, w, h,  argb(8,  8,  15, 255));

        // ── Saat ──────────────────────────────────────────────────────────────
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        String currentTime = fmt.format(new Date());

        float heightScale     = h / 480.0f;
        int   scaledTimeYOff  = (int)(-210 * Math.min(heightScale, 1.2f));
        int   timeStrWidth    = tr.method_1727(currentTime) * 2;
        int   timeX           = (w - timeStrWidth) / 2;
        int   timeY           = h / 2 + scaledTimeYOff;
        drawScaled(context, tr, currentTime, timeX, timeY, 0xFFFFFFFF);

        // ── Logo ─────────────────────────────────────────────────────────────
        long  t       = System.currentTimeMillis() - startTime;
        float logoHue = (t % 3000) / 3000.0f;
        String logo   = "AZR CLIENT";
        int logoW     = tr.method_1727(logo) * 2;
        drawScaled(context, tr, logo,
                w / 2 - logoW / 2, (int)(h / 10f),
                Color.getHSBColor(logoHue, 0.6f, 1.0f).getRGB() | 0xFF000000);

        // ── Butonlar / Changelog ──────────────────────────────────────────────
        drawCustomButtons(context, tr, mouseX, mouseY);
        drawChangelog(context, tr);

        super.method_25394(context, mouseX, mouseY, delta);
    }

    // ── Butonlar ──────────────────────────────────────────────────────────────

    private void drawCustomButtons(class_332 ctx, class_327 tr, int mouseX, int mouseY) {
        int cx   = this.field_22789 / 2;
        int sy   = this.field_22790 / 2 + buttonsYOffset;
        int tot  = (buttonWidth * 3) + (buttonSpacing * 2);
        int sx   = cx - tot / 2;

        drawButton(ctx, tr, sx,                                  sy,                           buttonWidth, buttonHeight, "singleplayer", mouseX, mouseY);
        drawButton(ctx, tr, sx + buttonWidth + buttonSpacing,    sy,                           buttonWidth, buttonHeight, "multiplayer",  mouseX, mouseY);
        drawButton(ctx, tr, sx + (buttonWidth+buttonSpacing)*2,  sy,                           buttonWidth, buttonHeight, "alts",         mouseX, mouseY);

        drawButton(ctx, tr, sx + (buttonWidth+buttonSpacing)*2,  sy+(buttonHeight+buttonSpacing), buttonWidth, buttonHeight, "change bg", mouseX, mouseY);
        drawButton(ctx, tr, sx,                                  sy+(buttonHeight+buttonSpacing), buttonWidth, buttonHeight, "options",    mouseX, mouseY);
        drawButton(ctx, tr, sx + (buttonWidth+buttonSpacing),    sy+(buttonHeight+buttonSpacing), buttonWidth, buttonHeight, "quit",       mouseX, mouseY);
    }

    private void drawButton(class_332 ctx, class_327 tr,
                            int x, int y, int width, int height,
                            String text, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height;

        long  time    = System.currentTimeMillis() - startTime;
        float hue     = ((time + x*100L + y*600L) % 3000L) / 3000f;

        Color bgColor = hovered ? new Color(60, 60, 60, 200) : new Color(40, 40, 40, 180);

        // Gökkuşağı üst çizgi (orijinaldeki drawRect x, y-1, width, 1)
        ctx.method_25294(x, y-1, x+width, y, colorARGB(Color.getHSBColor(hue, 0.5f, 0.95f)));
        // Buton arka planı
        ctx.method_25294(x, y, x+width, y+height, colorARGB(bgColor));

        // Metin ortala
        int tx = x + (width  - tr.method_1727(text)) / 2;
        int ty = y + (height - 9) / 2;
        ctx.method_51433(tr, text, tx, ty, 0xFFFFFF, false);
    }

    // ── Changelog ─────────────────────────────────────────────────────────────

    private void drawChangelog(class_332 ctx, class_327 tr) {
        int x = 20, y = 20, width = 170;

        long  time     = System.currentTimeMillis() - startTime;
        float baseHue  = (time % 3000) / 3000.0f;

        int fontH        = 9;
        int titleOffset  = fontH + 14;
        int entrySpacing = fontH + 4;

        ArrayList<String> entries = changelogEntries;
        if (entries == null) entries = new ArrayList<>();

        int totalHeight = titleOffset + entries.size() * entrySpacing + 2;

        // Yuvarlatılmış panel simülasyonu (drawRoundedRect yerine)
        int panelCol = argb(20, 25, 30, 120);
        ctx.method_25294(x+2, y,             x+width-2, y+1,            panelCol);
        ctx.method_25294(x+1, y+1,           x+width-1, y+2,            panelCol);
        ctx.method_25294(x,   y+2,           x+width,   y+totalHeight-2, panelCol);
        ctx.method_25294(x+1, y+totalHeight-2, x+width-1, y+totalHeight-1, panelCol);
        ctx.method_25294(x+2, y+totalHeight-1, x+width-2, y+totalHeight,   panelCol);

        // Başlık (gökkuşağı)
        ctx.method_51433(tr, "changelog", x+8, y+6,
                Color.getHSBColor(baseHue, 0.8f, 1.0f).getRGB() | 0xFF000000, true);

        int entryY = y + titleOffset;
        for (int i = 0; i < entries.size(); i++) {
            String txt = entries.get(i).toLowerCase();
            if (txt.length() > 20) txt = txt.substring(0, 20);

            float entryHue = ((time + i * 500L) % 3000L) / 3000.0f;
            ctx.method_51433(tr, txt, x+8, entryY,
                    Color.getHSBColor(entryHue, 0.5f, 0.95f).getRGB() | 0xFF000000, true);
            entryY += entrySpacing;
        }
    }

    // ── Mouse ─────────────────────────────────────────────────────────────────

    @Override
    public boolean method_25402(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int cx  = this.field_22789 / 2;
            int sy  = this.field_22790 / 2 + buttonsYOffset;
            int tot = (buttonWidth*3) + (buttonSpacing*2);
            int sx  = cx - tot / 2;

            class_310 mc = class_310.method_1551();

            // singleplayer
            if (over(mouseX, mouseY, sx, sy, buttonWidth, buttonHeight))
                mc.method_1507(new class_526(this));

            // multiplayer
            if (over(mouseX, mouseY, sx+buttonWidth+buttonSpacing, sy, buttonWidth, buttonHeight))
                mc.method_1507(new class_500(this));

            // quit
            if (over(mouseX, mouseY, sx+(buttonWidth+buttonSpacing), sy+(buttonHeight+buttonSpacing), buttonWidth, buttonHeight))
                mc.method_3782();

            // alts → ClickGUI aç
            if (over(mouseX, mouseY, sx+(buttonWidth+buttonSpacing)*2, sy, buttonWidth, buttonHeight))
                mc.method_1507(new com.azr.client.ClickGUI());

            // change bg → no-op (Azr'da BgProcess yok)

            // options
            if (over(mouseX, mouseY, sx, sy+(buttonHeight+buttonSpacing), buttonWidth, buttonHeight))
                mc.method_1507(new class_4667(this, mc.field_1690));
        }
        return super.method_25402(mouseX, mouseY, button);
    }

    // ── Yardımcılar ───────────────────────────────────────────────────────────

    private boolean over(double mx, double my, int bx, int by, int bw, int bh) {
        return mx >= bx && mx <= bx+bw && my >= by && my <= by+bh;
    }

    /** 2× büyüklük için 4 piksel baskı — shadow dahil */
    private void drawScaled(class_332 ctx, class_327 tr, String text, int x, int y, int color) {
        // shadow
        ctx.method_51433(tr, text, x+1, y+1, 0xFF000000, false);
        ctx.method_51433(tr, text, x+2, y+2, 0xFF000000, false);
        // main (4 px ile 2× simülasyonu)
        ctx.method_51433(tr, text, x,   y,   color, false);
        ctx.method_51433(tr, text, x+1, y,   color, false);
        ctx.method_51433(tr, text, x,   y+1, color, false);
        ctx.method_51433(tr, text, x+1, y+1, color, false);
    }

    private int argb(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private int colorARGB(Color c) {
        return argb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    // ── GitHub ────────────────────────────────────────────────────────────────

    public static ArrayList<String> fetchLatestCommitMessages(String owner, String repo, int limit) throws Exception {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=" + limit;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(java.time.Duration.ofSeconds(3))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "Java-GitHub-Client")
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new IOException("HTTP_" + response.statusCode());

        JsonArray commits = JsonParser.parseString(response.body()).getAsJsonArray();
        ArrayList<String> messages = new ArrayList<>(commits.size());
        for (int i = 0; i < commits.size(); i++) {
            JsonObject commitObj = commits.get(i).getAsJsonObject();
            messages.add(commitObj.getAsJsonObject("commit").get("message").getAsString());
        }
        return messages;
    }

    @Override
    public boolean shouldPause() { return false; }
}
