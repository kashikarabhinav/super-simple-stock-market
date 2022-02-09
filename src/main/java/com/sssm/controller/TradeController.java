package com.sssm.controller;

import com.sssm.domain.Trade;
import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.service.TradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/trade")
@Api(tags = "Trade API")
@Tag(name = "Trade API", description = "Trade Operations")
public class TradeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService tradeService;

    @ApiOperation(value = "Records a trade.", response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully recorded the trade."),
            @ApiResponse(code = 400, message = "Input data is invalid"),
            @ApiResponse(code = 404, message = "Stock not found for the given trade data.")
    })
    @PostMapping(value = "/record")
    public Trade recordTrade(@RequestBody Trade tradeRequest) {
        LOGGER.info("Received request to record trade: {}", tradeRequest);
        try {
            Trade trade = tradeService.recordTrade(tradeRequest);
            LOGGER.info("Trade recorded successfully.");
            return trade;
        } catch (InvalidDataException | NoDataException exception) {
            HttpStatus httpStatus = exception instanceof InvalidDataException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            LOGGER.error("Unable to record trade, the response status is {}", httpStatus);
            throw new ResponseStatusException(httpStatus, exception.getMessage());
        }
    }

    @ApiOperation(value = "Calculates Volume Weighted Stock Price for a given stock.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully calculated volume weighted stock price."),
            @ApiResponse(code = 400, message = "Input data is invalid"),
            @ApiResponse(code = 404, message = "Stock not found for the given stock symbol.")
    })
    @GetMapping(value = "/volume-weighted-stock-price/{symbol}")
    public double calculateVolumeWeightedStockPrice(@PathVariable("symbol") String symbol) {
        LOGGER.info("Received request to calculate volume weighted stock price for stock: {}", symbol);
        try {
            double volumeWeightedStockPrice = tradeService.calculateVolumeWeightedStockPrice(symbol);
            LOGGER.info("Volume Weighted Stock Price calculated successfully.");
            return volumeWeightedStockPrice;
        } catch (InvalidDataException | NoDataException exception) {
            HttpStatus httpStatus = exception instanceof InvalidDataException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            LOGGER.error("Volume Weighted Stock Price calculation failed, the response status is {}", httpStatus);
            throw new ResponseStatusException(httpStatus, exception.getMessage());
        }
    }

    @ApiOperation(value = "Calculates GBCE All Share Index.")
    @ApiResponse(code = 200, message = "Successfully calculated volume weighted stock price.")
    @GetMapping(value = "/gbce")
    public double calculateGBCEAllShareIndex() {
        LOGGER.info("Received request to calculate GBCE All Share Index.");
        double gbceAllShareIndex = tradeService.calculateGBCEAllShareIndex();
        LOGGER.info("GBCE All Share Index calculated successfully.");
        return gbceAllShareIndex;
    }
}
