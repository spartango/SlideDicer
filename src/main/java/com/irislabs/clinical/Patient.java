package com.irislabs.clinical;

import com.google.common.base.Optional;
import com.irislabs.slide.OpenSlideImage;

import java.io.IOException;
import java.util.*;

/**
 * Author: spartango
 * Date: 2/5/14
 * Time: 2:16 PM.
 */
public class Patient {
    private String site;
    private String identifier;

    private Map<String, String> clinicalData;

    private Map<String, OpenSlideImage> slides;

    public Patient(String site, String identifier) {
        this.site = site;
        this.identifier = identifier;

        slides = new HashMap<>();
        clinicalData = new LinkedHashMap<>();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Map<String, OpenSlideImage> getSlides() {
        return slides;
    }

    public Collection<OpenSlideImage> getSlideSet() {
        return slides.values();
    }

    public void clearSlides() {
        Iterator<Map.Entry<String, OpenSlideImage>> slideIterator = slides.entrySet().iterator();
        while (slideIterator.hasNext()) {
            Map.Entry<String, OpenSlideImage> entry = slideIterator.next();
            try {
                entry.getValue().close();
            } catch (IOException e) {
                System.err.println("Failed to close slide " + entry.getKey() + " because of " + e);
            }
            slideIterator.remove();
        }
    }

    public void addSlide(String id, OpenSlideImage slide) {
        slides.put(id, slide);
    }

    public void removeSlide(String id) {
        slides.remove(id);
    }

    public Map<String, String> getClinicalData() {
        return clinicalData;
    }

    public Set<String> getClinicalMarkers() {
        return clinicalData.keySet();
    }

    public String getClinical(String key) {
        return clinicalData.get(key);
    }

    public String addClinical(String key, String value) {
        return clinicalData.put(key, value);
    }

    public String removeClinical(String key) {
        return clinicalData.remove(key);
    }

    public static Optional<Patient> fromSample(String sample) {
        String[] parts = sample.split("-");
        if (parts.length >= 3) {
            // First part is 'TCGA'
            // Second part is Site
            String site = parts[1];
            // Third part is patient
            String identifier = parts[2];

            // We dont care about everything else
            return Optional.of(new Patient(site, identifier));
        }

        return Optional.absent();
    }

    @Override public String toString() {
        return "Patient{" +
               "site='" + site + '\'' +
               ", identifier='" + identifier + '\'' +
               ", clinical=" + clinicalData + ", " + slides.size() + " slides}";
    }
}
