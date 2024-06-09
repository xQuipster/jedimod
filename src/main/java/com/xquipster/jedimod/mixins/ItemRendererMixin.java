package com.xquipster.jedimod.mixins;

import com.xquipster.jedimod.JediMod;
import com.xquipster.jedimod.api.PlayerRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Final
    @Shadow
    private Minecraft mc;
    @Final
    @Shadow
    private RenderManager renderManager;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_)
    {
        boolean flag = p_187456_3_ != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(p_187456_2_);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float)Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        AbstractClientPlayer abstractclientplayer = this.mc.player;
        this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        PlayerRender renderplayer = JediMod.MOD.renderers.getOrDefault(abstractclientplayer.getName(), new PlayerRender(renderManager, JediMod.MOD.slim.contains(abstractclientplayer.getName()), JediMod.hdSkins.contains(abstractclientplayer.getName()), JediMod.hdCapes.contains(abstractclientplayer.getName())));
        if (!JediMod.MOD.renderers.containsKey(abstractclientplayer.getName())){
            JediMod.MOD.renderers.put(abstractclientplayer.getName(), renderplayer);
        }
        GlStateManager.disableCull();

        if (flag)
        {
            renderplayer.renderRightArm(abstractclientplayer);
        }
        else
        {
            renderplayer.renderLeftArm(abstractclientplayer);
        }

        GlStateManager.enableCull();
    }
}
