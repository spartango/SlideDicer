package com.irislabs.deploy;

import com.irislabs.dice.Tiler;
import com.irislabs.fetch.Fetcher;
import com.irislabs.fetch.FetcherListener;
import com.irislabs.slide.OpenSlideImage;
import com.irislabs.write.Writer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:16 PM.
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String targetFile = null;
        final int dimension;

        if (args.length == 0) {
            System.out.println("Usage: dicer [image locations]");
            return;
        }


        Fetcher fetcher = new Fetcher();
        // Parallel, pipelined work
        fetcher.fetch(Arrays.asList(args), new FetcherListener() {
            @Override public void onFetched(String source, OpenSlideImage image) {
                System.out.println("Tiling " + image);
                Tiler tiler = new Tiler(256, 256, 1.0);
                Writer writer = new Writer("/tmp/tiles", "png");
                tiler.tile(image, writer);
            }

            @Override public void onFetchingComplete(Collection<String> targets) {
                System.out.println("Finished fetching all " + targets.size() + " tiles");
            }
        });
        System.out.println("Wrote out all tiles");

        // Linear work
        Fetcher linearFetcher = new Fetcher();
        Tiler linearTiler = new Tiler(256, 256, 1.0);
        Writer linearWriter = new Writer("/tmp/tiles", "png");
        final Map<String, OpenSlideImage> fetched = fetcher.fetch(Arrays.asList(args));
        for (OpenSlideImage slide : fetched.values()) {
            final Map<Point, BufferedImage> tiles = linearTiler.tile(slide);
            for (Map.Entry<Point, BufferedImage> tileEntry : tiles.entrySet()) {
                BufferedImage tile = tileEntry.getValue();
                Point point = tileEntry.getKey();

                linearWriter.write(slide.getFile().getName(), tile, point.x, point.y);
            }
        }

    }
}
