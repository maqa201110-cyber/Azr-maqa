package com.azr.client.modules.client;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import net.minecraft.network.packet.Packet;

import java.util.ArrayDeque;
import java.util.Deque;

public class Blink extends Module {

    private static final Deque<Packet<?>> queued = new ArrayDeque<>();

    public Blink() { super("Blink", "Hold outgoing position packets until disabled", 0, ModuleCategory.CLIENT); }

    public static boolean active() {
        var m = com.azr.client.module.ModuleManager.get().byClass(Blink.class);
        return m != null && m.isEnabled();
    }

    public static boolean enqueue(Packet<?> p) {
        if (!active()) return false;
        synchronized (queued) { queued.addLast(p); }
        return true;
    }

    @Override
    public void onDisable() {
        if (mc.getNetworkHandler() == null) { synchronized (queued) { queued.clear(); } return; }
        synchronized (queued) {
            for (Packet<?> p : queued) mc.getNetworkHandler().sendPacket(p);
            queued.clear();
        }
    }
}
