package com.example.ReceiptServer.service.LLM;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatGptLLMService implements LLMService {

    private final OpenAIClient client;
    private final String defaultModel;
    private static final String resultKey = "content";

    public ChatGptLLMService(
//            @Value("${openai.api.key:#{environment.OPENAI_API_KEY}}")
            @Value("${OPENAI_API_KEY}")
            String apiKey, @Value("${OPENAI_DEFAULT_MODEL}") String model
            ) {

        // If properties are provided, use them; otherwise rely on environment variables
        if (apiKey != null && !apiKey.isEmpty()) {
            this.client = OpenAIOkHttpClient.builder()
                    .apiKey(apiKey)
                    .build();
        } else {
            this.client = OpenAIOkHttpClient.fromEnv();
        }

        this.defaultModel = model;
    }

    public String GetResultKey()
    {
        return resultKey;
    }

    @Override
    public String GetStringResponseFromQuery(String query, Map<String, Object> param) {
        ChatCompletion completion = executeQuery(query, param);
        return completion != null && !completion.choices().isEmpty()
                ? String.valueOf(completion.choices().get(0).message().content()) : "";
    }

    @Override
    public Map<String, Object> GetMapResponseFromQuery(String query, Map<String, Object> param) {
        ChatCompletion completion = executeQuery(query, param);
        if (!completion.choices().get(0).message().content().isPresent()) {
            return null;
        }


        Map<String, Object> response = new HashMap<>();
        response.put("content", completion.choices().get(0).message().content().get());
        response.put("model", completion.model());
        response.put("id", completion.id());
        response.put("created", completion.created());

        return response;
    }

    private ChatCompletion executeQuery(String query, Map<String, Object> param) {
        String model = defaultModel;
        double temperature = 0.7;

        if (param != null) {
            if (param.containsKey("temperature")) {
                temperature = Double.parseDouble(param.get("temperature").toString());
            }
        }

        try {
            ChatCompletionCreateParams.Builder paramsBuilder = ChatCompletionCreateParams.builder()
                    .addUserMessage(query)
                    .model(model)
                    .temperature(temperature);

            return client.chat().completions().create(paramsBuilder.build());
        } catch (Exception e) {
            // Add proper logging here
            throw new RuntimeException(e);
        }
    }
}