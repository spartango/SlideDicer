package com.irislabs.write;

import com.irislabs.dice.TilerListener;
import com.irislabs.slide.OpenSlideImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 1:25 PM.
 */
public class Writer implements TilerListener {
    static {
        ImageIO.setUseCache(false);
    }

    private final String path;
    private final String fileType;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

    @Override public void onNewTile(final OpenSlideImage source, final BufferedImage tile, final int x, final int y) {
        executor.submit(new Callable<Void>() {
            @Override public Void call() throws Exception {
                ImageIO.write(tile, fileType, new File(filenameFor(source, x, y)));
                return null;
            }
        });
    }

    private String filenameFor(OpenSlideImage source, int x, int y) {
        return path + File.separator + source.getFile().getName() + "_" + x + "_" + y + "." + fileType;
    }

    @Override public void onTilingComplete(OpenSlideImage target, int tiles) {
        // Ignore
    }
}
