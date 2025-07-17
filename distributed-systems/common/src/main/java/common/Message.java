package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Message types enum
    public enum Type {
        HEARTBEAT,
        RESERVE,
        CONFIRM,
        CANCEL,
        REQUEST,
        RESPONSE
    }
    
    private String messageId;
    private String correlationId;
    private String type;
    private Map<String, String> data;
    private long timestamp;
    private String senderId;
    
    public Message() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(String type, Map<String, String> data) {
        this();
        this.type = type;
        this.data = data;
    }
    
    public Message(String type, Map<String, String> data, String correlationId) {
        this(type, data);
        this.correlationId = correlationId;
    }
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public void setType(Type type) { this.type = type.name(); }
    
    // Convenience method to get type as enum
    public Type getTypeEnum() { 
        if (type == null) return null;
        try {
            return Type.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    // Convenience methods for accessing data fields
    public String getOrderId() { return data != null ? data.get("orderId") : null; }
    public void setOrderId(String orderId) { 
        if (data == null) data = new HashMap<>();
        data.put("orderId", orderId);
    }
    
    public String getProductId() { return data != null ? data.get("productId") : null; }
    public void setProductId(String productId) { 
        if (data == null) data = new HashMap<>();
        data.put("productId", productId);
    }
    
    public int getQuantity() { 
        if (data == null) return 0;
        String qty = data.get("quantity");
        return qty != null ? Integer.parseInt(qty) : 0;
    }
    public void setQuantity(int quantity) { 
        if (data == null) data = new HashMap<>();
        data.put("quantity", String.valueOf(quantity));
    }
    
    public String getReservationId() { return data != null ? data.get("reservationId") : null; }
    public void setReservationId(String reservationId) { 
        if (data == null) data = new HashMap<>();
        data.put("reservationId", reservationId);
    }
    
    public boolean isSuccess() { 
        if (data == null) return false;
        String success = data.get("success");
        return success != null ? Boolean.parseBoolean(success) : false;
    }
    public void setSuccess(boolean success) { 
        if (data == null) data = new HashMap<>();
        data.put("success", String.valueOf(success));
    }
    
    public String getReason() { return data != null ? data.get("reason") : null; }
    public void setReason(String reason) { 
        if (data == null) data = new HashMap<>();
        data.put("reason", reason);
    }
}
