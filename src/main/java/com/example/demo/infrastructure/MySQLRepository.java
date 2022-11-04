package com.example.demo.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        return this.statement.executeQuery("SELECT * FROM clientes");
    }

    public int AddClient(String estat_civil, String ocupacio, String nom, String cognoms, String dni, String nacionalitat, Integer telefon, String email) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO clientes VALUES ('" + dni + "','" + nom +
                "','" + cognoms + "','" + nacionalitat + "','" + telefon + "','" + email + "','" + ocupacio + "','" + estat_civil + "')");
    }
}
