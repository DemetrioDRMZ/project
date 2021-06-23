package project;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion{
    
    private static Connection conn;
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private String usuario = "root";
    private String password = "";

    public Conexion() {
        conn = null;
        String url = "jdbc:mysql://localhost:3306/project?useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&allowPublicKeyRetrieval=TRUE";
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, usuario, password);
            if(conn != null){
                System.out.println("Conexión establecida");
            }
        } catch (ClassNotFoundException | SQLException e){ 
            System.out.println("Error al conectar: " + e);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void desconectar() {
        try {
            conn.close();
        } catch (Exception ex) {
        }
    }

}