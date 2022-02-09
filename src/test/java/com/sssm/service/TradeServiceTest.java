package com.sssm.service;

import com.sssm.domain.Trade;
import com.sssm.domain.TradeType;
import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TradeService.class)
public class TradeServiceTest {

    @Autowired
    private TradeService tradeService;

    @MockBean
    private TradeRepository tradeRepository;

    @MockBean
    private StockService stockService;

    private static Trade newTrade(String symbol, int quantity, double price, TradeType tradeType) {
        Trade trade = new Trade();
        trade.setSymbol(symbol);
        trade.setType(tradeType);
        trade.setTradePrice(price);
        trade.setQuantity(quantity);
        trade.setTradeTimestamp(LocalDateTime.now());
        return trade;
    }

    private static Collection<List<Trade>> getTrades(List<Trade> trades) {
        return Collections.singleton(trades);
    }

    @Test
    public void recordTrade() {
        String stock = "TEA";
        Trade trade = newTrade(stock, 2, 52.2, TradeType.BUY);
        Mockito.when(tradeRepository.save(anyString(), any(Trade.class))).thenReturn(trade);
        Trade recordedTrade = tradeService.recordTrade(trade);
        assertNotNull(recordedTrade);
        assertEquals(stock, recordedTrade.getSymbol());
    }

    @Test
    public void recordTradeInvalidQuantity() {
        String stock = "TEA";
        Trade trade = newTrade(stock, 0, 25.0, TradeType.BUY);
        String errorMessage = "Please enter a valid quantity to record the trade.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(tradeRepository.save(anyString(), any(Trade.class))).thenThrow(new InvalidDataException(errorMessage));
            tradeService.recordTrade(trade);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void recordTradeInvalidPrice() {
        String stock = "TEA";
        Trade trade = newTrade(stock, 20, 0.0, TradeType.SELL);
        String errorMessage = "Please enter a valid trade price to record the trade.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(tradeRepository.save(anyString(), any(Trade.class))).thenThrow(new InvalidDataException(errorMessage));
            tradeService.recordTrade(trade);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void recordTradeInvalidTradeType() {
        String stock = "TEA";
        Trade trade = newTrade(stock, 20, 10.0, null);
        String errorMessage = "Trade type can only be BUY or SELL.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(tradeRepository.save(anyString(), any(Trade.class))).thenThrow(new InvalidDataException(errorMessage));
            tradeService.recordTrade(trade);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void calculateVolumeWeightedStockPrice() {
        String stock = "TEA";
        Trade trade = newTrade(stock, 2, 52.2, TradeType.BUY);
        Trade trade1 = newTrade(stock, 3, 42.2, TradeType.BUY);
        Mockito.when(tradeRepository.get(anyString())).thenReturn(List.of(trade, trade1));
        double volumeWeightedStockPrice = tradeService.calculateVolumeWeightedStockPrice(stock);
        assertEquals(46.2, volumeWeightedStockPrice);
    }

    @Test
    public void calculateVolumeWeightedStockPriceZeroIfNoTradeFound() {
        String stock = "TEA";
        Mockito.when(tradeRepository.get(anyString())).thenReturn(Collections.emptyList());
        double volumeWeightedStockPrice = tradeService.calculateVolumeWeightedStockPrice(stock);
        assertEquals(0.0, volumeWeightedStockPrice);
    }

    @Test
    public void invalidStockSymbolForTrade() {
        String stock = "";
        String errorMessage = "Stock symbol can not be empty.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(tradeRepository.get(stock)).thenThrow(new InvalidDataException(errorMessage));
            tradeService.calculateVolumeWeightedStockPrice(stock);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void noStockFoundForTrade() {
        String stock = "LEA";
        String errorMessage = "Stock with symbol: " + stock + " not found.";
        Exception exception = assertThrows(NoDataException.class, () -> {
            Mockito.when(tradeRepository.get(stock)).thenThrow(new NoDataException(errorMessage));
            tradeService.calculateVolumeWeightedStockPrice(stock);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void calculateGBCEAllShareIndex() {
        Trade trade = newTrade("TEA", 2, 52.2, TradeType.BUY);
        Trade trade1 = newTrade("GIN", 3, 42.2, TradeType.BUY);
        Mockito.when(tradeRepository.getAllTrades()).thenReturn(getTrades(List.of(trade, trade1)));
        double volumeWeightedStockPrice = tradeService.calculateGBCEAllShareIndex();
        assertEquals(2202.84, volumeWeightedStockPrice);
    }

    @Test
    public void calculateGBCEAllShareIndexZeroIfNoTradeFound() {
        Mockito.when(tradeRepository.getAllTrades()).thenReturn(Collections.emptyList());
        double volumeWeightedStockPrice = tradeService.calculateGBCEAllShareIndex();
        assertEquals(0.0, volumeWeightedStockPrice);
    }
}