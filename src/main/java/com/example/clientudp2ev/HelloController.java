package com.example.clientudp2ev;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class HelloController {
    protected static final int SERVER_PORT = 5010;
    protected static final int CLIENT_PORT = 6010;
    protected static final DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket(CLIENT_PORT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final String SERVER_ADDRESS = "localhost";
    @FXML
    private TextField nickname;
    protected static String userNickname;
    @FXML
    private Label labelError;
    private static boolean check = false;

    @FXML
    public void onLogin(ActionEvent actionEvent) throws IOException {
        userNickname = nickname.getText();
        checkNickname(userNickname);

        if(check) {
            changeWindow(actionEvent);
        } else {
            labelError.setText("Name is already being used, please choose another one.");
        }
    }

    private static void checkNickname(String username) throws IOException {
        String sendConnection = "FIRST_CONNECTION:" + username;
        DatagramPacket sendPacket = new DatagramPacket(
                sendConnection.getBytes(),
                sendConnection.length(),
                InetAddress.getByName(SERVER_ADDRESS),
                SERVER_PORT
        );
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] receiveData = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(responsePacket);

        String messageCheckNick = new String(responsePacket.getData(), 0, responsePacket.getLength());

        if (messageCheckNick.equals("NICKNAME_TRUE")) {
            check = true;
        }
    }

    @FXML
    private void changeWindow(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
                Parent root = fxmlLoader.load();

                Client clientController = fxmlLoader.getController();

                clientController.initializeSocket();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Cierra la ventana actual
                Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}