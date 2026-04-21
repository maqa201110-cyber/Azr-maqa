package com.azr.client.modules.movement;

import com.azr.client.module.Module;
import com.azr.client.module.ModuleCategory;
import com.azr.client.module.Setting;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module {
    public final Setting<Double> speed = register(new Setting<>("Speed", 1.0, 0.1, 5.0));
    public final Setting<Boolean> creative = register(new Setting<>("CreativeFly", true));

    public Fly() { super("Fly", "Allows flying", 0, ModuleCategory.MOVEMENT); }

    @Override
    public void onEnable() {
        if (!inGame()) return;
        if (creative.asBool()) {
            player().getAbilities().allowFlying = true;
            player().getAbilities().flying = true;
            player().sendAbilitiesUpdate();
        }
    }

    @Override
    public void onDisable() {
        if (!inGame()) return;
        if (!player().isCreative()) {
            player().getAbilities().allowFlying = false;
            player().getAbilities().flying = false;
            player().sendAbilitiesUpdate();
        }
    }

    @Override
    public void onTick() {
        if (!inGame()) return;
        if (creative.asBool()) {
            player().getAbilities().setFlySpeed(speed.asFloat() * 0.05f);
            return;
        }
        Vec3d v = player().getVelocity();
        double y = 0;
        if (mc.options.jumpKey.isPressed()) y = speed.asDouble();
        if (mc.options.sneakKey.isPressed()) y = -speed.asDouble();
        float yaw = (float) Math.toRadians(player().getYaw());
        double mx = 0, mz = 0;
        if (player().forwardSpeed != 0) {
            mx += -Math.sin(yaw) * speed.asDouble() * Math.signum(player().forwardSpeed);
            mz +=  Math.cos(yaw) * speed.asDouble() * Math.signum(player().forwardSpeed);
        }
        if (player().sidewaysSpeed != 0) {
            mx +=  Math.cos(yaw) * speed.asDouble() * Math.signum(player().sidewaysSpeed);
            mz +=  Math.sin(yaw) * speed.asDouble() * Math.signum(player().sidewaysSpeed);
        }
        player().setVelocity(mx, y, mz);
        player().fallDistance = 0;
    }
}
