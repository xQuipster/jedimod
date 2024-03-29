package com.xquipster.jedimod.api;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AutoUpdater {
    private final File file;
    public AutoUpdater(File file, String hashSumUrl, String fileUrl){
        this.file = file;
        this.fileUrl = fileUrl;
        this.hashSumUrl = hashSumUrl;
    }
    private boolean updated = false;

    public boolean isUpdated() {
        return updated;
    }

    private final String hashSumUrl;
    private final String fileUrl;
    public void start(){
        try {
            URL url = new URL(hashSumUrl);
            InputStream s = url.openConnection().getInputStream();
            String checksum;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s))){

                checksum = reader.readLine();
            }
            String thisChecksum = ServerMessage.Handler.getChecksum(Files.readAllBytes(Paths.get(file.toURI())));
            if (!thisChecksum.equals(checksum)){
                update();
            }
        }catch (Exception e){
            System.out.println("[JediMod] Failed to get checksum! Exception: " + e.getMessage());
        }
    }

    private void update() {
        try {
            URL url = new URL(fileUrl);
            ReadableByteChannel channel = Channels.newChannel(url.openConnection().getInputStream());
            try (FileOutputStream stream = new FileOutputStream(file.getAbsolutePath())){
                stream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                updated = true;
            }catch (Exception ignored){
                System.out.println("[JediMod] Failed to write file!");
            }
        }catch (Exception ignored){
            System.out.println("[JediMod] Failed to download file!");
        }
    }
}
