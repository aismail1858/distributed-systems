package common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple JSON parser using only native Java APIs.
 */
public class JsonParser {
    
    /**
     * Converts a Message object to JSON string.
     */
    public static String toJson(Message message) {
        if (message == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        boolean first = true;
        
        // Add basic message fields
        if (message.getType() != null) {
            if (!first) json.append(",");
            json.append("\"type\":\"").append(escapeJson(message.getType())).append("\"");
            first = false;
        }
        
        if (message.getMessageId() != null) {
            if (!first) json.append(",");
            json.append("\"messageId\":\"").append(escapeJson(message.getMessageId())).append("\"");
            first = false;
        }
        
        if (message.getCorrelationId() != null) {
            if (!first) json.append(",");
            json.append("\"correlationId\":\"").append(escapeJson(message.getCorrelationId())).append("\"");
            first = false;
        }
        
        if (message.getSenderId() != null) {
            if (!first) json.append(",");
            json.append("\"senderId\":\"").append(escapeJson(message.getSenderId())).append("\"");
            first = false;
        }
        
        if (message.getTimestamp() != 0) {
            if (!first) json.append(",");
            json.append("\"timestamp\":").append(message.getTimestamp());
            first = false;
        }
        
        // Add data fields
        if (message.getData() != null) {
            for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
                first = false;
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Parses a JSON string to Message object.
     */
    public static Message fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
        
        // Remove braces
        json = json.substring(1, json.length() - 1);
        
        // Parse key-value pairs
        Map<String, String> values = parseKeyValuePairs(json);
        
        // Create Message object
        Message message = new Message();
        Map<String, String> data = new HashMap<>();
        
        // Set basic message fields
        if (values.containsKey("type")) {
            message.setType(values.get("type"));
        }
        
        if (values.containsKey("messageId")) {
            message.setMessageId(values.get("messageId"));
        }
        
        if (values.containsKey("correlationId")) {
            message.setCorrelationId(values.get("correlationId"));
        }
        
        if (values.containsKey("senderId")) {
            message.setSenderId(values.get("senderId"));
        }
        
        if (values.containsKey("timestamp")) {
            try {
                message.setTimestamp(Long.parseLong(values.get("timestamp")));
            } catch (NumberFormatException e) {
                message.setTimestamp(System.currentTimeMillis());
            }
        }
        
        // Put all other fields into data map
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("type") && !key.equals("messageId") && !key.equals("correlationId") && 
                !key.equals("timestamp") && !key.equals("senderId")) {
                data.put(key, entry.getValue());
            }
        }
        
        message.setData(data);
        return message;
    }
    
    /**
     * Parses key-value pairs from JSON content.
     */
    private static Map<String, String> parseKeyValuePairs(String json) {
        Map<String, String> values = new HashMap<>();
        
        // Pattern to match "key":"value" or "key":value
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([^,}]+))");
        Matcher matcher = pattern.matcher(json);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            if (value != null) {
                values.put(key, value.trim());
            }
        }
        
        return values;
    }
    
    /**
     * Escapes special characters for JSON.
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Converts an Order object to JSON string.
     */
    public static String orderToJson(Object order) {
        // For order serialization - simplified implementation
        return "{\"order\":\"" + order.toString() + "\"}";
    }
    
    /**
     * Parses a simple JSON object to Map.
     */
    public static Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return result;
        }
        
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }
        
        // Remove braces
        json = json.substring(1, json.length() - 1);
        
        // Simple parsing for basic JSON structures
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([^,}]+))");
        Matcher matcher = pattern.matcher(json);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            
            if (value != null) {
                value = value.trim();
                // Try to parse as number or boolean
                if (value.equals("true") || value.equals("false")) {
                    result.put(key, Boolean.parseBoolean(value));
                } else {
                    try {
                        result.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        }
        
        return result;
    }
}