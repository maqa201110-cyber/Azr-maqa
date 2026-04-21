package com.azr.client.save;

import com.azr.client.AzrClient;
import com.azr.client.Module;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Modul durumlarini dosyaya kaydeder ve yukler.
 * Oyundan cikinca (JVM shutdown) otomatik kaydeder.
 * Oyun baslayinca 1 saniye bekleyip yukler.
 *
 * Kayit dosyasi: .minecraft/azrclient/modules.properties
 */
public final class ModuleStateSaver {

    private static final String DIR_NAME  = "azrclient";
    private static final String FILE_NAME = "modules.properties";

    private static volatile boolean saveInProgress = false;

    private ModuleStateSaver() {}

    public static void init() {
        // Shutdown hook: oyun kapaninca kaydet
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                save();
            } catch (Exception ignored) { }
        }, "AzrClient-SaveThread"));

        // Gecikmeli yukle (MC tam baslayana kadar bekle)
        Thread loadThread = new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) { }
            try {
                load();
            } catch (Exception ignored) { }
        }, "AzrClient-LoadThread");
        loadThread.setDaemon(true);
        loadThread.start();
    }

    public static void save() {
        if (saveInProgress) return;
        saveInProgress = true;
        try {
            File configFile = getConfigFile();
            if (configFile == null) return;

            Properties props = new Properties();
            for (Module m : AzrClient.getModules()) {
                props.setProperty(sanitize(m.getName()), String.valueOf(m.isEnabled()));
            }

            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                props.store(fos, "AzrClient Module States - DO NOT EDIT MANUALLY");
            }
        } catch (Exception ignored) {
        } finally {
            saveInProgress = false;
        }
    }

    public static void load() {
        File configFile = getConfigFile();
        if (configFile == null || !configFile.exists()) return;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        } catch (Exception ignored) { return; }

        for (Module m : AzrClient.getModules()) {
            String key = sanitize(m.getName());
            String val = props.getProperty(key);
            if (val == null) continue;

            boolean shouldBeEnabled = "true".equalsIgnoreCase(val);
            if (shouldBeEnabled != m.isEnabled()) {
                try {
                    m.toggle();
                } catch (Exception ignored) { }
            }
        }
    }

    // Dosya yolu: .minecraft/azrclient/modules.properties
    private static File getConfigFile() {
        File dir = getMcRunDirectory();
        if (dir == null) {
            // Fallback: kullanici ana dizini
            dir = new File(System.getProperty("user.home"),
                    ".minecraft" + File.separator + DIR_NAME);
        } else {
            dir = new File(dir, DIR_NAME);
        }
        if (!dir.exists() && !dir.mkdirs()) return null;
        return new File(dir, FILE_NAME);
    }

    // MinecraftClient.runDirectory'yi reflection ile bul
    private static File getMcRunDirectory() {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.class_310");
            Method getInstance = findMethod(mcClass, "method_1551");
            if (getInstance == null) return null;

            Object mc = getInstance.invoke(null);
            if (mc == null) return null;

            // runDirectory = File tipindeki ilk alan
            for (Field f : getAllFields(mcClass)) {
                if (f.getType() == File.class) {
                    f.setAccessible(true);
                    Object val = f.get(mc);
                    if (val instanceof File) {
                        return (File) val;
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }

    private static String sanitize(String name) {
        // Properties dosyasinda guvenli key
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private static Method findMethod(Class<?> clazz, String name) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().equals(name)) {
                    m.setAccessible(true);
                    return m;
                }
            }
        }
        return null;
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            Collections.addAll(list, c.getDeclaredFields());
        }
        return list;
    }
}
