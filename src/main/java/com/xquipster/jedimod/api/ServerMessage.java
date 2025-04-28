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
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
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

        private static final HashMap<String, String> receivingSkin = new HashMap<>();
        private static final HashMap<String, String> receivingCape = new HashMap<>();

        public static void clearTextureBuffer(){
            receivingCape.clear();
            receivingSkin.clear();
        }

        @Override
        public IMessage onMessage(ServerMessage message, MessageContext ctx) {
            if (!Objects.equals(message.getText(), "")) {
                try {
                    String[] s = message.getText().split("~");
                    if (s.length == 3) {
                        if(s[1].equalsIgnoreCase("skin")){
                            if(message.getText().endsWith("1")){
                                Skin skin = null;
                                for (int i = 0; i < JediMod.MOD.getSkins().size(); i++) {
                                    Skin skin1 = JediMod.MOD.getSkins().get(i);
                                    if (skin1.getName().equalsIgnoreCase(s[0])){
                                        skin = skin1;
                                        JediMod.MOD.skins.remove(i);
                                        break;
                                    }
                                }
                                if (skin == null) {
                                    skin = new Skin(s[0]);
                                }
                                String texture = receivingSkin.getOrDefault(s[0], "") + s[2].substring(0, s[2].length() - 1);
                                skin.setSkin(texture);
                                JediMod.MOD.addSkin(skin);
                                if (!JediMod.MOD.texturesLoad.contains(skin.getName())){
                                    JediMod.MOD.texturesLoad.add(skin.getName());
                                }
                                JediMod.MOD.renderers.remove(skin.getName());
                            }else if(message.getText().endsWith("0")){
                                receivingSkin.put(s[0], receivingSkin.getOrDefault(s[0], "") + s[2].substring(0, s[2].length() - 1));
                            }
                        }else if(s[1].equalsIgnoreCase("cape")){
                            if(message.getText().endsWith("1")){
                                Skin skin = null;
                                for (int i = 0; i < JediMod.MOD.getSkins().size(); i++) {
                                    Skin skin1 = JediMod.MOD.getSkins().get(i);
                                    if (skin1.getName().equalsIgnoreCase(s[0])){
                                        skin = skin1;
                                        JediMod.MOD.skins.remove(i);
                                        break;
                                    }
                                }
                                if (skin == null) {
                                    skin = new Skin(s[0]);
                                }
                                String texture = receivingCape.getOrDefault(s[0], "") + s[2].substring(0, s[2].length() - 1);
                                skin.setCape(texture);
                                JediMod.MOD.addSkin(skin);
                                if (!JediMod.MOD.texturesLoad.contains(skin.getName())){
                                    JediMod.MOD.texturesLoad.add(skin.getName());
                                }
                                JediMod.MOD.renderers.remove(skin.getName());
                            }else if(message.getText().endsWith("0")){
                                receivingCape.put(s[0], receivingCape.getOrDefault(s[0], "") + s[2].substring(0, s[2].length() - 1));
                            }
                        }
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
                            if(JediMod.MOD.slim.getOrDefault(p, true)){
                                JediMod.MOD.slim.put(p, false);
                                JediMod.MOD.renderers.remove(p);
                            }
                        }else if (s[1].equalsIgnoreCase("slim")){
                            if (!JediMod.MOD.slim.getOrDefault(p, false)){
                                JediMod.MOD.slim.put(p, true);
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
