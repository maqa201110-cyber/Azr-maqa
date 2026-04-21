package com.azr.client.gui;

import com.azr.client.AzrClient;
import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ClickGUI — Simp ClickInterface stilinden esinlenen, 1.21.4 DrawContext kullanan menü.
 *
 * Renk paleti & yerleşim Simp'tekiyle aynı:
 *   BG_COLOR = (22,22,22,220)
 *   PANEL_BG = (28,28,28,230)
 *   ACCENT   = (120,145,255)
 *   TEXT     = (210,210,210)
 *   PANEL_WIDTH=120, PANEL_SPACING=12, TOP_MARGIN=48
 *
 * Açılış tuşu: M (entrypoint'te kayıtlı). Kategori başlığını sürükle → panel taşı.
 * Modüle sol-tık → toggle, sağ-tık → genişlet (placeholder, ayar yok).
 *
 * Bilinen Simp hatasının düzeltmesi: panel pozisyonları sınıf seviyesi statik map'te
 * tutulur, böylece menüyü kapatıp açtığında pozisyon **korunur**.
 */
public final class ClickGuiScreen extends Screen {

    private static final int BG_COLOR    = argb(220, 22, 22, 22);
    private static final int PANEL_BG    = argb(230, 28, 28, 28);
    private static final int ACCENT      = argb(255, 120, 145, 255);
    private static final int ACCENT_DARK = argb(255, 80, 100, 200);
    private static final int TEXT_COLOR  = argb(255, 210, 210, 210);
    private static final int HOVER_COLOR = argb(255, 45, 45, 45);
    private static final int HEADER_LINE = argb(255, 40, 40, 40);

    private static final int PANEL_WIDTH    = 120;
    private static final int PANEL_SPACING  = 12;
    private static final int TOP_MARGIN     = 48;
    private static final int HEADER_HEIGHT  = 18;
    private static final int ROW_HEIGHT     = 16;

    /** Kategori paneli pozisyonları — sınıf seviyesi: menü kapanıp açılınca kalıcı. */
    private static final Map<ModuleCategory, int[]> PANEL_POS = new LinkedHashMap<>();

    private final List<Panel> panels = new ArrayList<>();
    private Panel dragging = null;
    private int dragOffX, dragOffY;

    public ClickGuiScreen() {
        super(Text.literal("Azr Client"));
    }

    @Override
    protected void init() {
        panels.clear();
        int x = 20;
        for (ModuleCategory cat : ModuleCategory.values()) {
            int[] pos = PANEL_POS.computeIfAbsent(cat, c -> new int[]{x, TOP_MARGIN});
            panels.add(new Panel(cat, pos));
            x += PANEL_WIDTH + PANEL_SPACING;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Yarı saydam koyu arka plan
        ctx.fill(0, 0, this.width, this.height, argb(140, 0, 0, 0));

        if (dragging != null) {
            dragging.pos[0] = mouseX - dragOffX;
            dragging.pos[1] = mouseY - dragOffY;
        }

        for (Panel p : panels) p.render(ctx, mouseX, mouseY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) mouseX, my = (int) mouseY;
        // En üstteki paneli önce kontrol etmek için tersten dolaş
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            // Header üzerinde mi?
            if (button == 0 && inRect(mx, my, p.pos[0], p.pos[1], PANEL_WIDTH, HEADER_HEIGHT)) {
                dragging = p;
                dragOffX = mx - p.pos[0];
                dragOffY = my - p.pos[1];
                return true;
            }
            // Modül butonları
            if (p.handleClick(mx, my, button)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /* ------------------------- Panel ------------------------- */

    private static final class Panel {
        final ModuleCategory category;
        final int[] pos;          // [x, y] — paylaşılan referans, drag güncellemesi kalıcı olur
        final List<Module> modules;

        Panel(ModuleCategory category, int[] pos) {
            this.category = category;
            this.pos = pos;
            this.modules = AzrClient.getInstance() != null
                    ? AzrClient.getInstance().getModuleManager().getModulesByCategory(category)
                    : new ArrayList<>();
        }

        void render(DrawContext ctx, int mouseX, int mouseY) {
            int x = pos[0], y = pos[1];
            int totalH = HEADER_HEIGHT + Math.max(modules.size(), 0) * ROW_HEIGHT;

            // Header
            ctx.fill(x, y, x + PANEL_WIDTH, y + HEADER_HEIGHT, PANEL_BG);
            ctx.fill(x, y + HEADER_HEIGHT - 1, x + PANEL_WIDTH, y + HEADER_HEIGHT, HEADER_LINE);
            ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    category.getDisplayName(), x + 6, y + 5, TEXT_COLOR, false);

            // Body
            int rowY = y + HEADER_HEIGHT;
            ctx.fill(x, rowY, x + PANEL_WIDTH, y + totalH, BG_COLOR);

            for (Module m : modules) {
                boolean hovered = inRect(mouseX, mouseY, x, rowY, PANEL_WIDTH, ROW_HEIGHT);
                int bg;
                if (m.isEnabled()) bg = ACCENT_DARK;
                else if (hovered) bg = HOVER_COLOR;
                else bg = BG_COLOR;
                ctx.fill(x, rowY, x + PANEL_WIDTH, rowY + ROW_HEIGHT, bg);

                int textColor = m.isEnabled() ? argb(255, 255, 255, 255) : TEXT_COLOR;
                ctx.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                        m.getName(), x + 6, rowY + 5, textColor, false);

                rowY += ROW_HEIGHT;
            }
        }

        boolean handleClick(int mx, int my, int button) {
            int rowY = pos[1] + HEADER_HEIGHT;
            for (Module m : modules) {
                if (inRect(mx, my, pos[0], rowY, PANEL_WIDTH, ROW_HEIGHT)) {
                    if (button == 0) m.toggle();
                    return true;
                }
                rowY += ROW_HEIGHT;
            }
            return false;
        }
    }

    /* ------------------------- yardımcılar ------------------------- */

    private static boolean inRect(int x, int y, int rx, int ry, int rw, int rh) {
        return x >= rx && y >= ry && x < rx + rw && y < ry + rh;
    }

    private static int argb(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
