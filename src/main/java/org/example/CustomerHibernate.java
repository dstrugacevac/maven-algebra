package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class CustomerHibernate {


    public List<Customer> getAllCustomers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT c FROM Customer c";
            Query<Customer> query = session.createQuery(hql, Customer.class);
            return query.list();
        }
    }


    public Customer getCustomerByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Customer WHERE email = :email";
            Query<Customer> query = session.createQuery(hql, Customer.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        }
    }

    public void updateCustomerEmail(Long id, String newEmail) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "UPDATE Customer SET email = :email WHERE id = :id";
            Query query = session.createQuery(hql);
            query.setParameter("email", newEmail);
            query.setParameter("id", id);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void deleteCustomer(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "DELETE FROM Customer WHERE id = :id";
            int query = session.createQuery(hql)
            .setParameter("id", id)
            .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
