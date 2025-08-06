package com.windowsxp.fsv.fcyt;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class PlanController {

    @FXML private TableView<Materia> tableView;
    @FXML private TableColumn<Materia, Number> colNumero;
    @FXML private TableColumn<Materia, String> colNivel;
    @FXML private TableColumn<Materia, String> colCodigo;
    @FXML private TableColumn<Materia, String> colMateria;
    @FXML private TableColumn<Materia, String> colEstado;
    @FXML private TableColumn<Materia, String> colDificultad;
    @FXML private TableColumn<Materia, String> colDescripcion;

    // La lista de materias que se muestra en la tabla
    private ObservableList<Materia> materias;

    // Una instancia de nuestro manejador de datos
    private final MateriaDataManager dataManager = new MateriaDataManager();

    @FXML
    public void initialize() {
        // 1. Intentamos cargar las materias desde el archivo JSON.
        this.materias = dataManager.cargarMaterias();

        // 2. Si no se pudo cargar (porque el archivo no existe), cargamos la lista por defecto.
        if (this.materias == null) {
            this.materias = cargarDatosPorDefecto();
        }

        // 3. Configuramos cómo se verá la tabla y cómo funcionará la edición.
        configurarTabla();

        // 4. Finalmente, le damos los datos a la tabla para que los muestre.
        tableView.setItems(materias);
    }

    private void configurarTabla() {
        tableView.setEditable(true);

        // Usamos los getters de la clase Materia simplificada.
        // Para el número, usamos .asObject() para que el Integer se trate como Number.
        colNumero.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNumero()));
        colNivel.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNivel()));
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colMateria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMateria()));
        colEstado.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colDificultad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDificultad()));
        colDescripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescripcion()));


        // La lógica de edición sigue siendo la misma.
        colEstado.setCellFactory(TextFieldTableCell.forTableColumn());
        colEstado.setOnEditCommit(event -> {
            Materia materia = event.getRowValue();
            materia.setEstado(event.getNewValue());
        });

        colDificultad.setCellFactory(TextFieldTableCell.forTableColumn());
        colDificultad.setOnEditCommit(event -> {
            Materia materia = event.getRowValue();
            materia.setDificultad(event.getNewValue());
        });

        colDescripcion.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescripcion.setOnEditCommit(event -> {
            Materia materia = event.getRowValue();
            materia.setDescripcion(event.getNewValue());
        });
    }

    /**
     * Este método público permite que otras clases (como la clase principal de la aplicación)
     * obtengan la lista actual de materias. Lo necesitaremos para guardar.
     */
    public ObservableList<Materia> getMaterias() {
        return materias;
    }

    /**
     * Este método ahora solo se llama UNA VEZ, la primera vez que se ejecuta el programa
     * y no se encuentra el archivo "plan_de_estudios.json".
     */
    private ObservableList<Materia> cargarDatosPorDefecto() {
        return FXCollections.observableArrayList(
                //
                // PEGA AQUÍ TU LISTA COMPLETA DE TODAS LAS MATERIAS
                //
                new Materia(1, "A", "1803001", "Inglés I", "Aprobada", "EZ",""),
                new Materia(2, "A", "2006063", "Física General", "Aprobada", "Media",""),
                new Materia(3, "A", "2008019", "Álgebra I", "Aprobada", "Media",""),
                new Materia(4, "A", "2008054", "Cálculo I", "Aprobada", "Alta",""),
                new Materia(5, "A", "2010010", "Introducción a la Programación", "Aprobada", "Alta",""),
                new Materia(6, "A", "2010140", "Metodología de Investigación y Técnicas de Comunicación", "Aprobada", "EZ",""),
                new Materia(7, "B", "2008022", "Álgebra II", "Aprobada", "Alta",""),
                new Materia(8, "B", "2008056", "Cálculo II", "Aprobada", "Alta",""),
                new Materia(9, "B", "2008057", "Matemática Discreta", "Aprobada", "EZ",""),
                new Materia(10, "B", "2010003", "Elementos de Programación y Estructuras de Datos", "Aprobada", "Muy Alta",""),
                new Materia(11, "B", "2010013", "Arquitectura de Computadoras I", "Aprobada", "EZ",""),
                new Materia(12, "C", "2008058", "Ecuaciones Diferenciales", "Aprobada", "Muy Alta",""),
                new Materia(13, "C", "2008059", "Estadística I", "Aprobada", "EZ",""),
                new Materia(14, "C", "2008060", "Cálculo Numérico", "Aprobada", "Media",""),
                new Materia(15, "C", "2010012", "Métodos, Técnicas y Taller de Programación", "Aprobada", "Media",""),
                new Materia(16, "C", "2010015", "Base de Datos I", "Aprobada", "EZ",""),
                new Materia(17, "C", "2010141", "Circuitos Electrónicos", "Aprobada", "Media",""),
                new Materia(18, "D", "2008061", "Estadística II", "Aprobada", "Media",""),
                new Materia(19, "D", "2010016", "Base de Datos II", "Aprobada", "Alta",""),
                new Materia(20, "D", "2010017", "Taller de Sistemas Operativos", "Aprobada", "Alta",""),
                new Materia(21, "D", "2010018", "Sistemas de Información I", "Aprobada", "Media",""),
                new Materia(22, "D", "2016046", "Contabilidad Básica", "Aprobada", "Media",""),
                new Materia(23, "D", "2016048", "Investigación Operativa I", "Aprobada", "Media",""),
                new Materia(24, "E", "1803002", "Inglés II", "Aprobada", "EZ",""),
                new Materia(25, "E", "2010022", "Sistemas de Información II", "Aprobada", "Media",""),
                new Materia(26, "E", "2010035", "Aplicación de Sistemas Operativos", "Pendiente", "",""),
                new Materia(27, "E", "2010053", "Taller de Base de Datos", "Pendiente", "",""),
                new Materia(28, "E", "2010142", "Sistemas I", "Aprobada", "EZ",""),
                new Materia(29, "E", "2016051", "Investigación Operativa II", "Aprobada", "Alta",""),
                new Materia(30, "E", "2016057", "Mercadotecnia", "Aprobada", "EZ",""),
                new Materia(31, "F", "2010019", "Simulación de Sistemas", "Aprobada", "EZ",""),
                new Materia(32, "F", "2010020", "Ingeniería de Software", "Pendiente", "",""),
                new Materia(33, "F", "2010027", "Inteligencia Artificial", "Pendiente", "",""),
                new Materia(34, "F", "2010047", "Redes de Computadoras", "Pendiente", "",""),
                new Materia(35, "F", "2010143", "Sistemas II", "Aprobada", "EZ",""),
                new Materia(36, "F", "2010144", "Sistemas Económicos", "Pendiente", "",""),
                new Materia(37, "F", "2010182", "Telefonía IP", "Pendiente", "",""),
                new Materia(38, "G", "2010024", "Taller de Ingeniería de Software", "Pendiente", "",""),
                new Materia(39, "G", "2010145", "Gestión de Calidad de Software", "Pendiente", "",""),
                new Materia(40, "G", "2010146", "Redes Avanzadas de Computadoras", "Pendiente", "",""),
                new Materia(41, "G", "2010186", "Dinámica de Sistemas", "Aprobada", "Alta",""),
                new Materia(42, "G", "2016092", "Planificación y Evaluación de Proyectos", "Pendiente", "",""),
                new Materia(43, "G", "2010211", "Aplicaciones Interactivas para Televisión Digital", "Pendiente", "Electiva recomendada",""),
                new Materia(44, "H", "1803009", "Inglés III", "Aprobada", "EZ",""),
                new Materia(45, "H", "2010102", "Evaluación y Auditoría de Sistemas", "Pendiente", "",""),
                new Materia(46, "H", "2010116", "Taller de Simulación de Sistemas", "Pendiente", "",""),
                new Materia(47, "H", "2010119", "Metodología y Planificación de Proyecto de Grado", "Pendiente", "",""),
                new Materia(48, "H", "2010209", "Seguridad de Sistemas", "Pendiente", "",""),
                new Materia(49, "H", "2016059", "Gestión Estratégica de Empresas", "Pendiente", "",""),
                new Materia(50, "H", "2010034", "Sistemas Colaborativos", "Pendiente", "Electiva recomendada",""),
                new Materia(51, "I", "2010122", "Proyecto Final (Taller de Titulación)", "Pendiente", "",""),
                new Materia(52, "I", "2010147", "Práctica Empresarial", "Pendiente", "",""),
                new Materia(53, "I", "2010079", "Web Semánticas", "Pendiente", "Electiva recomendada",""),
                new Materia(54, "I", "2010103", "Robótica", "Pendiente", "Electiva recomendada",""),
                new Materia(55, "J", "2010033", "Generación de Software", "Pendiente", "Electiva recomendada",""),
                new Materia(56, "J", "2010044", "Diseño de Compiladores", "Pendiente", "Electiva recomendada",""),
                new Materia(57, "J", "2010191", "Técnicas de Ruteo Avanzada", "Pendiente", "Electiva recomendada","")
        );
    }
}