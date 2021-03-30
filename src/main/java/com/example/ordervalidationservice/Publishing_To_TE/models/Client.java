package com.example.ordervalidationservice.Publishing_To_TE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class Client {
    private Long clientId;
    private Double accBalance;

    public Client() {
    }

    public Client(Long clientId, Double accBalance) {
        this.clientId = clientId;
        this.accBalance = accBalance;
    }

    public Long getClientId() {
        return clientId;
    }

    public Double getAccBalance() {
        return accBalance;
    }
}
