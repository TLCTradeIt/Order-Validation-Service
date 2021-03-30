package com.example.ordervalidationservice.Publishing_To_TE;

import com.example.ordervalidationservice.Publishing_To_TE.models.Client;
import com.example.ordervalidationservice.Publishing_To_TE.models.Portfolio;
import com.example.ordervalidationservice.Publishing_To_TE.models.Product;

import java.util.Date;


public class OrderDto {
    private Long orderId;
    private int quantity;
    private Double price;
    private String side;
    private Date timestamp;
    private Product product;
    private Client client;
    private Portfolio portfolio;


    public OrderDto() {
    }

    public OrderDto(Long orderId, int quantity, Double price, String side, Date timestamp, Product product, Client client, Portfolio portfolio) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.timestamp = timestamp;
        this.product = product;
        this.client = client;
        this.portfolio = portfolio;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "orderId=" + orderId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", timestamp=" + timestamp +
                ", product=" + product +
                ", client=" + client +
                ", portfolio=" + portfolio +
                '}';
    }
}
