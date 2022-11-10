package com.example.demo.domain;

import com.example.demo.Hotel;
import com.example.demo.infrastructure.MySQLRepository;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Escena {
    private FXMLLoader fxmlLoader;
    private MySQLRepository mySQLRepository;

    public Escena(MySQLRepository mySQLRepository) {
        this.mySQLRepository = mySQLRepository;
    }

    public Scene Error(Stage stage,String msg, Scene scene) throws IOException {

        this.fxmlLoader = new FXMLLoader(Hotel.class.getResource("Error.fxml"));
        Scene Error = new javafx.scene.Scene(fxmlLoader.load());

        Label err = (Label) Error.lookup("#error");
        err.setText(msg);

        Button volver = (Button) Error.lookup("#volver");
        volver.setOnAction(event -> stage.setScene(scene));

        return Error;
    }
    public Scene AdminLogin(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("Login.fxml"));
        javafx.scene.Scene AdminLogin = new javafx.scene.Scene(fxmlLoader.load());

        Button Loginbtn = (Button) AdminLogin.lookup("#loginbtn");
        Button regresar = (Button) AdminLogin.lookup("#regresar");
        Button exit = (Button) AdminLogin.lookup("#exitbtn");

        Loginbtn.setOnAction(event -> {
            String usuario = ((TextField) AdminLogin.lookup("#usuario")).getText();
            String password = ((PasswordField) AdminLogin.lookup("#password")).getText();
            try {
                if(this.mySQLRepository.GetAdminCredentials(usuario,password) == 1) {
                    javafx.scene.Scene inicio = AdministratorPanel(stage);
                    stage.setScene(inicio);
                    stage.centerOnScreen();
                }
                else {
                    Scene Error = Error(stage,"Las credenciales son incorrectas",AdminLogin(stage));
                    stage.setScene(Error);

                }
            } catch (SQLException | IOException ex) {
                System.out.println("No se pudo ejecutar la consulta: ");
                ex.printStackTrace();
            }
        });

        regresar.setOnAction(event -> {
            javafx.scene.Scene LoginPrincipal;
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

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("LoginPage.fxml"));
        javafx.scene.Scene LoginPage = new javafx.scene.Scene(fxmlLoader.load());

        Button Loginbtn = (Button) LoginPage.lookup("#loginbtn");
        Button registrar = (Button) LoginPage.lookup("#registrarbtn");
        Button exit = (Button) LoginPage.lookup("#exitbtn");
        Button logAdmin = (Button) LoginPage.lookup("#loginAdmin");

        logAdmin.setOnAction(event -> {
            try {
                javafx.scene.Scene loginAdmin = AdminLogin(stage);
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
                    javafx.scene.Scene ReceptionistPanel = ReceptionistPanel(stage);
                    stage.setScene(ReceptionistPanel);
                    stage.centerOnScreen();
                }
                else {
                    Scene Error = Error(stage,"Las credenciales son incorrectas",LoginPage(stage));
                    stage.setScene(Error);
                }
            } catch (SQLException | IOException ex) {
                System.out.println("No se pudo ejecutar la consulta: ");
                ex.printStackTrace();
            }
        });

        registrar.setOnAction(event -> {
            javafx.scene.Scene registro;
            try {
                registro = RegisterPage(stage);
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

    public Scene RegisterPage(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("ReceptionistRegister.fxml"));
        Scene registro = new javafx.scene.Scene(fxmlLoader.load());

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
                    Scene confirmRegister = ConfirmRegister(stage);
                    Integer tlf = Integer.parseInt(telefon);
                    String email = ((TextField) registro.lookup("#email")).getText();
                    Recepcionista r1 = new Recepcionista(usuario, password, nom, cognoms, DNI, nacionalitat, tlf, email);
                    int rows = this.mySQLRepository.RegisterRecepcionist(r1.getUsuario(), r1.getPassword(), r1.getNom(),
                            r1.getCognoms(), r1.getDNI(), r1.getNacionalitat(), r1.getTelefon(), r1.getEmail());
                    stage.setScene(confirmRegister);

                } catch (NumberFormatException e) {
                    Scene error = Error(stage,"El campo teléfono solo puede contener números",RegisterPage(stage));
                    stage.setScene(error);
                }
            } catch (SQLException | IOException e) {
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

    public Scene ConfirmRegister(Stage stage) throws IOException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("ConfirmarRegistro.fxml"));
        Scene confirmRegister = new javafx.scene.Scene(fxmlLoad.load());

        Button finalizar = (Button) confirmRegister.lookup("#finalizar");
        finalizar.setOnAction(event -> {
            try {
                stage.setScene(LoginPage(stage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return confirmRegister;
    }
    public Scene AdministratorPanel(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("PanelAdmin.fxml"));
        Scene panelAdmin = new javafx.scene.Scene(fxmlLoader.load());

        Button logOut = (Button) panelAdmin.lookup("#logOut");
        Button validacion = (Button) panelAdmin.lookup("#validar");
        Button roomManagement = (Button) panelAdmin.lookup("#roomManagement");

        logOut.setOnAction(event -> {
            try {
                stage.setScene(LoginPage(stage));
                stage.setTitle("Iniciar sesión");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        roomManagement.setOnAction(event -> {
            try {
                stage.setScene(RoomManagement(stage));
                stage.setTitle("Gestión de habitaciones");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }

        });

        validacion.setOnAction(event -> {
            Scene validar = null;
            try {
                validar = ValidateReceptionists(stage);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(validar);
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
        return panelAdmin;

    }
    public javafx.scene.Scene ValidateReceptionists(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("Validar.fxml"));
        Scene validarRecepcionista = new javafx.scene.Scene(fxmlLoad.load());

        tablaRecepcionistas(stage,validarRecepcionista);

        return validarRecepcionista;
    }

    public TableView tablaRecepcionistas(Stage stage, Scene validarRec) throws SQLException {
        TableView tableview = (TableView) validarRec.lookup("#tabla");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetValidatedReceptionist();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount() - 1; i++) {
                final int j = i;
                if(!rs.getMetaData().getColumnName(i + 1).equals("Password")) {
                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                    col.setStyle( "-fx-alignment: CENTER;");
                    col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                    tableview.getColumns().addAll(col);
                }




            }

            while (rs.next()) {
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
                    stage.setScene(AdministratorPanel(stage));
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
        Scene ReceptionistPanel = new javafx.scene.Scene(fxmlLoad.load());

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
            try {
                stage.setScene(RoomReservation(stage));
                stage.setTitle("Reserva de habitaciones");
                stage.centerOnScreen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

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


    public Scene RoomManagement(Stage stage) throws IOException, SQLException {

        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("RoomManagement.fxml"));
        Scene roomManagement = new Scene(fxmlLoad.load());

        TableView showRooms = roomTable(stage,roomManagement);

        Button addRoomBtn = (Button) roomManagement.lookup("#addRoom");
        Button returnbtn = (Button) roomManagement.lookup("#returnToPanel");
        Button deleteRoomBtn = (Button) roomManagement.lookup("#deleteRoom");
        Button editRoomBtn = (Button) roomManagement.lookup("#editRoom");

        editRoomBtn.setOnAction(event -> {
            List selected = showRooms.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                try {
                    stage.setScene(EditRoom(stage, (List) selected.get(0)));
                    stage.setTitle("Editar habitación");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    stage.setScene(Error(stage,"Por favor, seleccione una habitación",RoomManagement(stage)));
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        deleteRoomBtn.setOnAction(event -> {
            if(showRooms.getSelectionModel().getSelectedCells().isEmpty()) {
                try {
                    stage.setScene(Error(stage,"Por favor, seleccione una habitación",RoomManagement(stage)));
                } catch (IOException |SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                TablePosition pos = (TablePosition) showRooms.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn col = (TableColumn) showRooms.getColumns().get(0);
                String numero = (String) col.getCellObservableValue(showRooms.getItems().get(row)).getValue();
                try {
                    if(this.mySQLRepository.DeleteRoom(numero) == 1) {
                        showRooms.getItems().removeAll(showRooms.getSelectionModel().getSelectedItems());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        returnbtn.setOnAction(event -> {
            try {
                stage.setScene(AdministratorPanel(stage));
                stage.setTitle("Panel administrador");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        addRoomBtn.setOnAction(event -> {
            try {
                stage.setScene(AddRoom(stage));
                stage.setTitle("Añadir cliente");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return roomManagement;
    }

    public TableView roomTable(Stage stage, Scene ClientsManagement) throws SQLException {
        TableView roomTable = (TableView) ClientsManagement.lookup("#clientsTable");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetAllRooms();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount() - 1; i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle( "-fx-alignment: CENTER;");
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                roomTable.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            roomTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return roomTable;
    }

    public Scene AddRoom(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("AddRoom.fxml"));
        Scene addRoom = new javafx.scene.Scene(fxmlLoad.load());

        Button returnBtn = (Button) addRoom.lookup("#returnBtn");
        Button addBtn = (Button) addRoom.lookup("#addBtn");
        GridPane gridPane = (GridPane) addRoom.lookup("#gridPane");

        addBtn.setOnAction(event -> {
            ArrayList values = new ArrayList<>();
            for (Node node : gridPane.getChildren()) {
                if (node instanceof HBox) {
                    for(Node node1: ((HBox) node).getChildren()) {
                        if (node1 instanceof TextField) {
                            TextField text = (TextField) addRoom.lookup("#" + node1.getId());
                            if(!text.getText().isEmpty()) {
                                values.add(text.getText());
                            }

                        }
                        else if (node1 instanceof ChoiceBox) {
                            ChoiceBox comBox = (ChoiceBox) addRoom.lookup("#" + node1.getId());
                            if(comBox.getSelectionModel().getSelectedItem() != null) {
                                values.add(comBox.getSelectionModel().getSelectedItem());
                            }

                        }

                    }
                }
            }
            try {
                if(values.size() == 8) {
                    if(this.mySQLRepository.AddRoom(values) == 1) {
                        stage.setScene(RoomManagement(stage));
                    }
                }
                else {
                    stage.setScene(Error(stage,"No pueden haber campos vacíos.",AddRoom(stage)));
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        returnBtn.setOnAction(event -> {
            try {
                stage.setScene(RoomManagement(stage));
                stage.setTitle("Gestión de habitaciones");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });


        return addRoom;
    }

    public Scene EditRoom(Stage stage,List list) throws IOException {

        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("EditRoom.fxml"));
        Scene editClient = new javafx.scene.Scene(fxmlLoad.load());

        Button returnBtn = (Button) editClient.lookup("#returnBtn");
        Button editBtn = (Button) editClient.lookup("#editBtn");
        GridPane gridPane = (GridPane) editClient.lookup("#gridPane");

        int i = 0;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof HBox) {
                for(Node node1: ((HBox) node).getChildren()) {
                    if (node1 instanceof TextField) {
                        TextField text = (TextField) editClient.lookup("#" + node1.getId());
                        text.setPromptText((String) list.get(i));
                    }
                    else if (node1 instanceof ComboBox) {
                        ComboBox comBox = (ComboBox) editClient.lookup("#" + node1.getId());
                        comBox.setPromptText((String) list.get(i));
                    }

                }
                i++;
            }
        }

        editBtn.setOnAction(event -> {
            int num = 0;
            ArrayList values = new ArrayList<>();
            for (Node node : gridPane.getChildren()) {
                if (node instanceof HBox) {
                    for(Node node1: ((HBox) node).getChildren()) {
                        if (node1 instanceof TextField) {
                            TextField text = (TextField) editClient.lookup("#" + node1.getId());
                            if(node1.getId().equals("roomNumber")) {
                                num = Integer.parseInt(text.getPromptText());
                            }
                            if(text.getText().equals("")) {
                                values.add(text.getPromptText());
                            }
                            else {
                                values.add(text.getText());
                            }

                        }
                        else if (node1 instanceof ComboBox) {
                            ComboBox estadoCivil = (ComboBox) editClient.lookup("#" + node1.getId());
                            if(estadoCivil.getSelectionModel().getSelectedItem() == null) {
                                values.add(estadoCivil.getPromptText());
                            }
                            else {
                                values.add(estadoCivil.getSelectionModel().getSelectedItem());
                            }
                        }

                    }
                }
            }
            try {
                this.mySQLRepository.EditRoom(values,num);
                stage.setScene(RoomManagement(stage));
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        returnBtn.setOnAction(event -> {
            try {
                stage.setScene(RoomManagement(stage));
                stage.setTitle("Gestión de habitaciones");
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });


        return editClient;
    }

    public TableView clientsTable(Stage stage, Scene ClientsManagement) throws SQLException {
        TableView clientsTable = (TableView) ClientsManagement.lookup("#clientsTable");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetAllClients();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount() - 1; i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle( "-fx-alignment: CENTER;");
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
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
        Scene ClientsManagement = new javafx.scene.Scene(fxmlLoad.load());
        TableView showClients = clientsTable(stage,ClientsManagement);

        Button addClient = (Button) ClientsManagement.lookup("#addClient");
        Button returnbtn = (Button) ClientsManagement.lookup("#returnToPanel");
        Button deleteClientBtn = (Button) ClientsManagement.lookup("#deleteClient");
        Button editClientBtn = (Button) ClientsManagement.lookup("#editClient");

        editClientBtn.setOnAction(event -> {
            List selected = showClients.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                try {
                    stage.setScene(EditClient(stage, (List) selected.get(0)));
                    stage.setTitle("Editar cliente");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    stage.setScene(Error(stage,"Por favor, seleccione un cliente",ClientsManagement(stage)));
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
                stage.setTitle("Editar cliente");
            }

        });

        deleteClientBtn.setOnAction(event -> {
            if(showClients.getSelectionModel().getSelectedCells().isEmpty()) {
                try {
                    stage.setScene(Error(stage,"Por favor, seleccione un cliente",ClientsManagement(stage)));
                } catch (IOException |SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                TablePosition pos = (TablePosition) showClients.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn col = (TableColumn) showClients.getColumns().get(0);
                String dni = (String) col.getCellObservableValue(showClients.getItems().get(row)).getValue();
                try {
                    if(this.mySQLRepository.DeleteClient(dni) == 1) {
                        showClients.getItems().removeAll(showClients.getSelectionModel().getSelectedItems());
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });
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
        Scene AddClient = new javafx.scene.Scene(fxmlLoad.load());

        Button returnBtn = (Button) AddClient.lookup("#returnBtn");
        Button registerBtn = (Button) AddClient.lookup("#addBtn");


        registerBtn.setOnAction(event -> {
            try {
                ChoiceBox estatCivilBox = (ChoiceBox) AddClient.lookup("#estatCivil");
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

                    Scene confirmar = ConfirmRegister(stage);
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
                    Scene error = Error(stage,"El campo teléfono solo puede contener números",AddClient(stage));
                    stage.setScene(error);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException | IOException e) {
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

    public Scene EditClient(Stage stage,List list) throws IOException {

        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("EditClient.fxml"));
        Scene editClient = new javafx.scene.Scene(fxmlLoad.load());

        Button returnBtn = (Button) editClient.lookup("#returnBtn");
        Button editBtn = (Button) editClient.lookup("#editBtn");
        GridPane gridPane = (GridPane) editClient.lookup("#gridPane");

        int i = 0;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof HBox) {
                for(Node node1: ((HBox) node).getChildren()) {
                    if (node1 instanceof TextField) {
                        TextField text = (TextField) editClient.lookup("#" + node1.getId());
                        text.setPromptText((String) list.get(i));
                    }
                    else if (node1 instanceof ComboBox) {
                        ComboBox estadoCivil = (ComboBox) editClient.lookup("#" + node1.getId());
                        estadoCivil.setPromptText((String) list.get(i));
                    }

                }
                i++;
            }
        }

        editBtn.setOnAction(event -> {
            String dni = "";
            ArrayList values = new ArrayList<>();
            for (Node node : gridPane.getChildren()) {
                if (node instanceof HBox) {
                    for(Node node1: ((HBox) node).getChildren()) {
                        if (node1 instanceof TextField) {
                            TextField text = (TextField) editClient.lookup("#" + node1.getId());
                            if(node1.getId().equals("dni")) {
                                dni = text.getPromptText();
                            }
                            if(text.getText().equals("")) {
                                values.add(text.getPromptText());
                            }
                            else {
                                values.add(text.getText());
                            }

                        }
                        else if (node1 instanceof ComboBox) {
                            ComboBox estadoCivil = (ComboBox) editClient.lookup("#" + node1.getId());
                            if(estadoCivil.getSelectionModel().getSelectedItem() == null) {
                                values.add(estadoCivil.getPromptText());
                            }
                            else {
                                values.add(estadoCivil.getSelectionModel().getSelectedItem());
                            }
                        }

                    }
                }
            }
            try {
                System.out.println(dni);
                this.mySQLRepository.EditClient(values,dni);
                stage.setScene(ClientsManagement(stage));
            } catch (SQLException | IOException e) {
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


        return editClient;
    }

    public Scene RoomReservation(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("RoomReservation.fxml"));
        Scene roomReservation = new javafx.scene.Scene(fxmlLoad.load());
        TableView showReservations = reservationTable(stage,roomReservation);
        GridPane reservationGrid = (GridPane) roomReservation.lookup("#reservationGrid");
        reservationGrid.setVisible(false);
        HBox tableContainer = (HBox) roomReservation.lookup("#tableContainer");
        reservationGrid.setStyle("-fx-background-color: #6366f1;");
        tableContainer.setStyle("-fx-background-color: #156F70;");
        showReservations.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                reservationGrid.setVisible(true);
            }
        });




        return roomReservation;
    }

    public TableView reservationTable(Stage stage, Scene ClientsManagement) throws SQLException {
        TableView reservationTable = (TableView) ClientsManagement.lookup("#reservationTable");
        ObservableList<Object> data = FXCollections.observableArrayList();
        ResultSet rs = this.mySQLRepository.GetAllReservations();
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle( "-fx-alignment: CENTER;");
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                reservationTable.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            reservationTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        return reservationTable;
    }

}
