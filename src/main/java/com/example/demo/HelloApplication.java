package com.example.demo;

import java.awt.event.ActionEvent;

import com.example.demo.Conexion;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import static java.lang.String.valueOf;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws SQLException {
        Connection conexion = Conexion.conectar();
        Statement s = conexion.createStatement();

        stage.setTitle("Iniciar sesión");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        GridPane grid2 = new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));
        GridPane grid3 = new GridPane();
        grid3.setAlignment(Pos.CENTER);
        grid3.setHgap(10);
        grid3.setVgap(10);
        grid3.setPadding(new Insets(25, 25, 25, 25));

        Label userName = new Label("Usuario:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Image loginImage = new Image("file:///C:/Users/Alumne/IdeaProjects/demo/img/login.png");
        ImageView loginView = new ImageView(loginImage);
        loginView.setFitHeight(90);
        loginView.setPreserveRatio(true);
        StackPane login = new StackPane();
        login.getChildren().add(loginView);
        login.setPadding(new Insets(10, 10, 10, 10));
        grid.add(login, 2, 1, 1, 2);

        Image img = new Image("file:///C:/Users/Alumne/IdeaProjects/demo/img/save.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(20);
        view.setPreserveRatio(true);
        Button Loginbtn = new Button("Iniciar sesión");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        Loginbtn.setGraphic(view);
        hbBtn.getChildren().add(Loginbtn);
        grid.add(hbBtn, 1, 4);

        Image house = new Image("file:///C:/Users/Alumne/IdeaProjects/demo/img/house.png");
        ImageView view2 = new ImageView(house);
        view2.setFitHeight(20);
        view2.setPreserveRatio(true);
        Button exit = new Button("Salir");
        HBox exitBtn = new HBox(10);
        exitBtn.setAlignment(Pos.BOTTOM_RIGHT);
        exit.setGraphic(view2);
        exitBtn.getChildren().add(exit);
        grid.add(exitBtn, 1, 4);
        exitBtn.setPadding(new Insets(0, -50, 0, 0));

        Image register = new Image("file:///C:/Users/Alumne/IdeaProjects/demo/img/registro.png");
        ImageView view3 = new ImageView(register);
        view3.setFitHeight(20);
        view3.setPreserveRatio(true);
        Button registrar = new Button("Registrar");
        HBox registrarBtn = new HBox(10);
        registrarBtn.setAlignment(Pos.BOTTOM_RIGHT);
        registrar.setGraphic(view3);
        registrarBtn.getChildren().add(registrar);
        grid.add(registrarBtn, 1, 5);





        Scene iniciarsesion = new Scene(grid, 500, 450);
        Scene inicio = new Scene(grid2, 1000, 950);
        Scene registro = new Scene(grid3,1000,950);

        Loginbtn.setOnAction(event -> {
            String usuario = userTextField.getText();
            String password = pwBox.getText();
            try {
                if (Validar(usuario, password, s) == 1) {
                    stage.setScene(inicio);
                }
                else {
                    Text t = new Text();
                    t.setText("Las credenciales de inicio no son correctas");
                    grid.add(t,1,6);
                }
            } catch (SQLException ex) {
                System.out.println("No se pudo ejecutar la consulta");
            }
        });

        exit.setOnAction(event -> {
            Platform.exit();
        });

        stage.setScene(iniciarsesion);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        launch();


    }

    public int Validar(String usuario, String password, Statement s) throws SQLException {
        int rows = 0;
        ResultSet rs= s.executeQuery("SELECT * FROM admin WHERE user = '"+usuario+"' AND password = '"+password+"'");
        while(rs.next()) {
            rows++;
        }
        if(rows == 1) {
            return 1;
        }
        else {
            return 0;
        }
    }
}