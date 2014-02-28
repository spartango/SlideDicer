package com.irislabs.fetch;

import com.irislabs.slide.OpenSlideImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:05 PM.
 */
public class Fetcher {

    private String downloadPath = "/tmp/";

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

    public void fetch(final Collection<String> targets, final FetcherListener listener) {
        final AtomicInteger remaining = new AtomicInteger(targets.size());
        for (final String target : targets) {
            executor.submit(new Callable<OpenSlideImage>() {
                @Override public OpenSlideImage call() throws Exception {
                    OpenSlideImage slide = null;
                    try {
                        slide = fetch(target);
                        listener.onFetched(target, slide);
                    } catch (IOException e) {
                        System.err.println("Failed to fetch " + target + " because " + e);
                    }

                    if (remaining.decrementAndGet() == 0) {
                        listener.onFetchingComplete(targets);
                    }

                    return slide;
                }
            });
        }
    }


}
