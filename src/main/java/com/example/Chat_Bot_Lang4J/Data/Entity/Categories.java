package com.example.Chat_Bot_Lang4J.Data.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categories {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // Constructor tùy chỉnh để tạo Categories mà không cần products list
    public Categories(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.products = null; // Sẽ được set sau khi có products
    }
}
