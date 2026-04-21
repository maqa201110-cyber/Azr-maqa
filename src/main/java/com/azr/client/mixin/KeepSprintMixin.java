package com.azr.client.mixin;

import com.azr.client.modules.combat.KeepSprint;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class KeepSprintMixin {

    @Redirect(method = "attack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    private void azr$keepSprint(PlayerEntity self, boolean sprinting) {
        if (KeepSprint.active() && !sprinting) return;
        self.setSprinting(sprinting);
    }
}
