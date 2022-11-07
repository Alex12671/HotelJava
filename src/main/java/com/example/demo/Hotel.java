package com.example.demo;

import com.example.demo.domain.Escena;
import com.example.demo.infrastructure.DatabaseConnection;
import com.example.demo.infrastructure.MySQLRepository;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Hotel extends Application {

    private Escena scene;
    private MySQLRepository mySQLRepository;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws SQLException, IOException {
        Connection connection = DatabaseConnection.connect();
        Statement s = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        this.mySQLRepository = new MySQLRepository(s);
        this.scene = new Escena(mySQLRepository);

        stage.setTitle("Iniciar sesión");

        stage.setScene(this.scene.LoginPage(stage));
        stage.show();
    }







}