package citi_emea_intern_yyhc.hackproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class StockController {
    String PRIVATE_KEY = "5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU";
    StockData stockData;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/processStockData")
    public ResponseEntity<String> processStockData(@RequestBody StockData stockdata) {
        System.out.println("This is called");

        this.stockData = stockdata;

        System.out.println(stockdata.stockName);
        System.out.println(stockdata.stockPrices[0]);
        System.out.println(stockdata.startDate);
        System.out.println(stockdata.endDate);
        System.out.println(stockdata.requestId);
        System.out.println("end");

        return ResponseEntity.ok("Data processed successfully");
    }

//    Add another GetMapping endpoint for this backend
    @GetMapping("/chart")
    public ResponseEntity<byte[]> getChart() throws Exception {
        XYSeries series = new XYSeries("Stock Price");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < stockData.stockPrices.length; i++) {
            ObjectNode node = objectMapper.convertValue(stockData.stockPrices[i], ObjectNode.class);
            double price = node.get("vw").asDouble();
            series.add(i, price);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Stock Price for " + stockData.stockName,
                "Date",
                "Price",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        BufferedImage bufferedImage = chart.createBufferedImage(800, 600);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(baos, bufferedImage);
        byte[] pngData = baos.toByteArray();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(pngData);

//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(1, "Category 1", "Label 1");
//        dataset.addValue(4, "Category 1", "Label 2");
//        dataset.addValue(3, "Category 1", "Label 3");
//
//        JFreeChart barChart = ChartFactory.createBarChart(
//                "Sample Chart",
//                "Category",
//                "Score",
//                dataset
//        );
//        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
//        ChartUtils.writeChartAsPNG(chartOutputStream, barChart, 800, 600);
//
//        byte[] chartBytes = chartOutputStream.toByteArray();
//        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(chatBytes);
    }

}

class StockData {
    @JsonProperty("request_id")
    public String requestId;
    @JsonProperty("ticker")
    public String stockName;

    @JsonProperty("results")
    public Object[] stockPrices;
    public String startDate;
    public String endDate;

}