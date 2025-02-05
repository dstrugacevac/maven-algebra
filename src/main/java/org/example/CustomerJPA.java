package org.example;

import jakarta.persistence.*;

import java.util.List;

public class CustomerJPA {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("JpaExampleUnit");

    public List<Customer> getAllCustomers() {
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT c FROM Customer c";
        TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
        List<Customer> customers = query.getResultList();
        em.close();
        return customers;
    }


    public Customer getCustomerByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT c FROM Customer c WHERE c.email = :email";
        TypedQuery<Customer> query = em.createQuery(jpql, Customer.class);
        query.setParameter("email", email);
        Customer customer = query.getSingleResult();
        em.close();
        return customer;
    }

    public void updateCustomerEmail(Long id, String newEmail) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        String jpql = "UPDATE Customer c SET c.email = :email WHERE c.id = :id";
        int updatedCount = em.createQuery(jpql)
                .setParameter("email", newEmail)
                .setParameter("id", id)
                .executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    public void deleteCustomer(Long id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        String jpql = "DELETE FROM Customer c WHERE c.id = :id";
        Query query = em.createQuery(jpql);
        query.setParameter("id", id);
        query.executeUpdate();

        em.getTransaction().commit();
        em.close();
    }
}
