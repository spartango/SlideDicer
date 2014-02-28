package com.irislabs.parallel;

import com.irislabs.slide.OpenSlideImage;
import com.irislabs.write.Writer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.RecursiveAction;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 3:59 PM.
 */
public class TileTask extends RecursiveAction {
    private OpenSlideImage slide;
    private int            x;
    private int            y;
    private int            tileWidth;
    private int            tileHeight;
    private double         zoom;

    private Writer writer;

    public TileTask(OpenSlideImage slide, int x, int y, int tileWidth, int tileHeight, double zoom, String outputDir) {
        this.slide = slide;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.zoom = zoom;

        writer = new Writer(outputDir, "png");
    }

    @Override protected void compute() {
        try {
            BufferedImage tile = slide.getRegion(x,
                                                 y,
                                                 (int) (tileWidth * zoom),
                                                 (int) (tileHeight * zoom),
                                                 zoom);
            writer.write(slide.getFile().getName(), tile, x, y);
        } catch (IOException e) {
            System.err.println("Error reading tile " + x + ", " + y + ": " + e);
        }
    }
}
