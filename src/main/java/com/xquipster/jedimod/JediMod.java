package com.xquipster.jedimod;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.xquipster.jedimod.api.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
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

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        disableCertificateValidation();
        File thisMod = null;
        String string = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] s = string.split("!");
        System.out.println("[JediMod] Mod class: " + string);
        if (s.length == 2){
            try {
                thisMod = new File(s[0].substring(6));
            }catch (Exception ignored){
            }
        }else if (s.length > 2){
            try {
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < s.length - 1; i++){
                    str.append("!").append(s[i]);
                }
                thisMod = new File(str.substring(7));
            }catch (Exception ignored){
            }
        }
        AutoUpdater updater = null;
        if (thisMod != null){
            System.out.println("[JediMod] Mod file: " + thisMod.getAbsolutePath());
            updater = new AutoUpdater(thisMod, "https://raw.githubusercontent.com/xQuipster/jedimod/refs/heads/master/checksum.txt", "https://github.com/xQuipster/jedimod/releases/download/autoUpdate/jedimod.jar");
            updater.start();
        }else{
            System.err.println("[JediMod] Failed to find mod file!");
            System.err.println("[JediMod] UPDATE FAILED.");
        }
        if (updater != null && updater.isUpdated()){
            Minecraft.getMinecraft().shutdown();
            return;
        }
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
        try {
            URL url = new URL("https://raw.githubusercontent.com/xQuipster/jedimod/refs/heads/master/newgenips.txt");

            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if(line.startsWith(("<"))){
                        break;
                    }
                    ips.add(line);
                    System.out.println(line);
                }
            }catch (Exception e){
                ips.add("jedinewgeneasy.enderman.cloud");
            }
        }catch (Exception ignored){
            ips.add("jedinewgeneasy.enderman.cloud");
        }
        if(ips.isEmpty()){
            ips.add("jedinewgeneasy.enderman.cloud");
        }
        ips1 = new ArrayList<>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/xQuipster/jedimod/refs/heads/master/newips.txt");

            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if(line.startsWith(("<"))){
                        break;
                    }
                    ips1.add(line);
                    System.out.println(line);
                }
            }catch (Exception e){
                ips.add("jedicraftneweasy.enderman.cloud");
            }
        }catch (Exception ignored) {
            ips.add("jedicraftneweasy.enderman.cloud");
        }
        if(ips1.isEmpty()){
            ips1.add("jedicraftneweasy.enderman.cloud");
        }
        MinecraftForge.EVENT_BUS.register(this);
        SimpleNetworkWrapper channel = NetworkRegistry.INSTANCE.newSimpleChannel("jedimod");
        channel.registerMessage(ServerMessage.Handler.class, ServerMessage.class, '|', Side.CLIENT);
    }

    public static ArrayList<String> hdSkins = new ArrayList<>();
    public static ArrayList<String> hdCapes = new ArrayList<>();
    public ArrayList<String> texturesLoad = new ArrayList<>();

    public static ArrayList<String> currentlyLoading = new ArrayList<>();
    public HashMap<ItemStack, AbilityCd> abilityCd = new HashMap<>();
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile){
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
        ArrayList<Skin> skins = getSkins();
        if (!skins.isEmpty()){
            for (Skin s : skins){
                if (s.getName().equalsIgnoreCase(profile.getName())){
                    if (!s.getSkin().equals("")){
                        map.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(s.getSkin(), null));
                    }
                    if (!s.getCape().equals("")){
                        map.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(s.getCape(), null));
                    }
                }
            }
        }
        return map;
    }

    public ArrayList<String> slim = new ArrayList<>();
    public HashMap<String, PlayerRender> renderers = new HashMap<>();
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event){
        event.setCanceled(true);
        PlayerRender render = renderers.getOrDefault(event.getEntityPlayer().getName(), new PlayerRender(Minecraft.getMinecraft().getRenderManager(), slim.contains(event.getEntityPlayer().getName()), hdSkins.contains(event.getEntityPlayer().getName()), hdCapes.contains(event.getEntityPlayer().getName())));
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
    public static void disableCertificateValidation() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = (hostname, session) -> true;

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception ignored) {}
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
            }
        }
        if (a){
            Minecraft.getMinecraft().player.sendChatMessage(msg);
        }
    }
}
