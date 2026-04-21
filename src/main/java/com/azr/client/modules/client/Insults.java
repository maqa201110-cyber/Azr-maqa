package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;

public class Insults extends Module {
    private static final String[] LINES = {
            "ez", "go cry", "skill issue", "mald harder", "cope",
            "imagine getting clapped by Azr", "L + ratio", "get bonked"
    };
    private static final java.util.Random R = new java.util.Random();

    public Insults() { super("Insults", "Send a random insult after each kill", 0, ModuleCategory.CLIENT); }

    public void onKill(String victim) {
        if (!isEnabled() || mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendChatMessage(victim + " " + LINES[R.nextInt(LINES.length)]);
    }

    public static Insults instance() {
        return (Insults) com.azr.client.module.ModuleManager.get().byClass(Insults.class);
    }
}
