package citi_emea_intern_yyhc.hackproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class StockController {
    String PRIVATE_KEY = "5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU";
    String AlphaVantage_Key = "12JWP14CSW66DSLG";
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

    @GetMapping("/chart")
    public ResponseEntity<byte[]> getChart() throws Exception {
        XYSeries series = new XYSeries("Stock Price");

        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;

        double startPrice = 0;
        double endPrice = 0;

        for (int i = 0; i < stockData.stockPrices.length; i++) {
            ObjectNode node = objectMapper.convertValue(stockData.stockPrices[i], ObjectNode.class);
            double price = node.get("vw").asDouble();
            series.add(i, price);
            minPrice = Math.min(minPrice, price);
            maxPrice = Math.max(maxPrice, price);
            if (i==0){
                startPrice = price;
            }
            if (i==stockData.stockPrices.length-1){
                endPrice = price;
            }
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


        // title, background/grid line color, legend position
        chart.setTitle(new TextTitle("Stock Price for " + stockData.stockName, new Font("Serif", Font.BOLD, 24)));
        XYPlot plot = chart.getXYPlot();
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getLegend().setPosition(RectangleEdge.BOTTOM);

        // customize the renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        if(startPrice <= endPrice){
            renderer.setSeriesPaint(0, Color.RED);
        } else{
            renderer.setSeriesPaint(0, Color.GREEN);
        }
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        // customize axis labels and fonts
        plot.getDomainAxis().setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
        plot.getDomainAxis().setLabelFont(new Font("Dialog", Font.BOLD, 14));
        plot.getRangeAxis().setLabelFont(new Font("Dialog", Font.BOLD, 14));

        // set custom tick labels for start and end dates
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoTickUnitSelection(false);
        domainAxis.setTickUnit(new NumberTickUnit(stockData.stockPrices.length - 1));

        // convert start and end dates to Date objects
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = sdf.parse(stockData.startDate);
        Date endDate = sdf.parse(stockData.endDate);

        // format dates as strings
        String startLabel = new SimpleDateFormat("MMM dd, yyyy").format(startDate);
        String endLabel = new SimpleDateFormat("MMM dd, yyyy").format(endDate);

        // set custom tick labels for start and end dates
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setStandardTickUnits(new TickUnits());
        domainAxis.setTickMarkInsideLength(0.0f);

        domainAxis.setTickLabelPaint(Color.BLACK);
        domainAxis.setTickMarkPaint(Color.BLACK);
        domainAxis.setTickUnit(new NumberTickUnit(1));
        domainAxis.setTickUnit(new NumberTickUnit(stockData.stockPrices.length - 1));

        plot.getDomainAxis().setTickLabelsVisible(false);

        XYTextAnnotation startAnnotation = new XYTextAnnotation(startLabel, 0, series.getMinY());
        XYTextAnnotation endAnnotation = new XYTextAnnotation(endLabel, stockData.stockPrices.length - 1, series.getMinY());
        startAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 12));
        endAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 12));
        startAnnotation.setTextAnchor(TextAnchor.TOP_LEFT);
        endAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addAnnotation(startAnnotation);
        plot.addAnnotation(endAnnotation);

        // adjust the y-axis range
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(minPrice - 5, maxPrice + 5);

        // add annotations for all data points
        for (int i = 0; i < stockData.stockPrices.length; i++) {
            ObjectNode node = objectMapper.convertValue(stockData.stockPrices[i], ObjectNode.class);
            double price = node.get("vw").asDouble();
            XYTextAnnotation annotation = new XYTextAnnotation(
                    String.format("%.2f", price),
                    i,
                    price
            );
            annotation.setFont(new Font("Dialog", Font.PLAIN, 10));
            annotation.setTextAnchor(TextAnchor.HALF_ASCENT_CENTER);
            if (i == 0) {
                annotation.setX(i + 0.5);
            } else if (i % 2 == 0) {
                annotation.setY(price + 0.5);
            } else {
                annotation.setY(price - 0.5);
            }
            plot.addAnnotation(annotation);
        }

        // generate the image and return the image as a response
        BufferedImage bufferedImage = chart.createBufferedImage(800, 600);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeBufferedImageAsPNG(baos, bufferedImage);
        byte[] pngData = baos.toByteArray();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(pngData);
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