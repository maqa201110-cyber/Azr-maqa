package com.azr.client.modules.visuals;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.text.Text;

public class Greeting extends Module {
    public Greeting() { super("Greeting", "Send greeting on join (handled at network connect)", 0, ModuleCategory.VISUALS); }

    public void greet() {
        if (player() == null) return;
        player().sendMessage(Text.literal("§dWelcome to the server, " + player().getName().getString() + "!"), false);
    }
}
