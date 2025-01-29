package org.example;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.math.BigDecimal;

public class App {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public static void main(String[] args) {

        deleteAllBankAccounts();

        // Inicijalizacija računa
        createAccount("HR12345", new BigDecimal("5000"));
        createAccount("HR67890", new BigDecimal("2000"));

        // Pokretanje dva threada za simulaciju transakcija
        Thread thread1 = new Thread(() -> {
            System.out.println("[Thread 1] Stanje prije: " + findAccountByNumber("HR12345"));
            System.out.println("[Thread 1] Stanje prije: " + findAccountByNumber("HR67890"));
            transferMoney("HR12345", "HR67890", new BigDecimal("500"));
            try {
                Thread.sleep(5000); // Čekaj 5 sekundi
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Thread 1] Stanje nakon 5 sekundi: " + findAccountByNumber("HR12345"));
            System.out.println("[Thread 1] Stanje nakon 5 sekundi: " + findAccountByNumber("HR67890"));
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("[Thread 2] Stanje prije: " + findAccountByNumber("HR12345"));
            System.out.println("[Thread 2] Stanje prije: " + findAccountByNumber("HR67890"));
            transferMoney("HR67890", "HR12345", new BigDecimal("300"));
            try {
                Thread.sleep(3000); // Čekaj 3 sekunde
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Thread 2] Stanje nakon 3 sekunde: " + findAccountByNumber("HR12345"));
            System.out.println("[Thread 2] Stanje nakon 3 sekundi: " + findAccountByNumber("HR67890"));
        });

        thread1.start();
        thread2.start();
    }

    public static void deleteAllBankAccounts() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            session.createQuery("DELETE FROM BankAccount").executeUpdate();

            tx.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    // 🏦 Kreiranje bankovnog računa
    public static void createAccount(String accountNumber, BigDecimal balance) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            BankAccount account = new BankAccount(accountNumber, balance);
            session.persist(account);
            tx.commit();
            System.out.println("Račun kreiran: " + account);
        }
    }

    // 💰 Prijenos novca s jednog računa na drugi
    public static void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            BankAccount fromAccount = findAccountByNumber(fromAccountNumber);
            BankAccount toAccount = findAccountByNumber(toAccountNumber);

            // 🛑 Ako bilo koji račun ne postoji, prekini transakciju
            if (fromAccount == null || toAccount == null) {
                System.out.println("❌ ERROR: One of the accounts does not exist!");
                tx.rollback();
                return;
            }

            // 🏦 Provjera stanja računa
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                System.out.println("❌ ERROR: Insufficient funds!");
                tx.rollback();
                return;
            }

            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            session.update(fromAccount);
            session.update(toAccount);


            tx.commit();
            System.out.println("Prijenos uspješan: " + amount + " preneseno s računa " + fromAccount.getAccountNumber() + " na " + toAccount.getAccountNumber());
        }
    }

    public static BankAccount findAccountByNumber(String accountNumber) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM BankAccount WHERE accountNumber = :accNum";
            return session.createQuery(hql, BankAccount.class)
                    .setParameter("accNum", accountNumber)
                    .uniqueResult(); // Vraća jedan rezultat ili null
        }
    }
}