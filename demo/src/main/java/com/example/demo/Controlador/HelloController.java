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
    @FXML private Label txtPregunta, txtPuntaje, lblError, txtProgreso;
    @FXML private Label lblCategoria, lblDificultad, lblPuntosValor;
    @FXML private Button btnOp1, btnOp2, btnOp3, btnOp4, btnComenzar, btnReiniciar;
    @FXML private ProgressBar barraProgreso;

    private GestionDatos preguntaActual;
    private TriviaService servicio = new TriviaService();
    private int puntos = 0, numPreguntas = 0;
    private String respuestaCorrectaActual = "";

    @FXML
    public void initialize() {
    }

    @FXML
    protected void onComenzarClick() {
        btnComenzar.setDisable(true);
        btnComenzar.setText("Cargando...");
        lblError.setVisible(false);
        lblError.setManaged(false);

        new Thread(() -> {
            try {
                preguntaActual = servicio.obtenerPregunta();
                Platform.runLater(() -> {
                    pantallaInicio.setVisible(false);
                    pantallaInicio.setManaged(false);
                    contenedorJuego.setVisible(true);
                    contenedorJuego.setManaged(true);
                    mostrarPregunta();
                });
            } catch (Exception e) {
                mostrarError(e.getMessage());
            }
        }).start();
    }

    @FXML
    protected void onReiniciarClick() {
        puntos = 0;
        numPreguntas = 0;

        btnReiniciar.setVisible(false);
        btnReiniciar.setManaged(false);

        contenedorJuego.setVisible(false);
        contenedorJuego.setManaged(false);
        pantallaInicio.setVisible(true);
        pantallaInicio.setManaged(true);

        btnComenzar.setDisable(false);
        btnComenzar.setText("Comenzar Juego");
    }

    private void mostrarError(String msg) {
        Platform.runLater(() -> {
            lblError.setText(msg);
            lblError.setVisible(true);
            lblError.setManaged(true);
            btnComenzar.setDisable(false);
            btnComenzar.setText("Comenzar Juego");
        });
    }

    private void cambiarBotones(boolean estado) {
        btnOp1.setDisable(estado);
        btnOp2.setDisable(estado);
        btnOp3.setDisable(estado);
        btnOp4.setDisable(estado);
    }

    private void resetearEstiloBotones() {
        String estiloNormal = "btn-answer";
        btnOp1.getStyleClass().removeAll("btn-correct", "btn-wrong");
        btnOp2.getStyleClass().removeAll("btn-correct", "btn-wrong");
        btnOp3.getStyleClass().removeAll("btn-correct", "btn-wrong");
        btnOp4.getStyleClass().removeAll("btn-correct", "btn-wrong");

        if (!btnOp1.getStyleClass().contains(estiloNormal)) btnOp1.getStyleClass().add(estiloNormal);
        if (!btnOp2.getStyleClass().contains(estiloNormal)) btnOp2.getStyleClass().add(estiloNormal);
        if (!btnOp3.getStyleClass().contains(estiloNormal)) btnOp3.getStyleClass().add(estiloNormal);
        if (!btnOp4.getStyleClass().contains(estiloNormal)) btnOp4.getStyleClass().add(estiloNormal);
    }

    private void mostrarPregunta() {
        if (numPreguntas < 10 && preguntaActual != null) {
            resetearEstiloBotones();

            txtPregunta.setText(limpiarTexto(preguntaActual.getEnunciado()));

            String categoria = limpiarTexto(preguntaActual.getCategoria());
            if (categoria.contains(":")) {
                categoria = categoria.substring(categoria.indexOf(":") + 1).trim();
            }
            lblCategoria.setText(categoria);

            String dificultad = preguntaActual.getDificultad();
            lblDificultad.getStyleClass().removeAll("badge-easy", "badge-medium", "badge-hard");

            switch (dificultad.toLowerCase()) {
                case "easy":
                    lblDificultad.setText("Facil");
                    lblDificultad.getStyleClass().add("badge-easy");
                    break;
                case "medium":
                    lblDificultad.setText("Media");
                    lblDificultad.getStyleClass().add("badge-medium");
                    break;
                case "hard":
                    lblDificultad.setText("Dificil");
                    lblDificultad.getStyleClass().add("badge-hard");
                    break;
                default:
                    lblDificultad.setText(dificultad);
                    lblDificultad.getStyleClass().add("badge-easy");
            }

            lblPuntosValor.setText(String.valueOf(puntos));

            txtProgreso.setText("Pregunta " + (numPreguntas + 1) + " de 10");
            barraProgreso.setProgress((numPreguntas + 1) / 10.0);

            List<String> opc = new ArrayList<>(preguntaActual.getRespuestasIncorrectas());
            opc.add(preguntaActual.getRespuestaCorrecta());
            Collections.shuffle(opc);

            respuestaCorrectaActual = limpiarTexto(preguntaActual.getRespuestaCorrecta());

            btnOp1.setText("A.  " + limpiarTexto(opc.get(0)));
            btnOp2.setText("B.  " + limpiarTexto(opc.get(1)));
            btnOp3.setText("C.  " + limpiarTexto(opc.get(2)));
            btnOp4.setText("D.  " + limpiarTexto(opc.get(3)));

            cambiarBotones(false);

        } else {
            mostrarResultadoFinal();
        }
    }

    private void mostrarResultadoFinal() {
        txtPregunta.setText("Juego Terminado!");
        txtPregunta.setStyle("-fx-font-size: 28px;");

        lblCategoria.setText("Resultado Final");
        lblDificultad.setVisible(false);
        lblDificultad.setManaged(false);

        lblPuntosValor.setText(puntos + "/100");

        txtProgreso.setText(getResultadoMensaje());
        barraProgreso.setProgress(puntos / 100.0);

        btnOp1.setVisible(false); btnOp1.setManaged(false);
        btnOp2.setVisible(false); btnOp2.setManaged(false);
        btnOp3.setVisible(false); btnOp3.setManaged(false);
        btnOp4.setVisible(false); btnOp4.setManaged(false);

        btnReiniciar.setVisible(true);
        btnReiniciar.setManaged(true);
    }

    private String getResultadoMensaje() {
        if (puntos >= 80) return "Excelente! Eres un experto!";
        if (puntos >= 60) return "Muy bien! Buen conocimiento!";
        if (puntos >= 40) return "No esta mal! Sigue practicando!";
        return "Necesitas estudiar mas!";
    }

    @FXML
    protected void onRespuestaClick(ActionEvent e) {
        if (preguntaActual == null) return;

        Button botonSeleccionado = (Button) e.getSource();
        String respuestaSeleccionada = botonSeleccionado.getText().substring(4); // Quitar "A.  ", "B.  ", etc.

        boolean esCorrecta = respuestaSeleccionada.equals(respuestaCorrectaActual);

        if (esCorrecta) {
            puntos += 10;
            botonSeleccionado.getStyleClass().add("btn-correct");
        } else {
            botonSeleccionado.getStyleClass().add("btn-wrong");
            mostrarRespuestaCorrecta();
        }

        lblPuntosValor.setText(String.valueOf(puntos));

        numPreguntas++;
        cambiarBotones(true);

        new Thread(() -> {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException ignored) {}

            for (int i = 0; i < 3; i++) {
                try {
                    if (numPreguntas < 10) {
                        preguntaActual = servicio.obtenerPregunta();
                    }
                    Platform.runLater(() -> {
                        btnOp1.setVisible(true); btnOp1.setManaged(true);
                        btnOp2.setVisible(true); btnOp2.setManaged(true);
                        btnOp3.setVisible(true); btnOp3.setManaged(true);
                        btnOp4.setVisible(true); btnOp4.setManaged(true);
                        lblDificultad.setVisible(true); lblDificultad.setManaged(true);
                        txtPregunta.setStyle("");
                        mostrarPregunta();
                    });
                    return;
                } catch (Exception ex) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {}
                    if (i == 2) {
                        Platform.runLater(() -> {
                            txtPregunta.setText("Error al cargar. Intenta de nuevo.");
                        });
                    }
                }
            }
        }).start();
    }

    private void mostrarRespuestaCorrecta() {
        Button[] botones = {btnOp1, btnOp2, btnOp3, btnOp4};
        for (Button btn : botones) {
            String textoBoton = btn.getText().substring(4);
            if (textoBoton.equals(respuestaCorrectaActual)) {
                btn.getStyleClass().add("btn-correct");
                break;
            }
        }
    }

    private String limpiarTexto(String t) {
        return t == null ? "" : t
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&rsquo;", "'")
                .replace("&deg;", "°")
                .replace("&ldquo;", "\"")
                .replace("&rdquo;", "\"")
                .replace("&eacute;", "e")
                .replace("&ntilde;", "n");
    }
}
