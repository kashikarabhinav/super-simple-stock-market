package com.sssm.service;

import com.sssm.domain.Stock;
import com.sssm.domain.StockType;
import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockRepository stockRepository;

    @Override
    public Stock getStock(String symbol) {
        Optional<Stock> result = Optional.ofNullable(stockRepository.get(symbol));
        if (result.isPresent()) {
            Stock stock = result.get();
            LOGGER.info("Stock data: {}", stock);
            return stock;
        }
        throw new NoDataException("Stock with symbol: " + symbol + " not found.");
    }

    @Override
    public double calculateDividendYield(String symbol, double price) {
        LOGGER.info("Calculating dividend yield.");
        Stock stock = validateInputAndGetStockData(symbol, price);
        return getDividendYield(stock, price);
    }

    public double calculatePERatio(String symbol, double price) {
        LOGGER.info("Calculating P/E ratio.");
        double peRatio = 0.0;
        Stock stock = validateInputAndGetStockData(symbol, price);
        double dividend = getDividendYield(stock, price);
        if (dividend > 0.0) {
            peRatio = price / dividend;
        }
        LOGGER.info("The P/E ratio is: {}", peRatio);
        return peRatio;
    }

    private Stock validateInputAndGetStockData(String symbol, double price) {
        if (!StringUtils.hasText(symbol)) throw new InvalidDataException("Stock symbol can not be empty.");
        if (price <= 0.0)
            throw new InvalidDataException("Please enter a valid price for the calculation.");
        return getStock(symbol);
    }

    private double getDividendYield(Stock stock, double price) {
        double dividendYield;
        if (StockType.COMMON == stock.getType()) {
            dividendYield = stock.getLastDividend() / price;
        } else {
            dividendYield = (stock.getFixedDividend() * stock.getParValue()) / price;
        }
        LOGGER.info("The dividend yield is: {}", dividendYield);
        return dividendYield;
    }
}
