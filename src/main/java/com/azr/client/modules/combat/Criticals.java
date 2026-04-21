package com.azr.client.modules.combat;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {

    public Criticals() { super("Criticals", "Send tiny offset packets so attacks become criticals", 0, ModuleCategory.COMBAT); }

    public void onAttack() {
        if (!isEnabled() || !inGame() || !player().isOnGround()) return;
        var p = player();
        var net = mc.getNetworkHandler();
        if (net == null) return;
        net.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY() + 0.0625, p.getZ(), false, false));
        net.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(), p.getY(),         p.getZ(), false, false));
    }

    public static Criticals instance() {
        return (Criticals) com.azr.client.module.ModuleManager.get().byClass(Criticals.class);
    }
}
