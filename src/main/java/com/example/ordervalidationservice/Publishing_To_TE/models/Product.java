package com.example.ordervalidationservice.Publishing_To_TE.models;


public class Product {
    private Long productId;
    private String ticker;
    private String exchange;
    private Integer prodQuantity;
    private Portfolio portfolio;

    public Product() {
    }

    public Product(Long productId, String ticker, String exchange, Integer prodQuantity, Portfolio portfolio) {
        this.productId = productId;
        this.ticker = ticker;
        this.exchange = exchange;
        this.prodQuantity = prodQuantity;
        this.portfolio = portfolio;
    }

    public Long getProductId() {
        return productId;
    }

    public String getTicker() {
        return ticker;
    }

    public String getExchange() {
        return exchange;
    }

    public Integer getProdQuantity() {
        return prodQuantity;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}
