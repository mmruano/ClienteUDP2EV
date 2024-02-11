module com.example.clientupd2ev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.clientudp2ev to javafx.fxml;
    exports com.example.clientudp2ev;
}