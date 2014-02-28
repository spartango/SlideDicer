package com.irislabs.dice;

import com.irislabs.slide.OpenSlideImage;

import java.awt.image.BufferedImage;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 12:44 PM.
 */
public interface TilerListener {

    public void onNewTile(OpenSlideImage source, BufferedImage tile, int x, int y);

    public void onTilingComplete(OpenSlideImage target, int tiles);

}
