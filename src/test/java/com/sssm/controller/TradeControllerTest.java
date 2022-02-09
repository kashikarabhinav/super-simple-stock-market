package com.sssm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sssm.domain.Trade;
import com.sssm.domain.TradeType;
import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.service.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TradeController.class)
public class TradeControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeService tradeService;

    public TradeControllerTest() {
    }

    private static Trade newTrade(String symbol, int quantity) {
        Trade trade = new Trade();
        trade.setSymbol(symbol);
        trade.setType(TradeType.BUY);
        trade.setTradePrice(100.0);
        trade.setQuantity(quantity);
        return trade;
    }

    @Test
    public void recordTrade() throws Exception {
        String symbol = "TEA";
        Trade trade = newTrade("TEA", 10);
        Mockito.when(tradeService.recordTrade(any(Trade.class))).thenReturn(trade);
        mockMvc.perform(post("/trade/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is(symbol)))
                .andExpect(jsonPath("$.tradePrice", is(100.0)));
    }

    @Test
    public void recordTradeInvalidData() throws Exception {
        Mockito.when(tradeService.recordTrade(any(Trade.class))).thenThrow(new InvalidDataException("Please enter a valid quantity to record the trade."));
        mockMvc.perform(post("/trade/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrade("TEA", 0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void recordTradeNoStockData() throws Exception {
        String symbol = "GIN";
        Mockito.when(tradeService.recordTrade(any(Trade.class))).thenThrow(new NoDataException("Stock with symbol: " + symbol + " not found."));
        mockMvc.perform(post("/trade/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTrade(symbol, 5))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void calculateVolumeWeightedStockPrice() throws Exception {
        Mockito.when(tradeService.calculateVolumeWeightedStockPrice("POP")).thenReturn(23.59);
        mockMvc.perform(get("/trade/volume-weighted-stock-price/POP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(23.59)));
    }

    @Test
    public void calculateVolumeWeightedStockPriceInvalidData() throws Exception {
        Mockito.when(tradeService.calculateVolumeWeightedStockPrice(" ")).thenThrow(new InvalidDataException("Stock symbol can not be empty."));
        mockMvc.perform(get("/trade/volume-weighted-stock-price/ "))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void calculateVolumeWeightedStockPriceNoStockData() throws Exception {
        String symbol = "RUN";
        Mockito.when(tradeService.calculateVolumeWeightedStockPrice(symbol)).thenThrow(new NoDataException("Stock with symbol: " + symbol + " not found."));
        mockMvc.perform(get("/trade/volume-weighted-stock-price/RUN"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void calculateGBCEAllShareIndex() throws Exception {
        Mockito.when(tradeService.calculateGBCEAllShareIndex()).thenReturn(147.89);
        mockMvc.perform(get("/trade/gbce"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(147.89)));
    }
}