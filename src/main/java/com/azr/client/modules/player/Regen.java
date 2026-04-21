package com.azr.client.modules.player;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Regen extends Module {
    public final Setting<Double> healthBelow = register(new Setting<>("HealthBelow", 18.0, 1.0, 20.0));
    public final Setting<Double> packetCount = register(new Setting<>("Packets", 20.0, 1.0, 80.0));

    public Regen() { super("Regen", "Force-regen by spamming on-ground packets when low HP", 0, ModuleCategory.PLAYER); }

    @Override
    public void onTick() {
        if (!inGame() || mc.getNetworkHandler() == null) return;
        if (player().getHealth() >= healthBelow.asDouble()) return;
        if (player().getHungerManager().getFoodLevel() < 18) return;
        for (int i = 0; i < packetCount.asInt(); i++) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, false));
        }
    }
}
