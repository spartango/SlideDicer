package com.irislabs.deploy;

import com.irislabs.dice.Tiler;
import com.irislabs.fetch.Fetcher;
import com.irislabs.parallel.FullServiceTiler;
import com.irislabs.slide.OpenSlideImage;
import com.irislabs.write.Writer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

        long linearStart = System.currentTimeMillis();
        // Linear work
        Fetcher linearFetcher = new Fetcher();
        Tiler linearTiler = new Tiler(256, 256, 1.0);
        Writer linearWriter = new Writer("/tmp/tiles", "png");
        final Map<String, OpenSlideImage> fetched = linearFetcher.fetch(Arrays.asList(args));
        for (OpenSlideImage slide : fetched.values()) {
            final Map<Point, BufferedImage> tiles = linearTiler.tile(slide);
            for (Map.Entry<Point, BufferedImage> tileEntry : tiles.entrySet()) {
                BufferedImage tile = tileEntry.getValue();
                Point point = tileEntry.getKey();

                linearWriter.write(slide.getFile().getName(), tile, point.x, point.y);
            }
        }
        long linearEnd = System.currentTimeMillis();
        System.out.println("Linear runtime: " + (linearEnd - linearStart) + " ms");

        long parallelStart = System.currentTimeMillis();
        // Parallel work
        FullServiceTiler superTiler = new FullServiceTiler("/tmp/tiles");
        final Future<Void> result = superTiler.tile(Arrays.asList(args));
        try {
            result.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long parallelEnd = System.currentTimeMillis();

        System.out.println("Parallel runtime: " + (parallelEnd - parallelStart) + " ms");
    }
}
