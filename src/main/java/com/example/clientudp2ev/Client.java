package com.example.clientudp2ev;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Base64;

public class Client {

    private final int SERVER_PORT = HelloController.SERVER_PORT;
    private static final DatagramSocket socket = HelloController.socket;

    private final String SERVER_ADDRESS = HelloController.SERVER_ADDRESS;
    @FXML
    private TextArea chatArea;
    @FXML
    public TextField messageField;
    @FXML
    public Button messageButton;
    @FXML
    public Button imageButton;

    public void initializeSocket() {
        new Thread(() -> {
            try {
                while (true) {

                    byte[] receiveData = new byte[65507];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    Platform.runLater(() -> chatArea.appendText(message + "\n"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void sendMessage() {
        try {
            String messageHead = "MESSAGE:";
            String messageText = messageField.getText();
            if (!messageText.isEmpty()) {
                String message = messageHead + messageText;
                // Enviar el mensaje
                DatagramPacket sendMessage = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        InetAddress.getByName(SERVER_ADDRESS),
                        SERVER_PORT
                );
                socket.send(sendMessage);
                chatArea.appendText(HelloController.userNickname + ": " + messageText + "\n");
                messageField.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void chooseAndSendImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image:");
        File fileToSend = fileChooser.showOpenDialog(null);

        if (fileToSend != null) {
            sendImage(fileToSend);
        }
    }

    public void sendImage(File fileToSend) {
        try {
            if (fileToSend != null) {
                byte[] imageBytes = Files.readAllBytes(fileToSend.toPath());
                String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                // Enviar el marcador IMAGE_FLAG y la imagen cifrada
                String message = "IMAGE:" + encodedImage;
                DatagramPacket sendPacket = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        InetAddress.getByName(SERVER_ADDRESS),
                        SERVER_PORT
                );
                socket.send(sendPacket);
            } else {
                System.out.println("You haven't chosen a file to send.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}