package com.example.mediai.data.remote;

import android.os.Build;

import com.example.mediai.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AiApiClient {

    public static String callApi(String userMessage, String apiKey, AiProvider provider) {
        if (provider == AiProvider.DEMO || apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        try {
            String baseUrl = provider == AiProvider.MISTRAL ?
                    Constants.MISTRAL_BASE_URL : Constants.DEEPSEEK_BASE_URL;
            String chatPath = provider == AiProvider.MISTRAL ?
                    Constants.MISTRAL_CHAT_PATH : Constants.DEEPSEEK_CHAT_PATH;
            String model = provider == AiProvider.MISTRAL ?
                    Constants.MISTRAL_MODEL : Constants.DEEPSEEK_MODEL;

            URL url = new URL(baseUrl + chatPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            JSONArray messages = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", Constants.SYSTEM_PROMPT);
            messages.put(systemMessage);

            JSONObject userMessageObj = new JSONObject();
            userMessageObj.put("role", "user");
            userMessageObj.put("content", userMessage);
            messages.put(userMessageObj);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 500);
            requestBody.put("stream", false);

            OutputStream os = connection.getOutputStream();
            os.write(requestBody.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                // Read error stream
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                return "Error del servidor: " + responseCode;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content");
            }

            return "No se pudo obtener respuesta.";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}