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

    protected String target;
    protected int    tileHeight;
    protected int    tileWidth;
    protected double zoom;
    protected String outputDir;

    protected Fetcher fetcher;

    public FetchTask(String target, int tileHeight, int tileWidth, double zoom, String outputDir) {
        this.target = target;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.zoom = zoom;
        this.outputDir = outputDir;

        fetcher = new Fetcher();
    }

    @Override protected void compute() {
        try {
            System.out.println("Fetching "+target);
            final OpenSlideImage slide = fetcher.fetch(target);
            tile(slide);
        } catch (IOException e) {
            System.err.println("Failed to fetch " + target + " because of " + e);
        }
    }

    protected void tile(OpenSlideImage slide) {
        List<TileTask> tilingTasks = new LinkedList<>();
        for (int y = 0; y < slide.getHeight(); y += tileHeight) {
            for (int x = 0; x < slide.getWidth(); x += tileWidth) {
                tilingTasks.add(new TileTask(slide, x, y, tileWidth, tileHeight, zoom, outputDir));
            }
        }
        invokeAll(tilingTasks);
    }

}
