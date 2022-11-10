package com.example.demo.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLRepository {
    private Statement statement;

    public MySQLRepository(Statement statement) {
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
}
