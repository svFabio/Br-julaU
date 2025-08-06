package com.windowsxp.fsv.fcyt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class LoginController implements Initializable {

    // Variables para todos los componentes con fx:id del FXML
    @FXML private VBox rootVBox;
    @FXML private ImageView logoImageView;
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;
    @FXML private CheckBox recordarmeCheck;
    @FXML private Hyperlink olvideContrasenaLink;
    @FXML private Button loginButton;
    @FXML private Button registerButton; // <- Variable para el botón de registro

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lógica para hacer el logo responsivo
        if (logoImageView != null && rootVBox != null) {
            logoImageView.fitWidthProperty().bind(rootVBox.widthProperty().multiply(0.40));
        }
    }

    /**
     * Este método se llama cuando se presiona el botón INICIAR SESIÓN.
     * El nombre "onLoginButtonClick" debe coincidir exactamente con el onAction del FXML.
     */
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String usuario = usuarioField.getText();
        String contrasena = contrasenaField.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Por favor, ingresa tu nombre de usuario y contraseña.");
            return;
        }

        int idUsuario = DatabaseManager.validarUsuarioYObtenerId(usuario, contrasena);

        if (idUsuario != -1) {
            System.out.println("Login exitoso para el usuario con ID: " + idUsuario);

            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Ahora llamamos al método pasándole el ID que obtuvimos de la base de datos.
            abrirVentanaPrincipal(idUsuario);

        } else {
            mostrarAlerta("Error de Autenticación", "Usuario o contraseña incorrectos.");
        }
    }

    /**
     * Este método se llama cuando se presiona el botón REGISTRARSE.
     * El nombre "onRegisterButtonClick" debe coincidir exactamente con el onAction del FXML.
     */
    @FXML
    protected void onRegisterButtonClick(ActionEvent event) {
        String usuario = usuarioField.getText();
        String contrasena = contrasenaField.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Por favor, ingresa un nombre de usuario y una contraseña para registrar.");
            return;
        }

        boolean exito = DatabaseManager.registrarUsuario(usuario, contrasena);

        if (exito) {
            mostrarAlerta("Registro Exitoso", "El usuario '" + usuario + "' ha sido registrado. Ahora puedes iniciar sesión.");
        } else {
            mostrarAlerta("Error de Registro", "El nombre de usuario '" + usuario + "' ya está en uso. Por favor, elige otro.");
        }
    }

    // --- Métodos de Ayuda ---

    private void abrirVentanaPrincipal(int idUsuario) {
        try {
            Stage stageActual = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/windowsxp/fsv/fcyt/main-view.fxml"));
            Parent root = loader.load();

            PlanController planController = loader.getController();
            planController.initData(idUsuario);

            stageActual.setOnCloseRequest(event -> planController.guardarProgreso());

            Scene scene = new Scene(root);

            // --- CÓDIGO CORREGIDO Y MÁS SEGURO ---
            // 1. Buscamos el archivo CSS de una forma más simple y robusta.
            //    (Busca "styles.css" en la misma carpeta que esta clase)
            URL cssUrl = getClass().getResource("styles.css");

            // 2. Verificamos que el archivo se haya encontrado ANTES de intentar usarlo.
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Si no lo encuentra, solo muestra una advertencia en lugar de crashear.
                System.err.println("¡ADVERTENCIA! No se encontró el archivo de estilos: styles.css");
            }

            stageActual.setScene(scene);
            stageActual.setTitle("Plan de Estudios - Usuario ID: " + idUsuario);
            stageActual.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "No se pudo cargar la ventana principal.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert.AlertType tipo = titulo.toLowerCase().contains("error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}