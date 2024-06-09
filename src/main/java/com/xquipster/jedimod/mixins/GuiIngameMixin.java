package com.xquipster.jedimod.mixins;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.xquipster.jedimod.JediMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {

    /**
     * @author xQuipster
     * @reason because
     */
    @Overwrite
    protected void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes)
    {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>()
        {
            public boolean apply(@Nullable Score p_apply_1_)
            {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
        }));

        if (list.size() > 15)
        {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        }
        else
        {
            collection = list;
        }

        int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

        for (Score score : collection)
        {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + TextFormatting.RED + score.getScorePoints();
            i = Math.max(i, this.getFontRenderer().getStringWidth(s));
        }
        boolean a = false;
        if (Minecraft.getMinecraft().getCurrentServerData() != null){
            for (String ip : JediMod.MOD.ips){
                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                    a = true;
                    break;
                }
            }
            for (String ip : JediMod.MOD.ips1){
                if (Minecraft.getMinecraft().getCurrentServerData().serverIP.equalsIgnoreCase(ip)) {
                    a = true;
                    break;
                }
            }
        }

        int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - 3;
        int j = 0;

        for (Score score1 : collection)
        {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = TextFormatting.RED + "" + score1.getScorePoints();
            int k = j1 - j * this.getFontRenderer().FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - 3 + 2;
            drawRect(l1 - (a ? -4 : 2), k, l, k + this.getFontRenderer().FONT_HEIGHT, 1342177280);
            this.getFontRenderer().drawString(s1, l1 + (a ? 10 : 0), k, 553648127);
            if (!a){
                this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 553648127);
            }

            if (j == collection.size())
            {
                String s3 = objective.getDisplayName();
                drawRect(l1 - (a ? -4 : 2), k - this.getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
                drawRect(l1 - (a ? -4 : 2), k - 1, l, k, 1342177280);
                this.getFontRenderer().drawString(s3, l1 + (a ? 3 : 0) + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k - this.getFontRenderer().FONT_HEIGHT, 553648127);
            }
        }

        int x = (int) (scaledRes.getScaledWidth() - scaledRes.getScaledWidth() * (20f / 1920f));
        int x1 = (int) (scaledRes.getScaledWidth() * (80f / 1920f));
        int y = (int) (scaledRes.getScaledHeight() - scaledRes.getScaledHeight() * (50f / 1080f));
        int y1 = (int) (scaledRes.getScaledHeight() * (100f / 1080f));
        int c = 0;
        ArrayList<ItemStack> stacks = new ArrayList<>();
        JediMod.MOD.abilityCd.keySet().stream().sorted((a1, a2) -> JediMod.MOD.abilityCd.get(a2).getCd() - JediMod.MOD.abilityCd.get(a1).getCd()).forEach(stacks::add);
        for (ItemStack stack : stacks){
            String s = "";
            switch (Item.REGISTRY.getIDForObject(stack.getItem())){
                case 351:
                    switch (stack.getItemDamage()){
                        case 5:
                            s = Keyboard.getKeyName(JediMod.MOD.lightningBind.getKeyCode());
                            break;
                        case 6:
                            s = Keyboard.getKeyName(JediMod.MOD.pushBind.getKeyCode());
                            break;
                        case 7:
                            s = Keyboard.getKeyName(JediMod.MOD.breakWeaponBind.getKeyCode());
                            break;
                        case 11:
                            s = Keyboard.getKeyName(JediMod.MOD.spearBind.getKeyCode());
                            break;
                    }
                    break;
                case 406:
                    s = Keyboard.getKeyName(JediMod.MOD.telekinesisBind.getKeyCode());
                    break;
                case 417:
                    s = Keyboard.getKeyName(JediMod.MOD.deceptionBind.getKeyCode());
                    break;
                case 370:
                    s = Keyboard.getKeyName(JediMod.MOD.shieldBind.getKeyCode());
                    break;
                case 339:
                    s = Keyboard.getKeyName(JediMod.MOD.jumpBind.getKeyCode());
                    break;
                case 331:
                    s = Keyboard.getKeyName(JediMod.MOD.disappearanceBind.getKeyCode());
                    break;
                case 398:
                    s = Keyboard.getKeyName(JediMod.MOD.petrificationBind.getKeyCode());
                    break;
                case 38:
                    switch (stack.getItemDamage()){
                        case 0:
                            s = Keyboard.getKeyName(JediMod.MOD.meditationBind.getKeyCode());
                            break;
                        case 4:
                            s = Keyboard.getKeyName(JediMod.MOD.attractionBind.getKeyCode());
                            break;
                    }
                    break;
                case 414:
                    s = Keyboard.getKeyName(JediMod.MOD.blockerBind.getKeyCode());
                    break;
                case 340:
                    s = Keyboard.getKeyName(JediMod.MOD.drainBind.getKeyCode());
                    break;
                case 420:
                    s = Keyboard.getKeyName(JediMod.MOD.reflectionBind.getKeyCode());
                    break;
            }
            boolean a2 = !s.equalsIgnoreCase("NONE");
            int iX = (int) (x - (x1 + scaledRes.getScaledWidth() * (32f / 1920f)) / 2f);
            int iY = (int) (y - (y1 + scaledRes.getScaledWidth() * (32f / 1920f)) / 2f);
            if (JediMod.MOD.abilityCd.get(stack).getCd() == 0){
                if (a2){
                    Gui.drawRect(x - x1, y - y1, x, y, new Color(0.3f, 0.3f, 0.3f, 0.5f).getRGB());
                    Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, iX, iY);
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("[" + s + "]", x - ((float) x1 / 2) - ((float) Minecraft.getMinecraft().fontRenderer.getStringWidth("[" + s + "]")) / 2, y - (scaledRes.getScaledHeight() * (18f / 1080f)), new Color(0.66f, 0.49f, 0f, 1f).getRGB());
                    c++;
                    if (c%5==0){
                        x = (int) (scaledRes.getScaledWidth() - scaledRes.getScaledWidth() * (20f / 1920f));
                        y-=(int) (scaledRes.getScaledHeight() * (120f / 1080f));
                    }else{
                        x-=(int) (scaledRes.getScaledWidth() * (100f / 1920f));
                    }
                }
            }else{
                float p = ((float) JediMod.MOD.abilityCd.get(stack).getCd()) / ((float) JediMod.MOD.abilityCd.get(stack).getMaxCd());
                if (JediMod.MOD.abilityCd.get(stack).getCd() < JediMod.MOD.abilityCd.get(stack).getMaxCd()){
                    Gui.drawRect(x - x1, y - y1, x, y - (int) (y1 * p), new Color(0.2f, 0.2f, 0.2f, 0.4f).getRGB());
                }
                Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, iX, iY);
                Gui.drawRect(x - x1, y - (int) (y1 *  p), x, y, new Color(0.75f, 0f, 0f, 0.4f).getRGB());
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("" + JediMod.MOD.abilityCd.get(stack).getCd(), x - (x1 / 2f) - ((float) Minecraft.getMinecraft().fontRenderer.getStringWidth("" + JediMod.MOD.abilityCd.get(stack).getCd())) / 2, y - (scaledRes.getScaledHeight() * (18f / 1080f)), new Color(0.6f, 0.6f, 0.6f, 1f).getRGB());
                c++;
                if (c%5==0){
                    x = (int) (scaledRes.getScaledWidth() - scaledRes.getScaledWidth() * (20f / 1920f));
                    y-=(int) (scaledRes.getScaledHeight() * (120f / 1080f));
                }else{
                    x-=(int) (scaledRes.getScaledWidth() * (100f / 1920f));
                }
            }
        }
    }

    @Shadow
    public FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }
}
