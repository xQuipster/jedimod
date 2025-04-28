package com.xquipster.jedimod.api;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLoadSkin extends SimpleTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
    @Nullable
    private final File cacheFile;
    @Nullable
    private final IImageBuffer imageBuffer;
    @Nullable
    private BufferedImage bufferedImage;
    private boolean textureUploaded;
    private Thread imageThread;
    private String base64;

    public ThreadLoadSkin(@Nullable File cacheFileIn, String base64, ResourceLocation textureResourceLocation, @Nullable IImageBuffer imageBufferIn)
    {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageBuffer = imageBufferIn;
        this.base64 = base64;
    }

    private void checkTextureUploaded()
    {
        if (!this.textureUploaded)
        {
            if (this.bufferedImage != null)
            {
                if (this.textureLocation != null)
                {
                    this.deleteGlTexture();
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                this.textureUploaded = true;
            }
        }
    }

    public int getGlTextureId()
    {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage bufferedImageIn)
    {
        this.bufferedImage = bufferedImageIn;

        if (this.imageBuffer != null)
        {
            this.imageBuffer.skinAvailable();
        }
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        if (this.bufferedImage == null && this.textureLocation != null)
        {
            super.loadTexture(resourceManager);
        }

        if (this.imageThread == null)
        {
            if (this.cacheFile != null && this.cacheFile.isFile())
            {
                LOGGER.info("Loading http texture from local cache ({})", this.cacheFile);

                try
                {
                    this.bufferedImage = ImageIO.read(this.cacheFile);

                    if (this.imageBuffer != null)
                    {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                    }
                }
                catch (IOException ioexception)
                {
                    LOGGER.info("Couldn't load skin {}", this.cacheFile, ioexception);
                    this.loadTextureFromServer();
                }
            }
            else
            {
                this.loadTextureFromServer();
            }
        }
    }

    private BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    protected void loadTextureFromServer()
    {
        this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet())
        {
            public void run()
            {
                try
                {
                    if (!Objects.equals(base64, ""))
                    {

                        BufferedImage image = base64StringToImg(base64);
                        if (cacheFile != null)
                        {
                            if(!cacheFile.exists()){
                                cacheFile.mkdirs();
                                cacheFile.createNewFile();
                            }
                            ImageIO.write(image, "png", cacheFile);
                        }

                        if (imageBuffer != null)
                        {
                            image = imageBuffer.parseUserSkin(image);
                        }
                        setBufferedImage(image);
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }
}
