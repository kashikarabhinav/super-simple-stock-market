package com.sssm.data;

import com.sssm.domain.Trade;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TradeData {

    private ConcurrentHashMap<String, List<Trade>> tradeData;

    public ConcurrentHashMap<String, List<Trade>> getTradeData() {
        return tradeData;
    }

    @PostConstruct
    private void init() {
        tradeData = new ConcurrentHashMap<>();
    }
}
