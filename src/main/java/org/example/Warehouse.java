package org.example;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Warehouse {
    @Id
    private Long warehouseId;
    private String name;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Warehouse() {}

    public Warehouse(Long warehouseId, String name) {
        this.warehouseId = warehouseId;
        this.name = name;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public Product findProductByName(String productName) {
        for (Product product : products) {
            if (product.getName().equals(productName)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId='" + warehouseId + '\'' +
                ", name='" + name + '\'' +
                ", products=" + products +
                '}';
    }
}