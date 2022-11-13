package com.example.demo.infrastructure;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLRepository {
    private Statement statement;

    public MySQLRepository(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public int GetAdminCredentials(String usuario, String password) throws SQLException {
        ResultSet rs = this.statement.executeQuery("SELECT COUNT(*) AS 'rows' FROM admin WHERE user = '" + usuario + "' AND password = '" + password + "'");
        rs.next();
        int rows = rs.getInt("rows");
        return rows;
    }

    public int GetReceptionistCredentials(String usuario, String password) throws SQLException {
        ResultSet rs = this.statement.executeQuery("SELECT COUNT(*) AS 'rows' FROM recepcionistas WHERE Usuario = '" + usuario + "' AND Password = '" + password + "' AND Validado = 1");
        rs.next();
        int rows = rs.getInt("rows");
        return rows;
    }

    public ResultSet GetValidatedReceptionist() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM recepcionistas WHERE Validado = '0'");
    }

    public int RegisterRecepcionist(String usuario, String password, String nom, String cognoms, String dni, String nacionalitat, Integer telefon, String email) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO recepcionistas VALUES (DEFAULT,'" + usuario + "','" + password +
                "','" + nom + "','" + cognoms + "','" + dni + "','" + nacionalitat + "','" + telefon + "','" + email + "','0')");
    }

    public int ValidateRecepcionist(int id) throws SQLException {
        return this.statement.executeUpdate("UPDATE recepcionistas SET Validado = 1 WHERE Id = '" + id + "'");
    }

    public int RejectReceptionist(int id) throws SQLException {

        return this.statement.executeUpdate("DELETE FROM recepcionistas WHERE Id = '" + id + "'");
    }

    public ResultSet GetAllClients() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM clientes WHERE Activado = 1");
    }

    public ResultSet GetAllReceptionists() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM recepcionistas WHERE Validado = 1");
    }

    public int AddClient(String estat_civil, String ocupacio, String nom, String cognoms, String dni, String nacionalitat, Integer telefon, String email) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO clientes VALUES ('" + dni + "','" + nom +
                "','" + cognoms + "','" + nacionalitat + "','" + telefon + "','" + email + "','" + ocupacio + "','" + estat_civil + "',1)");
    }

    public int DeleteClient(String DNI) throws SQLException {
        return this.statement.executeUpdate("UPDATE clientes SET Activado = 0 WHERE DNI = '" + DNI + "'");
    }

    public int EditClient(ArrayList array,String DNI) throws SQLException {
        return this.statement.executeUpdate("UPDATE clientes SET DNI = '" + array.get(0) + "', Nom = '" + array.get(1) + "', Cognoms = '" + array.get(2) + "', Nacionalitat = '" + array.get(3) + "', Telefon = '" + array.get(4) + "', Email = '" + array.get(5) + "', Ocupacio = '" + array.get(6) + "', Estat_Civil = '" + array.get(7) + "' WHERE DNI = " + DNI + "");
    }

    public ResultSet GetAllRooms() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM habitaciones WHERE Activado = 1");
    }

    public int DeleteRoom(String numero) throws SQLException {
        return this.statement.executeUpdate("UPDATE habitaciones SET Activado = 0 WHERE Numero = '" + numero + "'");
    }

    public int AddRoom(ArrayList array) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO habitaciones VALUES ('" + array.get(0) + "','" + array.get(1) +
                "','" + array.get(2) + "','Disponible','" + array.get(3) + "','" + array.get(4) + "','" + array.get(5) + "','" + array.get(6) + "','" + array.get(7) + "',1)");
    }

    public int EditRoom(ArrayList array,int num) throws SQLException {
        return this.statement.executeUpdate("UPDATE habitaciones SET Numero = '" + array.get(0) + "', Planta = '" + array.get(1) + "', Precio = '" + array.get(2) + "', Estado = '" + array.get(3) + "', Tipo = '" + array.get(4) + "', Numero_Camas = '" + array.get(5) + "', Superficie = '" + array.get(6) + "', Wifi = '" + array.get(7) + "', Minibar = '" + array.get(8) + "' WHERE Numero = " + num + "");
    }

    public ResultSet GetAllReservations() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM reservas");
    }

    public int NewReservation(ArrayList array, String idCliente, String idRecepcionista, int precio) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO reservas VALUES (DEFAULT,'" + idCliente + "','" + idRecepcionista +
                "','" + array.get(0) + "','" + array.get(4) + "','Pendiente','" + array.get(5) + "','Pendiente','" + precio + "','" + array.get(3) + "')");

    }

    public ResultSet CheckDates(ArrayList array) throws SQLException {
        return this.statement.executeQuery("SELECT COUNT(*) AS 'Match' FROM reservas WHERE ((Fecha_Ingreso BETWEEN '" + array.get(4) + "' AND '" + array.get(5) + "' OR Fecha_Salida BETWEEN '" + array.get(4) + "' AND '" + array.get(5) + "') OR (Fecha_Ingreso < '" + array.get(4) + "' AND Fecha_Salida > '" + array.get(5) + "')) AND Numero = '" + array.get(0) + "' ");
    }

    public ResultSet GetPrice(int numero) throws SQLException {
        return this.statement.executeQuery("SELECT Precio FROM habitaciones WHERE Numero = '" + numero + "'");
    }

    public ResultSet GetReservationPrice(int numero) throws SQLException {
        return this.statement.executeQuery("SELECT Costo FROM reservas WHERE IdReserva = '" + numero + "'");
    }

    public int ChangePaymentStatus(int numero) throws SQLException {
        return this.statement.executeUpdate("UPDATE reservas SET Estado = 'Pagada' WHERE IdReserva = '" + numero + "'");
    }

    public int CancelReservation(int numero) throws SQLException {
        return this.statement.executeUpdate("UPDATE reservas SET Estado = 'Anulada' WHERE IdReserva = '" + numero + "'");
    }

    public ResultSet GetReservationDates(int numero) throws SQLException {
        return this.statement.executeQuery("SELECT Fecha_Ingreso,Fecha_Salida,Estado,Hora_Ingreso FROM reservas WHERE IdReserva = '" + numero + "'");
    }

    public int CheckIn(int numero, String admitTime) throws SQLException {
        return this.statement.executeUpdate("UPDATE reservas SET Hora_Ingreso = '" + admitTime + "' WHERE IdReserva = '" + numero + "'");
    }

    public int CheckOut(int numero, String exitTime, String exitDate) throws SQLException {
        return this.statement.executeUpdate("UPDATE reservas SET Hora_Salida = '" + exitTime + "',Fecha_Salida = '" + exitDate + "' WHERE IdReserva = '" + numero + "'");
    }

}
