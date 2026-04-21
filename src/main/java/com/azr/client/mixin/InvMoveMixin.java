package com.azr.client.mixin;

import com.azr.client.modules.player.InvMove;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class InvMoveMixin {

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void azr$invMove(CallbackInfoReturnable<Boolean> cir) {
        if (!InvMove.active()) return;
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.currentScreen == null) return;
        KeyBinding self = (KeyBinding)(Object)this;
        if (self == mc.options.forwardKey || self == mc.options.backKey ||
            self == mc.options.leftKey    || self == mc.options.rightKey ||
            self == mc.options.jumpKey    || self == mc.options.sprintKey) {
            InputUtil.Key bound = ((KeyBindingAccessor) self).getBoundKey();
            cir.setReturnValue(InputUtil.isKeyPressed(mc.getWindow().getHandle(), bound.getCode()));
        }
    }
}
