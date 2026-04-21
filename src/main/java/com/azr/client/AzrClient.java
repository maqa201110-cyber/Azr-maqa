package com.azr.client;

import com.azr.client.gui.ClickGuiScreen;
import com.azr.client.module.ModuleManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class AzrClient implements ClientModInitializer {

    public static final String MOD_ID = "azrclient";

    private static AzrClient INSTANCE;
    private final ModuleManager moduleManager = new ModuleManager();
    private KeyBinding clickGuiKey;

    public static AzrClient getInstance() {
        return INSTANCE;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        moduleManager.registerDefaults();

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.azrclient.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.azrclient.main"));

        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient mc) {
        while (clickGuiKey.wasPressed()) {
            if (mc.currentScreen == null) {
                mc.setScreen(new ClickGuiScreen());
            }
        }

        if (mc.player != null && mc.world != null) {
            moduleManager.tick();
        }

        moduleManager.handleToggleKeys();
    }
}
