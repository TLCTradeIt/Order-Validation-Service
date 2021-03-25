package com.example.ordervalidationservice.Publishing_To_TE.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private Long clientId;
    private Double accBalance;
}
