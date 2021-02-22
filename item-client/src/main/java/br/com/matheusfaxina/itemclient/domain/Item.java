package br.com.matheusfaxina.itemclient.domain;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    private String id;

    private String description;

    private Double price;

}
