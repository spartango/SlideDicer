package com.irislabs.deploy;

import com.irislabs.fetch.Fetcher;
import com.irislabs.parallel.FullServiceTiler;
import com.irislabs.write.Writer;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:16 PM.
 */
public class Main {

    private static final int    WIDTH  = 256;
    private static final int    HEIGHT = 256;
    private static final double ZOOM   = 1.0;

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage: dicer [image locations]");
            return;
        }

        Fetcher.setDownloadPath("/tmp/slides");
        Writer.setFileType("jpeg");

        long parallelStart = System.currentTimeMillis();
        // Parallel work
        FullServiceTiler superTiler = new FullServiceTiler(WIDTH, HEIGHT, ZOOM, "/tmp/tiles");
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
