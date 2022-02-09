package com.sssm.controller;

import com.sssm.exception.InvalidDataException;
import com.sssm.exception.NoDataException;
import com.sssm.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/stock")
@Api(tags = "Stock API")
@Tag(name = "Stock API", description = "Stock Operations")
public class StockController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    @ApiOperation(value = "Calculates the Dividend Yield for a given stock with a price.")
    @GetMapping(value = "/dividend-yield/{symbol}/{price}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully calculated the dividend yield."),
            @ApiResponse(code = 400, message = "Input data is invalid"),
            @ApiResponse(code = 404, message = "Stock not found in the stock data.")
    })
    public double calculateDividendYield(@PathVariable("symbol") String symbol, @PathVariable("price") double price) {
        LOGGER.info("Received request to calculate dividend yield for stock: {}, price: {}", symbol, price);
        try {
            double dividendYield = stockService.calculateDividendYield(symbol, price);
            LOGGER.info("Dividend yield calculated successfully.");
            return dividendYield;
        } catch (InvalidDataException | NoDataException exception) {
            HttpStatus httpStatus = exception instanceof InvalidDataException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            LOGGER.error("Dividend yield calculation failed, the response status is {}", httpStatus);
            throw new ResponseStatusException(httpStatus, exception.getMessage());
        }
    }

    @ApiOperation(value = "Calculates the P/E Ratio for a given stock with a market price.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully calculated the P/E ratio."),
            @ApiResponse(code = 400, message = "Input data is invalid"),
            @ApiResponse(code = 404, message = "Stock not found in the stock data.")
    })
    @GetMapping(value = "/pe-ratio/{symbol}/{price}")
    public double calculatePERatio(@PathVariable("symbol") String symbol, @PathVariable("price") double price) {
        LOGGER.info("Received request to calculate PE Ratio for stock: {}, price: {}", symbol, price);
        try {
            double peRatio = stockService.calculatePERatio(symbol, price);
            LOGGER.info("P/E ratio calculated successfully.");
            return peRatio;
        } catch (InvalidDataException | NoDataException exception) {
            HttpStatus httpStatus = exception instanceof InvalidDataException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
            LOGGER.error("P/E ratio calculation failed, the response status is {}", httpStatus);
            throw new ResponseStatusException(httpStatus, exception.getMessage());
        }
    }
}
