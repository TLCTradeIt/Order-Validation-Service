package com.example.ordervalidationservice.Publishing_To_TE;

import com.example.ordervalidationservice.Publishing_To_TE.models.Client;
import com.example.ordervalidationservice.Publishing_To_TE.models.Portfolio;
import com.example.ordervalidationservice.Publishing_To_TE.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDto {
    private Long orderId;
    private int quantity;
    private Double price;
    private String side;
    private Date timestamp;
    private Product product;
    private Client client;
    private Portfolio portfolio;


}
