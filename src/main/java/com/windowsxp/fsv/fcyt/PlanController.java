package com.windowsxp.fsv.fcyt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.cell.ComboBoxTableCell;

public class PlanController {

    // --- Tomado de tu código viejo: Todas las columnas declaradas ---
    @FXML private TableView<Materia> tableView;
    @FXML private TableColumn<Materia, Number> colNumero;
    @FXML private TableColumn<Materia, String> colNivel;
    @FXML private TableColumn<Materia, String> colCodigo;
    @FXML private TableColumn<Materia, String> colMateria;
    @FXML private TableColumn<Materia, String> colEstado;
    @FXML private TableColumn<Materia, String> colDificultad;
    @FXML private TableColumn<Materia, String> colDescripcion;
    @FXML private Button logoutButton;
    // --- Tomado del código nuevo: Lógica de usuario ---
    private int usuarioIdActual;
    private ObservableList<Materia> materias;

    /**
     * El nuevo punto de entrada. Es llamado por el LoginController después de un login exitoso.
     */
    public void initData(int idUsuario) {
        this.usuarioIdActual = idUsuario;

        List<Materia> materiasGuardadas = DatabaseManager.cargarMateriasDeUsuario(this.usuarioIdActual);

        if (materiasGuardadas.isEmpty()) {
            System.out.println("Usuario nuevo detectado. Cargando plan de estudios por defecto.");
            this.materias = cargarDatosPorDefecto();
        } else {
            System.out.println("Cargando progreso guardado para el usuario " + this.usuarioIdActual);
            this.materias = FXCollections.observableArrayList(materiasGuardadas);
        }

        // Llamamos a los métodos de configuración que ya tenías
        configurarTabla();
        tableView.setItems(this.materias);
    }

    @FXML
    public void initialize() {
        // Este método ahora está intencionadamente vacío. La inicialización real ocurre en initData().
    }

