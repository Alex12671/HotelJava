package com.example.demo.domain;

public class Cliente {
    private String nom;
    private String cognoms;
    private String DNI;
    private String nacionalitat;
    private Integer telefon;
    private String email;
    private String ocupacio;
    private String estat_civil;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNacionalitat() {
        return nacionalitat;
    }

    public void setNacionalitat(String nacionalitat) {
        this.nacionalitat = nacionalitat;
    }

    public Integer getTelefon() {
        return telefon;
    }

    public void setTelefon(Integer telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOcupacio() {
        return ocupacio;
    }

    public void setOcupacio(String ocupacio) {
        this.ocupacio = ocupacio;
    }

    public String getEstat_civil() {
        return estat_civil;
    }

    public void setEstat_civil(String estat_civil) {
        this.estat_civil = estat_civil;
    }

    public Cliente(String estat_civil, String ocupacio, String nom, String cognoms, String DNI, String nacionalitat, Integer telefon, String email) {

        this.nom = nom;
        this.cognoms = cognoms;
        this.DNI = DNI;
        this.nacionalitat = nacionalitat;
        this.telefon = telefon;
        this.email = email;
        this.ocupacio = ocupacio;
        this.estat_civil = estat_civil;

    }
}
