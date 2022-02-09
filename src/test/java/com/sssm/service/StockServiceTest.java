package com.sssm.service;

import com.sssm.domain.Stock;
import com.sssm.domain.StockType;
import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StockService.class)
public class StockServiceTest {

    @Autowired
    private StockService stockService;

    @MockBean
    private StockRepository stockRepository;

    private static Stock newStock(String symbol, StockType stockType) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setType(stockType);
        stock.setFixedDividend(2.0);
        stock.setLastDividend(8.0);
        stock.setParValue(100.0);
        return stock;
    }

    @Test
    public void calculateCommonDividendYield() {
        String stock = "TEA";
        Mockito.when(stockRepository.get(stock)).thenReturn(newStock(stock, StockType.COMMON));
        double dividendYield = stockService.calculateDividendYield(stock, 10.0);
        assertEquals(0.8, dividendYield);
    }

    @Test
    public void calculatePreferredDividendYield() {
        String stock = "GIN";
        Mockito.when(stockRepository.get(stock)).thenReturn(newStock(stock, StockType.PREFERRED));
        double dividendYield = stockService.calculateDividendYield(stock, 5.0);
        assertEquals(40.0, dividendYield);
    }

    @Test
    public void calculatePERatio() {
        String stock = "TEA";
        Mockito.when(stockRepository.get(stock)).thenReturn(newStock(stock, StockType.COMMON));
        double peRatio = stockService.calculatePERatio(stock, 5.0);
        assertEquals(3.125, peRatio);
    }

    @Test
    public void invalidSymbol() {
        String stock = "";
        String errorMessage = "Stock symbol can not be empty.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(stockRepository.get(stock)).thenThrow(new InvalidDataException(errorMessage));
            stockService.calculateDividendYield(stock, 5.0);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void invalidPrice() {
        String stock = "GIN";
        String errorMessage = "Please enter a valid price for the calculation.";
        Exception exception = assertThrows(InvalidDataException.class, () -> {
            Mockito.when(stockRepository.get(stock)).thenThrow(new InvalidDataException(errorMessage));
            stockService.calculatePERatio(stock, 0.0);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Test
    public void noStock() {
        String stock = "BEV";
        String errorMessage = "Stock with symbol: " + stock + " not found.";
        Exception exception = assertThrows(NoDataException.class, () -> {
            Mockito.when(stockRepository.get(stock)).thenReturn(null);
            stockService.getStock(stock);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }
}