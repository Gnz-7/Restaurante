package com.restaurante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CrearBaseDatos {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:Restaurante.db";

        // SQL para crear tabla Usuario
        String sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "rol TEXT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT NOT NULL," +
                "dni TEXT NOT NULL UNIQUE," +   // DNI único
                "cuil TEXT," +
                "direccion TEXT," +
                "localidad TEXT," +
                "fecha_ingreso TEXT," +
                "perfil TEXT," +
                "contrasena TEXT NOT NULL" +    // Sin ñ para evitar errores en Java
                ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsuario);
            System.out.println("Base de datos y tabla 'Usuario' creada correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }
}
