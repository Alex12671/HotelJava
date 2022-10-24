package com.example.demo;
import java.sql.*;

public class Conexion {

    private static Connection conexion;
    private static final String usuario = "root";
    private static final String contraseña = "root";
    private static final String url = "jdbc:mysql://localhost:3306/hotel";

    public static Connection conectar(){
        try{
            conexion = DriverManager.getConnection(url, usuario, contraseña);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return conexion;
    }

}
