package com.example.demo.Modelo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class TriviaService {

    public GestionDatos obtenerPregunta() throws Exception {
        try {
            String respuesta = new Scanner(new URL("https://opentdb.com/api.php?amount=1&type=multiple").openStream(), "UTF-8").useDelimiter("\\A").next();

            JSONObject jsonResponse = new JSONObject(respuesta);
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() == 0) {
                throw new Exception("JSON vacío");
            }
            JSONObject preguntaJson = results.getJSONObject(0);
            GestionDatos pregunta = new GestionDatos();

            pregunta.type = preguntaJson.getString("type");
            pregunta.difficulty = preguntaJson.getString("difficulty");
            pregunta.category = preguntaJson.getString("category");
            pregunta.question = preguntaJson.getString("question");
            pregunta.correct_answer = preguntaJson.getString("correct_answer");

            pregunta.incorrect_answers = new ArrayList<>();
            JSONArray incorrectAnswers = preguntaJson.getJSONArray("incorrect_answers");
            for (int i = 0; i < incorrectAnswers.length(); i++) {
                pregunta.incorrect_answers.add(incorrectAnswers.getString(i));
            }

            return pregunta;

        } catch (Exception e) {
            System.err.println("Fallo de red: " + e.getMessage());
            throw new Exception("Error de red. Reintentando...");
        }
    }
}