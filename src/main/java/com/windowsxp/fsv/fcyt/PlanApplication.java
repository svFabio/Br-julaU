package com.windowsxp.fsv.fcyt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlanApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        // Carga la vista de login como la primera y única ventana al iniciar.
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/windowsxp/fsv/fcyt/login-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Iniciar Sesión - Plan de Estudios");
        stage.setScene(scene);

        // Establecemos el tamaño mínimo para la ventana para que el diseño no se rompa.
        stage.setMinWidth(420.0);
        stage.setMinHeight(550.0);

        stage.show();
    }

    public static void main(String[] args) {
        // Le decimos al DatabaseManager que se conecte y prepare todo.
        // Si el archivo .db no existe, lo creará aquí.
        DatabaseManager.conectar();

        // Después de que la base de datos está lista, lanzamos la interfaz gráfica.
        launch(args);
    }
}