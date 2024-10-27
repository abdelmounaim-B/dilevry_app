package org.tpjava.emsbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "ImageLink")
    private String ImageLink;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "category", nullable = true)
    private String category;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;
}
