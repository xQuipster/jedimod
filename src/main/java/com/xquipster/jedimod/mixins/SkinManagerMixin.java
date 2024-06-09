package com.xquipster.jedimod.mixins;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.xquipster.jedimod.JediMod;
import com.xquipster.jedimod.api.ImageBufferDownloadCustom;
import com.xquipster.jedimod.api.Skin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
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
        String player = "";
        String s1 = "";
        for (Skin skin : JediMod.MOD.skins){
            if ((textureType == MinecraftProfileTexture.Type.SKIN && skin.getSkin().equalsIgnoreCase(profileTexture.getUrl())) || (textureType == MinecraftProfileTexture.Type.CAPE && skin.getCape().equalsIgnoreCase(profileTexture.getUrl()))){
                s1 = skin.getName();
                break;
            }
        }
        if (!s1.equalsIgnoreCase("")){
            for (int i = 0; i < JediMod.currentlyLoading.size(); i++){
                String s = JediMod.currentlyLoading.get(i);
                if(s.equalsIgnoreCase(s1)){
                    player = s1;
                    JediMod.currentlyLoading.remove(i);
                    break;
                }
            }
        }

        String s = player.toLowerCase() + (textureType == MinecraftProfileTexture.Type.SKIN ? "_skin" : "_cape");
        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + (!player.equalsIgnoreCase("") ? s : profileTexture.getHash()));
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
            File file1 = new File(this.skinCacheDir, (!player.equalsIgnoreCase("") ? player.toLowerCase() : profileTexture.getHash()).length() > 2 ? (!player.equalsIgnoreCase("") ? player.toLowerCase() : profileTexture.getHash()).substring(0, 2) : "xx");
            File file2 = new File(file1, (!player.equalsIgnoreCase("") ? s : profileTexture.getHash()));
            final IImageBuffer iimagebuffer = textureType == MinecraftProfileTexture.Type.SKIN ? new ImageBufferDownloadCustom() : null;
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer()
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

            if (map.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId()))
            {
                profile.getProperties().clear();
                profile.getProperties().putAll(Minecraft.getMinecraft().getProfileProperties());
                map.putAll(JediMod.MOD.getTextures(profile));
            }
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
