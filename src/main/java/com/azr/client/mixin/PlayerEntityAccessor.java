package com.azr.client.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface PlayerEntityAccessor {
    @Accessor("jumpingCooldown")
    void setJumpingCooldown(int v);
}
