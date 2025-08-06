module com.windowsxp.fsv.fcyt {
    requires javafx.controls;
    requires javafx.fxml;

    //oara el Json
    requires com.google.gson;


    opens com.windowsxp.fsv.fcyt to javafx.fxml, com.google.gson;
    exports com.windowsxp.fsv.fcyt;

}