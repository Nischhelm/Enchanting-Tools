package com.sirsquidly.enchanter_tools.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * An ENTIRE MIXIN to make a Vanilla Method NOT try preforming math with a Power of 0.
 * Wow this sucks.
 * */
@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper
{
    @Inject(method = "calcItemStackEnchantability", at = @At("HEAD"), cancellable = true)
    private static void enchantertools$preventNegativePower(Random rand, int slot, int power, ItemStack stack, CallbackInfoReturnable<Integer> cir)
    { if (power <= 0) cir.setReturnValue(0); }
}
