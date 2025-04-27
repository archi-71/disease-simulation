package simulation.params;

import java.util.Map;

/**
 * Class to represent a discrete distribution parameter
 * 
 * @param <K> Value type
 */
public class DiscreteDistributionParam<K> extends MapParam<K, FloatParam> {

    // Sum of the weights of all values in the distribution
    private float totalWeight;

    /**
     * Construct a new discrete distribution parameter
     * @param name Parameter name
     */
    public DiscreteDistributionParam(String name) {
        super(name);
        totalWeight = 0;
    }

    /**
     * Clone a discrete distribution parameter
     * @param distribution Discrete distribution to copy
     */
    public DiscreteDistributionParam(DiscreteDistributionParam<K> distribution) {
        super(distribution);
        totalWeight = distribution.totalWeight;
    }

    /**
     * Add a new value and weight to the distribution
     */
    @Override
    public void setValue(K key, FloatParam weight) {
        FloatParam weightParam = getValue(key);
        if (weightParam != null) {
            totalWeight -= weightParam.getValue();
        }
        super.setValue(key, weight);
        totalWeight += weight.getValue();
    }

    /**
     * Randomly sample from the distribution
     * @return Randomly sampled value
     */
    public K sample() {
        float random = (float) Math.random() * totalWeight;
        float sum = 0;

        // Return the first key with cumulative weight greater than 'random'
        for (Map.Entry<K, FloatParam> entry : map.entrySet()) {
            sum += entry.getValue().getValue();
            if (random < sum) {
                return entry.getKey();
            }
        }

        return null;
    }
}
