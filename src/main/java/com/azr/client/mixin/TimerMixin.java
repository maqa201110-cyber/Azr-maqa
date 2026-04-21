package com.azr.client.mixin;

import com.azr.client.modules.client.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderTickCounter.Dynamic.class)
public abstract class TimerMixin {

    @ModifyVariable(method = "beginRenderTick", at = @At("HEAD"), argsOnly = true)
    private long azr$timer(long timeMillis) {
        if (!Timer.active()) return timeMillis;
        return (long) (timeMillis * Timer.speed());
    }
}
