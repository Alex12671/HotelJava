package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.TableColumn.CellDataFeatures;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
        TableView tableview = (TableView) validarRec.lookup("#tabla");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = s.executeQuery("SELECT * FROM recepcionistas WHERE Validado = '0'");
        try {
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                tableview.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return tableview;
    }

}