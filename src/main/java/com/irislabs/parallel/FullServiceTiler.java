package com.irislabs.parallel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;

/**
 * Author: spartango
 * Date: 2/28/14
 * Time: 3:38 PM.
 */
public class FullServiceTiler {

    private final ForkJoinPool pool = new ForkJoinPool();

    private String outputDir;

    public FullServiceTiler(String outputDir) {
        this.outputDir = outputDir;
    }

    public Future<Void> tile(final String target) {
        return pool.submit(new FetchTask(target, outputDir));
    }

    public Future<Void> tile(final Collection<String> targets) {
        return pool.submit(new RecursiveAction() {
            @Override protected void compute() {
                List<FetchTask> tasks = new LinkedList<>();
                for (String target : targets) {
                    tasks.add(new FetchTask(target, outputDir));
                }
                invokeAll(tasks);
            }
        });
    }

}
