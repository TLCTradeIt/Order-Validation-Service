package com.example.ordervalidationservice.Publishing_To_TE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Portfolio {
    private Long portfolioId;

    public Portfolio() {
    }

    public Portfolio(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

}
