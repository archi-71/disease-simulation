package simulation.params;

import java.util.Map;
import java.util.LinkedHashMap;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Class to represent a map of parameters
 * 
 * @param <K> Key type
 * @param <V> Parameter type
 */
public class MapParam<K, V extends IParam> implements IParam {

    private String name;
    protected LinkedHashMap<K, V> map;

    /**
     * Construct a new map parameter
     * 
     * @param name Parameter name
     */
    public MapParam(String name) {
        this.name = name;
        map = new LinkedHashMap<K, V>();
    }

    /**
     * Clone a map parameter
     * 
     * @param mapParam Map parameter to copy
     */
    public MapParam(MapParam<K, V> mapParam) {
        map = new LinkedHashMap<K, V>(mapParam.map);
    }

    /**
     * Retrieve a parameter from the map
     * @param key Parameter key
     * @return Parameter
     */
    public V getValue(K key) {
        return map.get(key);
    }

    /**
     * Add a new parameter to the map
     * @param key Parameter key
     * @param value Parameter
     */
    public void setValue(K key, V value) {
        map.put(key, value);
    }

    /**
     * Check if parameter has been modified
     * 
     * @return True if parameter has been modified
     */
    public boolean isDirty() {
        for (V value : map.values()) {
            if (value.isDirty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mark parameter as up to date
     */
    public void clean() {
        for (V value : map.values()) {
            value.clean();
        }
    }

    /**
     * Generate UI to input map parameter
     * 
     * @return Pane for map parameter input
     */
    public Region getInputUI() {
        VBox inputs = new VBox();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            inputs.getChildren().add(entry.getValue().getInputUI());
        }
        TitledPane titledPane = new TitledPane(name, inputs);
        titledPane.setExpanded(false);
        return titledPane;
    }
}