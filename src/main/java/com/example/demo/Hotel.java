package com.example.demo;

import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.example.demo.domain.Recepcionista;
import com.example.demo.infrastructure.DatabaseConnection;
import com.example.demo.infrastructure.MySQLRepository;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        Statement s = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
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
                tablaRecepcionistas(stage,validarRec);
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
            final String IDLE_BUTTON_STYLE = "-fx-background-color: lightcoral";
            final String HOVERED_BUTTON_STYLE = "-fx-background-color: #DF2929;";

            validacion.setStyle(IDLE_BUTTON_STYLE);
            validacion.setOnMouseEntered(e -> validacion.setStyle(HOVERED_BUTTON_STYLE));
            validacion.setOnMouseExited(e -> validacion.setStyle(IDLE_BUTTON_STYLE));
        }
        return inicio;

    }

    public Scene validarRecepcionista() throws IOException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("Validar.fxml"));
        Scene validarRecepcionista = new Scene(fxmlLoad.load());

        return validarRecepcionista;
    }

    public TableView tablaRecepcionistas(Stage stage, Scene validarRec) throws SQLException, IOException {
        TableView tableview = (TableView) validarRec.lookup("#tabla");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetValidatedReceptionist();
        try {
            var wrapper = new Object(){int rows = -1; };
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                if (rs.getMetaData().getColumnName(i + 1).equals("Validado")) {
                    TableColumn colBtn = new TableColumn("Validar");
                    colBtn.setStyle( "-fx-alignment: CENTER;");
                    Callback<TableColumn<Integer, Void>, TableCell<Integer, Void>> cellFactory = new Callback<TableColumn<Integer, Void>, TableCell<Integer, Void>>() {
                        @Override
                        public TableCell<Integer, Void> call(final TableColumn<Integer, Void> param) {
                            final TableCell<Integer, Void> cell = new TableCell<Integer, Void>() {

                                private final Button btn = new Button("Validar");
                                    final int pos = wrapper.rows;
                                {
                                    btn.setOnAction((ActionEvent event) -> {
                                        try {

                                            TableColumn col = (TableColumn) tableview.getColumns().get(0);

                                            String id = (String) col.getCellObservableValue(tableview.getItems().get(pos)).getValue();
                                            System.out.println(id);

                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
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
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setStyle( "-fx-alignment: CENTER;");
                    col.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    tableview.getColumns().addAll(col);

                }

            }

            while (rs.next()) {
                wrapper.rows++;
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            tableview.setItems(data);

            Button returnBtn = (Button) validarRec.lookup("#returnToLogin");
            returnBtn.setOnAction((ActionEvent event) -> {
                try {
                    tableview.getItems().clear();
                    tableview.getColumns().clear();
                    stage.setScene(panelAdministrador(stage,validarRec));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return tableview;
    }
}