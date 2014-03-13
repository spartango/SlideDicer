package com.irislabs.parallel;

import com.irislabs.clinical.Patient;
import com.irislabs.clinical.image.SlideIndex;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;

/**
 * Author: spartango
 * Date: 3/13/14
 * Time: 4:07 PM.
 */
public class PatientTiler extends URLTiler {
    private SlideIndex index;

    public PatientTiler(SlideIndex index, int tileWidth,
                        int tileHeight,
                        double zoom,
                        String outputDir) {
        super(tileWidth, tileHeight, zoom, outputDir);
        this.index = index;
    }

    public Future<Void> tile(Patient patient) {
        return pool.submit(new PatientFetchTask(patient, tileWidth, tileHeight, zoom, index, outputDir));
    }

    public Future<Void> tilePatients(final Collection<Patient> targets) {
        return pool.submit(new RecursiveAction() {
            @Override protected void compute() {
                List<FetchTask> tasks = new LinkedList<>();
                for (Patient patient : targets) {
                    tasks.add(new PatientFetchTask(patient, tileWidth, tileHeight, zoom, index, outputDir));
                }
                invokeAll(tasks);
            }
        });
    }
}
