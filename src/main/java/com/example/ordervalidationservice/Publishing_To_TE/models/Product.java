package com.example.ordervalidationservice.Publishing_To_TE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long productId;
    private String ticker;
    private String exchange;
    private Integer prodQuantity;
    private Portfolio portfolio;
}
