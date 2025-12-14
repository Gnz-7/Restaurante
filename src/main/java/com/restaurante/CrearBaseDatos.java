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

        // Tabla Comanda
        String sqlComanda = "CREATE TABLE IF NOT EXISTS Comanda (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "numero_mesa INTEGER NOT NULL," +
                "fecha_hora TEXT NOT NULL," +
                "estado TEXT DEFAULT 'pendiente'," +
                "total REAL" +
                ");";

        // Tabla Plato
        String sqlPlato = "CREATE TABLE IF NOT EXISTS Plato (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "comanda_id INTEGER NOT NULL," +
                "nombre TEXT NOT NULL," +
                "precio REAL NOT NULL," +
                "numero_comensal INTEGER," +
                "FOREIGN KEY(comanda_id) REFERENCES Comanda(id) ON DELETE CASCADE" +
                ");";

        // Tabla Acompanamiento
        String sqlAcompanamiento = "CREATE TABLE IF NOT EXISTS Acompanamiento (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "plato_id INTEGER NOT NULL," +
                "nombre TEXT NOT NULL," +
                "precio REAL NOT NULL," +
                "FOREIGN KEY(plato_id) REFERENCES Plato(id) ON DELETE CASCADE" +
                ");";

        // Tabla Ganancia
        String sqlGanancia = "CREATE TABLE IF NOT EXISTS Ganancia (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fecha TEXT NOT NULL," +
                "monto_total REAL NOT NULL" +
                ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Crear tablas
            stmt.execute(sqlUsuario);
            stmt.execute(sqlDatosEmpleado);
            stmt.execute(sqlComanda);
            stmt.execute(sqlPlato);
            stmt.execute(sqlAcompanamiento);
            stmt.execute(sqlGanancia);

            System.out.println("Base de datos y tablas creadas correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al crear las tablas: " + e.getMessage());
        }
    }
}
