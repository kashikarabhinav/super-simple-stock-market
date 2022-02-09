package com.sssm.controller;


import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    public void calculateDividendYield() throws Exception {
        Mockito.when(stockService.calculateDividendYield("TEA", 100.0)).thenReturn(1.8891);
        mockMvc.perform(get("/stock/dividend-yield/TEA/100.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1.8891)));
    }

    @Test
    public void calculateDividendYieldInvalidData() throws Exception {
        Mockito.when(stockService.calculateDividendYield("TEA", 0.0)).thenThrow(new InvalidDataException("Please enter a valid price for the calculation."));
        mockMvc.perform(get("/stock/dividend-yield/TEA/0.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void calculateDividendYieldNoData() throws Exception {
        Mockito.when(stockService.calculateDividendYield("HOP", 10.0)).thenThrow(new NoDataException("Stock with symbol: HOP not found."));
        mockMvc.perform(get("/stock/dividend-yield/HOP/10.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void calculatePERatio() throws Exception {
        Mockito.when(stockService.calculatePERatio("POP", 555.00)).thenReturn(38503.125);
        mockMvc.perform(get("/stock/pe-ratio/POP/555.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(38503.125)));
    }

    @Test
    public void calculatePERatioInvalidData() throws Exception {
        Mockito.when(stockService.calculatePERatio("TEA", 0.0)).thenThrow(new InvalidDataException("Please enter a valid price for the calculation."));
        mockMvc.perform(get("/stock/pe-ratio/TEA/0.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void calculatePERatioNoData() throws Exception {
        Mockito.when(stockService.calculatePERatio("HOP", 10.0)).thenThrow(new NoDataException("Stock with symbol: HOP not found."));
        mockMvc.perform(get("/stock/pe-ratio/HOP/10.0"))
                .andExpect(status().isNotFound());
    }
}