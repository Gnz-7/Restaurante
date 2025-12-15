package com.restaurante;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CrearBaseDatos {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:Restaurante.db";

        // Tabla Usuario (no se modifica)
        String sqlUsuario = "CREATE TABLE IF NOT EXISTS Usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "rol TEXT," +
                "nombre TEXT NOT NULL," +
                "apellido TEXT NOT NULL," +
                "dni TEXT NOT NULL UNIQUE," +
                "cuil TEXT," +
                "direccion TEXT," +
                "localidad TEXT," +
                "fecha_ingreso TEXT," +
                "perfil TEXT," +
                "contrasena TEXT NOT NULL" +
                ");";

        // Tabla DatosEmpleado (modificada)
        String sqlDatosEmpleado = "CREATE TABLE IF NOT EXISTS DatosEmpleado (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "sueldo_bruto_inicial REAL," + // agregado
                "anos_experiencia INTEGER," +
                "recargo REAL," +
                "sueldo_bruto_final REAL," +
                "esta_activo INTEGER DEFAULT 1," +  // 1 = activo, 0 = inactivo
                "FOREIGN KEY(usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE" +
                ");";

         // =========================
        // TABLA MESA
        // =========================
        String sqlMesa = "CREATE TABLE IF NOT EXISTS Mesa (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numero_mesa INTEGER NOT NULL" +
                ");";

        // =========================
        // TABLA PLATO
        // =========================
        String sqlPlato = "CREATE TABLE IF NOT EXISTS Plato (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "precio REAL NOT NULL" +
                ");";

        // =========================
        // TABLA ACOMPANAMIENTO
        // =========================
        String sqlAcompanamiento = "CREATE TABLE IF NOT EXISTS Acompanamiento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "precio REAL NOT NULL" +
                ");";

        // =========================
        // TABLA GANANCIA
        // =========================
        String sqlGanancia = "CREATE TABLE IF NOT EXISTS Ganancia (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mesa_id INTEGER NOT NULL," +
                "fecha TEXT NOT NULL," +     // YYYY-MM-DD
                "hora TEXT NOT NULL," +      // HH:MM:SS
                "total REAL NOT NULL," +
                "FOREIGN KEY (mesa_id) REFERENCES Mesa(id) ON DELETE RESTRICT" +
                ");";

        // =========================
        // CREAR BASE Y TABLAS
        // =========================
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsuario);
            stmt.execute(sqlDatosEmpleado);
            stmt.execute(sqlMesa);
            stmt.execute(sqlPlato);
            stmt.execute(sqlAcompanamiento);
            stmt.execute(sqlGanancia);

            System.out.println("Base de datos y todas las tablas creadas correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}