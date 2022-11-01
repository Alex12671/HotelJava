package com.example.demo.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLRepository {
    private Statement statement;

    public MySQLRepository(Statement statement) {
        this.statement = statement;
    }

    public ResultSet GetAdminCredentials(String usuario, String password) throws SQLException {
        return this.statement.executeQuery("SELECT * FROM admin WHERE user = '" + usuario + "' AND password = '" + password + "'");
    }

    public ResultSet GetValidatedReceptionist() throws SQLException {
        return this.statement.executeQuery("SELECT * FROM recepcionistas WHERE Validado = '0'");
    }

    public int UpdateRecepcionist(String usuario, String password, String nom, String cognoms, String dni, String nacionalitat, Integer telefon, String email) throws SQLException {
        return this.statement.executeUpdate("INSERT INTO recepcionistas VALUES (DEFAULT,'" + usuario + "','" + password +
                "','" + nom + "','" + cognoms + "','" + dni + "','" + nacionalitat + "','" + telefon + "','" + email + "','0')");
    }
}
