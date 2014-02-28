package com.irislabs.dice;

import com.irislabs.slide.OpenSlideImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 12:22 PM.
 */
public class Tiler {
    private final int    tileWidth;
    private final int    tileHeight;
    private final double zoom;

    public Tiler(int tileWidth, int tileHeight, double zoom) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.zoom = zoom;
    }

    public Map<Point, BufferedImage> tile(OpenSlideImage slide) {
        Map<Point, BufferedImage> map = new HashMap<>();
        for (int y = 0; y < slide.getHeight(); y += tileHeight) {
            for (int x = 0; x < slide.getWidth(); x += tileWidth) {
                try {
                    BufferedImage tile = slide.getRegion(x,
                                                         y,
                                                         (int) (tileWidth * zoom),
                                                         (int) (tileHeight * zoom),
                                                         zoom);
                    map.put(new Point(x, y), tile);
                } catch (IOException e) {
                    System.err.println("Error reading tile " + x + ", " + y + ": " + e);
                }
            }
        }

        return map;
    }
}
