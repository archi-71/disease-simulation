package simulation.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import simulation.disease.Disease;
import simulation.environment.Environment;
import simulation.params.SimulationParams;
import simulation.population.Population;

public class Simulation {

    private final int startTime = 32400000;
    private final int dayLength = 86400000;

    private SimulationParams parameters;

    private Environment environment;
    private Population population;
    private Disease disease;

    private int day;
    private int time;
    private ScheduledExecutorService scheduler;
    private long lastUpdateTime;
    private boolean isPaused = true;
    private double speed = 1000;
    private Runnable updateCallback;

    public Environment getEnvironment() {
        return environment;
    }

    public Population getPopulation() {
        return population;
    }

    public Disease getDisease() {
        return disease;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public void setScheduleCallback(Runnable callback) {
        updateCallback = callback;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Simulation() {
        parameters = new SimulationParams();
    }

    public void initialise(SimulationParams params) {
        if (!isPaused) {
            pause();
        }
        parameters = new SimulationParams(params);
        environment = new Environment(parameters.getEnvironmentParams());
        population = new Population(parameters.getPopulationParams(), environment);
        disease = new Disease(parameters.getDiseaseParams(), population);

        day = 0;
        time = startTime;
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void play() {
        if (population != null) {
            isPaused = false;
            startScheduler();
        }
    }

    public void pause() {
        if (population != null) {
            isPaused = true;
            stopScheduler();
        }
    }

    public void reset() {
        if (population != null) {
            if (!isPaused) {
                pause();
            }
            environment.reset();
            population.reset();
            disease.reset();
            day = 0;
            time = startTime;
            if (updateCallback != null) {
                updateCallback.run();
            }
        }
    }

    public void setSpeed(double speed) {
        if (population != null) {
            this.speed = speed;
            stopScheduler();
            startScheduler();
        }
    }

    private void step(double deltaTime) {
        time += (int) deltaTime;
        if (time >= dayLength) {
            time -= dayLength;
            day++;
        }
        population.step(time, deltaTime);
        disease.step(time, deltaTime);
        if (updateCallback != null) {
            updateCallback.run();
        }
    }

    private void startScheduler() {
        lastUpdateTime = System.currentTimeMillis();
        scheduler.scheduleWithFixedDelay(() -> {
            if (!isPaused) {
                long currentUpdateTime = System.currentTimeMillis();
                step((currentUpdateTime - lastUpdateTime) * speed);
                lastUpdateTime = currentUpdateTime;
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void stopScheduler() {
        scheduler.shutdown();
        scheduler = Executors.newScheduledThreadPool(1);
    }
}
