/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventarisstudiofoto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    public static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/inventory_studio";
        String username = "root"; // Sesuaikan dengan username MySQL Anda
        String password = "";     // Kosongkan jika tanpa password
        
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal terhubung ke database!");
        }
    }
}
