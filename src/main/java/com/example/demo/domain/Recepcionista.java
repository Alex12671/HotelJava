package com.example.demo.domain;

public class Recepcionista {
    private String usuario;
    private String password;
    private String nom;
    private String cognoms;
    private String DNI;
    private String nacionalitat;
    private Integer telefon;
    private String email;


    public String getUsuario() {
        return this.usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public Recepcionista(String usuario, String password, String nom, String cognoms, String DNI, String nacionalitat, Integer telefon, String email) {
        this.usuario = usuario;
        this.password = password;
        this.nom = nom;
        this.cognoms = cognoms;
        this.DNI = DNI;
        this.nacionalitat = nacionalitat;
        this.telefon = telefon;
        this.email = email;

    }
}
