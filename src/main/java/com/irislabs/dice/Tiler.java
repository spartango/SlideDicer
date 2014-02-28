package com.irislabs.dice;

import com.irislabs.slide.OpenSlideImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 12:22 PM.
 */
public class Tiler {
    private final int    tileWidth;
    private final int    tileHeight;
    private final double zoom;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Tiler(int tileWidth, int tileHeight, double zoom) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.zoom = zoom;
    }

    public void tile(final OpenSlideImage slide, final TilerListener listener) {
        // Raster the image
        final int expectedTiles = (slide.getHeight() / tileHeight) *
                                  (slide.getWidth() / tileWidth);
        final AtomicInteger tilesRemaining = new AtomicInteger(expectedTiles);
        for (int y = 0; y < slide.getHeight(); y += tileHeight) {
            for (int x = 0; x < slide.getWidth(); x += tileWidth) {
                final int xOffset = x;
                final int yOffset = y;

                executor.submit(new Callable<BufferedImage>() {
                    @Override public BufferedImage call() throws Exception {
                        try {
                            BufferedImage tile = slide.getRegion(xOffset,
                                                                 yOffset,
                                                                 (int) (tileWidth * zoom),
                                                                 (int) (tileHeight * zoom),
                                                                 zoom);
                            listener.onNewTile(slide, tile, xOffset, yOffset);

                            if (tilesRemaining.decrementAndGet() == 0) {
                                listener.onTilingComplete(slide, expectedTiles);
                            }

                            return tile;
                        } catch (IOException e) {
                            System.err.println("Error reading tile " + xOffset + ", " + yOffset + ": " + e);
                        }
                        return null;
                    }
                });

            }
        }
    }

}