package com.azr.client.mixin;

import com.azr.client.modules.combat.HitboxExpand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class HitboxExpandMixin {

    @Inject(method = "getBoundingBox()Lnet/minecraft/util/math/Box;", at = @At("RETURN"), cancellable = true)
    private void azr$expand(CallbackInfoReturnable<Box> cir) {
        Entity self = (Entity)(Object)this;
        if (!(self instanceof PlayerEntity)) return;
        double e = HitboxExpand.extra();
        if (e <= 0) return;
        cir.setReturnValue(cir.getReturnValue().expand(e));
    }
}
