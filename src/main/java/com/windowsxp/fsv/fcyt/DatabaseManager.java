package com.windowsxp.fsv.fcyt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
    /**
     * Registra un nuevo usuario en la base de datos con una contraseña hasheada.
     * @return true si el registro fue exitoso, false si el usuario ya existe.
     */
    public static boolean registrarUsuario(String nombreUsuario, String contrasenaPlana) {
        String sql = "INSERT INTO usuarios(nombre_usuario, contrasena_hash) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, hashPassword(contrasenaPlana)); // Guardamos el hash, no la contraseña
            pstmt.executeUpdate();
            System.out.println("Usuario '" + nombreUsuario + "' registrado exitosamente.");
            return true;

        } catch (SQLException e) {
            // El código de error "19" en SQLite significa una violación de restricción ÚNICA (el usuario ya existe)
            if (e.getErrorCode() == 19) {
                System.err.println("Error: El nombre de usuario '" + nombreUsuario + "' ya existe.");
            } else {
                System.err.println("Error al registrar usuario: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Crea un hash SHA-256 de una contraseña.
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error de hashing", e);
        }
    }
    /**
     * Valida un usuario y contraseña contra la base de datos.
     * @return El ID del usuario si el login es exitoso, o -1 si falla.
     */
    public static int validarUsuarioYObtenerId(String nombreUsuario, String contrasenaPlana) {
        String sql = "SELECT id, contrasena_hash FROM usuarios WHERE nombre_usuario = ?";
        String hashAlmacenado = null;
        int idUsuario = -1;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreUsuario);
            ResultSet rs = pstmt.executeQuery();

            // Verificamos si el usuario fue encontrado
            if (rs.next()) {
                idUsuario = rs.getInt("id");
                hashAlmacenado = rs.getString("contrasena_hash");
            }

        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
            return -1; // Retornamos -1 en caso de error
        }

        // Si se encontró un hash, lo comparamos
        if (hashAlmacenado != null) {
            String hashIngresado = hashPassword(contrasenaPlana); // Usamos el mismo método de hash
            if (hashAlmacenado.equals(hashIngresado)) {
                return idUsuario; // ¡Éxito! Retornamos el ID del usuario.
            }
        }

        // Si el usuario no existe o la contraseña no coincide, retornamos -1.
        return -1;
    }
    /**
     * Carga la lista de materias para un ID de usuario específico.
     */
    public static List<Materia> cargarMateriasDeUsuario(int idUsuario) {
        String sql = "SELECT * FROM materias WHERE id_usuario = ?";
        List<Materia> materias = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Materia materia = new Materia();
                materia.setNumero(rs.getInt("numero"));
                materia.setNivel(rs.getString("nivel"));
                materia.setCodigo(rs.getString("codigo"));
                materia.setMateria(rs.getString("materia"));
                materia.setEstado(rs.getString("estado"));
                materia.setDificultad(rs.getString("dificultad"));
                materia.setDescripcion(rs.getString("descripcion"));
                materias.add(materia);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar materias: " + e.getMessage());
        }
        return materias;
    }

    /**
     * Guarda la lista completa de materias para un usuario.
     * Borra las anteriores e inserta las nuevas para asegurar consistencia.
     */
    public static void guardarMateriasDeUsuario(List<Materia> materias, int idUsuario) {
        String sqlDelete = "DELETE FROM materias WHERE id_usuario = ?";
        String sqlInsert = "INSERT INTO materias(id_usuario, numero, nivel, codigo, materia, estado, dificultad, descripcion) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Usamos una transacción para que sea todo o nada
            conn.setAutoCommit(false);

            // 1. Borramos todas las materias viejas del usuario
            try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                pstmtDelete.setInt(1, idUsuario);
                pstmtDelete.executeUpdate();
            }

            // 2. Insertamos todas las materias de la lista actual
            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                for (Materia materia : materias) {
                    pstmtInsert.setInt(1, idUsuario);
                    pstmtInsert.setInt(2, materia.getNumero());
                    pstmtInsert.setString(3, materia.getNivel());
                    pstmtInsert.setString(4, materia.getCodigo());
                    pstmtInsert.setString(5, materia.getMateria());
                    pstmtInsert.setString(6, materia.getEstado());
                    pstmtInsert.setString(7, materia.getDificultad());
                    pstmtInsert.setString(8, materia.getDescripcion());
                    pstmtInsert.addBatch(); // Agrupamos los inserts para eficiencia
                }
                pstmtInsert.executeBatch();
            }

            conn.commit(); // Confirmamos la transacción
            System.out.println("Progreso del usuario con ID " + idUsuario + " guardado exitosamente.");

        } catch (SQLException e) {
            System.err.println("Error al guardar materias: " + e.getMessage());
        }
    }

}
