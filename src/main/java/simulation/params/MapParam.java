package simulation.params;

import java.util.Map;
import java.util.LinkedHashMap;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MapParam<K, V extends IParam> implements IParam {

    private String name;
    protected LinkedHashMap<K, V> map;

    public MapParam(String name) {
        this.name = name;
        map = new LinkedHashMap<K, V>();
    }

    public MapParam(MapParam<K, V> mapParam) {
        map = new LinkedHashMap<K, V>(mapParam.map);
    }

    public V getValue(K key) {
        return map.get(key);
    }

    public void setValue(K key, V value) {
        map.put(key, value);
    }

    public boolean isDirty() {
        for (V value : map.values()) {
            if (value.isDirty()) {
                return true;
            }
        }
        return false;
    }

    public void clean() {
        for (V value : map.values()) {
            value.clean();
        }
    }

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