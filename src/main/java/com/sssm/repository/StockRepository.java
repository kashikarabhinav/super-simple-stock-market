package com.sssm.repository;

import com.sssm.data.StockData;
import com.sssm.domain.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StockRepository {

    @Autowired
    private StockData stockData;

    private ConcurrentHashMap<String, Stock> getMap() {
        return stockData.getStockData();
    }

    public Stock get(String symbol) {
        return getMap().get(symbol);
    }
}
