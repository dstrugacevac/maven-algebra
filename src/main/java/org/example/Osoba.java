package org.example;

import jakarta.persistence.*;
import java.util.Date;



import jakarta.persistence.*;

@Entity
@Table(name="Covjek")
public class Osoba {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "Ime")
    private String ime;

    @Column(name = "Prezime")
    private String prezime;

    @Column(name = "DatumRodenja")
    private String datumRodenja;

    public Osoba() {
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getDatumRodenja() {
        return datumRodenja;
    }

    public void setDatumRodenja(String datumRodenja) {
        this.datumRodenja = datumRodenja;
    }
}