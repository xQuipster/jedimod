package com.xquipster.jedimod;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class JediMessage implements IMessage {
    public String message;
    @Override
    public void fromBytes(ByteBuf buf) {
        message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message);
    }
    public static class MessageHandler implements IMessageHandler<JediMessage, JediMessage> {

        @Override
        public JediMessage onMessage(JediMessage message, MessageContext ctx) {
            String[] args = message.message.split(";");
            for (String s : args){
                String[] ability = s.split(":");
                if (ability.length >= 2){
                    int id = -1;
                    int damage = 0;
                    if (ability[0].contains("~")){
                        String[] idData = ability[0].split("~");
                        if (idData.length >= 2){
                            try {
                                id = Integer.parseInt(idData[0]);
                                damage = Integer.parseInt(idData[1]);
                            }catch (Exception ignored){
                            }
                        }
                    }else{
                        try {
                            id = Integer.parseInt(ability[0]);
                        }catch (Exception ignored){
                        }
                    }
                    if (id != -1){
                        int time = -1;
                        try {
                            time = (int) Double.parseDouble(ability[1]);
                        }catch (Exception ignored){
                        }
                        if (time != -1){
                            JediMod.MOD.timerAbilities.put(new ItemStack(Item.getItemById(id), 1, damage), time);
                        }
                    }
                }
            }
            return null;
        }
    }
}
