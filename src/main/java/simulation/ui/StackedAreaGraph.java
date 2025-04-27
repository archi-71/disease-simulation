package simulation.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.StackedAreaChart;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import simulation.core.Simulation;

/**
 * Class for stacked area graph plots shown in the data panel
 */
public class StackedAreaGraph extends StackedAreaChart<Number, Number> {

    private List<Series<Number, Number>> seriesList;

    /**
     * Construct a new stacked area graph
     * 
     * @param title      Graph title
     * @param yAxisLabel Y-axis label
     * @param population Population size
     * @param duration   Simulation duration
     */
    public StackedAreaGraph(String title, String yAxisLabel, int population, int duration) {
        super(new NumberAxis(), new NumberAxis());
        setPrefHeight(1000);

        NumberAxis xAxis = (NumberAxis) getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Time (days)");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(duration);
        xAxis.setTickUnit(Simulation.DAY_LENGTH);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue() / 86400);
            }
        });

        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLabel(yAxisLabel);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(population);
        yAxis.setTickUnit(Math.max(1, Math.pow(10, Math.floor(Math.log10(population)) - 1)));

        setTitle(title);
        setCreateSymbols(false);

        seriesList = new ArrayList<>();
    }

    /**
     * Add a new series to the graph
     * 
     * @param name Series name
     */
    public void addSeries(String name) {
        Series<Number, Number> series = new Series<>();
        series.setName(name);
        getData().add(series);
        seriesList.add(series);
    }

    /**
     * Update the series data with given data
     * 
     * @param updatedSeries Latest series data
     */
    public void update(List<List<Integer>> updatedSeries) {
        for (int i = 0; i < seriesList.size(); i++) {
            List<Data<Number, Number>> seriesData = seriesList.get(i).getData();
            for (int j = seriesData.size(); j < updatedSeries.size(); j++) {
                seriesData.add(new XYChart.Data<>(updatedSeries.get(j).get(0), updatedSeries.get(j).get(i + 1)));
            }
        }
    }
}
