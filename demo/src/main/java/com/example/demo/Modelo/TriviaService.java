package com.example.demo.Modelo;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class TriviaService {

    public GestionDatos obtenerPregunta() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://opentdb.com/api.php?amount=1&type=multiple"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error de conexión a la API. Código: " + response.statusCode());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray results = jsonResponse.getJSONArray("results");

        if (results.length() == 0) {
            throw new Exception("No se han cargado las preguntas. Intentalo de nuevo.");
        }

        JSONObject preguntaJson = results.getJSONObject(0);

        GestionDatos pregunta = new GestionDatos();
        pregunta.type = preguntaJson.getString("type");
        pregunta.difficulty = preguntaJson.getString("difficulty");
        pregunta.category = preguntaJson.getString("category");
        pregunta.question = preguntaJson.getString("question");
        pregunta.correct_answer = preguntaJson.getString("correct_answer");

        JSONArray incorrectAnswers = preguntaJson.getJSONArray("incorrect_answers");
        pregunta.incorrect_answers = new ArrayList<>();
        for (int i = 0; i < incorrectAnswers.length(); i++) {
            pregunta.incorrect_answers.add(incorrectAnswers.getString(i));
        }

        return pregunta;
    }
}