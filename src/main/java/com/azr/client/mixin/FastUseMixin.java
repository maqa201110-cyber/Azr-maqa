package com.azr.client.mixin;

import com.azr.client.modules.player.FastUse;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FastUseMixin {
    @Inject(method = "getItemUseTime", at = @At("HEAD"), cancellable = true)
    private void azr$fastUse(CallbackInfoReturnable<Integer> cir) {
        if (FastUse.active()) cir.setReturnValue(72000);
    }
}
