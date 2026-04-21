package com.azr.client.menu;

public class MenuReplacer {
    private static boolean done = false;

    public static void tick() {
        if (done) return;
        try {
            Class<?> mcClass = Class.forName("net.minecraft.class_310");
            Object mc = mcClass.getMethod("method_1551").invoke(null);

            java.lang.reflect.Field screenField = mcClass.getDeclaredField("field_1755");
            screenField.setAccessible(true);
            Object currentScreen = screenField.get(mc);

            if (currentScreen != null && currentScreen.getClass().getName().equals("net.minecraft.class_442")) {
                done = true;
                Class<?> screenClass = Class.forName("net.minecraft.class_437");
                Object menu = new CustomMainMenu();
                mcClass.getMethod("method_1507", screenClass).invoke(mc, menu);
            }
        } catch (Exception ignored) {}
    }
}
