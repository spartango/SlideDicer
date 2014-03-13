package com.irislabs.clinical.image;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.irislabs.clinical.Patient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;

/**
 * Author: spartango
 * Date: 2/5/14
 * Time: 7:15 PM.
 */
public class SlideIndex {

    private String                   rootUrl;
    private Multimap<String, String> slideUrls;

    public SlideIndex(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public synchronized Multimap<String, String> getSlideUrls() {
        if (slideUrls == null) {
            slideUrls = fetch();
        }
        return slideUrls;
    }

    private Multimap<String, String> fetch() {
        return scrapePage(rootUrl);
    }

    private Multimap<String, String> scrapePage(String url) {
        ListMultimap<String, String> urls = LinkedListMultimap.create();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a");
            for (Element link : links) {
                String text = link.text();
                String target = link.attr("href");

                // Check if this is a directory
                if (text.endsWith("/")) {
                    // Recursively scan the directory
                    urls.putAll(scrapePage(url + target));
                } else if (text.endsWith(".svs")) {
                    // This is an image
                    // Parse the patient+site identifier out
                    String[] patientParts = text.split("-");
                    if (patientParts.length >= 3) {
                        urls.put(patientParts[1] + "-" + patientParts[2], url + target);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error indexing " + url + ": " + e);
        }
        return urls;
    }

    public Collection<String> getSlideUrls(String identifier) {
        return getSlideUrls().get(identifier);
    }

    public Collection<String> getSlideUrls(String site, String patient) {
        return getSlideUrls(site + "-" + patient);
    }

    public Collection<String> getSlideUrls(Patient patient) {
        return getSlideUrls(patient.getSite(), patient.getIdentifier());
    }

}
