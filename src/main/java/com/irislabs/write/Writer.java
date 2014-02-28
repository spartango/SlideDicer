package com.irislabs.write;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 1:25 PM.
 */
public class Writer {
    static {
        ImageIO.setUseCache(false);
    }

    private final String path;
    private final String fileType;

    public Writer(String path, String fileType) {
        this.path = path;
        File dir = new File(path);
        if (!dir.exists()) {
            try {
                Files.createDirectory(dir.toPath());
            } catch (IOException e) {
                System.err.println("Failed to create directory for tiles");
            }
        }

        this.fileType = fileType;
    }

    public void write(final String id, final BufferedImage tile, final int x, final int y) throws
                                                                                           IOException {
        ImageIO.write(tile, fileType, new File(filenameFor(id, x, y)));
    }

    private String filenameFor(String id, int x, int y) {
        return path + File.separator + id + "_" + x + "_" + y + "." + fileType;
    }

}
