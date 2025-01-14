package simulation.population;

import java.util.HashMap;

public class Distribution<K> {
    private HashMap<K, Float> map;
    private float totalWeight;

    public Distribution() {
        map = new HashMap<K, Float>();
        totalWeight = 0;
    }

    public Distribution(Distribution<K> distribution) {
        map = new HashMap<K, Float>(distribution.map);
        totalWeight = distribution.totalWeight;
    }

    public float get(K key) {
        return map.get(key);
    }

    public void set(K key, float weight) {
        totalWeight -= map.getOrDefault(key, 0f);
        map.put(key, weight);
        totalWeight += weight;
    }

    public K randomSample() {
        float random = (float) Math.random() * totalWeight;
        float sum = 0;
        for (K key : map.keySet()) {
            sum += map.get(key);
            if (random < sum) {
                return key;
            }
        }
        return null;
    }
}
