package com.xquipster.jedimod.mixins;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.xquipster.jedimod.JediMod;
import com.xquipster.jedimod.api.ImageBufferSkin;
import com.xquipster.jedimod.api.Skin;
import com.xquipster.jedimod.api.ThreadLoadSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Mixin(SkinManager.class)
public class SkinManagerMixin {

    @Final
    @Shadow
    private static ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
    @Final
    @Shadow
    private TextureManager textureManager;
    @Final
    @Shadow
    private File skinCacheDir;
    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    public ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type textureType, @Nullable final SkinManager.SkinAvailableCallback skinAvailableCallback)
    {
        String name = profileTexture.getUrl();
        name = name.substring(0, name.length() - 5);
        Skin skin = null;
        String base64 = "";
        for (Skin skin1 : JediMod.MOD.getSkins()){
            if (skin1.getName().equalsIgnoreCase(name)){
                skin = skin1;
                if(textureType == MinecraftProfileTexture.Type.SKIN){
                    if(!Objects.equals(skin.getSkin(), "")){
                        base64 = skin.getSkin();
                        int length = base64.length();
                        base64 = base64.substring(length / 2, length - length / 4);
                        if(base64.length() > 20){
                            base64 = base64.substring(0, 20);
                        }
                        base64 =  base64.replaceAll("\\W", "");
                    }
                }else if(textureType == MinecraftProfileTexture.Type.CAPE){
                    if(!Objects.equals(skin.getCape(), "")){
                        base64 = skin.getCape();
                        int length = base64.length();
                        base64 = base64.substring(length / 2, length - length / 4);
                        if(base64.length() > 20){
                            base64 = base64.substring(0, 20);
                        }
                        base64 =  base64.replaceAll("\\W", "");
                    }
                }
                break;
            }
        }
        if (skin != null){
            for (int i = 0; i < JediMod.currentlyLoading.size(); i++){
                String s = JediMod.currentlyLoading.get(i);
                if(s.equalsIgnoreCase(skin.getName())){
                    JediMod.currentlyLoading.remove(i);
                    break;
                }
            }
        }
        if(base64.isEmpty()){
            base64 = profileTexture.getHash();
        }

        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + base64);
        ITextureObject itextureobject = this.textureManager.getTexture(resourcelocation);

        if (itextureobject != null)
        {
            if (skinAvailableCallback != null)
            {
                skinAvailableCallback.skinAvailable(textureType, resourcelocation, profileTexture);
            }
        }
        else
        {
            File file1 = new File(this.skinCacheDir, base64.length() > 2 ? base64.substring(0, 2) : "xx");
            File file2 = new File(file1, base64);
            final IImageBuffer iimagebuffer = textureType == MinecraftProfileTexture.Type.SKIN ? new ImageBufferSkin() : null;
            if(skin != null){
                if(textureType == MinecraftProfileTexture.Type.SKIN) base64 = skin.getSkin();
                else if(textureType == MinecraftProfileTexture.Type.CAPE){
                    base64 = skin.getCape();
                } else base64 = "";
            }else base64 = "";
            ThreadLoadSkin threaddownloadimagedata = new ThreadLoadSkin(file2, base64, DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer()
            {
                public BufferedImage parseUserSkin(@Nonnull BufferedImage image)
                {
                    if (iimagebuffer != null)
                    {
                        image = iimagebuffer.parseUserSkin(image);
                    }

                    return image;
                }
                public void skinAvailable()
                {
                    if (iimagebuffer != null)
                    {
                        iimagebuffer.skinAvailable();
                    }

                    if (skinAvailableCallback != null)
                    {
                        skinAvailableCallback.skinAvailable(textureType, resourcelocation, profileTexture);
                    }
                }
            });
            this.textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }

        return resourcelocation;
    }
    /**
     * @author a
     * @reason a
     */
    @Overwrite
    public void loadProfileTextures(final GameProfile profile, final SkinManager.SkinAvailableCallback skinAvailableCallback, final boolean requireSecure)
    {
        THREAD_POOL.submit(() -> {
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();

            try
            {
                map.putAll(JediMod.MOD.getTextures(profile));
            }
            catch (InsecureTextureException ignored){}
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                if (map.containsKey(MinecraftProfileTexture.Type.SKIN))
                {
                    loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, skinAvailableCallback);
                }

                if (map.containsKey(MinecraftProfileTexture.Type.CAPE))
                {
                    loadSkin(map.get(MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, skinAvailableCallback);
                }
            });
        });
    }
}
