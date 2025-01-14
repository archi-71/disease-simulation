package simulation.params;

import simulation.population.Distribution;
import simulation.population.AgeGroup;

public class PopulationParams {
    private int populationSize = 500;
    private Distribution<Integer> householdSizeDistribution = new Distribution<Integer>() {
        {
            set(1, 0.2f);
            set(2, 0.3f);
            set(3, 0.25f);
            set(4, 0.15f);
            set(5, 0.055f);
            set(6, 0.025f);
            set(7, 0.015f);
            set(8, 0.005f);
        }
    };
    private Distribution<AgeGroup> ageDistribution = new Distribution<AgeGroup>() {
        {
            set(AgeGroup._0_TO_4, 5.9f);
            set(AgeGroup._5_TO_9, 5.8f);
            set(AgeGroup._10_TO_14, 5.6f);
            set(AgeGroup._15_TO_19, 5.4f);
            set(AgeGroup._20_TO_24, 6.2f);
            set(AgeGroup._25_TO_29, 6.6f);
            set(AgeGroup._30_TO_34, 6.5f);
            set(AgeGroup._35_TO_39, 6.1f);
            set(AgeGroup._40_TO_44, 5.8f);
            set(AgeGroup._45_TO_49, 5.6f);
            set(AgeGroup._50_TO_54, 5.7f);
            set(AgeGroup._55_TO_59, 5.4f);
            set(AgeGroup._60_TO_64, 5.0f);
            set(AgeGroup._65_TO_69, 4.5f);
            set(AgeGroup._70_TO_74, 3.7f);
            set(AgeGroup._75_TO_79, 3.1f);
            set(AgeGroup._80_TO_84, 2.4f);
            set(AgeGroup._85_TO_89, 1.7f);
            set(AgeGroup._90_TO_94, 1.0f);
            set(AgeGroup._95_TO_99, 0.5f);
            set(AgeGroup._100_PLUS, 0.1f);
        }
    };
    private float unemploymentRate = 0.05f;
    private float universityEntryRate = 0.35f;

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setHouseholdSizeDistribution(Distribution<Integer> householdSizeDistribution) {
        this.householdSizeDistribution = householdSizeDistribution;
    }

    public void setAgeDistribution(Distribution<AgeGroup> ageDistribution) {
        this.ageDistribution = ageDistribution;
    }

    public void setUnemploymentRate(float unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public void setUniversityEntryRate(float universityEntryRate) {
        this.universityEntryRate = universityEntryRate;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public Distribution<Integer> getHouseholdSizeDistribution() {
        return householdSizeDistribution;
    }

    public Distribution<AgeGroup> getAgeDistribution() {
        return ageDistribution;
    }

    public float getUnemploymentRate() {
        return unemploymentRate;
    }

    public float getUniversityEntryRate() {
        return universityEntryRate;
    }

    public PopulationParams() {
    };

    public PopulationParams(PopulationParams params) {
        populationSize = params.populationSize;
        householdSizeDistribution = new Distribution<Integer>(params.householdSizeDistribution);
        ageDistribution = new Distribution<AgeGroup>(params.ageDistribution);
        unemploymentRate = params.unemploymentRate;
        universityEntryRate = params.universityEntryRate;
    }
}
