package citi_emea_intern_yyhc.hackproject;

import com.fasterxml.jackson.annotation.JsonProperty;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

@RestController
public class StockController {
    String PRIVATE_KEY = "5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU";

    @PostMapping("/processStockData")
    public ResponseEntity<String> processStockData(@RequestBody StockData stockdata) throws IOException {
        System.out.println("This is called");

        System.out.println(stockdata.stockName);
        System.out.println(stockdata.stockPrices[0]);
        System.out.println(stockdata.startDate);
        System.out.println(stockdata.endDate);
        System.out.println(stockdata.requestId);
        System.out.println("end");

        return ResponseEntity.ok("Data processed successfully");
    }

    @GetMapping("/chart")
    public ResponseEntity<byte[]> getChart() throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "Category 1", "Label 1");
        dataset.addValue(4, "Category 1", "Label 2");
        dataset.addValue(3, "Category 1", "Label 3");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Sample Chart",
                "Category",
                "Score",
                dataset
        );
        ByteArrayOutputStream chartOutputStream = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOutputStream, barChart, 800, 600);

        byte[] chartBytes = chartOutputStream.toByteArray();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(chartBytes);
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