package com.sirsquidly.enchanter_tools.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * An ENTIRE MIXIN to make a Vanilla Method NOT try preforming math with a negative Power.
 * Mixins are great :)
 * */
@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper
{
    @ModifyArg(method = "calcItemStackEnchantability", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private static int enchantertools$preventNegativePower(int bound)
    { return Math.max(1, bound);}
}
