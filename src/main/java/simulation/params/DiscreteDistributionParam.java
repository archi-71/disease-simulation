package simulation.params;

import java.util.Map;

public class DiscreteDistributionParam<K> extends MapParam<K, FloatParam> {

    private float totalWeight;

    public DiscreteDistributionParam(String name) {
        super(name);
        totalWeight = 0;
    }

    public DiscreteDistributionParam(DiscreteDistributionParam<K> distribution) {
        super(distribution);
        totalWeight = distribution.totalWeight;
    }

    @Override
    public void setValue(K key, FloatParam weight) {
        FloatParam weightParam = getValue(key);
        if (weightParam != null) {
            totalWeight -= weightParam.getValue();
        }
        super.setValue(key, weight);
        totalWeight += weight.getValue();
    }

    public K sample() {
        float random = (float) Math.random() * totalWeight;
        float sum = 0;
        for (Map.Entry<K, FloatParam> entry : map.entrySet()) {
            sum += entry.getValue().getValue();
            if (random < sum) {
                return entry.getKey();
            }
        }
        return null;
    }
}
