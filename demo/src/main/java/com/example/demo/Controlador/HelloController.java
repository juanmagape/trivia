package com.example.demo.Controlador;

import com.example.demo.Modelo.GestionDatos;
import com.example.demo.Modelo.TriviaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;

public class HelloController {

    @FXML private VBox pantallaInicio, contenedorJuego;
    @FXML private Label txtPregunta, txtPuntaje, lblError;
    @FXML private Button btnOp1, btnOp2, btnOp3, btnOp4, btnComenzar;

    private GestionDatos preguntaActual;
    private TriviaService servicio;
    private int puntos = 0;
    private int numPreguntasRespondidas = 0;
    private final int MAX_PREGUNTAS = 10;

    @FXML
    protected void onComenzarClick() {
        btnComenzar.setDisable(true);
        lblError.setVisible(false);
        btnComenzar.setText("Cargando...");

        Thread hilo = new Thread(() -> {
            try {
                servicio = new TriviaService();
                // Cargar la primera pregunta
                preguntaActual = servicio.obtenerPregunta();

                javafx.application.Platform.runLater(() -> {
                    pantallaInicio.setVisible(false);
                    pantallaInicio.setManaged(false);
                    contenedorJuego.setVisible(true);
                    contenedorJuego.setManaged(true);
                    mostrarPregunta();
                });
            } catch (Exception e) {
                mostrarError("Error: " + e.getMessage());
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void mostrarError(String mensaje) {
        javafx.application.Platform.runLater(() -> {
            lblError.setText(mensaje);
            lblError.setVisible(true);
            btnComenzar.setDisable(false);
            btnComenzar.setText("Comenzar Trivia");
        });
    }

    private void mostrarPregunta() {
        if (numPreguntasRespondidas < MAX_PREGUNTAS && preguntaActual != null) {
            txtPregunta.setText(limpiarTexto(preguntaActual.getEnunciado()));
            txtPuntaje.setText("Puntos: " + puntos + " (" + numPreguntasRespondidas + "/" + MAX_PREGUNTAS + ")");

            ArrayList<String> opciones = new ArrayList<>(preguntaActual.getRespuestasIncorrectas());
            opciones.add(preguntaActual.getRespuestaCorrecta());
            Collections.shuffle(opciones);

            btnOp1.setText(limpiarTexto(opciones.get(0)));
            btnOp2.setText(limpiarTexto(opciones.get(1)));
            btnOp3.setText(limpiarTexto(opciones.get(2)));
            btnOp4.setText(limpiarTexto(opciones.get(3)));

            btnOp1.setDisable(false);
            btnOp2.setDisable(false);
            btnOp3.setDisable(false);
            btnOp4.setDisable(false);
        } else {
            txtPregunta.setText("¡Has terminado! Puntuación final: " + puntos + "/ " + (MAX_PREGUNTAS));
            btnOp1.setDisable(true);
            btnOp2.setDisable(true);
            btnOp3.setDisable(true);
            btnOp4.setDisable(true);
        }
    }

    @FXML
    protected void onRespuestaClick(ActionEvent event) {
        if (preguntaActual == null) return;

        Button botonPulsado = (Button) event.getSource();
        String respuestaJugador = botonPulsado.getText();
        String respuestaCorrecta = limpiarTexto(preguntaActual.getRespuestaCorrecta());

        if (respuestaJugador.equals(respuestaCorrecta)) {
            puntos += 10;
            System.out.println("¡Correcto!");
        } else {
            System.out.println("Fallaste. Era: " + respuestaCorrecta);
        }

        numPreguntasRespondidas++;

        btnOp1.setDisable(true);
        btnOp2.setDisable(true);
        btnOp3.setDisable(true);
        btnOp4.setDisable(true);

        Thread hilo = new Thread(() -> {
            try {
                if (numPreguntasRespondidas < MAX_PREGUNTAS) {
                    preguntaActual = servicio.obtenerPregunta();
                    javafx.application.Platform.runLater(this::mostrarPregunta);
                } else {
                    javafx.application.Platform.runLater(this::mostrarPregunta);
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> mostrarError("Error al cargar pregunta: " + e.getMessage()));
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private String limpiarTexto(String texto) {
        if (texto == null) return "";
        return texto.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&rsquo;", "'")
                .replace("&deg;", "°");
    }
}