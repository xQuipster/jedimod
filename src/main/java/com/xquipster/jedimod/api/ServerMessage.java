package com.xquipster.jedimod.api;

import com.xquipster.jedimod.JediMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class ServerMessage implements IMessage {

    private String text = "";

    public ServerMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = new String(ByteBufUtil.getBytes(buf), StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
    }

    public String getText() {
        return text;
    }

    public static class Handler implements IMessageHandler<ServerMessage, IMessage> {

        @Override
        public IMessage onMessage(ServerMessage message, MessageContext ctx) {
            if (!Objects.equals(message.getText(), "")) {
                try {
                    String[] s = message.getText().split("~");
                    if (s.length == 5) {
                        Skin skin = null;
                        int w = Integer.parseInt(s[3]);
                        int h = Integer.parseInt(s[4]);
                        for (int i = 0; i < JediMod.MOD.getSkins().size(); i++) {
                            Skin skin1 = JediMod.MOD.getSkins().get(i);
                            if (skin1.getName().equalsIgnoreCase(s[0])) {
                                skin = skin1;
                                JediMod.MOD.skins.remove(i);
                                break;
                            }
                        }
                        if (skin == null) {
                            skin = new Skin(s[0]);
                        }
                        if (s[1].equalsIgnoreCase("skin")) {
                            skin.setSkin(s[2].equalsIgnoreCase("none") ? "" : s[2]);
                            if (w == 1024 && h == 1024) {
                                JediMod.hdSkins.add(skin.getName());
                            }else{
                                JediMod.hdSkins.remove(skin.getName());
                            }
                        } else if (s[1].equalsIgnoreCase("cape")) {
                            skin.setCape(s[2].equalsIgnoreCase("none") ? "" : s[2]);
                            if (w == 1024 && h == 512) {
                                JediMod.hdCapes.add(skin.getName());
                            }else{
                                JediMod.hdCapes.remove(skin.getName());
                            }
                        }
                        if (!Objects.equals(skin.getSkin(), "") || !Objects.equals(skin.getCape(), "")) {
                            JediMod.MOD.addSkin(skin);
                        }
                        if (!JediMod.MOD.texturesLoad.contains(skin.getName())){
                            JediMod.MOD.texturesLoad.add(skin.getName());
                        }
                        JediMod.MOD.renderers.remove(skin.getName());
                    }else if (s.length == 4){
                        boolean b = false;
                        if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                            for (String ip : JediMod.MOD.ips){
                                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                                    b = true;
                                    break;
                                }
                            }
                        }
                        if (b){
                            try {
                                int id = Integer.parseInt(s[0]);
                                int damage = Integer.parseInt(s[1]);
                                int cd = Integer.parseInt(s[2]);
                                int maxCd = Integer.parseInt(s[3]);
                                if (cd >= 0){
                                    boolean a = true;
                                    for (ItemStack st : JediMod.MOD.abilityCd.keySet()){
                                        if (Item.REGISTRY.getIDForObject(st.getItem()) == id && st.getItemDamage() == damage){
                                            JediMod.MOD.abilityCd.put(st, new AbilityCd(cd, maxCd));
                                            a = false;
                                            break;
                                        }
                                    }
                                    if (a){
                                        ItemStack stack = new ItemStack(Item.getItemById(id));
                                        stack.setItemDamage(damage);
                                        JediMod.MOD.abilityCd.put(stack, new AbilityCd(cd, maxCd));
                                    }
                                }else{
                                    for (ItemStack st : JediMod.MOD.abilityCd.keySet()){
                                        if (Item.REGISTRY.getIDForObject(st.getItem()) == id && st.getItemDamage() == damage){
                                            JediMod.MOD.abilityCd.remove(st);
                                            break;
                                        }
                                    }
                                }
                            }catch (Exception ignored){
                            }
                        }
                    }else if (s.length == 2){
                        String p = s[0];
                        if (s[1].equalsIgnoreCase("classic")){
                            for (int i = 0; i < JediMod.MOD.slim.size(); i++){
                                if (JediMod.MOD.slim.get(i).equalsIgnoreCase(p)){
                                    JediMod.MOD.slim.remove(i);
                                    JediMod.MOD.renderers.remove(p);
                                    break;
                                }
                            }
                        }else if (s[1].equalsIgnoreCase("slim")){
                            boolean a = true;
                            for (int i = 0; i < JediMod.MOD.slim.size(); i++){
                                if (JediMod.MOD.slim.get(i).equalsIgnoreCase(p)){
                                    a = false;
                                    break;
                                }
                            }
                            if (a){
                                JediMod.MOD.slim.add(p);
                                JediMod.MOD.renderers.remove(p);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            return null;
        }
        public static String getChecksum(byte[] bytes) throws NoSuchAlgorithmException {
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            return new BigInteger(1, hash).toString(16);
        }
    }
}
