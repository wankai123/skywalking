package org.apache.skywalking.oap.server.mask.masker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.skywalking.oap.server.mask.strategy.MaskStrategy;

public class JsonMasker extends InfoMasker {
    private final Map<String[], MaskStrategy> maskRules = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonMasker() {
    }

    public void addMaskRule(String fieldPath, MaskStrategy maskStrategy) {
        if (fieldPath == null || fieldPath.trim().isEmpty()) {
            return;
        }

        String path = fieldPath.trim();

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with '/'");
        }

        path = path.substring(1);
        String[] pathArray = path.split("/");

        if (pathArray.length == 0) {
            throw new IllegalArgumentException("Field path is invalid." + fieldPath);
        }

        maskRules.put(pathArray, maskStrategy);
    }

    public String maskJson(String json) throws Exception {
        if (maskRules.isEmpty()) {
            return json;
        }
        JsonNode rootNode = objectMapper.readTree(json);

        for (Map.Entry<String[], MaskStrategy> entry : maskRules.entrySet()) {
            maskField(rootNode, entry.getKey(), entry.getValue(), 0);
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private void maskField(JsonNode node, String[] fieldPath, MaskStrategy strategy, int depth) {
        if (node == null || depth >= fieldPath.length) {
            return;
        }

        String currentField = fieldPath[depth];

        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            JsonNode childNode = objectNode.get(currentField);

            if (childNode != null) {
                if (depth == fieldPath.length - 1) {
                    // reach the target field, apply masking
                    String originalValue = childNode.isTextual() ?
                        childNode.asText() : childNode.toString();
                    String maskedValue = strategy.mask(originalValue);
                    objectNode.put(currentField, maskedValue);
                } else {
                    // continue traversing
                    maskField(childNode, fieldPath, strategy, depth + 1);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                maskField(arrayElement, fieldPath, strategy, depth);
            }
        }
    }
}