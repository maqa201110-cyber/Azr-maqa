package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiVoid extends Module {
    public final Setting<Double> threshold = register(new Setting<>("YThreshold", -10.0, -64.0, 64.0));

    public AntiVoid() { super("AntiVoid", "Send fake on-ground packet if falling into void", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (player().getY() < threshold.asDouble()) {
            var net = mc.getNetworkHandler();
            if (net != null) {
                net.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        player().getX(), player().getY(), player().getZ(), true, false));
            }
        }
    }
}
