package com.irislabs.fetch;

import com.irislabs.slide.OpenSlideImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:05 PM.
 */
public class Fetcher {
    private static String downloadPath = "/tmp/";

    public OpenSlideImage fetch(String target) throws IOException {
        if (target.startsWith("http://") || target.startsWith("https://")) {
            return fetch(new URL(target));
        } else {
            return new OpenSlideImage(target);
        }
    }

    public OpenSlideImage fetch(URL url) throws IOException {
        String[] urlParts = url.getFile().split("/");
        String filename = urlParts[urlParts.length - 1];

        File target = new File(downloadPath + filename);

        // Only download the file if we dont have it (Note, doesnt check validity)
        if (!target.exists()) {
            System.out.println("Downloading " + url + " to " + downloadPath);
            try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                 FileOutputStream fos = new FileOutputStream(target);) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new OpenSlideImage(target);
    }

    public Map<String, OpenSlideImage> fetch(final Collection<String> targets) {
        Map<String, OpenSlideImage> map = new HashMap<>();
        for (String target : targets) {
            try {
                map.put(target, fetch(target));
            } catch (IOException e) {
                System.err.println("Failed to fetch " + target + " because " + e);
            }
        }
        return map;
    }

    public static void setDownloadPath(String downloadPath) {
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            try {
                Files.createDirectory(dir.toPath());
            } catch (IOException e) {
                System.err.println("Failed to create " + dir + " for slides because of " + e);
            }
        }
        Fetcher.downloadPath = downloadPath;
    }
}
