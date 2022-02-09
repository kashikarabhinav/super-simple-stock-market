package com.sssm.service;

import com.sssm.domain.Stock;

public interface StockService {

    Stock getStock(String symbol);

    double calculateDividendYield(String symbol, double price);

    double calculatePERatio(String symbol, double price);
}
