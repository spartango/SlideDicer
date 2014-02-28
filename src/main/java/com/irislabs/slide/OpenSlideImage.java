package com.irislabs.slide;

import org.openslide.OpenSlide;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Author: spartango
 * Date: 12/23/13
 * Time: 10:07 AM.
 */
public class OpenSlideImage {
    private static final int MAX_THUMBNAIL = 400;

    protected OpenSlide slide;
    protected File      file;

    public OpenSlideImage(String path) throws IOException {
        this(new File(path));
    }

    public OpenSlideImage(File file) throws IOException {
        this.file = file;
        slide = new OpenSlide(file);
    }

    public int getWidth() {
        return (int) slide.getLevel0Width();
    }

    public int getHeight() {
        return (int) slide.getLevel0Height();
    }

    public BufferedImage getRegion(int xOffset, int yOffset, int width, int height) throws IOException {
        // Build a bufferedimage
        BufferedImage region = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = region.createGraphics();
        slide.paintRegion(g2, 0, 0, xOffset, yOffset, width, height, 1.0);
        g2.dispose();

        return region;
    }

    public BufferedImage getRegion(int xOffset, int yOffset, int width, int height, double divider) throws
                                                                                                    IOException {
        // Build a bufferedimage
        BufferedImage region = new BufferedImage((int) (width / divider),
                                                 (int) (height / divider),
                                                 BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = region.createGraphics();
        slide.paintRegion(g2, 0, 0, xOffset, yOffset, width, height, divider);
        g2.dispose();

        return region;
    }

    public BufferedImage getThumbnail() throws IOException {
        return slide.createThumbnailImage(MAX_THUMBNAIL);
    }

    public void close() throws IOException {
        slide.close();
    }

    public File getFile() {
        return file;
    }
}
