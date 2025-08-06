package com.windowsxp.fsv.fcyt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // La ruta a nuestra base de datos. Será un archivo en la carpeta del proyecto.
    private static final String URL = "jdbc:sqlite:plan_de_estudios.db";

    /**
     * Establece una conexión con la base de datos SQLite.
     * Si el archivo de la base de datos no existe, lo creará.
     */
    public static void conectar() {
        Connection conn = null;
        try {
            // Creamos la conexión a la base de datos
            conn = DriverManager.getConnection(URL);
            System.out.println("Conexión a SQLite establecida.");

            // Llamamos a un método para crear las tablas si es la primera vez
            crearTablasSiNoExisten(conn);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * Crea las tablas 'usuarios' y 'materias' si aún no existen.
     */
    private static void crearTablasSiNoExisten(Connection conn) throws SQLException {
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario TEXT NOT NULL UNIQUE,
                contrasena_hash TEXT NOT NULL
            );""";

        String sqlMaterias = """
            CREATE TABLE IF NOT EXISTS materias (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_usuario INTEGER NOT NULL,
                numero INTEGER NOT NULL,
                nivel TEXT,
                codigo TEXT,
                materia TEXT,
                estado TEXT,
                dificultad TEXT,
                descripcion TEXT,
                FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
            );""";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlMaterias);
            System.out.println("Tablas 'usuarios' y 'materias' listas.");
        }
    }
}
