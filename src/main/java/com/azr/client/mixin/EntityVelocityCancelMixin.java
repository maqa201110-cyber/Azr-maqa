package com.azr.client.mixin;

import com.azr.client.modules.combat.Velocity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class EntityVelocityCancelMixin {

    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
    private void azr$cancelKnockback(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (packet.getEntityId() != mc.player.getId()) return;
        if (!Velocity.isActive()) return;

        double hx = Velocity.horizontalMul();
        double vy = Velocity.verticalMul();
        if (hx == 1.0 && vy == 1.0) return; // hicbir sey degistirme

        if (hx == 0.0 && vy == 0.0) {
            ci.cancel();
            return;
        }

        // Kismi azaltma: paketten gelen velocity'i oyuncuya elle uygula, sonra orijinal isleyiciyi iptal et.
        double vx = packet.getVelocityX() / 8000.0 * hx;
        double vyy = packet.getVelocityY() / 8000.0 * vy;
        double vz = packet.getVelocityZ() / 8000.0 * hx;
        mc.player.setVelocity(vx, vyy, vz);
        ci.cancel();
    }
}
