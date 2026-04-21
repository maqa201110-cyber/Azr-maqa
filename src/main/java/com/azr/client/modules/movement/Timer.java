package com.azr.client.modules.movement;

import com.azr.client.Module;
import com.azr.client.ModuleCategory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Timer Modulu - Oyunun render tick hizini degistirir.
 * Paket: com.azr.client (projenin gercek paketi)
 *
 * Not: Bu modul cc.silk yerine com.azr.client paketini kullanir,
 * cunku mevcut jar'in gercek paketi budur.
 *
 * renderTickCounter.tickTime alani reflection ile manipule edilir,
 * boylece obfuscated isimlere bagimlilik olmaz.
 */
public final class Timer extends Module {

    // Hiz: 0.1 (yavash cekim) - 10.0 (cok hizli). Artirabilirsin.
    private float speed = 1.0f;

    // Onceden cozumlenecek reflection nesneleri (performans icin cache)
    private static Object cachedMcInstance = null;
    private static Object cachedTickCounter = null;
    private static List<Field> cachedTickFields = null;

    public Timer() {
        super("Timer", "Oyunun genel hizini degistirir.", -1, ModuleCategory.MOVEMENT);
    }

    public float getSpeed() { return speed; }
    public void setSpeed(float s) { this.speed = Math.max(0.1f, Math.min(10.0f, s)); }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        applyTickTime(50.0f / speed);
    }

    @Override
    public void onDisable() {
        applyTickTime(50.0f); // Normale don (50ms = 1x hiz)
        super.onDisable();
    }

    // ---------------------------------------------------------------
    // Reflection ile MinecraftClient.renderTickCounter.tickTime ayarla
    // ---------------------------------------------------------------

    private void applyTickTime(float targetTime) {
        try {
            Object mc = getMcInstance();
            if (mc == null) return;

            Object counter = getTickCounter(mc);
            if (counter == null) return;

            List<Field> fields = getTickTimeFields(counter);
            for (Field f : fields) {
                f.setFloat(counter, targetTime);
            }
        } catch (Exception ignored) { }
    }

    private Object getMcInstance() {
        try {
            if (cachedMcInstance != null) return cachedMcInstance;

            // MinecraftClient.getInstance() - intermediary: net.minecraft.class_310.method_1551()
            // Once yarn adi, sonra intermediary adi dene
            String[] classNames = {
                "net.minecraft.client.MinecraftClient",
                "net.minecraft.class_310"
            };
            String[] methodNames = { "getInstance", "method_1551" };

            for (String cls : classNames) {
                try {
                    Class<?> mcClass = Class.forName(cls);
                    for (String mth : methodNames) {
                        try {
                            Method m = mcClass.getMethod(mth);
                            Object inst = m.invoke(null);
                            if (inst != null) {
                                cachedMcInstance = inst;
                                return inst;
                            }
                        } catch (NoSuchMethodException ignored2) { }
                    }
                } catch (ClassNotFoundException ignored3) { }
            }
        } catch (Exception ignored) { }
        return null;
    }

    private Object getTickCounter(Object mc) {
        try {
            if (cachedTickCounter != null) {
                // Gecerli mi kontrol et
                try { cachedTickCounter.getClass(); return cachedTickCounter; }
                catch (Exception e) { cachedTickCounter = null; }
            }

            // Tum alanlari tara - renderTickCounter'i bul
            for (Field f : getAllFields(mc.getClass())) {
                f.setAccessible(true);
                Object val = f.get(mc);
                if (val == null) continue;
                String typeName = val.getClass().getName().toLowerCase();
                // "rendertick", "tickcounter", "timer" icerenler
                if (typeName.contains("rendertick") || typeName.contains("tickcounter")) {
                    cachedTickCounter = val;
                    cachedTickFields = null; // cache'i sifirla
                    return val;
                }
            }
            // Ikinci gecis: daha genis arama
            for (Field f : getAllFields(mc.getClass())) {
                f.setAccessible(true);
                Object val = f.get(mc);
                if (val == null) continue;
                String fieldName = f.getName().toLowerCase();
                if (fieldName.contains("timer") || fieldName.contains("ticker") || fieldName.contains("tick")) {
                    // float degil mi? float ise dogrudan float dene, nesne ise counter olabilir
                    if (!f.getType().isPrimitive()) {
                        cachedTickCounter = val;
                        cachedTickFields = null;
                        return val;
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }

    private List<Field> getTickTimeFields(Object counter) {
        if (cachedTickFields != null) return cachedTickFields;
        List<Field> result = new ArrayList<>();
        try {
            for (Field f : getAllFields(counter.getClass())) {
                if (f.getType() != float.class) continue;
                f.setAccessible(true);
                String name = f.getName().toLowerCase();
                // tickTime, ticktime, field_XXXXX gibi adlari hedefle
                if (name.contains("tick") || name.contains("time") || name.startsWith("field_")) {
                    result.add(f);
                }
            }
            // Hic bulunamadiysa tum float alanlari dene (son care)
            if (result.isEmpty()) {
                for (Field f : getAllFields(counter.getClass())) {
                    if (f.getType() == float.class) {
                        f.setAccessible(true);
                        result.add(f);
                    }
                }
            }
        } catch (Exception ignored) { }
        cachedTickFields = result;
        return result;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
