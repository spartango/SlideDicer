package com.irislabs.parallel;

import com.irislabs.fetch.Fetcher;
import com.irislabs.slide.OpenSlideImage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 3:58 PM.
 */
public class FetchTask extends RecursiveAction {

    private String target;
    private int    tileHeight;
    private int    tileWidth;
    private double zoom;
    private String outputDir;

    private Fetcher fetcher;

    public FetchTask(String target, String output) {
        this.target = target;
        this.outputDir = output;
        fetcher = new Fetcher();
    }

    @Override protected void compute() {
        try {
            final OpenSlideImage slide = fetcher.fetch(target);
            List<TileTask> tilingTasks = new LinkedList<>();
            for (int y = 0; y < slide.getHeight(); y += tileHeight) {
                for (int x = 0; x < slide.getWidth(); x += tileWidth) {
                    tilingTasks.add(new TileTask(slide, x, y, tileWidth, tileHeight, zoom, outputDir));
                }
            }

            invokeAll(tilingTasks);
        } catch (IOException e) {
            System.err.println("Failed to fetch " + target + " because of " + e);
        }
    }
}
