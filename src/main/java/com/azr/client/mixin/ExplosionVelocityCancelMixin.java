package com.azr.client.mixin;

import com.azr.client.modules.combat.Velocity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ExplosionVelocityCancelMixin {

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    private void azr$cancelExplosionKb(ExplosionS2CPacket packet, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (!Velocity.isActive()) return;
        double hx = Velocity.horizontalMul();
        double vy = Velocity.verticalMul();
        if (hx == 0.0 && vy == 0.0) {
            ci.cancel();
        }
        // Kismi azaltma icin daha karmasik logic gerek; basitlikten dolayi sadece tam iptal destekli.
    }
}
