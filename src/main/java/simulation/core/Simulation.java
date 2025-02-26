package simulation.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import simulation.params.SimulationParams;
import simulation.environment.Environment;
import simulation.interventions.Interventions;
import simulation.population.Population;
import simulation.disease.Disease;

public class Simulation {

    public static final int TIME_STEP = 600;
    public static final int DAY_LENGTH = 86400;
    public final int THREAD_NUM = Math.min(10, Runtime.getRuntime().availableProcessors());

    private SimulationParams parameters;
    private SimulationOutput output;

    private Environment environment;
    private Population population;
    private Disease disease;
    private Interventions interventions;

    private SimulationState state;
    private int run;
    private int day;
    private int time;
    private int speed = 1;
    private ScheduledExecutorService scheduler;
    private Runnable stateChangeCallback;

    public Simulation() {
        environment = new Environment();
        population = new Population();
        disease = new Disease();
        interventions = new Interventions();
        output = new SimulationOutput();
        changeState(SimulationState.UNINITIALISED);
    }

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

    public Interventions getInterventions() {
        return interventions;
    }

    public SimulationState getState() {
        return state;
    }

    public int getRun() {
        return run;
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

    public void initialise(SimulationParams params) throws InitialisationException {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.UNINITIALISED);
        parameters = new SimulationParams(params);

        scheduler = Executors.newScheduledThreadPool(THREAD_NUM);

        if (parameters.getEnvironmentParams().isDirty()) {
            try {
                environment.initialise(parameters.getEnvironmentParams());
            } catch (InitialisationException e) {
                throw new InitialisationException("Environment initialisation failed: " + e.getMessage());
            }
        }

        if (parameters.getInterventionParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                interventions.initialise(parameters.getInterventionParams(), environment);
            } catch (InitialisationException e) {
                throw new InitialisationException("Intervention initialisation failed: " + e.getMessage());
            }
        }

        if (parameters.getRuns().isDirty() || parameters.getInterventionParams().isDirty()) {
            output.initialise(parameters.getRuns().getValue(), interventions);
        }

        if (parameters.getPopulationParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                population.initialise(parameters.getPopulationParams(), environment, output, scheduler);
            } catch (InitialisationException e) {
                throw new InitialisationException("Population initialisation failed: " + e.getMessage());
            }
        }

        if (parameters.getDiseaseParams().isDirty()
                || parameters.getPopulationParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                disease.initialise(parameters.getDiseaseParams(), population, interventions, output);
            } catch (InitialisationException e) {
                throw new InitialisationException("Disease initialisation failed: " + e.getMessage());
            }
        }

        reset();
    }

    public void play() {
        changeState(SimulationState.PLAYING);
        startScheduler();
    }

    public void pause() {
        changeState(SimulationState.PAUSED);
        pauseScheduler();
    }

    public void reset() {
        run = 0;
        output.reset();
        resetRun();
    }

    public void resetRun() {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.INITIALISED);
        output.resetRun();
        population.reset();
        disease.reset();
        interventions.reset();
        day = time = 0;
        changeState(SimulationState.INITIALISED);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if (state == SimulationState.PLAYING) {
            pauseScheduler();
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
        output.step(time, day, run);
        time += TIME_STEP;
        population.step(scheduler, time);
        disease.step(scheduler, time);
        if (time >= DAY_LENGTH) {
            time -= DAY_LENGTH;
            day++;
            interventions.step(day);
            if (day >= parameters.getDuration().getValue()) {
                output.step(time, day, run);
                run++;
                if (run >= parameters.getRuns().getValue()) {
                    output.averageRuns();
                    changeState(SimulationState.FINISHED);
                    stopScheduler();
                } else {
                    resetRun();
                    play();
                }
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

    private void pauseScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newScheduledThreadPool(THREAD_NUM);
    }

    private void stopScheduler() {
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(THREAD_NUM);
    }
}
