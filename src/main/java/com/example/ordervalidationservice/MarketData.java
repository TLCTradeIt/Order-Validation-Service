package com.example.ordervalidationservice;

public class MarketData {
    private Double LAST_TRADED_PRICE;
    private Double BID_PRICE;
    private Integer SELL_LIMIT;
    private Double MAX_PRICE_SHIFT;
    private String TICKER;
    private Double ASK_PRICE;
    private Integer BUY_LIMIT;

    public Double getLAST_TRADED_PRICE() {
        return LAST_TRADED_PRICE;
    }

    public Double getBID_PRICE() {
        return BID_PRICE;
    }

    public Integer getSELL_LIMIT() {
        return SELL_LIMIT;
    }

    public Double getMAX_PRICE_SHIFT() {
        return MAX_PRICE_SHIFT;
    }

    public String getTICKER() {
        return TICKER;
    }

    public Double getASK_PRICE() {
        return ASK_PRICE;
    }

    public Integer getBUY_LIMIT() {
        return BUY_LIMIT;
    }

}
