package com.example.demo.domain;

import com.example.demo.Hotel;
import com.example.demo.infrastructure.DatabaseConnection;
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
import javax.xml.transform.Result;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Escena {
    private FXMLLoader fxmlLoader;
    private MySQLRepository mySQLRepository;

    public Escena(MySQLRepository mySQLRepository) {
        this.mySQLRepository = mySQLRepository;
    }

    public Scene AdminLogin(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Hotel.class.getResource("Login.fxml"));
        javafx.scene.Scene AdminLogin = new javafx.scene.Scene(fxmlLoader.load());

        Button Loginbtn = (Button) AdminLogin.lookup("#loginbtn");
        Button regresar = (Button) AdminLogin.lookup("#regresar");
        Button exit = (Button) AdminLogin.lookup("#exitbtn");
        Alert alert = new Alert(Alert.AlertType.ERROR,"Las credenciales son incorrectas",ButtonType.OK);

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
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }

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
        Alert alert = new Alert(Alert.AlertType.ERROR,"Las credenciales son incorrectas",ButtonType.OK);

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
                    Sesión session = new Sesión(usuario);
                    stage.setScene(ReceptionistPanel(stage));
                    stage.centerOnScreen();
                }
                else {
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }
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
        Alert alert = new Alert(Alert.AlertType.WARNING,"Por favor, llene todos los campos",ButtonType.OK);
        GridPane gridPane = (GridPane) registro.lookup("#gridPane");
        registerBtn.setOnAction(event -> {
            try {
                ArrayList values = new ArrayList<>();
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof HBox) {
                        for(Node node1: ((HBox) node).getChildren()) {
                            if (node1 instanceof TextField) {
                                TextField text = (TextField) registro.lookup("#" + node1.getId());
                                if(!text.getText().isEmpty()) {
                                    values.add(text.getText());
                                }
                            }

                        }
                    }
                }
                if(values.size() < 8) {
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.OK) {
                        alert.close();
                    }
                }
                else {
                    String email = (String) values.get(7);
                    if (!email.matches("^.{1,}[@].{1,}[.].{1,4}$")) {
                        alert.setContentText("El formato del email no es correcto");
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.OK) {
                            alert.close();
                        }
                    }
                    else {
                        Recepcionista r1 = new Recepcionista((String) values.get(0), (String) values.get(1), (String) values.get(2), (String) values.get(3), (String) values.get(4), (String) values.get(5), Integer.parseInt((String) values.get(6)), (String) values.get(7));
                        if(this.mySQLRepository.RegisterRecepcionist(r1.getUsuario(), r1.getPassword(), r1.getNom(),
                                r1.getCognoms(), r1.getDNI(), r1.getNacionalitat(), r1.getTelefon(), r1.getEmail()) == 1) {
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setContentText("Recepcionista registrado correctamente");
                            alert.showAndWait();
                            if (alert.getResult() == ButtonType.OK) {
                                alert.close();
                                stage.setScene(LoginPage(stage));
                                stage.centerOnScreen();
                            }
                        }
                        else {
                            alert.setAlertType(Alert.AlertType.ERROR);
                            alert.setContentText("Hubo un error al realizar el registro");
                            alert.showAndWait();
                            if (alert.getResult() == ButtonType.OK) {
                                alert.close();
                            }
                        }
                    }
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
        Alert alert = new Alert(Alert.AlertType.WARNING,"Por favor, seleccione un recepcionista",ButtonType.CLOSE);
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.OK,ButtonType.CLOSE);
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
                if(tableview.getSelectionModel().getSelectedCells().isEmpty()) {
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.CLOSE) {
                        alert.close();
                    }
                }
                else {
                    TablePosition pos = (TablePosition) tableview.getSelectionModel().getSelectedCells().get(0);
                    int row = pos.getRow();
                    TableColumn col = (TableColumn) tableview.getColumns().get(0);
                    String id = (String) col.getCellObservableValue(tableview.getItems().get(row)).getValue();
                    confirmation.setContentText("Desea validar al recepcionista con id " + id + "?");
                    confirmation.showAndWait();
                    if (confirmation.getResult() == ButtonType.CLOSE) {
                        confirmation.close();
                    }
                    else {
                        try {

                            if (this.mySQLRepository.ValidateRecepcionist(Integer.parseInt(id)) == 1) {
                                alert.setAlertType(Alert.AlertType.INFORMATION);
                                alert.setContentText("El recepcionista con Id " + id + " ha sido validado");
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.CLOSE) {
                                    alert.close();
                                }
                                tableview.getItems().removeAll(
                                        tableview.getSelectionModel().getSelectedItems());
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

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
        Button editRoomBtn = (Button) roomManagement.lookup("#editRoom");
        Alert warning = new Alert(Alert.AlertType.WARNING,"Por favor, seleccione una habitación",ButtonType.CLOSE);

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
                warning.showAndWait();
                if (warning.getResult() == ButtonType.CLOSE) {
                    warning.close();
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
                stage.setTitle("Añadir habitación");
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
        Alert warning = new Alert(Alert.AlertType.WARNING,"No pueden haber campos vacíos",ButtonType.CLOSE);


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
                    warning.showAndWait();
                    if (warning.getResult() == ButtonType.CLOSE) {
                        warning.close();
                    }
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
        Button editClientBtn = (Button) ClientsManagement.lookup("#editClient");
        Alert warning = new Alert(Alert.AlertType.WARNING,"Por favor, seleccione un cliente",ButtonType.CLOSE);

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
                    warning.showAndWait();
                    if (warning.getResult() == ButtonType.CLOSE) {
                        warning.close();
                    }
            }

        });

        returnbtn.setOnAction(event -> {
            try {
                stage.setScene(ReceptionistPanel(stage));
                stage.centerOnScreen();
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
        GridPane gridPane = (GridPane) AddClient.lookup("#gridPane");
        Alert warning = new Alert(Alert.AlertType.WARNING,"No pueden haber campos vacíos",ButtonType.CLOSE);
        Alert confirm = new Alert(Alert.AlertType.INFORMATION,"Cliente registrado correctamente",ButtonType.OK);

        registerBtn.setOnAction(event -> {
            try {
                ArrayList values = new ArrayList<>();
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof HBox) {
                        for(Node node1: ((HBox) node).getChildren()) {
                            if (node1 instanceof TextField) {
                                TextField text = (TextField) AddClient.lookup("#" + node1.getId());
                                if(!text.getText().isEmpty()) {
                                    values.add(text.getText());
                                }
                            }
                            else if (node1 instanceof ComboBox) {
                                ComboBox estatCivil = (ComboBox) AddClient.lookup("#" + node1.getId());
                                if(estatCivil.getSelectionModel().getSelectedItem() != null) {
                                    values.add(estatCivil.getSelectionModel().getSelectedItem());
                                }
                            }

                        }
                    }
                }
                if(values.size() < 8) {
                    warning.showAndWait();
                    if(warning.getResult() == ButtonType.CLOSE) {
                        warning.close();
                    }
                }
                else {
                    String email = (String) values.get(6);
                    if (!email.matches("^.{1,}[@].{1,}[.].{1,4}$")) {
                        warning.setContentText("El formato del email no es correcto");
                        warning.showAndWait();
                        if (warning.getResult() == ButtonType.OK) {
                            warning.close();
                        }
                    } else {
                        Cliente c1 = new Cliente((String) values.get(7), (String) values.get(4), (String) values.get(1), (String) values.get(2), (String) values.get(0), (String) values.get(3), Integer.parseInt((String) values.get(5)), (String) values.get(6));
                        if (this.mySQLRepository.AddClient(c1.getEstat_civil(), c1.getOcupacio(), c1.getNom(),
                                c1.getCognoms(), c1.getDNI(), c1.getNacionalitat(), c1.getTelefon(), c1.getEmail()) == 1) {
                            confirm.showAndWait();
                            if (confirm.getResult() == ButtonType.OK) {
                                stage.setScene(ClientsManagement(stage));
                                stage.centerOnScreen();
                                confirm.close();

                            }
                        } else {
                            warning.setAlertType(Alert.AlertType.ERROR);
                            warning.setContentText("Error al ejecutar la consulta");
                            warning.showAndWait();
                            if (warning.getResult() == ButtonType.OK) {
                                warning.close();
                            }
                        }
                    }
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
                Alert confirm = new Alert(Alert.AlertType.INFORMATION,"Cliente editado correctamente",ButtonType.OK);
                confirm.showAndWait();
                if (confirm.getResult() == ButtonType.OK) {
                    stage.setScene(ClientsManagement(stage));
                    stage.centerOnScreen();
                    confirm.close();
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


        return editClient;
    }

    public Scene RoomReservation(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoad = new FXMLLoader(Hotel.class.getResource("RoomReservation.fxml"));
        Scene roomReservation = new javafx.scene.Scene(fxmlLoad.load());
        stage.setTitle("Reserva de habitaciones");
        TableView showReservations = reservationTable(stage,roomReservation);
        GridPane reservationGrid = (GridPane) roomReservation.lookup("#reservationGrid");
        HBox tableContainer = (HBox) roomReservation.lookup("#tableContainer");
        HBox tableHbox = (HBox) roomReservation.lookup("#tableHbox");
        reservationGrid.setStyle("-fx-background-color: #6366f1;");
        tableContainer.setStyle("-fx-background-color: #156F70;");
        tableHbox.setStyle("-fx-background-color: #156F70;");
        Alert error = new Alert(Alert.AlertType.ERROR,"Esta reserva ya está pagada",ButtonType.OK);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,"Desea pagar esta reserva?",ButtonType.YES,ButtonType.NO);

        Button returnbtn = (Button) roomReservation.lookup("#returnBtn");
        Button newReservation = (Button) roomReservation.lookup("#newReservation");
        Button makePayment = (Button) roomReservation.lookup("#makePayment");
        Button cancelReservation = (Button) roomReservation.lookup("#cancelReservation");
        Button checkIn = (Button) roomReservation.lookup("#checkIn");
        Button checkOut = (Button) roomReservation.lookup("#checkOut");
        Button search = (Button) roomReservation.lookup(("#search"));

        search.setOnAction(event -> {

        });

        makePayment.setOnAction(event -> {
            List selected = showReservations.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                selected = (List) selected.get(0);
                    if(selected.get(9).equals("Pagada")) {
                        error.setAlertType(Alert.AlertType.ERROR);
                        error.setContentText("Esta reserva ya está pagada");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }
                    else if (selected.get(9).equals("Anulada")) {
                        error.setAlertType(Alert.AlertType.ERROR);
                        error.setContentText("No se puede pagar una reserva anulada");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }
                    else {
                        confirm.setContentText("Desea pagar la reserva por un costo de " + selected.get(8) + "€ ?");
                        confirm.showAndWait();
                        if (confirm.getResult() == ButtonType.YES) {
                            try {
                                if(this.mySQLRepository.ChangePaymentStatus(Integer.parseInt((String)selected.get(0))) == 1) {
                                    stage.setScene(RoomReservation(stage));
                                    stage.centerOnScreen();
                                }
                                else {
                                    error.setAlertType(Alert.AlertType.ERROR);
                                    error.setContentText("Hubo un error al ejecutar la consulta");
                                    error.showAndWait();
                                    if (error.getResult() == ButtonType.OK) {
                                        error.close();
                                    }
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            confirm.close();
                        }
                    }
            }
            else {
                    error.setAlertType(Alert.AlertType.WARNING);
                    error.setContentText("Por favor, seleccione una reserva");
                    error.showAndWait();
                    if (error.getResult() == ButtonType.OK) {
                        error.close();
                    }
            }
        });

        checkIn.setOnAction(event -> {
            List selected = showReservations.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                selected = (List) selected.get(0);
                try {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String admitTime = formatter.format(date).split(" ")[1];
                    String admitDate = formatter.format(date).split(" ")[0];
                    int numReserva = (Integer.parseInt((String) selected.get(0)));
                    ResultSet rs = this.mySQLRepository.GetReservationDates(numReserva);
                    rs.next();
                    if(rs.getString("Fecha_Ingreso").equals(admitDate)) {
                        confirm.setContentText("Quiere hacer el check-in de la reserva número " + numReserva + " ?");
                        confirm.showAndWait();
                        if (confirm.getResult() == ButtonType.YES) {
                            if(this.mySQLRepository.CheckIn(numReserva,admitTime) == 1) {
                                stage.setScene(RoomReservation(stage));
                                stage.centerOnScreen();
                            }
                            else {
                                error.setAlertType(Alert.AlertType.ERROR);
                                error.setContentText("Hubo un error al ejecutar la consulta");
                                error.showAndWait();
                                if (error.getResult() == ButtonType.OK) {
                                    error.close();
                                }
                            }
                        }
                    }
                    else {
                        error.setAlertType(Alert.AlertType.ERROR);
                        error.setContentText("Solo se puede hacer el check-in el mismo día del ingreso");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }


                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                    error.setAlertType(Alert.AlertType.WARNING);
                    error.setContentText("Por favor, seleccione una reserva");
                    error.showAndWait();
                    if (error.getResult() == ButtonType.OK) {
                        error.close();
                    }
            }

        });

        checkOut.setOnAction(event -> {
            List selected = showReservations.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                selected = (List) selected.get(0);
                try {
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String exittime = formatter.format(date).split(" ")[1];
                    String today = formatter.format(date).split(" ")[0];
                    today = today.trim();
                    int numReserva = (Integer.parseInt((String) selected.get(0)));
                    ResultSet rs = this.mySQLRepository.GetReservationDates(numReserva);
                    rs.next();
                    if(rs.getString("Estado").equals("Pendiente de pago")) {
                        error.setAlertType(Alert.AlertType.WARNING);
                        error.setContentText("Por favor, realice el pago primero");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }
                    else {
                        if(rs.getString("Fecha_Ingreso").compareTo(today) < 0 && rs.getString("Fecha_Salida").compareTo(today) > 0) {
                            confirm.setContentText("Quiere hacer el check-out de la reserva número " + numReserva + " ?");
                            confirm.showAndWait();
                            if (confirm.getResult() == ButtonType.YES) {
                                if(this.mySQLRepository.CheckOut(numReserva,exittime,today) == 1) {
                                    stage.setScene(RoomReservation(stage));
                                    stage.centerOnScreen();
                                }
                                else {
                                    error.setAlertType(Alert.AlertType.ERROR);
                                    error.setContentText("Hubo un error al ejecutar la consulta");
                                    error.showAndWait();
                                    if (error.getResult() == ButtonType.OK) {
                                        error.close();
                                    }
                                }
                            }
                        }
                        else {
                            error.setAlertType(Alert.AlertType.ERROR);
                            error.setContentText("Solo puede hacer el check-out dentro de la fecha de la reserva");
                            error.showAndWait();
                            if (error.getResult() == ButtonType.OK) {
                                error.close();
                            }
                        }
                    }



                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                    error.setAlertType(Alert.AlertType.WARNING);
                    error.setContentText("Por favor, seleccione una reserva");
                    error.showAndWait();
                    if (error.getResult() == ButtonType.OK) {
                        error.close();
                    }
            }
        });

        cancelReservation.setOnAction(event -> {
            List selected = showReservations.getSelectionModel().getSelectedItems();
            if(!selected.isEmpty()) {
                selected = (List) selected.get(0);
                try {
                    if(selected.get(9).equals("Anulada")) {
                        error.setAlertType(Alert.AlertType.ERROR);
                        error.setContentText("Esta reserva ya está anulada");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }
                    else {
                        if(this.mySQLRepository.CancelReservation(Integer.parseInt((String) selected.get(0))) == 1) {
                            stage.setScene(RoomReservation(stage));
                            stage.centerOnScreen();
                        }
                        else {
                            error.setAlertType(Alert.AlertType.ERROR);
                            error.setContentText("Hubo un error al ejecutar la consulta");
                            error.showAndWait();
                            if (error.getResult() == ButtonType.OK) {
                                error.close();
                            }
                        }
                    }

                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                    error.setAlertType(Alert.AlertType.WARNING);
                    error.setContentText("Por favor, seleccione una reserva");
                    error.showAndWait();
                    if (error.getResult() == ButtonType.OK) {
                        error.close();
                    }
            }
        });

        newReservation.setOnAction(event -> {
            AddReservation(stage,roomReservation);
        });
        returnbtn.setOnAction(event -> {
            try {
                stage.setScene(ReceptionistPanel(stage));
                stage.centerOnScreen();
                stage.setTitle("Panel recepcionista");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ComboBox cmbox = (ComboBox) roomReservation.lookup("#reserveType");
        ComboBox chooseRoom = (ComboBox) roomReservation.lookup("#chooseRoom");
        ComboBox chooseClient = (ComboBox) roomReservation.lookup("#chooseClient");
        ComboBox chooseReceptionist = (ComboBox) roomReservation.lookup("#chooseReceptionist");

        ResultSet rs = this.mySQLRepository.GetAllRooms();
        while(rs.next()) {
            chooseRoom.getItems().add(rs.getInt("Numero"));
        }

        ResultSet rs2 = this.mySQLRepository.GetAllClients();
        while(rs2.next()) {
            chooseClient.getItems().add(rs2.getString("DNI") + " - " + rs2.getString("Nom") + " " + rs2.getString("Cognoms"));
        }

        ResultSet rs3 = this.mySQLRepository.GetAllReceptionists();
        while(rs3.next()) {
            chooseReceptionist.getItems().add(rs3.getString("DNI") + " - " + rs3.getString("Nombre") + " " + rs3.getString("Apellidos"));
        }

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

    public void AddReservation(Stage stage,Scene roomReservation) {
        GridPane reservationGrid = (GridPane) roomReservation.lookup("#reservationGrid");
        Alert error = new Alert(Alert.AlertType.ERROR,"La habitación está ocupada en la fecha seleccionada",ButtonType.OK);
        ArrayList values = new ArrayList<>();
        for (Node node : reservationGrid.getChildren()) {
            if (node instanceof HBox) {
                for(Node node1: ((HBox) node).getChildren()) {
                    if (node1 instanceof TextField) {
                        TextField text = (TextField) roomReservation.lookup("#" + node1.getId());
                        if(!text.getText().isEmpty()) {
                            values.add(text.getText());
                        }

                    }
                    else if (node1 instanceof ComboBox) {
                        ComboBox comBox = (ComboBox) roomReservation.lookup("#" + node1.getId());
                        if(comBox.getSelectionModel().getSelectedItem() != null) {
                            values.add(comBox.getSelectionModel().getSelectedItem());
                        }

                    }
                    else if (node1 instanceof DatePicker) {
                        DatePicker date = (DatePicker) roomReservation.lookup("#" + node1.getId());
                        if(date.getValue() != null) {
                            LocalDate datepicker = date.getValue();
                            values.add(datepicker);
                        }

                    }

                }
            }
        }
        try {
            if(values.size() == 5) {
                ResultSet rs = this.mySQLRepository.CheckDates(values);
                rs.next();
                if(rs.getInt("Match") != 0) {
                    error.setAlertType(Alert.AlertType.ERROR);
                    error.showAndWait();
                    if (error.getResult() == ButtonType.OK) {
                        error.close();
                    }
                }
                else {
                    LocalDate admitDate = (LocalDate) values.get(4);
                    LocalDate exitDate = (LocalDate) values.get(5);
                    long diff = ChronoUnit.DAYS.between(admitDate,exitDate);
                    if(diff < 0) {
                        error.setAlertType(Alert.AlertType.ERROR);
                        error.setContentText("La fecha de entrada no puede ser posterior a la fecha de salida");
                        error.showAndWait();
                        if (error.getResult() == ButtonType.OK) {
                            error.close();
                        }
                    }
                    else {
                        int numeroNoches = (int) diff;
                        String idCliente = (String) values.get(1);
                        idCliente.trim();
                        idCliente = idCliente.split("-")[0];
                        int numero = (Integer) values.get(0);
                        ResultSet rs2 = this.mySQLRepository.GetPrice(numero);
                        rs2.next();
                        int precio = rs2.getInt("Precio") * numeroNoches;
                        if(this.mySQLRepository.NewReservation(values,idCliente,Sesión.username,precio) == 1) {
                            stage.setScene(RoomReservation(stage));
                            stage.centerOnScreen();
                        }

                    }

                }


            }
            else {
                error.setAlertType(Alert.AlertType.WARNING);
                error.setContentText("No pueden haber campos vacíos");
                error.showAndWait();
                if (error.getResult() == ButtonType.OK) {
                    error.close();
                }
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
