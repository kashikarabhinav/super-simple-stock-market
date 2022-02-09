package com.sssm.repository;

import com.sssm.data.TradeData;
import com.sssm.domain.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TradeRepository {

    @Autowired
    private TradeData tradeData;

    private ConcurrentHashMap<String, List<Trade>> getMap() {
        return tradeData.getTradeData();
    }

    public Trade save(String symbol, Trade trade) {
        List<Trade> trades = get(symbol);
        if (!CollectionUtils.isEmpty(trades)) {
            getMap().get(symbol).add(trade);
        } else {
            trades = new ArrayList<>();
            trades.add(trade);
            getMap().put(symbol, trades);
        }
        return trade;
    }

    public List<Trade> get(String symbol) {
        return getMap().get(symbol);
    }

    public Collection<List<Trade>> getAllTrades() {
        return getMap().values();
    }
}

