package com.sssm.service;

import com.sssm.domain.Trade;

public interface TradeService {

    Trade recordTrade(Trade tradeRequest);

    double calculateVolumeWeightedStockPrice(String symbol);

    double calculateGBCEAllShareIndex();
}