    /**
     * Tomado de tu código viejo: La configuración completa de la tabla.
     */
    private void configurarTabla() {
        tableView.setEditable(true);

        // --- Configuración de las celdas para mostrar datos (esto está perfecto) ---
        colNumero.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNumero()));
        colNivel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNivel()));
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colMateria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMateria()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colDificultad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDificultad()));
        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));

        // --- Celdas Editables ---

        // ComboBox para "ESTADO"
        ObservableList<String> estadosPosibles = FXCollections.observableArrayList("Aprobado", "Cursando", "Pendiente");
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(estadosPosibles));
        colEstado.setOnEditCommit(event -> {
            event.getRowValue().setEstado(event.getNewValue());
            tableView.refresh(); // Refrescamos para que el color se actualice al instante
        });

        // ComboBox para "DIFICULTAD" (¡muy bien implementado por ti!)
        ObservableList<String> dificultadesPosibles = FXCollections.observableArrayList("Baja", "Media", "Alta", "Muy Alta");
        colDificultad.setCellFactory(ComboBoxTableCell.forTableColumn(dificultadesPosibles));
        colDificultad.setOnEditCommit(event -> event.getRowValue().setDificultad(event.getNewValue()));

        // TextField para "DESCRIPCION"
        colDescripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescripcion.setOnEditCommit(event -> event.getRowValue().setDescripcion(event.getNewValue()));


        // --- El RowFactory Definitivo (Usa 100% el CSS) ---
        tableView.setRowFactory(tv -> new TableRow<Materia>() {
            @Override
            protected void updateItem(Materia item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("aprobado-row", "cursando-row", "pendiente-row");

                if (empty || item == null) {
                    // No se aplica estilo a filas vacías
                } else {
                    // Se aplica la clase de estilo correcta según el estado
                    switch (item.getEstado()) {
                        case "Aprobado":
                            getStyleClass().add("aprobado-row");
                            break;
                        case "Cursando":
                            getStyleClass().add("cursando-row");
                            break;
                        case "Pendiente":
                            getStyleClass().add("pendiente-row");
                            break;
                    }
                }
            }
        });
    }

    /**
     * Tomado del código nuevo: Guarda el progreso del usuario actual en la base de datos.
     */
    public void guardarProgreso() {
        if (this.materias != null && this.usuarioIdActual > 0) {
            DatabaseManager.guardarMateriasDeUsuario(this.materias, this.usuarioIdActual);
        }
    }

    /**
     * Tomado de tu código viejo: La lista de materias por defecto completa.
     */
    private ObservableList<Materia> cargarDatosPorDefecto() {
        return FXCollections.observableArrayList(
                new Materia(1, "A", "1803001", "Inglés I", "Pendiente", "Baja",""),
                new Materia(2, "A", "2006063", "Física General", "Pendiente", "Media",""),
                new Materia(3, "A", "2008019", "Álgebra I", "Pendiente", "Media",""),
                new Materia(4, "A", "2008054", "Cálculo I", "Pendiente", "Alta",""),
                new Materia(5, "A", "2010010", "Introducción a la Programación", "Pendiente", "Alta",""),
                new Materia(6, "A", "2010140", "Metodología de Investigación y Técnicas de Comunicación", "Pendiente", "Baja",""),
                new Materia(7, "B", "2008022", "Álgebra II", "Pendiente", "Alta",""),
                new Materia(8, "B", "2008056", "Cálculo II", "Pendiente", "Alta",""),
                new Materia(9, "B", "2008057", "Matemática Discreta", "Pendiente", "Baja",""),
                new Materia(10, "B", "2010003", "Elementos de Programación y Estructuras de Datos", "Pendiente", "Muy Alta",""),
                new Materia(11, "B", "2010013", "Arquitectura de Computadoras I", "Pendiente", "Baja",""),
                new Materia(12, "C", "2008058", "Ecuaciones Diferenciales", "Pendiente", "Muy Alta",""),
                new Materia(13, "C", "2008059", "Estadística I", "Pendiente", "Baja",""),
                new Materia(14, "C", "2008060", "Cálculo Numérico", "Pendiente", "Media",""),
                new Materia(15, "C", "2010012", "Métodos, Técnicas y Taller de Programación", "Pendiente", "Media",""),
                new Materia(16, "C", "2010015", "Base de Datos I", "Pendiente", "Baja",""),
                new Materia(17, "C", "2010141", "Circuitos Electrónicos", "Pendiente", "Media",""),
                new Materia(18, "D", "2008061", "Estadística II", "Pendiente", "Media",""),
                new Materia(19, "D", "2010016", "Base de Datos II", "Pendiente", "Alta",""),
                new Materia(20, "D", "2010017", "Taller de Sistemas Operativos", "Pendiente", "Alta",""),
                new Materia(21, "D", "2010018", "Sistemas de Información I", "Pendiente", "Media",""),
                new Materia(22, "D", "2016046", "Contabilidad Básica", "Pendiente", "Media",""),
                new Materia(23, "D", "2016048", "Investigación Operativa I", "Pendiente", "Media",""),
                new Materia(24, "E", "1803002", "Inglés II", "Pendiente", "Baja",""),
                new Materia(25, "E", "2010022", "Sistemas de Información II", "Pendiente", "Media",""),
                new Materia(26, "E", "2010035", "Aplicación de Sistemas Operativos", "Pendiente", "Alta",""),
                new Materia(27, "E", "2010053", "Taller de Base de Datos", "Pendiente", "Alta",""),
                new Materia(28, "E", "2010142", "Sistemas I", "Pendiente", "Baja",""),
                new Materia(29, "E", "2016051", "Investigación Operativa II", "Pendiente", "Alta",""),
                new Materia(30, "E", "2016057", "Mercadotecnia", "Pendiente", "Baja",""),
                new Materia(31, "F", "2010019", "Simulación de Sistemas", "Pendiente", "Baja",""),
                new Materia(32, "F", "2010020", "Ingeniería de Software", "Pendiente", "Media",""),
                new Materia(33, "F", "2010027", "Inteligencia Artificial", "Pendiente", "Media",""),
                new Materia(34, "F", "2010047", "Redes de Computadoras", "Pendiente", "Alta",""),
                new Materia(35, "F", "2010143", "Sistemas II", "Pendiente", "Baja",""),
                new Materia(36, "F", "2010144", "Sistemas Económicos", "Pendiente", "Baja",""),
                new Materia(37, "F", "2010182", "Telefonía IP", "Pendiente", "Electiva",""),
                new Materia(38, "G", "2010024", "Taller de Ingeniería de Software", "Pendiente", "Alta",""),
                new Materia(39, "G", "2010145", "Gestión de Calidad de Software", "Pendiente", "Media",""),
                new Materia(40, "G", "2010146", "Redes Avanzadas de Computadoras", "Pendiente", "Alta",""),
                new Materia(41, "G", "2010186", "Dinámica de Sistemas", "Pendiente", "Alta",""),
                new Materia(42, "G", "2016092", "Planificación y Evaluación de Proyectos", "Pendiente", "Media",""),
                new Materia(43, "G", "2010211", "Aplicaciones Interactivas para Televisión Digital", "Pendiente", "Electiva",""),
                new Materia(44, "H", "1803009", "Inglés III", "Pendiente", "Baja",""),
                new Materia(45, "H", "2010102", "Evaluación y Auditoría de Sistemas", "Pendiente", "Media",""),
                new Materia(46, "H", "2010116", "Taller de Simulación de Sistemas", "Pendiente", "Media",""),
                new Materia(47, "H", "2010119", "Metodología y Planificación de Proyecto de Grado", "Pendiente", "Alta",""),
                new Materia(48, "H", "2010209", "Seguridad de Sistemas", "Pendiente", "Alta",""),
                new Materia(49, "H", "2016059", "Gestión Estratégica de Empresas", "Pendiente", "Media",""),
                new Materia(50, "H", "2010034", "Sistemas Colaborativos", "Pendiente", "Electiva",""),
                new Materia(51, "I", "2010122", "Proyecto Final (Taller de Titulación)", "Pendiente", "Muy Alta",""),
                new Materia(52, "I", "2010147", "Práctica Empresarial", "Pendiente", "Baja",""),
                new Materia(53, "I", "2010079", "Web Semánticas", "Pendiente", "Electiva",""),
                new Materia(54, "I", "2010103", "Robótica", "Pendiente", "Electiva",""),
                new Materia(55, "J", "2010033", "Generación de Software", "Pendiente", "Electiva",""),
                new Materia(56, "J", "2010044", "Diseño de Compiladores", "Pendiente", "Electiva",""),
                new Materia(57, "J", "2010191", "Técnicas de Ruteo Avanzada", "Pendiente", "Electiva","")
        );
    }

    // 2. Añade este nuevo método completo a la clase
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) {
        // Primero, guardamos cualquier cambio que el usuario haya hecho
        guardarProgreso();

        try {
            // Obtenemos la ventana actual
            Stage stageActual = (Stage) logoutButton.getScene().getWindow();

            // Cargamos la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/windowsxp/fsv/fcyt/login-view.fxml"));
            Parent root = loader.load();

            // Creamos la nueva escena
            Scene scene = new Scene(root, 500, 650); // Usamos el mismo tamaño de inicio

            // Reemplazamos la escena actual por la de login
            stageActual.setScene(scene);
            stageActual.setTitle("Iniciar Sesión - Plan de Estudios");
            stageActual.centerOnScreen(); // La centramos de nuevo

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}