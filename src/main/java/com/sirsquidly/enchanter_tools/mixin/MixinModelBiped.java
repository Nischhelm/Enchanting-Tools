package com.sirsquidly.enchanter_tools.mixin;

import com.sirsquidly.enchanter_tools.init.EnchanterToolsItems;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Alters the player's arm rotation when using a Trident, in 3ed person
 */
@SideOnly(Side.CLIENT)
@Mixin(ModelBiped.class)
public abstract class MixinModelBiped extends ModelBase
{
    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public ModelRenderer bipedLeftArm;

    @Shadow
    public ModelBiped.ArmPose leftArmPose;

    @Shadow
    public ModelBiped.ArmPose rightArmPose;

    @Inject(method = "setRotationAngles(FFFFFFLnet/minecraft/entity/Entity;)V",
            at = @At(value = "TAIL"))
    private void renderTrident(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo info)
    {
    	if (entityIn instanceof EntityLivingBase)
    	{
    		if (((EntityLivingBase) entityIn).getHeldItem(EnumHand.MAIN_HAND).getItem() == EnchanterToolsItems.EXTRACTING_BOOK)
    		{
                this.bipedRightArm.rotateAngleX = 3.1F;
    		}
    	}
    }
}