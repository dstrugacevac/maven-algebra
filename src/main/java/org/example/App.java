package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.Arrays;

public class App {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("UserPU");


    public static void main(String[] args) {
        // Persistiranje korisnika
        User user = persistUser("john_doe", "john@example.com");
        System.out.println("Persisted: " + user);

        // Odvajanje korisnika
        detachUser(user);

        // Ponovno povezivanje korisnika
        User mergedUser = reattachUser(user);
        System.out.println("Reattached: " + mergedUser);

        // Brisanje korisnika
        deleteUser(mergedUser);
        System.out.println("User deleted.");
    }

    public static User persistUser(String username, String email) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        User user1 = new User();
        user1.setUsername(username);
        user1.setEmail(email);

        User user3 = new User();
        user3.setUsername(username);
        user3.setEmail(email);

        try {
            tx.begin();
            em.persist(user);
            em.persist(user1);
            em.persist(user3);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return user;
    }

    public static void detachUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User managedUser = em.find(User.class, user.getId());
            System.out.println("Managed before detach: " + managedUser);
            em.detach(managedUser); // Odvajanje objekta iz sesije
            System.out.println("Detached: " + managedUser);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static User reattachUser(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        User mergedUser = null;
        try {
            tx.begin();
            mergedUser = em.merge(user); // Ponovno povezivanje objekta
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return mergedUser;
    }

    public static void deleteUser(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            User managedUser = em.find(User.class, user.getId());
            em.remove(managedUser); // Brisanje objekta
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}