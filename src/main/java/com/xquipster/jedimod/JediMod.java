package com.xquipster.jedimod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

@Mod(modid = JediMod.MOD_ID, name = JediMod.NAME, version = JediMod.VERSION)
public class JediMod
{
    public static final String MOD_ID = "jedimod";
    public static final String NAME = "JediMod";
    public static final String VERSION = "1.0";

    public static JediMod MOD;

    public KeyBinding lightningBind;
    public KeyBinding pushBind;
    public KeyBinding jumpBind;
    public KeyBinding drainBind;
    public KeyBinding telekinesisBind;
    public KeyBinding spearBind;
    public KeyBinding deceptionBind;
    public KeyBinding breakWeaponBind;
    public KeyBinding shieldBind;
    public KeyBinding reflectionBind;
    public KeyBinding blockerBind;
    public ArrayList<KeyBinding> keyBindings;
    public ArrayList<String> ips;
    public ArrayList<String> ips1;

    public HashMap<ItemStack, Integer> timerAbilities = new HashMap<>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MOD = this;
        lightningBind = new KeyBinding(I18n.format("binds.lightning"), 0, "JediMod");
        pushBind = new KeyBinding(I18n.format("binds.push"), 0, "JediMod");
        jumpBind = new KeyBinding(I18n.format("binds.jump"), 0, "JediMod");
        drainBind = new KeyBinding(I18n.format("binds.drain"), 0, "JediMod");
        telekinesisBind = new KeyBinding(I18n.format("binds.telekinesis"), 0, "JediMod");
        spearBind = new KeyBinding(I18n.format("binds.spear"), 0, "JediMod");
        deceptionBind = new KeyBinding(I18n.format("binds.deception"), 0, "JediMod");
        breakWeaponBind = new KeyBinding(I18n.format("binds.breakWeapon"), 0, "JediMod");
        shieldBind = new KeyBinding(I18n.format("binds.shield"), 0, "JediMod");
        reflectionBind = new KeyBinding(I18n.format("binds.reflection"), 0, "JediMod");
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
        ips = new ArrayList<>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/xQuipster/jedicraft-newgen-easy/main/ips.txt");

            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = null;

                // read each line and write to System.out
                while ((line = br.readLine()) != null) {
                    ips.add(line);
                }
            }
        }catch (Exception ignored){
        }
        ips1 = new ArrayList<>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/xQuipster/jedicraft-new-easy/main/ips.txt");

            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = null;

                // read each line and write to System.out
                while ((line = br.readLine()) != null) {
                    ips1.add(line);
                }
            }
        }catch (Exception ignored){
        }
        MinecraftForge.EVENT_BUS.register(this);
        NetworkRegistry.INSTANCE.newSimpleChannel("jedimod").registerMessage(JediMessage.MessageHandler.class, JediMessage.class, 0, Side.SERVER);

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(lightningBind);
        ClientRegistry.registerKeyBinding(pushBind);
        ClientRegistry.registerKeyBinding(jumpBind);
        ClientRegistry.registerKeyBinding(drainBind);
        ClientRegistry.registerKeyBinding(telekinesisBind);
        ClientRegistry.registerKeyBinding(spearBind);
        ClientRegistry.registerKeyBinding(deceptionBind);
        ClientRegistry.registerKeyBinding(breakWeaponBind);
        ClientRegistry.registerKeyBinding(shieldBind);
        ClientRegistry.registerKeyBinding(reflectionBind);
        ClientRegistry.registerKeyBinding(blockerBind);
    }
    @SubscribeEvent
    public void onJoinServer(RenderGameOverlayEvent event){
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && Minecraft.getMinecraft().getCurrentServerData() != null){
            boolean a = false;
            for (String ip : ips){
                if (ip.equalsIgnoreCase(Minecraft.getMinecraft().getCurrentServerData().serverIP)){
                    a = true;
                    break;
                }
            }
            boolean b = false;
            for (String ip : ips1){
                if (ip.equalsIgnoreCase(Minecraft.getMinecraft().getCurrentServerData().serverIP)){
                    b = true;
                    break;
                }
            }
            if (a || b){
                HashMap<ItemStack, Integer> abilities = getTimerAbilites();
                if (!abilities.isEmpty()){
                    int y = 8;
                    for (ItemStack stack : abilities.keySet()){
                        assert Minecraft.getMinecraft().currentScreen != null;
                        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, Minecraft.getMinecraft().currentScreen.width - 64, y);
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("" + abilities.get(stack), Minecraft.getMinecraft().currentScreen.width - 32, y, Color.white.getRGB());
                        y+=16;
                    }
                }
            }
        }
    }

    private HashMap<ItemStack, Integer> getTimerAbilites() {
        String msg = "/jfsjjdkfj";
        Minecraft.getMinecraft().player.sendChatMessage(msg);
        return timerAbilities;
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
                if (binding == drainBind){
                    msg = "/use drain";
                }
                if (binding == telekinesisBind){
                    msg = "/use telekinesis";
                }
                if (binding == spearBind){
                    msg = "/use spear";
                }
                if (binding == deceptionBind){
                    msg = "/use minddeception";
                }
                if (binding == breakWeaponBind){
                    msg = "/use breakweapon";
                }
                if (binding == shieldBind){
                    msg = "/use shield";
                }
                if (binding == reflectionBind){
                    msg = "/use reflection";
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
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
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
