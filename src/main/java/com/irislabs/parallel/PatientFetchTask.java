package com.irislabs.parallel;

import com.irislabs.clinical.Patient;
import com.irislabs.clinical.image.SlideIndex;
import com.irislabs.slide.OpenSlideImage;

/**
 * Author: spartango
 * Date: 3/13/14
 * Time: 3:51 PM.
 */
public class PatientFetchTask extends FetchTask {

    private Patient    patient;
    private SlideIndex index;

    public PatientFetchTask(Patient patient, int tileHeight,
                            int tileWidth,
                            double zoom,
                            SlideIndex index,
                            String outputDir) {
        super(patient.getIdentifier(), tileHeight, tileWidth, zoom, outputDir);

        this.patient = patient;
        this.index = index;
    }

    @Override protected void compute() {
        System.out.println("Fetching " + target);
        fetcher.updateSlides(patient, index);
        for (OpenSlideImage slide : patient.getSlideSet()) {
            tile(slide);
        }
        patient.clearSlides();
    }
}
