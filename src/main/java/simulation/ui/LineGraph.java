package simulation.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import simulation.core.Simulation;

/**
 * Class for line graph plots shown in the data panel
 */
public class LineGraph extends LineChart<Number, Number> {

    private List<Series<Number, Number>> seriesList;

    /**
     * Construct a new line graph
     * 
     * @param title      Graph title
     * @param yAxisLabel Y-axis label
     * @param population Population size
     * @param duration   Simulation duration
     */
    public LineGraph(String title, String yAxisLabel, int population, int duration) {
        this(title, yAxisLabel, population, duration, false);
    }

    /**
     * Construct a new line graph
     * 
     * @param title      Graph title
     * @param yAxisLabel Y-axis label
     * @param population Population size
     * @param duration   Simulation duration
     * @param showLegend Whether to show the legend
     */
    public LineGraph(String title, String yAxisLabel, int population, int duration, boolean showLegend) {
        super(new NumberAxis(), new NumberAxis());
        setPrefHeight(1000);

        // Configure X-axis
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

        // Configure Y-axis
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setLabel(yAxisLabel);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(population);

        // Configure chart
        setTitle(title);
        setCreateSymbols(false);
        setLegendVisible(showLegend);

        // Initialise new series
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
     * Add a new series to the graph
     * 
     * @param name   Series name
     * @param colour Series colour
     */
    public void addSeries(String name, String colour) {
        addSeries(name);
        seriesList.get(seriesList.size() - 1).getNode().setStyle("-fx-stroke: " + colour + ";");
    }

    /**
     * Update the series data with given data
     * 
     * @param updatedSeries Latest series data
     */
    public void update(List<List<Integer>> updatedSeries) {
        for (int i = 0; i < seriesList.size(); i++) {
            List<Data<Number, Number>> currentSeries = seriesList.get(i).getData();
            for (int j = currentSeries.size(); j < updatedSeries.size(); j++) {
                currentSeries.add(new XYChart.Data<>(updatedSeries.get(j).get(0), updatedSeries.get(j).get(i + 1)));
            }
        }
    }
}
