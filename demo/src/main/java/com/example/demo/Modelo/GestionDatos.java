package com.example.demo.Modelo;

import java.util.ArrayList;

public class GestionDatos {
    public String type;
    public String difficulty;
    public String category;
    public String question;
    public String correct_answer;
    public ArrayList<String> incorrect_answers;

    public String getTipo() { return type; }
    public String getDificultad() { return difficulty; }
    public String getCategoria() { return category; }
    public String getEnunciado() { return question; }
    public String getRespuestaCorrecta() { return correct_answer; }
    public ArrayList<String> getRespuestasIncorrectas() { return incorrect_answers; }

}
