package com.windowsxp.fsv.fcyt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Variables para controlar los componentes del FXML
    @FXML private VBox rootVBox;
    @FXML private ImageView logoImageView;
    @FXML private TextField usuarioField;
    @FXML private PasswordField contrasenaField;
    @FXML private CheckBox recordarmeCheck;
    @FXML private Hyperlink olvideContrasenaLink;
    @FXML private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Atamos el ancho de la imagen para que siempre sea el 40% del ancho del VBox.
        logoImageView.fitWidthProperty().bind(rootVBox.widthProperty().multiply(0.40));
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        System.out.println("Botón de login presionado!");
        String usuario = usuarioField.getText();
        String contrasena = contrasenaField.getText();

        // AQUÍ IRÁ LA LÓGICA DEL FUTURO:
        // 1. Validar que el usuario y contraseña no estén vacíos.
        // 2. Llamar a un DatabaseManager para verificar las credenciales.
        // 3. Si son correctas, cerrar esta ventana y abrir la ventana principal.
        // 4. Si son incorrectas, mostrar un mensaje de error.
        System.out.println("Usuario: " + usuario + ", Contraseña: " + contrasena);
    }
}