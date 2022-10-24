package com.example.demo;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import static java.lang.String.valueOf;

public class Hotel extends Application {


    public static void main(String[] args) throws SQLException {

        launch();

    }

    @Override
    public void start(Stage stage) throws SQLException, IOException {
        Connection conexion = Conexion.conectar();
        Statement s = conexion.createStatement();

        stage.setTitle("Iniciar sesión");
        Scene iniciarsesion = iniciarSesion(stage, s);

        stage.setScene(iniciarsesion);
        stage.show();
    }


    public int Validar(String usuario, String password, Statement s) throws SQLException {
        int rows = 0;
        ResultSet rs = s.executeQuery("SELECT * FROM admin WHERE user = '" + usuario + "' AND password = '" + password + "'");
        while (rs.next()) {
            rows++;
        }
        if (rows == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public Scene iniciarSesion(Stage stage, Statement s) throws IOException {

        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("Login.fxml"));
        Scene iniciarsesion = new Scene(fxmlLoader.load());

        Button Loginbtn = (Button) iniciarsesion.lookup("#loginbtn");
        Button registrar = (Button) iniciarsesion.lookup("#registrarbtn");
        Button exit = (Button) iniciarsesion.lookup("#exitbtn");
        PasswordField pwBox = (PasswordField) iniciarsesion.lookup("#password");

        Loginbtn.setOnAction(event -> {
            String usuario = ((TextField) iniciarsesion.lookup("#usuario")).getText();
            String password = ((PasswordField) iniciarsesion.lookup("#password")).getText();
            try {
                if (Validar(usuario, password, s) == 1) {

                    Scene inicio = panelAdministrador(stage, s, validarRecepcionista(stage, s));
                    stage.setScene(inicio);
                    stage.centerOnScreen();
                } else {
                    stage.setScene(error);
                    Label err = (Label) error.lookup("#error");
                    err.setText("Las credenciales son incorrectas");
                    Button volver = (Button) error.lookup("#volver");
                    volver.setOnAction(event2 -> {
                        stage.setScene(iniciarsesion);
                    });
                }
            } catch (SQLException | IOException ex) {
                System.out.println("No se pudo ejecutar la consulta: ");
                ex.printStackTrace();
            }
        });

        registrar.setOnAction(event -> {
            Scene registro = null;
            try {
                registro = pantallaRegistro(stage, s);
                stage.setTitle("Registrar recepcionista");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(registro);
            stage.centerOnScreen();
        });

        exit.setOnAction(event -> {
            Platform.exit();
        });

        return iniciarsesion;
    }

    public Scene pantallaRegistro(Stage stage, Statement s) throws IOException {
        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("Register.fxml"));
        Scene registro = new Scene(fxmlLoader.load());

        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("ConfirmarRegistro.fxml"));
        Scene confirmar = new Scene(fxmlLoad.load());

        Button ReturnBtn = (Button) registro.lookup("#returnbtn");
        Button registerBtn = (Button) registro.lookup("#registerbtn");


        registerBtn.setOnAction(event -> {
            try {

                String usuario = ((TextField) registro.lookup("#usuario")).getText();
                String password = ((PasswordField) registro.lookup("#password")).getText();
                String nom = ((TextField) registro.lookup("#nom")).getText();
                String cognoms = ((TextField) registro.lookup("#cognoms")).getText();
                String DNI = ((TextField) registro.lookup("#dni")).getText();
                String nacionalitat = ((TextField) registro.lookup("#nacionalitat")).getText();
                String telefon = ((TextField) registro.lookup("#telefon")).getText();
                try {
                    Integer tlf = Integer.parseInt(telefon);
                    String email = ((TextField) registro.lookup("#email")).getText();
                    Recepcionista r1 = new Recepcionista(usuario, password, nom, cognoms, DNI, nacionalitat, tlf, email);
                    int rows = s.executeUpdate("INSERT INTO recepcionistas VALUES ('" + r1.getUsuario() + "','" + r1.getPassword() + "','" + r1.getNom() + "','" + r1.getCognoms() + "','" + r1.getDNI() + "','" + r1.getNacionalitat() + "','" + r1.getTelefon() + "','" + r1.getEmail() + "','0')");
                    stage.setScene(confirmar);
                    Button finalizar = (Button) confirmar.lookup("#finalizar");
                    finalizar.setOnAction(event2 -> {
                        try {
                            stage.setScene(iniciarSesion(stage, s));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (NumberFormatException e) {

                    stage.setScene(error);
                    Label err = (Label) error.lookup("#error");
                    err.setText("El campo teléfono solo puede contener números");
                    Button volver = (Button) error.lookup("#volver");
                    volver.setOnAction(event2 -> {
                        stage.setScene(registro);
                    });
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        ReturnBtn.setOnAction(event -> {
            try {
                stage.setScene(iniciarSesion(stage, s));
                stage.setTitle("Iniciar sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return registro;
    }

    public Scene panelAdministrador(Stage stage, Statement s, Scene validarRec) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("PanelAdmin.fxml"));
        Scene inicio = new Scene(fxmlLoader.load());

        Button validacion = (Button) inicio.lookup("#validar");
        validacion.setOnAction(event -> {
            stage.setScene(validarRec);
            try {
                tablaRecepcionistas(stage, s, validarRec);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        int rows = 0;
        ResultSet rs = s.executeQuery("SELECT * FROM recepcionistas WHERE Validado = '0'");
        while (rs.next()) {
            rows++;
        }
        if (rows != 0) {
            validacion.setStyle("-fx-background-color: lightcoral");
        }
        return inicio;

    }

    public Scene validarRecepcionista(Stage stage, Statement s) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("Validar.fxml"));
        Scene validarRecepcionista = new Scene(fxmlLoad.load());


        return validarRecepcionista;
    }

    public TableView tablaRecepcionistas(Stage stage, Statement s, Scene validarRec) throws SQLException, IOException {
        TableView tabla = (TableView) validarRec.lookup("#tabla");
        TableColumn userCol = new TableColumn<>("Usuario");
        TableColumn passwordCol = new TableColumn("Password");
        TableColumn nomCol = new TableColumn("Nom");
        TableColumn cognomsCol = new TableColumn("Cognoms");
        TableColumn dniCol = new TableColumn("DNI");
        TableColumn nacCol = new TableColumn("Nacionalitat");
        TableColumn tlfCol = new TableColumn("Teléfon");
        TableColumn emailCol = new TableColumn("Email");
        tabla.setEditable(true);
        ResultSet rs = s.executeQuery("SELECT * FROM recepcionistas WHERE Validado = '0'");
        while (rs.next()) {
            userCol.setCellValueFactory(new PropertyValueFactory<>(rs.getString("User")));
            passwordCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Password")));
            nomCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Nom")));
            cognomsCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Cognoms")));
            dniCol.setCellValueFactory(new PropertyValueFactory(rs.getString("DNI")));
            nacCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Nacionalitat")));
            tlfCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Teléfon")));
            emailCol.setCellValueFactory(new PropertyValueFactory(rs.getString("Email")));
            System.out.println(rs.getString("User"));
            tabla.getColumns().add(userCol);
            tabla.getItems().add(
                    new Recepcionista("Pepito", "", "", "",
                            "", "", 0, ""));
        }

        return tabla;
    }

}