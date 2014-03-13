package com.irislabs.deploy;

import com.irislabs.clinical.Patient;
import com.irislabs.clinical.grading.ClinicalFetcher;
import com.irislabs.clinical.image.SlideIndex;
import com.irislabs.fetch.Fetcher;
import com.irislabs.parallel.PatientTiler;
import com.irislabs.write.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 2:16 PM.
 */
public class Main {
    private static final String CANCER   = "brca";
    private static final int    WIDTH    = 256;
    private static final int    HEIGHT   = 256;
    private static final double ZOOM     = 1.0;
    private static final int    PATIENTS = 1;

    private static final String CLINICAL_URL =
            "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/"
            + CANCER
            + "/bcr/nationwidechildrens.org/biotab/clin/nationwidechildrens.org_clinical_patient_"
            + CANCER
            + ".txt";

    private static final String SLIDE_URL =
            "https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/tumor/"
            + CANCER
            + "/bcr/";

    public static void main(String[] args) throws IOException, InterruptedException {
        Fetcher.setDownloadPath("/tmp/slides/");
        Writer.setFileType("jpeg");

        final ClinicalFetcher nationwideFetcher = new ClinicalFetcher(CLINICAL_URL);
        final SlideIndex slideIndex = new SlideIndex(SLIDE_URL);

        final Collection<Patient> allPatients = nationwideFetcher.getPatients();

        System.out.println(allPatients.size() + " patients retrieved");
        System.out.println(slideIndex.getSlideUrls().size() + " slides indexed");

        List<Patient> selectedPatients = new ArrayList<>(PATIENTS);
        int i = 0;
        for (Patient patient : allPatients) {
            if (i >= PATIENTS) {
                break;
            }

            selectedPatients.add(patient);
            i++;
        }

        long parallelStart = System.currentTimeMillis();
        // Parallel work
        PatientTiler superTiler = new PatientTiler(slideIndex, WIDTH, HEIGHT, ZOOM, "/tmp/tiles");
        final Future<Void> result = superTiler.tilePatients(selectedPatients);
        try {
            result.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        long parallelEnd = System.currentTimeMillis();

        System.out.println("Parallel runtime: " + (parallelEnd - parallelStart) + " ms");
    }
}
