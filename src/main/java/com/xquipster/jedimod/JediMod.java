package com.xquipster.jedimod;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.xquipster.jedimod.api.AbilityCd;
import com.xquipster.jedimod.api.PlayerRender;
import com.xquipster.jedimod.api.ServerMessage;
import com.xquipster.jedimod.api.Skin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@Mod(modid = JediMod.MOD_ID, name = JediMod.NAME, version = JediMod.VERSION)
@SideOnly(Side.CLIENT)
public class JediMod
{
    public static final String MOD_ID = "jedimod";
    public static final String NAME = "JediMod";
    public static final String VERSION = "1.0";

    public static JediMod MOD;

    public KeyBinding lightningBind;
    public KeyBinding pushBind;
    public KeyBinding jumpBind;
    public KeyBinding meditationBind;
    public KeyBinding drainBind;
    public KeyBinding telekinesisBind;
    public KeyBinding reflectionBind;
    public KeyBinding petrificationBind;
    public KeyBinding disappearanceBind;
    public KeyBinding spearBind;
    public KeyBinding deceptionBind;
    public KeyBinding attractionBind;
    public KeyBinding breakWeaponBind;
    public KeyBinding shieldBind;
    public KeyBinding blockerBind;
    public ArrayList<KeyBinding> keyBindings;
    public ArrayList<String> ips;
    public ArrayList<String> ips1;
    public ArrayList<String> ips2;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MOD = this;
        lightningBind = new KeyBinding(I18n.format("binds.lightning"), 0, "JediMod");
        pushBind = new KeyBinding(I18n.format("binds.push"), 0, "JediMod");
        jumpBind = new KeyBinding(I18n.format("binds.jump"), 0, "JediMod");
        meditationBind = new KeyBinding(I18n.format("binds.meditation"), 0, "JediMod");
        drainBind = new KeyBinding(I18n.format("binds.drain"), 0, "JediMod");
        telekinesisBind = new KeyBinding(I18n.format("binds.telekinesis"), 0, "JediMod");
        reflectionBind = new KeyBinding(I18n.format("binds.reflection"), 0, "JediMod");
        petrificationBind = new KeyBinding(I18n.format("binds.petrification"), 0, "JediMod");
        disappearanceBind = new KeyBinding(I18n.format("binds.disappearance"), 0, "JediMod");
        spearBind = new KeyBinding(I18n.format("binds.spear"), 0, "JediMod");
        deceptionBind = new KeyBinding(I18n.format("binds.deception"), 0, "JediMod");
        attractionBind = new KeyBinding(I18n.format("binds.attraction"), 0, "JediMod");
        breakWeaponBind = new KeyBinding(I18n.format("binds.breakWeapon"), 0, "JediMod");
        shieldBind = new KeyBinding(I18n.format("binds.shield"), 0, "JediMod");
        blockerBind = new KeyBinding(I18n.format("binds.blocker"), 0, "JediMod");
        keyBindings = new ArrayList<>();
        keyBindings.add(lightningBind);
        keyBindings.add(pushBind);
        keyBindings.add(jumpBind);
        keyBindings.add(drainBind);
        keyBindings.add(telekinesisBind);
        keyBindings.add(spearBind);
        keyBindings.add(deceptionBind);
        keyBindings.add(breakWeaponBind);
        keyBindings.add(shieldBind);
        keyBindings.add(reflectionBind);
        keyBindings.add(blockerBind);
        keyBindings.add(meditationBind);
        keyBindings.add(petrificationBind);
        keyBindings.add(disappearanceBind);
        keyBindings.add(attractionBind);
        ips = new ArrayList<>();
        ips.add("jedinewgeneasy.enderman.cloud");
        ips1 = new ArrayList<>();
        ips1.add("jedicraftneweasy.enderman.cloud");
        ips2 = new ArrayList<>();
        ips2.add("jedineweasy.joinserver.xyz");
        MinecraftForge.EVENT_BUS.register(this);
        SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("jedimod");
        channel.registerMessage(ServerMessage.Handler.class, ServerMessage.class, '|', Side.CLIENT);
    }

    public ArrayList<String> texturesLoad = new ArrayList<>();

    public static ArrayList<String> currentlyLoading = new ArrayList<>();
    public HashMap<ItemStack, AbilityCd> abilityCd = new HashMap<>();
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile){
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
        for (Skin skin : getSkins()){
            if(Objects.equals(skin.getName(), profile.getName())){
                if(skin.getSkin() != null) map.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(profile.getName() + "_skin", null));
                if(skin.getCape() != null) map.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(profile.getName() + "_cape", null));
            }
        }
        return map;
    }

    public HashMap<String, Boolean> slim = new HashMap<>();
    public HashMap<String, PlayerRender> renderers = new HashMap<>();
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event){
        event.setCanceled(true);
        PlayerRender render = renderers.getOrDefault(event.getEntityPlayer().getName(), new PlayerRender(Minecraft.getMinecraft().getRenderManager(), slim.getOrDefault(event.getEntityPlayer().getName(), ((AbstractClientPlayer) event.getEntityPlayer()).getSkinType().equals("slim"))));
        if (!renderers.containsKey(event.getEntityPlayer().getName())){
            renderers.put(event.getEntityPlayer().getName(), render);
        }
        render.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(),event.getY(), event.getZ(), event.getEntityPlayer().getRotationYawHead(), event.getPartialRenderTick());
    }
    @SubscribeEvent
    public void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        skins.clear();
        abilityCd.clear();
        renderers.clear();
        ServerMessage.Handler.clearTextureBuffer();
    }
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event){
        event.setCanceled(true);
    }
    public final ArrayList<Skin> skins = new ArrayList<>();

    public ArrayList<Skin> getSkins() {
        return skins;
    }

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event){
        boolean b = false;
        if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            for (String ip : ips){
                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                    b = true;
                    break;
                }
            }
            for (String ip : ips1){
                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                    b = true;
                    break;
                }
            }
            for (String ip : ips2){
                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                    b = true;
                    break;
                }
            }
        }
        if (b){
            Timer timer = new Timer("1");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Minecraft.getMinecraft().player.sendChatMessage("/ejTOwh2$2y84sajFjsuGsujrth");
                }
            };
            timer.schedule(task, 1000);
        }
    }

    public void addSkin(Skin skin){
        skins.add(skin);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(lightningBind);
        ClientRegistry.registerKeyBinding(pushBind);
        ClientRegistry.registerKeyBinding(jumpBind);
        ClientRegistry.registerKeyBinding(meditationBind);
        ClientRegistry.registerKeyBinding(drainBind);
        ClientRegistry.registerKeyBinding(telekinesisBind);
        ClientRegistry.registerKeyBinding(reflectionBind);
        ClientRegistry.registerKeyBinding(petrificationBind);
        ClientRegistry.registerKeyBinding(disappearanceBind);
        ClientRegistry.registerKeyBinding(spearBind);
        ClientRegistry.registerKeyBinding(deceptionBind);
        ClientRegistry.registerKeyBinding(attractionBind);
        ClientRegistry.registerKeyBinding(breakWeaponBind);
        ClientRegistry.registerKeyBinding(shieldBind);
        ClientRegistry.registerKeyBinding(blockerBind);
    }
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event){
        String msg = "/jfusjfjsdkosoorwi";
        boolean a = false;
        for (KeyBinding binding : keyBindings){
            if (binding.isPressed()){
                if (binding == lightningBind){
                    msg = "/use lightning";
                }
                if (binding == pushBind){
                    msg = "/use mover";
                }
                if (binding == jumpBind){
                    msg = "/use jump";
                }
                if (binding == meditationBind){
                    msg = "/use meditation";
                }
                if (binding == drainBind){
                    msg = "/use drain";
                }
                if (binding == telekinesisBind){
                    msg = "/use telekinesis";
                }
                if (binding == reflectionBind){
                    msg = "/use reflection";
                }
                if (binding == petrificationBind){
                    msg = "/use petrification";
                }
                if (binding == disappearanceBind){
                    msg = "/use disappearance";
                }
                if (binding == spearBind){
                    msg = "/use spear";
                }
                if (binding == deceptionBind){
                    msg = "/use minddeception";
                }
                if (binding == attractionBind){
                    msg = "/use attraction";
                }
                if (binding == breakWeaponBind){
                    msg = "/use breakweapon";
                }
                if (binding == shieldBind){
                    msg = "/use shield";
                }
                if (binding == blockerBind){
                    msg = "/use blocker";
                }
                if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                    for (String ip : ips){
                        if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                            a = true;
                            break;
                        }
                    }
                }
            }
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()){
            if (Minecraft.getMinecraft().getCurrentServerData() != null){
                for (String ip : ips1) {
                    if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                        a = true;
                        break;
                    }
                }
                if(!a){
                    for (String ip : ips2) {
                        if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                            a = true;
                            break;
                        }
                    }
                }
            }
        }
        if (a){
            Minecraft.getMinecraft().player.sendChatMessage(msg);
        }
    }
}
