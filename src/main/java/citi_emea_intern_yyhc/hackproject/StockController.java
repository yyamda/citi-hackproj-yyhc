package citi_emea_intern_yyhc.hackproject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {
    String PRIVATE_KEY = "5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU";

    @PostMapping("/processStockData")
    public ResponseEntity<String> processStockData(@RequestBody StockData stockdata) {
        System.out.println("This is called");
        System.out.println(stockdata.stockName);
        System.out.println(stockdata.stockPrices[0]);
        System.out.println(stockdata.startDate);
        System.out.println(stockdata.endDate);
        System.out.println(stockdata.requestId);


        System.out.println("end");


        return ResponseEntity.ok("Data processed successfully");
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