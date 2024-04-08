package org.qualitified.composable.prompts.operation;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.runtime.api.Framework;

import java.util.HashMap;
import java.util.Map;

@Operation(id = ComposablePromptsOp.ID, category = "ComposablePrompts", label = "Execute Composable Prompts Interaction",
        description = "Call the Composable Prompts service to run an interaction")

public class ComposablePromptsOp {
    public final static String ID = "Qualitified.ComposablePrompts";

    @Context
    private CoreSession session;

    @Param(name = "interactionId", required = true)
    private String interactionId;

    @Param(name = "data", required = true)
    private Map<String, Object> data;

    @Param(name = "model", required = true)
    private String model;

    private final Log logger = LogFactory.getLog(ComposablePromptsOp.class);

    private static final String BASE_URL = "https://api.composableprompts.com/api/v1/runs";

    private static final String API_TOKEN = "composable.prompts.api.token";

    private static final String PROJECT_ID = "composable.prompts.project.id";

    @OperationMethod()
    public StringBlob run() throws JsonProcessingException {

       // Prepare headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("interactionId", interactionId);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", String.format("Bearer %s", Framework.getProperty(API_TOKEN)));
        headers.put("X-Project-Id", Framework.getProperty(PROJECT_ID));

        // Construct JSON body
        Map<String, Object> config = new HashMap<>();
        config.put("model", model);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("interactionId", interactionId);
        requestBody.put("data", data);
        requestBody.put("config", config);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestBody);

        // Call external API
        String promptResponse = callExternalAPI(BASE_URL, headers, json);
        return new StringBlob(promptResponse,"application/json");
    }

    private String callExternalAPI(String url, Map<String, String> headers, String requestBody) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Create HTTP request
            HttpPost httpPost = new HttpPost(url);
            // Set headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
            // Set body
            if (requestBody != null && !requestBody.isEmpty()) {
                StringEntity stringEntity = new StringEntity(requestBody);
                httpPost.setEntity(stringEntity);
            }

            // Execute the request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            // Read response
            if (entity != null) {
                String jsonResponse = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return jsonResponse;
            } else {
                return "response is null"; // Empty string if response is null
            }
        } catch (Exception e) {
            // Handle exceptions
            logger.error("could not execute operation: "+ e.getMessage());
            throw new NuxeoException(e);
        }
    }

}
