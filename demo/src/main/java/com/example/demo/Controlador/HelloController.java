package com.example.demo.Controlador;

import com.example.demo.Modelo.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.*;

public class HelloController {

    @FXML private VBox pantallaInicio, contenedorJuego;
    @FXML private Label txtPregunta, txtPuntaje, lblError;
    @FXML private Button btnOp1, btnOp2, btnOp3, btnOp4, btnComenzar;

    private GestionDatos preguntaActual;
    private TriviaService servicio = new TriviaService();
    private int puntos = 0, numPreguntas = 0;

    @FXML protected void onComenzarClick() {
        btnComenzar.setDisable(true); btnComenzar.setText("Cargando...");
        new Thread(() -> {
            try {
                preguntaActual = servicio.obtenerPregunta();
                Platform.runLater(() -> {
                    pantallaInicio.setVisible(false); pantallaInicio.setManaged(false);
                    contenedorJuego.setVisible(true); contenedorJuego.setManaged(true);
                    mostrarPregunta();
                });
            } catch (Exception e) { mostrarError(e.getMessage()); }
        }).start();
    }

    private void mostrarError(String msg) {
        Platform.runLater(() -> { lblError.setText(msg); lblError.setVisible(true); btnComenzar.setDisable(false); });
    }

    private void cambiarBotones(boolean estado) {
        btnOp1.setDisable(estado); btnOp2.setDisable(estado);
        btnOp3.setDisable(estado); btnOp4.setDisable(estado);
    }

    private void mostrarPregunta() {
        if (numPreguntas < 10 && preguntaActual != null) {
            txtPregunta.setText(limpiarTexto(preguntaActual.getEnunciado()));
            txtPuntaje.setText("Puntos: " + puntos + " (" + numPreguntas + "/10)");

            List<String> opc = new ArrayList<>(preguntaActual.getRespuestasIncorrectas());
            opc.add(preguntaActual.getRespuestaCorrecta());
            Collections.shuffle(opc);

            btnOp1.setText(limpiarTexto(opc.get(0))); btnOp2.setText(limpiarTexto(opc.get(1)));
            btnOp3.setText(limpiarTexto(opc.get(2))); btnOp4.setText(limpiarTexto(opc.get(3)));
            cambiarBotones(false);
        } else {
            txtPregunta.setText("¡Terminado! Puntuación final: " + puntos + "/100");
            cambiarBotones(true);
        }
    }

    @FXML protected void onRespuestaClick(ActionEvent e) {
        if (preguntaActual == null) return;
        if (((Button) e.getSource()).getText().equals(limpiarTexto(preguntaActual.getRespuestaCorrecta()))) puntos += 10;

        numPreguntas++;
        cambiarBotones(true);
        txtPregunta.setText("Cargando siguiente pregunta...");

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    if (numPreguntas < 10) preguntaActual = servicio.obtenerPregunta();
                    Platform.runLater(this::mostrarPregunta);
                    return;
                } catch (Exception ex) {
                    try { Thread.sleep(1000); } catch (Exception ignored) {}
                    if (i == 2) Platform.runLater(() -> mostrarError("Error tras 3 intentos."));
                }
            }
        }).start();
    }

    private String limpiarTexto(String t) {
        return t == null ? "" : t.replace("&quot;", "\"").replace("&#039;", "'").replace("&amp;", "&").replace("&rsquo;", "'").replace("&deg;", "°");
    }
}