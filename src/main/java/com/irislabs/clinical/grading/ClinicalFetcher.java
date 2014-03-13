package com.irislabs.clinical.grading;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.irislabs.clinical.Patient;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Scanner;

/**
 * Author: spartango
 * Date: 2/5/14
 * Time: 2:49 PM.
 */
public class ClinicalFetcher {
    private String                    url;
    private Multimap<String, Patient> patients;

    private String[] columns;

    public ClinicalFetcher(String url) {
        this.url = url;
    }

    public Multimap<String, Patient> getSites() {
        if (patients == null) {
            patients = fetch();
        }
        return patients;
    }

    public Collection<Patient> getPatients() {
        return getSites().values();
    }

    private Multimap<String, Patient> fetch() {
        ListMultimap<String, Patient> patients = LinkedListMultimap.create();
        try (Scanner reader = new Scanner(new URL(url).openStream(), "UTF-8")) {
            // Find the target columns first line
            findColumns(reader.nextLine());

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                Optional<Patient> patient = parsePatient(line);
                if (patient.isPresent()) {
                    patients.put(patient.get().getSite(), patient.get());
                } else {
                    System.err.println("Failed to parse patient " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return patients;
    }

    private void findColumns(String s) {
        columns = s.split("\t");
    }

    private Optional<Patient> parsePatient(String line) {
        String[] parts = line.split("\t");
        String barcode = parts[0];
        Optional<Patient> patient = Patient.fromSample(barcode);

        if (patient.isPresent() && parts.length >= 2) {
            for (int i = 1; i < columns.length; i++) {
                if(i < parts.length) {
                    patient.get().addClinical(columns[i], parts[i]);
                } else {
                    patient.get().addClinical(columns[i], "");
                }
            }
        }
        return patient;
    }

}
