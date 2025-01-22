package simulation.ui;

import java.util.List;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import simulation.core.Simulation;

public class LineGraph extends LineChart<Number, Number> {

    private Series<Number, Number> series;

    public LineGraph(String title, String yAxisLabel, int population, int duration) {
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
        yAxis.setLabel(yAxisLabel);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(population);

        setTitle(title);
        setCreateSymbols(false);
        setLegendVisible(false);

        series = new Series<>();
        getData().add(series);
        series.getNode().setStyle("-fx-stroke: red;");
    }

    public void update(List<List<Integer>> updatedSeries) {
        List<Data<Number, Number>> seriesData = series.getData();
        for (int i = seriesData.size(); i < updatedSeries.size(); i++) {
            seriesData.add(new Data<>(updatedSeries.get(i).get(0), updatedSeries.get(i).get(1)));
        }
    }
}
