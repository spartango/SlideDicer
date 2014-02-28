package com.irislabs.fetch;

import com.irislabs.slide.OpenSlideImage;

import java.util.Collection;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:55 PM.
 */
public interface FetcherListener {
    public void onFetched(String source, OpenSlideImage image);

    public void onFetchingComplete(Collection<String> targets);
}
