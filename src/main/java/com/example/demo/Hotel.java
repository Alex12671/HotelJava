package com.example.demo;

import com.example.demo.domain.Recepcionista;
import com.example.demo.infrastructure.DatabaseConnection;
import com.example.demo.infrastructure.MySQLRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
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
    private MySQLRepository mySQLRepository;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws SQLException, IOException {
        Connection connection = DatabaseConnection.connect();
        Statement s = connection.createStatement();
        this.mySQLRepository = new MySQLRepository(s);

        stage.setTitle("Iniciar sesión");
        Scene iniciarsesion = iniciarSesion(stage);

        stage.setScene(iniciarsesion);
        stage.show();
    }


    public boolean Validar(String usuario, String password) throws SQLException {
        ResultSet rs = this.mySQLRepository.GetAdminCredentials(usuario, password);
        int rows = 0;
        while (rs.next()) {
            rows++;
        }

        if (rows == 1) {
            return true;
        }

        return false;
    }

    public Scene iniciarSesion(Stage stage) throws IOException {

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
                validateCredentials(stage, error, iniciarsesion, usuario, password);
            } catch (SQLException | IOException ex) {
                System.out.println("No se pudo ejecutar la consulta: ");
                ex.printStackTrace();
            }
        });

        registrar.setOnAction(event -> {
            Scene registro;
            try {
                registro = pantallaRegistro(stage);
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

    private void validateCredentials(Stage stage, Scene error, Scene iniciarsesion, String usuario, String password) throws SQLException, IOException {
        if (Validar(usuario, password)) {
            Scene inicio = panelAdministrador(stage, validarRecepcionista());
            stage.setScene(inicio);
            stage.centerOnScreen();
        } else {
            stage.setScene(error);
            Label err = (Label) error.lookup("#error");
            err.setText("Las credenciales son incorrectas");
            Button volver = (Button) error.lookup("#volver");
            volver.setOnAction(event2 -> stage.setScene(iniciarsesion));
        }
    }

    public Scene pantallaRegistro(Stage stage) throws IOException {
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
                    int rows = this.mySQLRepository.UpdateRecepcionist(r1.getUsuario(), r1.getPassword(), r1.getNom(),
                            r1.getCognoms(), r1.getDNI(), r1.getNacionalitat(), r1.getTelefon(), r1.getEmail());
                    stage.setScene(confirmar);
                    Button finalizar = (Button) confirmar.lookup("#finalizar");
                    finalizar.setOnAction(event2 -> {
                        try {
                            stage.setScene(iniciarSesion(stage));
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
                stage.setScene(iniciarSesion(stage));
                stage.setTitle("Iniciar sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return registro;
    }

    public Scene panelAdministrador(Stage stage, Scene validarRec) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("PanelAdmin.fxml"));
        Scene inicio = new Scene(fxmlLoader.load());

        Button validacion = (Button) inicio.lookup("#validar");
        validacion.setOnAction(event -> {
            stage.setScene(validarRec);
            try {
                tablaRecepcionistas(validarRec);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        int rows = 0;
        ResultSet rs = this.mySQLRepository.GetValidatedReceptionist();
        while (rs.next()) {
            rows++;
        }
        if (rows != 0) {
            validacion.setStyle("-fx-background-color: lightcoral");
        }
        return inicio;

    }

    public Scene validarRecepcionista() throws IOException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("Validar.fxml"));
        Scene validarRecepcionista = new Scene(fxmlLoad.load());

        return validarRecepcionista;
    }

    public TableView tablaRecepcionistas(Scene validarRec) throws SQLException, IOException {
        TableView tableview = (TableView) validarRec.lookup("#tabla");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetValidatedReceptionist();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn colBtn = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                if(rs.getMetaData().getColumnCount() - 1 == i) {
                    Callback<TableColumn<XYChart.Data, Void>, TableCell<XYChart.Data, Void>> cellFactory = new Callback<TableColumn<XYChart.Data, Void>, TableCell<XYChart.Data, Void>>() {
                        @Override
                        public TableCell<XYChart.Data, Void> call(final TableColumn<XYChart.Data, Void> param) {
                            final TableCell<XYChart.Data, Void> cell = new TableCell<XYChart.Data, Void>() {

                                private final Button btn = new Button("Action");

                                {
                                    btn.setOnAction((ActionEvent event) -> {
                                        XYChart.Data data = getTableView().getItems().get(getIndex());
                                        System.out.println("selectedData: " + data);
                                    });
                                }

                                @Override
                                public void updateItem(Void item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                    } else {
                                        setGraphic(btn);
                                    }
                                }
                            };
                            return cell;
                        }
                    };

                    colBtn.setCellFactory(cellFactory);

                    tableview.getColumns().add(colBtn);
                }
                else {
                    colBtn.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    tableview.getColumns().addAll(colBtn);
                    System.out.println("Column [" + i + "] ");
                }


            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }
            tableview.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return tableview;
    }

}