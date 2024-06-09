package com.xquipster.jedimod.mixins;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.xquipster.jedimod.JediMod;

import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin {

    @Shadow
    private boolean playerTexturesLoaded;
    @Shadow
    private String skinType;
    @Final
    @Shadow
    private GameProfile gameProfile;
    @Shadow
    Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);

    /**
     * @author 1
     * @reason 1
     */
    @Overwrite
    protected void loadPlayerTextures()
    {
        if (!playerTexturesLoaded)
        {
            this.playerTexturesLoaded = true;
            Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (typeIn, location, profileTexture) -> {
                switch (typeIn)
                {
                    case SKIN:
                        playerTextures.put(MinecraftProfileTexture.Type.SKIN, location);
                        skinType = "default";
                        break;
                    case CAPE:
                        playerTextures.put(MinecraftProfileTexture.Type.CAPE, location);
                        break;
                }
            }, false);
        }else{
            boolean a = true;
            for (int i = 0; i < JediMod.MOD.texturesLoad.size(); i++){
                String player = JediMod.MOD.texturesLoad.get(i);
                if (player.equalsIgnoreCase(gameProfile.getName())){
                    a = false;
                    JediMod.MOD.texturesLoad.remove(i);
                    break;
                }
            }
            if (!a){
                final boolean[] skinUpdated = {false};
                final boolean[] capeUpdated = {false};
                JediMod.currentlyLoading.add(gameProfile.getName());
                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (typeIn, location, profileTexture) -> {
                    switch (typeIn)
                    {
                        case SKIN:
                            playerTextures.put(MinecraftProfileTexture.Type.SKIN, location);
                            skinType = "default";
                            skinUpdated[0] = true;
                            break;
                        case CAPE:
                            playerTextures.put(MinecraftProfileTexture.Type.CAPE, location);
                            capeUpdated[0] = true;
                            break;
                    }
                }, false);
                if (!skinUpdated[0]){
                    playerTextures.remove(MinecraftProfileTexture.Type.SKIN);
                }
                if (!capeUpdated[0]){
                    playerTextures.remove(MinecraftProfileTexture.Type.CAPE);
                }
            }
        }
    }
}
