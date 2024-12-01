package simulation.params;

public class PopulationParams {
    private int populationSize = 500;

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public PopulationParams() {
    };

    public PopulationParams(PopulationParams params) {
        populationSize = params.populationSize;
    }
}
