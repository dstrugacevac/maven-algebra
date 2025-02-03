package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class App {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    public static void main(String[] args) {


        createWarehouse(123L, "Warehouse A");
        createWarehouse(456L, "Warehouse B");

        addProduct(123L, "TV", 100);
        addProduct(123L, "Laptop", 50);

        addProduct(456L, "Smartphone", 80);


        Thread thread1 = new Thread(() -> {
            System.out.println("[Thread 1] Initial inventory: " + findWarehouseById(123L));
            System.out.println("[Thread 1] Initial inventory: " + findWarehouseById(456L));
            transferProduct(123L, 456L, "TV", 30);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Thread 1] Inventory after 5 seconds: " + findWarehouseById(123L));
            System.out.println("[Thread 1] Inventory after 5 seconds: " + findWarehouseById(456L));
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("[Thread 2] Initial inventory: " + findWarehouseById(123L));
            System.out.println("[Thread 2] Initial inventory: " + findWarehouseById(456L));
            transferProduct(456L, 123L, "Smartphone", 20);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Thread 2] Inventory after 3 seconds: " + findWarehouseById(123L));
            System.out.println("[Thread 2] Inventory after 3 seconds: " + findWarehouseById(456L));
        });

        thread1.start();
        thread2.start();
    }

    public static void createWarehouse(Long warehouseId, String warehouseName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Warehouse warehouse = new Warehouse(warehouseId, warehouseName);
            session.persist(warehouse);
            tx.commit();
            System.out.println("Warehouse created: " + warehouse);
        }
    }

    public static Warehouse findWarehouseById(Long warehouseId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Warehouse WHERE warehouseId = :warehouseId";
            return session.createQuery(hql, Warehouse.class)
                    .setParameter("warehouseId", warehouseId)
                    .uniqueResult();
        }
    }

    public static void addProduct(Long warehouseId, String productName, int quantity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Warehouse warehouse = findWarehouseById(warehouseId);

            if (warehouse == null) {
                System.out.println("Warehouse not found!");
                return;
            }

            Product product = new Product(productName, quantity, warehouse);
            warehouse.addProduct(product);
            session.update(warehouse);
            session.persist(product);
            tx.commit();
            System.out.println("Product added");
        }
    }

    public static void transferProduct(Long fromWarehouseId, Long toWarehouseId, String productName, int quantity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Warehouse fromWarehouse = findWarehouseById(fromWarehouseId);
            Warehouse toWarehouse = findWarehouseById(toWarehouseId);

            if (fromWarehouse == null || toWarehouse == null) {
                System.out.println("Warehouses does not exist!");
                tx.rollback();
                return;
            }

            Product product = fromWarehouse.findProductByName(productName);
            if (product == null || product.getQuantity() < quantity) {
                System.out.println("Something went wrong ");
                tx.rollback();
                return;
            }

            product.setQuantity(product.getQuantity() - quantity);
            Product toProduct = toWarehouse.findProductByName(productName);

            if (toProduct == null) {
                toProduct = new Product(productName, quantity, toWarehouse);
                toWarehouse.addProduct(toProduct);
            } else {
                toProduct.setQuantity(toProduct.getQuantity() + quantity);
            }

            session.update(fromWarehouse);
            session.update(toWarehouse);

            tx.commit();
            System.out.println("Transfer successful: " + productName + " (" + quantity + " units) from Warehouse " + fromWarehouseId + " to Warehouse " + toWarehouseId);
        }
    }

}