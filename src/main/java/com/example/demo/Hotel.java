package com.example.demo;

import com.dlsc.formsfx.view.controls.SimpleRadioButtonControl;
import com.example.demo.domain.Cliente;
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
        Scene iniciarsesion = LoginPage(stage);

        stage.setScene(iniciarsesion);
        stage.show();
    }



    public Scene AdminLogin(Stage stage) throws IOException {

        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("Login.fxml"));
        Scene AdminLogin = new Scene(fxmlLoader.load());

        Button Loginbtn = (Button) AdminLogin.lookup("#loginbtn");
        Button regresar = (Button) AdminLogin.lookup("#regresar");
        Button exit = (Button) AdminLogin.lookup("#exitbtn");

        Loginbtn.setOnAction(event -> {
            String usuario = ((TextField) AdminLogin.lookup("#usuario")).getText();
            String password = ((PasswordField) AdminLogin.lookup("#password")).getText();
            try {
                if(this.mySQLRepository.GetAdminCredentials(usuario,password) == 1) {
                    Scene inicio = panelAdministrador(stage, validarRecepcionista());
                    stage.setScene(inicio);
                    stage.centerOnScreen();
                }
                else {
                    stage.setScene(error);
                    Label err = (Label) error.lookup("#error");
                    err.setText("Las credenciales son incorrectas");
                    Button volver = (Button) error.lookup("#volver");
                    volver.setOnAction(event2 -> stage.setScene(AdminLogin));
                }
            } catch (SQLException | IOException ex) {
                System.out.println("No se pudo ejecutar la consulta: ");
                ex.printStackTrace();
            }
        });

        regresar.setOnAction(event -> {
            Scene LoginPrincipal;
            try {
                LoginPrincipal = LoginPage(stage);
                stage.setTitle("Iniciar Sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(LoginPrincipal);
            stage.centerOnScreen();
        });

        exit.setOnAction(event -> {
            Platform.exit();
        });

        return AdminLogin;
    }
    public Scene LoginPage(Stage stage) throws IOException {

        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("LoginPage.fxml"));
        Scene LoginPage = new Scene(fxmlLoader.load());

        Button Loginbtn = (Button) LoginPage.lookup("#loginbtn");
        Button registrar = (Button) LoginPage.lookup("#registrarbtn");
        Button exit = (Button) LoginPage.lookup("#exitbtn");
        Button logAdmin = (Button) LoginPage.lookup("#loginAdmin");

        logAdmin.setOnAction(event -> {
            try {
                Scene loginAdmin = AdminLogin(stage);
                stage.setTitle("Inicio sesión admin");
                stage.setScene(loginAdmin);
                stage.centerOnScreen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Loginbtn.setOnAction(event -> {
            String usuario = ((TextField) LoginPage.lookup("#usuario")).getText();
            String password = ((PasswordField) LoginPage.lookup("#password")).getText();
            try {
                if(this.mySQLRepository.GetReceptionistCredentials(usuario,password) == 1) {
                    Scene ReceptionistPanel = ReceptionistPanel(stage);
                    stage.setScene(ReceptionistPanel);
                    stage.centerOnScreen();
                }
                else {
                    stage.setScene(error);
                    Label err = (Label) error.lookup("#error");
                    err.setText("Las credenciales son incorrectas");
                    Button volver = (Button) error.lookup("#volver");
                    volver.setOnAction(event2 -> stage.setScene(LoginPage));
                }
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

        return LoginPage;
    }

    public Scene pantallaRegistro(Stage stage) throws IOException {
        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("ReceptionistRegister.fxml"));
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
                    int rows = this.mySQLRepository.RegisterRecepcionist(r1.getUsuario(), r1.getPassword(), r1.getNom(),
                            r1.getCognoms(), r1.getDNI(), r1.getNacionalitat(), r1.getTelefon(), r1.getEmail());
                    stage.setScene(confirmar);
                    Button finalizar = (Button) confirmar.lookup("#finalizar");
                    finalizar.setOnAction(event2 -> {
                        try {
                            stage.setScene(LoginPage(stage));
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
                stage.setScene(LoginPage(stage));
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

        Button logOut = (Button) inicio.lookup("#logOut");
        Button validacion = (Button) inicio.lookup("#validar");

        logOut.setOnAction(event -> {
            try {
                stage.setScene(LoginPage(stage));
                stage.setTitle("Iniciar sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



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
            for (int i = 0; i < rs.getMetaData().getColumnCount() - 1; i++) {
                final int j = i;
                if(!rs.getMetaData().getColumnName(i + 1).equals("Password")) {
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

            Button validar = (Button) validarRec.lookup("#validar");
            validar.setOnAction((ActionEvent event) -> {
                TablePosition pos = (TablePosition) tableview.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn col = (TableColumn) tableview.getColumns().get(0);
                String id = (String) col.getCellObservableValue(tableview.getItems().get(row)).getValue();
                try {
                    if(this.mySQLRepository.ValidateRecepcionist(Integer.parseInt(id)) == 1) {
                        tableview.getItems().removeAll(
                                tableview.getSelectionModel().getSelectedItems());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            Button rechazar = (Button) validarRec.lookup("#rechazar");
            rechazar.setOnAction((ActionEvent event) -> {
                TablePosition pos = (TablePosition) tableview.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn col = (TableColumn) tableview.getColumns().get(0);
                String id = (String) col.getCellObservableValue(tableview.getItems().get(row)).getValue();
                try {
                    if(this.mySQLRepository.RejectReceptionist(Integer.parseInt(id)) == 1) {
                        tableview.getItems().removeAll(
                                tableview.getSelectionModel().getSelectedItems());
                    }
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

    public Scene ReceptionistPanel(Stage stage) throws IOException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("ReceptionistPanel.fxml"));
        Scene ReceptionistPanel = new Scene(fxmlLoad.load());

        Button clientsManagement = (Button) ReceptionistPanel.lookup("#clientsManage");
        Button roomReservation = (Button) ReceptionistPanel.lookup("#roomReservation");
        Button logOut = (Button) ReceptionistPanel.lookup("#logOut");

        clientsManagement.setOnAction(event -> {
            try {
                stage.setScene(ClientsManagement(stage));
                stage.setTitle("Panel recepcionista");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //ToDo: crear escena "roomReservation" y añadirla a evento
        roomReservation.setOnAction(event -> {

        });

        logOut.setOnAction(event -> {
            try {
                stage.setScene(LoginPage(stage));
                stage.setTitle("Iniciar sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return ReceptionistPanel;
    }

    public TableView clientsTable(Stage stage, Scene ClientsManagement) throws SQLException, IOException {
        TableView clientsTable = (TableView) ClientsManagement.lookup("#clientsTable");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetAllClients();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle( "-fx-alignment: CENTER;");
                col.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                clientsTable.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            clientsTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return clientsTable;
    }

    public Scene ClientsManagement(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("ClientsManagement.fxml"));
        Scene ClientsManagement = new Scene(fxmlLoad.load());
        TableView showClients = clientsTable(stage,ClientsManagement);

        Button addClient = (Button) ClientsManagement.lookup("#addClient");
        Button returnbtn = (Button) ClientsManagement.lookup("#returnToPanel");

        returnbtn.setOnAction(event -> {
            try {
                stage.setScene(ReceptionistPanel(stage));
                stage.setTitle("Panel recepcionista");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        addClient.setOnAction(event -> {
            try {
                stage.setScene(AddClient(stage));
                stage.setTitle("Añadir cliente");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });



        return ClientsManagement;
    }

    public Scene AddClient(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("AddClient.fxml"));
        Scene AddClient = new Scene(fxmlLoad.load());

        FXMLLoader fxmlLoader2 = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene error = new Scene(fxmlLoader2.load());

        FXMLLoader fxmlLoader3 = new FXMLLoader(Hotel.class.getResource("ConfirmarRegistro.fxml"));
        Scene confirmar = new Scene(fxmlLoader3.load());

        Button returnBtn = (Button) AddClient.lookup("#returnBtn");
        Button registerBtn = (Button) AddClient.lookup("#addBtn");


        registerBtn.setOnAction(event -> {
            try {
                ChoiceBox estatCivilBox = (ChoiceBox) AddClient.lookup("#estatCivil");
                ObservableList<String> availableChoices = FXCollections.observableArrayList("Soltero", "Casado","Separado","Divorciado");
                estatCivilBox.setItems(availableChoices);
                String estatCivil = (String) estatCivilBox.getSelectionModel().getSelectedItem();
                String ocupacio = ((TextField) AddClient.lookup("#ocupacio")).getText();
                String nom = ((TextField) AddClient.lookup("#nom")).getText();
                String cognoms = ((TextField) AddClient.lookup("#cognoms")).getText();
                String DNI = ((TextField) AddClient.lookup("#dni")).getText();
                String nacionalitat = ((TextField) AddClient.lookup("#nacionalitat")).getText();
                String telefon = ((TextField) AddClient.lookup("#telefon")).getText();
                try {
                    Integer tlf = Integer.parseInt(telefon);
                    String email = ((TextField) AddClient.lookup("#email")).getText();
                    Cliente c1 = new Cliente(estatCivil, ocupacio, nom, cognoms, DNI, nacionalitat, tlf, email);
                    int rows = this.mySQLRepository.AddClient(c1.getEstat_civil(), c1.getOcupacio(), c1.getNom(),
                            c1.getCognoms(), c1.getDNI(), c1.getNacionalitat(), c1.getTelefon(), c1.getEmail());
                    stage.setScene(confirmar);
                    Label confirmacion = (Label) confirmar.lookup("#confirmar");
                    confirmacion.setText("Cliente añadido correctamente");
                    Button finalizar = (Button) confirmar.lookup("#finalizar");
                    finalizar.setOnAction(event2 -> {
                        try {
                            stage.setScene(ClientsManagement(stage));
                        } catch (IOException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (NumberFormatException e) {
                    stage.setScene(error);
                    Label err = (Label) error.lookup("#error");
                    err.setText("El campo teléfono solo puede contener números");
                    Button volver = (Button) error.lookup("#volver");
                    volver.setOnAction(event2 -> {
                        stage.setScene(AddClient);
                    });
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        returnBtn.setOnAction(event -> {
            try {
                stage.setScene(ClientsManagement(stage));
                stage.setTitle("Gestión de clientes");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });


        return AddClient;
    }
}