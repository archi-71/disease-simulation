package simulation.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import simulation.params.SimulationParams;
import simulation.environment.Environment;
import simulation.interventions.Interventions;
import simulation.population.Population;
import simulation.disease.Disease;

/**
 * Main class for the simulation.
 * Handles the simulation lifecycle and manages its components.
 */
public class Simulation {

    // Fixed 10 minute time step in seconds
    public static final int TIME_STEP = 600;

    // Day length in seconds
    public static final int DAY_LENGTH = 86400;

    // Number of threads to use for multithreading
    public static final int THREAD_NUM = Math.min(10, Runtime.getRuntime().availableProcessors());

    // Core simulation components
    private SimulationParams parameters;
    private SimulationOutput output;
    private Environment environment;
    private Population population;
    private Disease disease;
    private Interventions interventions;

    // Current simulation state
    private SimulationState state;

    // Current run, day, and time of day
    private int run;
    private int day;
    private int time;

    // Simulation speed in steps per second
    private int speed = 1;

    // Scheduler for multi-threading
    private ScheduledExecutorService scheduler;

    // Callback for state changes, used by UI for updates
    private Runnable stateChangeCallback;

    /**
     * Construct the simulation
     */
    public Simulation() {
        environment = new Environment();
        population = new Population();
        disease = new Disease();
        interventions = new Interventions();
        output = new SimulationOutput();
        changeState(SimulationState.UNINITIALISED);
    }

    /**
     * Get the simulation parameters
     * @return
     */
    public SimulationParams getParameters() {
        return parameters;
    }

    /**
     * Get the simulation output
     * @return Output
     */
    public SimulationOutput getOutput() {
        return output;
    }

    /**
     * Get the simulation environment
     * @return Environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Get the simulation population
     * @return Population
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Get the simulation disease
     * @return Disease
     */
    public Disease getDisease() {
        return disease;
    }

    /**
     * Get the simulation interventions
     * @return Interventions
     */
    public Interventions getInterventions() {
        return interventions;
    }

    /**
     * Get the current simulation state
     * @return Current state
     */
    public SimulationState getState() {
        return state;
    }

    /**
     * Get the current run
     * @return Current run number
     */
    public int getRun() {
        return run;
    }

    /**
     * Get the current day
     * @return Current day number
     */
    public int getDay() {
        return day;
    }

    /**
     * Get the current time of day
     * @return Current day time in seconds since midnight
     */
    public int getTime() {
        return time;
    }

    /**
     * Get the current simulation speed
     * @return Speed in steps per second
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Set the callback for state changes
     * @param callback Callback to be called on state change
     */
    public void setStateChangeCallback(Runnable callback) {
        stateChangeCallback = callback;
    }

    /**
     * Initialise the simulation's components with the given parameters
     * @param params Simulation parameters
     * @throws InitialisationException If invalid parameters are provided
     */
    public void initialise(SimulationParams params) throws InitialisationException {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.UNINITIALISED);
        parameters = new SimulationParams(params);
        scheduler = Executors.newScheduledThreadPool(THREAD_NUM);

        // Initialise environment
        if (parameters.getEnvironmentParams().isDirty()) {
            try {
                environment.initialise(parameters.getEnvironmentParams());
            } catch (InitialisationException e) {
                throw new InitialisationException("Environment initialisation failed: " + e.getMessage());
            }
        }

        // Initialise interventions
        if (parameters.getInterventionParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                interventions.initialise(parameters.getInterventionParams(), environment);
            } catch (InitialisationException e) {
                throw new InitialisationException("Intervention initialisation failed: " + e.getMessage());
            }
        }

        // Initialise output
        if (parameters.getRuns().isDirty() || parameters.getInterventionParams().isDirty()) {
            output.initialise(parameters.getRuns().getValue(), interventions);
        }

        // Initialise population
        if (parameters.getPopulationParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                population.initialise(parameters.getPopulationParams(), environment, output, scheduler);
            } catch (InitialisationException e) {
                throw new InitialisationException("Population initialisation failed: " + e.getMessage());
            }
        }

        // Initialise disease
        if (parameters.getDiseaseParams().isDirty()
                || parameters.getPopulationParams().isDirty()
                || parameters.getEnvironmentParams().isDirty()) {
            try {
                disease.initialise(parameters.getDiseaseParams(), population, interventions, output);
            } catch (InitialisationException e) {
                throw new InitialisationException("Disease initialisation failed: " + e.getMessage());
            }
        }

        // Reset simulation
        reset();
    }

    /**
     * Run the simulation
     */
    public void play() {
        changeState(SimulationState.PLAYING);
        startScheduler();
    }

    /**
     * Pause the simulation
     */
    public void pause() {
        changeState(SimulationState.PAUSED);
        pauseScheduler();
    }

    /**
     * Reset the simulation to its initial state
     */
    public void reset() {
        run = 0;
        output.reset();
        resetRun();
    }

    /**
     * Reset the current run of the simulation for the next run
     */
    public void resetRun() {
        if (state == SimulationState.PLAYING) {
            stopScheduler();
        }
        changeState(SimulationState.INITIALISED);

        // Reset all components
        output.resetRun();
        population.reset();
        disease.reset();
        interventions.reset();

        day = time = 0;
        changeState(SimulationState.INITIALISED);
    }

    /**
     * Set the simulation speed
     * @param speed Speed in steps per second
     */
    public void setSpeed(int speed) {
        this.speed = speed;
        if (state == SimulationState.PLAYING) {
            // Restart the scheduler with the new speed
            pauseScheduler();
            startScheduler();
        }
    }

    /**
     * Change the simulation state and trigger callback if set
     * @param newState New state to transition to
     */
    private void changeState(SimulationState newState) {
        if (state != newState) {
            state = newState;
            if (stateChangeCallback != null) {
                stateChangeCallback.run();
            }
        }
    }

    /**
     * Run a single step of the simulation
     */
    private void step() {
        output.step(time, day, run);
        time += TIME_STEP;
        population.step(scheduler, time);
        disease.step(scheduler, time);
        // Check for new day
        if (time >= DAY_LENGTH) {
            time -= DAY_LENGTH;
            day++;
            interventions.step(day);
            // Check for new run
            if (day >= parameters.getDuration().getValue()) {
                output.step(time, day, run);
                run++;
                // Check for end of simulation
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

    /**
     * Start the scheduler for the simulation
     */
    private void startScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            if (state == SimulationState.PLAYING) {
                step();
            }
        }, 0, 1000000000 / speed, TimeUnit.NANOSECONDS);
    }

    /**
     * Pause the scheduler for the simulation
     */
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

    /**
     * Shutdown the scheduler for the simulation
     */
    private void stopScheduler() {
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(THREAD_NUM);
    }
}
