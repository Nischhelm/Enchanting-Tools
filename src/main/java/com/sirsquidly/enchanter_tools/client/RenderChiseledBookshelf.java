package com.sirsquidly.enchanter_tools.client;

import com.sirsquidly.enchanter_tools.common.blocks.BlockChiseledBookshelf;
import com.sirsquidly.enchanter_tools.common.blocks.tileentity.TileChiseledBookshelf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderChiseledBookshelf extends TileEntitySpecialRenderer<TileChiseledBookshelf>
{
    private static final Map<ModelResourceLocation, IBakedModel> MODEL_CACHE = new HashMap<>();

    private static final String MODEL_BASE_PATH = "chiseled_bookshelf_display/book";

    @Override
    public void render(TileChiseledBookshelf te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (te == null || te.getWorld() == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        EnumFacing facing = state.getValue(BlockChiseledBookshelf.FACING);
        rotateForFacing(facing);

        for (int slot = 0; slot < 6; slot++)
        {
            //if (te.getStackFromSlot(slot).isEmpty()) continue;
            IBakedModel model = getModelForSlot(slot);
            if (model == null) continue;
            //System.out.println(model == Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel());

            renderModel(model, state);
        }

        GlStateManager.popMatrix();
    }

    /* ------------------------------------------------------------ */
    /* Model Rendering */
    /* ------------------------------------------------------------ */

    private void renderModel(IBakedModel model, IBlockState state)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();

        /*
         * Tiny Z push prevents z-fighting with shelf face.
         * Safe because models control their own offsets.
         */
        GlStateManager.translate(0, 0, 0.0005);

        RenderHelper.enableStandardItemLighting();

        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(model, state, 1.0F, true);
        GlStateManager.popMatrix();
    }

    /* ------------------------------------------------------------ */
    /* Model Lookup + Cache */
    /* ------------------------------------------------------------ */

    private IBakedModel getModelForSlot(int slot)
    {
        ModelResourceLocation location = new ModelResourceLocation("enchanter_tools:chiseled_bookshelf_display/book" + slot, "normal");

        return MODEL_CACHE.computeIfAbsent(location, loc ->
        {
            try
            { return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(loc); }
            catch (Exception e)
            { return null; }
        });
    }

    /* ------------------------------------------------------------ */
    /* Facing Rotation */
    /* ------------------------------------------------------------ */

    private void rotateForFacing(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH:
                GlStateManager.rotate(180F, 0F, 1F, 0F);
                break;

            case WEST:
                GlStateManager.rotate(90F, 0F, 1F, 0F);
                break;

            case EAST:
                GlStateManager.rotate(-90F, 0F, 1F, 0F);
                break;

            default:
                break;
        }
    }
}