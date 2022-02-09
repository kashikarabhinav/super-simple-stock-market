package com.sssm.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sssm.domain.Stock;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockData {

    private ConcurrentHashMap<String, Stock> stockData;

    public ConcurrentHashMap<String, Stock> getStockData() {
        return stockData;
    }

    @PostConstruct
    private void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        stockData = new ConcurrentHashMap<>();
        try {
            List<Stock> stocks = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("stock-data.json"), new TypeReference<>() {
            });
            stocks.forEach(stock -> stockData.put(stock.getSymbol(), stock));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
