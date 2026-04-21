package com.azr.client.mixin;

import com.azr.client.modules.player.FastPlace;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class FastPlaceMixin {
    @Inject(method = "handleBlockBreaking", at = @At("HEAD"))
    private void azr$fastPlace(boolean breaking, CallbackInfo ci) {
        if (FastPlace.active()) {
            MinecraftClient mc = (MinecraftClient)(Object)this;
            ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
        }
    }
}
