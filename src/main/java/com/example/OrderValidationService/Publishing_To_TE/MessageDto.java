package com.example.OrderValidationService.Publishing_To_TE;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private int id;
    private String name;
    private int qty;
    private long price;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                ", price=" + price +
                '}';
    }
}
