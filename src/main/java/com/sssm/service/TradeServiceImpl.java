package com.sssm.service;

import com.sssm.domain.Trade;
import com.sssm.domain.TradeType;
import com.sssm.exception.InvalidDataException;
import com.sssm.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeServiceImpl.class);

    @Value("${minutes.last.trades}")
    private Integer minutes;

    @Autowired
    private StockService stockService;

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    public Trade recordTrade(Trade tradeRequest) {
        LOGGER.info("Recording the trade.");
        String symbol = tradeRequest.getSymbol();
        validateTrade(tradeRequest);
        validateStock(symbol);
        tradeRequest.setTradeTimestamp(LocalDateTime.now());
        Trade recordedTrade = tradeRepository.save(symbol, tradeRequest);
        LOGGER.info("The recorded trade is: {}", recordedTrade);
        return recordedTrade;
    }

    public double calculateVolumeWeightedStockPrice(String symbol) {
        LOGGER.info("Calculating Volume Weighted Stock Price.");
        validateStock(symbol);
        double volumeWeightedStockPrice = 0;
        List<Trade> tradeList = tradeRepository.get(symbol);
        LOGGER.info("All Trades for {} are: {}", symbol, tradeList);
        if (!CollectionUtils.isEmpty(tradeList)) {
            List<Trade> tradesInGivenTime = tradeList
                    .stream()
                    .filter(trade -> trade.getTradeTimestamp().isAfter(LocalDateTime.now().minusMinutes(minutes)))
                    .collect(Collectors.toList());
            LOGGER.info("Trades in past {} minutes are: {}", minutes, tradesInGivenTime);
            double priceQuantitySum = tradesInGivenTime.stream().mapToDouble(trade -> trade.getQuantity() * trade.getTradePrice()).sum();
            int quantitySum = tradesInGivenTime.stream().mapToInt(Trade::getQuantity).sum();
            volumeWeightedStockPrice = priceQuantitySum / quantitySum;
        }
        LOGGER.info("The Volume Weighted Stock Price is: {}", volumeWeightedStockPrice);
        return volumeWeightedStockPrice;
    }

    public double calculateGBCEAllShareIndex() {
        LOGGER.info("Calculating GBCE All Share Index.");
        double gbceAllShareIndex = 0;
        Collection<List<Trade>> tradeList = tradeRepository.getAllTrades();
        LOGGER.info("All Trades are: {}", tradeList);
        if (!CollectionUtils.isEmpty(tradeList)) {
            double priceResult = 1;
            List<Trade> trades = tradeList.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            for (Trade trade : trades) {
                priceResult *= trade.getTradePrice();
            }
            double tradeSize = tradeList.size();
            gbceAllShareIndex = Math.pow(priceResult, 1 / tradeSize);
        }
        LOGGER.info("The GBCE All Share Index is: {}", gbceAllShareIndex);
        return gbceAllShareIndex;
    }

    private void validateTrade(Trade tradeRequest) {
        if (tradeRequest.getQuantity() <= 0)
            throw new InvalidDataException("Please enter a valid quantity to record the trade.");
        if (tradeRequest.getTradePrice() <= 0.0)
            throw new InvalidDataException("Please enter a valid trade price to record the trade.");
        if (TradeType.BUY != tradeRequest.getType() && TradeType.SELL != tradeRequest.getType())
            throw new InvalidDataException("Trade type can only be BUY or SELL.");
    }

    private void validateStock(String symbol) {
        if (!StringUtils.hasText(symbol)) throw new InvalidDataException("Stock symbol can not be empty.");
        stockService.getStock(symbol);
    }
}
