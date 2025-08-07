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
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlanController {

    // --- Componentes FXML ---
    @FXML private TableView<Materia> tableView;
    @FXML private TableColumn<Materia, Number> colNumero;
    @FXML private TableColumn<Materia, String> colNivel;
    @FXML private TableColumn<Materia, String> colCodigo;
    @FXML private TableColumn<Materia, String> colMateria;
    @FXML private TableColumn<Materia, String> colEstado;
    @FXML private TableColumn<Materia, String> colDificultad;
    @FXML private TableColumn<Materia, String> colDescripcion;
    @FXML private Button logoutButton;
    @FXML private ProgressBar progresoBarra;
    @FXML private Label progresoLabel;

    // --- VARIABLES @FXML QUE FALTABAN ---
    @FXML private Button agregarButton;
    @FXML private Button eliminarButton;

    // --- Componentes para la Pestaña Recomendaciones ---
    @FXML private Button generarPlanButton;
    @FXML private TitledPane casoIdealPane;
    @FXML private TextArea casoIdealTextArea;
    @FXML private TitledPane casoOptimistaPane;
    @FXML private TextArea casoOptimistaTextArea;

    private int usuarioIdActual;
    private ObservableList<Materia> materias;

    // --- NUEVOS CAMPOS DE TEXTO PARA EL PLAN PERSONALIZADO ---
    @FXML private TextField semestreCargaField;
    @FXML private TextField temporadaCargaField;


    /**
     * El punto de entrada principal. Carga los datos y configura la interfaz.
     */
    public void initData(int idUsuario) {
        this.usuarioIdActual = idUsuario;
        List<Materia> materiasGuardadas = DatabaseManager.cargarMateriasDeUsuario(this.usuarioIdActual);

        if (materiasGuardadas.isEmpty()) {
            this.materias = cargarDatosPorDefecto();
        } else {
            this.materias = FXCollections.observableArrayList(materiasGuardadas);
        }

        configurarTabla();
        tableView.setItems(this.materias);

        // Llamamos al método para establecer el progreso inicial
        actualizarProgreso();
    }

    @FXML
    public void initialize() {
        // Intencionadamente vacío. La lógica se mueve a initData.
    }

    /**
     * Configura las celdas de la tabla y sus comportamientos de edición.
     */
    private void configurarTabla() {
        tableView.setEditable(true);

        // Configuración de las celdas para mostrar datos
        colNumero.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNumero()));
        colNivel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNivel()));
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colMateria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMateria()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colDificultad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDificultad()));
        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));

        // ComboBox para "ESTADO"
        ObservableList<String> estadosPosibles = FXCollections.observableArrayList("Aprobado", "Cursando", "Pendiente");
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn(estadosPosibles));
        colEstado.setOnEditCommit(event -> {
            event.getRowValue().setEstado(event.getNewValue());
            tableView.refresh();
            // CADA VEZ QUE CAMBIA EL ESTADO, ACTUALIZAMOS LA BARRA
            actualizarProgreso();
        });

        // ComboBox para "DIFICULTAD"
        ObservableList<String> dificultadesPosibles = FXCollections.observableArrayList("Baja", "Media", "Alta", "Muy Alta");
        colDificultad.setCellFactory(ComboBoxTableCell.forTableColumn(dificultadesPosibles));
        colDificultad.setOnEditCommit(event -> event.getRowValue().setDificultad(event.getNewValue()));

        // TextField para "DESCRIPCION"
        colDescripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescripcion.setOnEditCommit(event -> event.getRowValue().setDescripcion(event.getNewValue()));

        // Fábrica de filas para los colores
        tableView.setRowFactory(tv -> new TableRow<Materia>() {
            @Override
            protected void updateItem(Materia item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("aprobado-row", "cursando-row", "pendiente-row");
                if (!empty && item != null) {
                    switch (item.getEstado()) {
                        case "Aprobado" -> getStyleClass().add("aprobado-row");
                        case "Cursando" -> getStyleClass().add("cursando-row");
                        case "Pendiente" -> getStyleClass().add("pendiente-row");
                    }
                }
            }
        });
    }
    @FXML
    private void onAgregarMateriaClick(ActionEvent event) {
        // Creamos una nueva materia en blanco
        Materia nuevaMateria = new Materia(materias.size() + 1, "N/A", "Nuevo Código", "Nueva Materia", "Pendiente", "Media", "");
        materias.add(nuevaMateria);
        tableView.scrollTo(nuevaMateria);
        tableView.getSelectionModel().select(nuevaMateria);
        actualizarProgreso();
    }

    // --- NUEVO: Lógica para el botón de Eliminar Materia ---
    @FXML
    private void onEliminarMateriaClick(ActionEvent event) {
        Materia materiaSeleccionada = tableView.getSelectionModel().getSelectedItem();

        if (materiaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ninguna Selección");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona la materia que deseas eliminar.");
            alert.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("Estás a punto de eliminar la materia: " + materiaSeleccionada.getMateria());
        confirmacion.setContentText("¿Estás seguro de que quieres continuar?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            materias.remove(materiaSeleccionada);
            actualizarProgreso();
        }
    }
    /**
     * Calcula y actualiza la barra de progreso y su etiqueta.
     */
    private void actualizarProgreso() {
        if (materias == null || materias.isEmpty()) {
            progresoBarra.setProgress(0.0);
            progresoLabel.setText("Progreso: 0.0%");
            return;
        }

        // Contamos cuántas materias tienen el estado "Aprobado"
        long aprobadas = materias.stream()
                .filter(m -> "Aprobado".equalsIgnoreCase(m.getEstado()))
                .count();

        double totalMaterias = materias.size();
        // Calculamos el progreso como una fracción (valor entre 0.0 y 1.0)
        double progreso = (totalMaterias > 0) ? (double) aprobadas / totalMaterias : 0.0;

        // Actualizamos los componentes de la interfaz
        progresoBarra.setProgress(progreso);
        progresoLabel.setText(String.format("Progreso: %.1f%%", progreso * 100));
    }

    /**
     * Guarda el progreso del usuario en la base de datos.
     */
    public void guardarProgreso() {
        if (this.materias != null && this.usuarioIdActual > 0) {
            DatabaseManager.guardarMateriasDeUsuario(this.materias, this.usuarioIdActual);
        }
    }

    /**
     * Se activa con el botón de "Cerrar Sesión".
     */
    @FXML
    protected void onLogoutButtonClick(ActionEvent event) {
        guardarProgreso();
        try {
            Stage stageActual = (Stage) logoutButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/windowsxp/fsv/fcyt/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 650);

            URL cssUrl = getClass().getResource("styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("¡ADVERTENCIA! No se encontró el archivo de estilos: styles.css");
            }

            stageActual.setScene(scene);
            stageActual.setTitle("Iniciar Sesión - Plan de Estudios");
            stageActual.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la lista de materias por defecto para un usuario nuevo.
     */
    private ObservableList<Materia> cargarDatosPorDefecto() {
        // Tu lista de 57 materias aquí...
        return FXCollections.observableArrayList(
                new Materia(1, "A", "1803001", "Inglés I", "Pendiente", "Baja", ""),
                new Materia(2, "A", "2006063", "Física General", "Pendiente", "Media", ""),
                new Materia(3, "A", "2008019", "Álgebra I", "Pendiente", "Media", ""),
                new Materia(4, "A", "2008054", "Cálculo I", "Pendiente", "Alta", ""),
                new Materia(5, "A", "2010010", "Introducción a la Programación", "Pendiente", "Alta", ""),
                new Materia(6, "A", "2010140", "Metodología de Investigación y Técnicas de Comunicación", "Pendiente", "Baja", ""),
                new Materia(7, "B", "2008022", "Álgebra II", "Pendiente", "Alta", ""),
                new Materia(8, "B", "2008056", "Cálculo II", "Pendiente", "Alta", ""),
                new Materia(9, "B", "2008057", "Matemática Discreta", "Pendiente", "Baja", ""),
                new Materia(10, "B", "2010003", "Elementos de Programación y Estructuras de Datos", "Pendiente", "Muy Alta", ""),
                new Materia(11, "B", "2010013", "Arquitectura de Computadoras I", "Pendiente", "Baja", ""),
                new Materia(12, "C", "2008058", "Ecuaciones Diferenciales", "Pendiente", "Muy Alta", ""),
                new Materia(13, "C", "2008059", "Estadística I", "Pendiente", "Baja", ""),
                new Materia(14, "C", "2008060", "Cálculo Numérico", "Pendiente", "Media", ""),
                new Materia(15, "C", "2010012", "Métodos, Técnicas y Taller de Programación", "Pendiente", "Media", ""),
                new Materia(16, "C", "2010015", "Base de Datos I", "Pendiente", "Baja", ""),
                new Materia(17, "C", "2010141", "Circuitos Electrónicos", "Pendiente", "Media", ""),
                new Materia(18, "D", "2008061", "Estadística II", "Pendiente", "Media", ""),
                new Materia(19, "D", "2010016", "Base de Datos II", "Pendiente", "Alta", ""),
                new Materia(20, "D", "2010017", "Taller de Sistemas Operativos", "Pendiente", "Alta", ""),
                new Materia(21, "D", "2010018", "Sistemas de Información I", "Pendiente", "Media", ""),
                new Materia(22, "D", "2016046", "Contabilidad Básica", "Pendiente", "Media", ""),
                new Materia(23, "D", "2016048", "Investigación Operativa I", "Pendiente", "Media", ""),
                new Materia(24, "E", "1803002", "Inglés II", "Pendiente", "Baja", ""),
                new Materia(25, "E", "2010022", "Sistemas de Información II", "Pendiente", "Media", ""),
                new Materia(26, "E", "2010035", "Aplicación de Sistemas Operativos", "Pendiente", "Alta", ""),
                new Materia(27, "E", "2010053", "Taller de Base de Datos", "Pendiente", "Alta", ""),
                new Materia(28, "E", "2010142", "Sistemas I", "Pendiente", "Baja", ""),
                new Materia(29, "E", "2016051", "Investigación Operativa II", "Pendiente", "Alta", ""),
                new Materia(30, "E", "2016057", "Mercadotecnia", "Pendiente", "Baja", ""),
                new Materia(31, "F", "2010019", "Simulación de Sistemas", "Pendiente", "Baja", ""),
                new Materia(32, "F", "2010020", "Ingeniería de Software", "Pendiente", "Media", ""),
                new Materia(33, "F", "2010027", "Inteligencia Artificial", "Pendiente", "Media", ""),
                new Materia(34, "F", "2010047", "Redes de Computadoras", "Pendiente", "Alta", ""),
                new Materia(35, "F", "2010143", "Sistemas II", "Pendiente", "Baja", ""),
                new Materia(36, "F", "2010144", "Sistemas Económicos", "Pendiente", "Baja", ""),
                new Materia(37, "F", "2010182", "Telefonía IP", "Pendiente", "Electiva", ""),
                new Materia(38, "G", "2010024", "Taller de Ingeniería de Software", "Pendiente", "Alta", ""),
                new Materia(39, "G", "2010145", "Gestión de Calidad de Software", "Pendiente", "Media", ""),
                new Materia(40, "G", "2010146", "Redes Avanzadas de Computadoras", "Pendiente", "Alta", ""),
                new Materia(41, "G", "2010186", "Dinámica de Sistemas", "Pendiente", "Alta", ""),
                new Materia(42, "G", "2016092", "Planificación y Evaluación de Proyectos", "Pendiente", "Media", ""),
                new Materia(43, "G", "2010211", "Aplicaciones Interactivas para Televisión Digital", "Pendiente", "Electiva", ""),
                new Materia(44, "H", "1803009", "Inglés III", "Pendiente", "Baja", ""),
                new Materia(45, "H", "2010102", "Evaluación y Auditoría de Sistemas", "Pendiente", "Media", ""),
                new Materia(46, "H", "2010116", "Taller de Simulación de Sistemas", "Pendiente", "Media", ""),
                new Materia(47, "H", "2010119", "Metodología y Planificación de Proyecto de Grado", "Pendiente", "Alta", ""),
                new Materia(48, "H", "2010209", "Seguridad de Sistemas", "Pendiente", "Alta", ""),
                new Materia(49, "H", "2016059", "Gestión Estratégica de Empresas", "Pendiente", "Media", ""),
                new Materia(50, "H", "2010034", "Sistemas Colaborativos", "Pendiente", "Electiva", ""),
                new Materia(51, "I", "2010122", "Proyecto Final (Taller de Titulación)", "Pendiente", "Muy Alta", ""),
                new Materia(52, "I", "2010147", "Práctica Empresarial", "Pendiente", "Baja", ""),
                new Materia(53, "I", "2010079", "Web Semánticas", "Pendiente", "Electiva", ""),
                new Materia(54, "I", "2010103", "Robótica", "Pendiente", "Electiva", ""),
                new Materia(55, "J", "2010033", "Generación de Software", "Pendiente", "Electiva", ""),
                new Materia(56, "J", "2010044", "Diseño de Compiladores", "Pendiente", "Electiva", ""),
                new Materia(57, "J", "2010191", "Técnicas de Ruteo Avanzada", "Pendiente", "Electiva", "")
        );
    }

    // --- NUEVO: Lógica para el botón "Generar Plan" ---
    @FXML
    private void onGenerarPlanClick(ActionEvent event) {
        int cargaSemestreIdeal;
        int cargaTemporadaIdeal;

        // --- VALIDACIÓN DE LA ENTRADA DEL USUARIO ---
        try {
            cargaSemestreIdeal = Integer.parseInt(semestreCargaField.getText());
            cargaTemporadaIdeal = Integer.parseInt(temporadaCargaField.getText());

            if (cargaSemestreIdeal <= 0 || cargaTemporadaIdeal < 0 || cargaSemestreIdeal > 7 || cargaTemporadaIdeal > 2) {
                throw new NumberFormatException("Valores fuera de rango.");
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entrada Inválida");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingresa números válidos y razonables (Semestre: 1-7, Temporada: 0-2).");
            alert.showAndWait();
            return; // Detenemos la ejecución si la entrada es mala
        }

        // 1. Tomamos solo las materias que no están aprobadas.
        List<Materia> materiasPorCursar = materias.stream()
                .filter(m -> !"Aprobado".equalsIgnoreCase(m.getEstado()))
                .collect(Collectors.toList());

        // 2. Generamos el plan para el "Caso Ideal" usando los valores del usuario.
        String planIdeal = generarPlan(new ArrayList<>(materiasPorCursar), cargaTemporadaIdeal, cargaSemestreIdeal, "Caso Ideal Personalizado");
        casoIdealTextArea.setText(planIdeal);

        // 3. Generamos el plan para el "Caso Optimista" usando valores fijos (la máxima carga).
        String planOptimista = generarPlan(new ArrayList<>(materiasPorCursar), 2, 6, "Caso Optimista (Carga Máxima)");
        casoOptimistaTextArea.setText(planOptimista);
    }

    // --- NUEVO: El algoritmo que genera el plan de estudios ---
    private String generarPlan(List<Materia> materiasPendientes, int cargaTemporada, int cargaSemestre, String nombreCaso) {
        if (materiasPendientes.isEmpty()) {
            return "¡Felicidades! Has completado todas las materias de tu plan.";
        }

        StringBuilder plan = new StringBuilder();
        int anio = 1;
        int periodosTotales = 0;
        String[] nombresPeriodos = {"Primer Semestre", "Curso de Invierno", "Segundo Semestre", "Curso de Verano"};
        int[] cargasMaximas = {cargaSemestre, cargaTemporada, cargaSemestre, cargaTemporada};

        while (!materiasPendientes.isEmpty()) {
            plan.append("--- Año ").append(anio).append(" ---\n");

            for (int i = 0; i < nombresPeriodos.length; i++) {
                if (materiasPendientes.isEmpty()) break;

                periodosTotales++;
                plan.append("\n  ").append(nombresPeriodos[i]).append(":\n");

                int cargaActual = cargasMaximas[i];
                List<Materia> materiasDelPeriodo = new ArrayList<>();
                while (materiasDelPeriodo.size() < cargaActual && !materiasPendientes.isEmpty()) {
                    materiasDelPeriodo.add(materiasPendientes.remove(0));
                }

                if (materiasDelPeriodo.isEmpty()) {
                    plan.append("    - (Ninguna materia asignada)\n");
                } else {
                    for (Materia m : materiasDelPeriodo) {
                        plan.append("    - ").append(m.getMateria()).append(" (").append(m.getCodigo()).append(")\n");
                    }
                }
            }
            anio++;
        }

        // --- NUEVO Y CORREGIDO CÁLCULO DE TIEMPO ---

        // 1. Convertimos los periodos totales a un número total de "semestres equivalentes".
        //    (Ej: 7 periodos son ~4 semestres; 5 periodos son ~3 semestres)
        int totalSemestres = (int) Math.ceil((double) periodosTotales / 2.0);

        // 2. Convertimos los semestres totales en años y semestres sobrantes.
        int aniosCompletos = totalSemestres / 2;
        int semestresSueltos = totalSemestres % 2;

        StringBuilder tiempoEstimado = new StringBuilder();
        if (aniosCompletos > 0) {
            tiempoEstimado.append(aniosCompletos).append(aniosCompletos > 1 ? " años" : " año");
        }

        if (semestresSueltos > 0) {
            if (!tiempoEstimado.isEmpty()) {
                tiempoEstimado.append(" y ");
            }
            tiempoEstimado.append(semestresSueltos).append(semestresSueltos > 1 ? " semestres" : " semestre");
        }

        // Si no hay nada, significa que es menos de un semestre.
        if (tiempoEstimado.isEmpty() && periodosTotales > 0) {
            tiempoEstimado.append("1 semestre");
        }

        plan.append("\n============================================\n");
        plan.append("Tiempo estimado para finalizar (").append(nombreCaso).append("): ").append(tiempoEstimado);

        return plan.toString();
    }


}