package simulation.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import simulation.disease.Disease;
import simulation.environment.Environment;
import simulation.params.SimulationParams;
import simulation.population.Population;

public class Simulation {

    public static final int timeStep = 600;
    public static final int dayLength = 86400;

    private SimulationParams parameters;
    private SimulationOutput output;

    private Environment environment;
    private Population population;
    private Disease disease;

    private SimulationState state;
    private int day;
    private int time;
    private ScheduledExecutorService scheduler;
    private int speed = 1;

    // private Runnable updateCallback;
    private Runnable stateChangeCallback;

    public SimulationParams getParameters() {
        return parameters;
    }

    public SimulationOutput getOutput() {
        return output;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Population getPopulation() {
        return population;
    }

    public Disease getDisease() {
        return disease;
    }

    public SimulationState getState() {
        return state;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public int getSpeed() {
        return speed;
    }

    public void setStateChangeCallback(Runnable callback) {
        stateChangeCallback = callback;
    }

    public Simulation() {
        changeState(SimulationState.UNINITIALISED);
    }

    public void initialise(SimulationParams params) {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.UNINITIALISED);
        parameters = new SimulationParams(params);
        output = new SimulationOutput();
        environment = new Environment(parameters.getEnvironmentParams());
        population = new Population(parameters.getPopulationParams(), environment, output);
        disease = new Disease(parameters.getDiseaseParams(), population, output);
        day = time = 0;
        scheduler = Executors.newScheduledThreadPool(1);
        changeState(SimulationState.INITIALISED);
    }

    public void play() {
        changeState(SimulationState.PLAYING);
        startScheduler();
    }

    public void pause() {
        changeState(SimulationState.PAUSED);
        stopScheduler();
    }

    public void reset() {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.UNINITIALISED);
        output.reset();
        population.reset();
        disease.reset();
        day = time = 0;
        changeState(SimulationState.INITIALISED);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if (state == SimulationState.PLAYING) {
            stopScheduler();
            startScheduler();
        }
    }

    private void changeState(SimulationState newState) {
        if (state != newState) {
            state = newState;
            if (stateChangeCallback != null) {
                stateChangeCallback.run();
            }
        }
    }

    private void step() {
        population.step(time);
        disease.step(time);
        output.step(time, day);

        time += timeStep;
        if (time >= dayLength) {
            time -= dayLength;
            day++;
            if (day >= parameters.getSimulationDuration().getValue()) {
                output.step(time, day);
                time = 0;
                changeState(SimulationState.FINISHED);
                stopScheduler();
            }
        }
    }

    private void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state == SimulationState.PLAYING) {
                step();
            }
        }, 0, 1000000000 / speed, TimeUnit.NANOSECONDS);
    }

    private void stopScheduler() {
        scheduler.shutdown();
        scheduler = Executors.newScheduledThreadPool(1);
    }
}
