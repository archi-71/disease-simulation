package simulation.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.StackedAreaChart;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import simulation.core.Simulation;

public class StackedAreaGraph extends StackedAreaChart<Number, Number> {

    private List<Series<Number, Number>> seriesList;

    public StackedAreaGraph(String title, String yAxisLabel, int population, int duration) {
        super(new NumberAxis(), new NumberAxis());

        NumberAxis xAxis = (NumberAxis) getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLabel("Time (days)");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(duration);
        xAxis.setTickUnit(Simulation.dayLength);
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

    public void addSeries(String name, String colour) {
        Series<Number, Number> series = new Series<>();
        series.setName(name);
        getData().add(series);
        seriesList.add(series);
    }

    public void update(List<List<Integer>> updatedSeries) {
        for (int i = 0; i < seriesList.size(); i++) {
            List<Data<Number, Number>> seriesData = seriesList.get(i).getData();
            for (int j = seriesData.size(); j < updatedSeries.size(); j++) {
                seriesData.add(new XYChart.Data<>(updatedSeries.get(j).get(0), updatedSeries.get(j).get(i + 1)));
            }
        }
    }
}
