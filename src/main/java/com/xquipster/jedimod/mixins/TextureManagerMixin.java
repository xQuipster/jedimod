package com.xquipster.jedimod.mixins;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Shadow
    private Map<ResourceLocation, ITextureObject> mapTextureObjects;
    /**
     * @author
     * @reason
     */
    @Overwrite
    public ITextureObject getTexture(ResourceLocation textureLocation)
    {
        return this.mapTextureObjects.getOrDefault(textureLocation, null);
    }
}
