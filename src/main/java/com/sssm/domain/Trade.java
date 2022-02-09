package com.sssm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class Trade {
    @ApiModelProperty(required = true, value = "Stock symbol")
    private String symbol;

    @ApiModelProperty(required = true, value = "Trade type: BUY|SELL")
    private TradeType type;

    @ApiModelProperty(required = true, value = "Trade price")
    private double tradePrice;

    @ApiModelProperty(required = true, value = "Trade quantity")
    private int quantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tradeTimestamp;

    public Trade() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonIgnore
    public LocalDateTime getTradeTimestamp() {
        return tradeTimestamp;
    }

    public void setTradeTimestamp(LocalDateTime tradeTimestamp) {
        this.tradeTimestamp = tradeTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (Double.compare(trade.tradePrice, tradePrice) != 0) return false;
        if (quantity != trade.quantity) return false;
        if (!Objects.equals(symbol, trade.symbol)) return false;
        if (type != trade.type) return false;
        return Objects.equals(tradeTimestamp, trade.tradeTimestamp);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = symbol != null ? symbol.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        temp = Double.doubleToLongBits(tradePrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + (tradeTimestamp != null ? tradeTimestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "symbol='" + symbol + '\'' +
                ", type=" + type +
                ", tradePrice=" + tradePrice +
                ", quantity=" + quantity +
                ", tradeTimestamp=" + tradeTimestamp +
                '}';
    }
}
