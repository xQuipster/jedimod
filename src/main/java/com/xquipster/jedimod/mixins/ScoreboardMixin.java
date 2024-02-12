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
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Mixin(GuiIngame.class)
public class ScoreboardMixin {

    @Final
    @Shadow
    protected Minecraft mc;

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
    }

    @Shadow
    public FontRenderer getFontRenderer() {
        return mc.fontRenderer;
    }
    @Unique
    private static void drawRect(int left, int top, int right, int bottom, int color){
        Gui.drawRect(left, top, right, bottom, color);
    }
}
