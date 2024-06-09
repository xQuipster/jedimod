package com.xquipster.jedimod.api;

import net.minecraft.client.renderer.IImageBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownloadCustom implements IImageBuffer {
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;

    @Nonnull
    public BufferedImage parseUserSkin(BufferedImage image)
    {
        this.imageWidth = image.getWidth() == 1024 ? 1024 : 64;
        this.imageHeight = image.getWidth() == 1024 ? 1024 : 64;
        BufferedImage bufferedimage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        boolean flag = image.getHeight() == 32 || image.getHeight() == 512;

        int i = (image.getWidth() == 1024 ? 16 : 1);
        if (flag)
        {
            graphics.setColor(new Color(0, 0, 0, 0));
            graphics.fillRect(0, 32 * i, 64 * i, 32 * i);
            graphics.drawImage(bufferedimage, 24 * i, 48 * i, 20 * i, 52 * i, 4 * i, 16 * i, 8 * i, 20 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28 * i, 48 * i, 24 * i, 52 * i, 8 * i, 16 * i, 12 * i, 20 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 20 * i, 52 * i, 16 * i, 64 * i, 8 * i, 20 * i, 12 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 24 * i, 52 * i, 20 * i, 64 * i, 4 * i, 20 * i, 8 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 28 * i, 52 * i, 24 * i, 64 * i, 0, 20 * i, 4 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 32 * i, 52 * i, 28 * i, 64 * i, 12 * i, 20 * i, 16 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40 * i, 48 * i, 36 * i, 52 * i, 44 * i, 16 * i, 48 * i, 20 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44 * i, 48 * i, 40 * i, 52 * i, 48 * i, 16 * i, 52 * i, 20 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 36 * i, 52 * i, 32 * i, 64 * i, 48 * i, 20 * i, 52 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 40 * i, 52 * i, 36 * i, 64 * i, 44 * i, 20 * i, 48 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 44 * i, 52 * i, 40 * i, 64 * i, 40 * i, 20 * i, 44 * i, 32 * i, (ImageObserver)null);
            graphics.drawImage(bufferedimage, 48 * i, 52 * i, 44 * i, 64 * i, 52 * i, 20 * i, 56 * i, 32 * i, (ImageObserver)null);
        }

        graphics.dispose();
        this.imageData = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
        this.setAreaOpaque(0, 0, 32 * i, 16 * i);

        if (flag)
        {
            this.setAreaTransparent(32 * i, 0, 64 * i, 32 * i);
        }

        this.setAreaOpaque(0, 16 * i, 64 * i, 32 * i);
        this.setAreaOpaque(16 * i, 48 * i, 48 * i, 64 * i);
        return bufferedimage;
    }

    public void skinAvailable()
    {
    }

    private void setAreaTransparent(int x, int y, int width, int height)
    {
        for (int i = x; i < width; ++i)
        {
            for (int j = y; j < height; ++j)
            {
                int k = this.imageData[i + j * this.imageWidth];

                if ((k >> 24 & 255) < 128)
                {
                    return;
                }
            }
        }

        for (int l = x; l < width; ++l)
        {
            for (int i1 = y; i1 < height; ++i1)
            {
                this.imageData[l + i1 * this.imageWidth] &= 16777215;
            }
        }
    }

    private void setAreaOpaque(int x, int y, int width, int height)
    {
        for (int i = x; i < width; ++i)
        {
            for (int j = y; j < height; ++j)
            {
                this.imageData[i + j * this.imageWidth] |= -16777216;
            }
        }
    }
}
