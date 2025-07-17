package marketplace;

import common.JsonParser;

import common.SagaState;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages saga state persistence and recovery for distributed transactions.
 * Ensures saga durability across system failures and restarts.
 */
public class SagaStateManager {
    private final Map<String, SagaSnapshot> sagaSnapshots = new ConcurrentHashMap<>();
    private final ScheduledExecutorService persistenceExecutor = Executors.newSingleThreadScheduledExecutor();
    private final String stateDirectory;

    private final long persistenceIntervalMs;
    
    /**
     * Creates a saga state manager with default settings.
     * @param stateDirectory Directory to store saga state files
     */
    public SagaStateManager(String stateDirectory) {
        this(stateDirectory, 10000); // 10 seconds default persistence interval
    }
    
    /**
     * Creates a saga state manager with custom settings.
     * @param stateDirectory Directory to store saga state files
     * @param persistenceIntervalMs Interval for periodic persistence in milliseconds
     */
    public SagaStateManager(String stateDirectory, long persistenceIntervalMs) {
        this.stateDirectory = stateDirectory;
        this.persistenceIntervalMs = persistenceIntervalMs;

        
        // Create state directory if it doesn't exist
        File dir = new File(stateDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Recover existing saga states
        recoverSagaStates();
        
        // Start periodic persistence
        persistenceExecutor.scheduleAtFixedRate(
            this::persistAllStates, 
            persistenceIntervalMs, 
            persistenceIntervalMs, 
            TimeUnit.MILLISECONDS
        );
        
        System.out.println("SagaStateManager initialized with " + sagaSnapshots.size() + " recovered sagas");
    }
    
    /**
     * Saves saga state immediately.
     * @param sagaId The saga identifier
     * @param snapshot The saga state snapshot
     */
    public void saveSagaState(String sagaId, SagaSnapshot snapshot) {
        sagaSnapshots.put(sagaId, snapshot);
        // Immediate persistence for critical state changes
        persistSagaState(sagaId, snapshot);
    }
    
    /**
     * Retrieves saga state.
     * @param sagaId The saga identifier
     * @return The saga state snapshot or null if not found
     */
    public SagaSnapshot getSagaState(String sagaId) {
        return sagaSnapshots.get(sagaId);
    }
    
    /**
     * Removes saga state from memory and disk.
     * @param sagaId The saga identifier
     */
    public void removeSagaState(String sagaId) {
        sagaSnapshots.remove(sagaId);
        File stateFile = new File(stateDirectory + "/" + sagaId + ".state");
        if (stateFile.exists()) {
            stateFile.delete();
        }
    }
    
    /**
     * Gets all active saga IDs.
     * @return List of active saga IDs
     */
    public List<String> getActiveSagaIds() {
        return new ArrayList<>(sagaSnapshots.keySet());
    }
    
    /**
     * Gets the count of active sagas.
     * @return Number of active sagas
     */
    public int getActiveSagaCount() {
        return sagaSnapshots.size();
    }
    
    /**
     * Persists a single saga state to disk.
     * @param sagaId The saga identifier
     * @param snapshot The saga state snapshot
     */
    private void persistSagaState(String sagaId, SagaSnapshot snapshot) {
        try {
            String data = serializeSnapshot(snapshot);
            Files.write(Paths.get(stateDirectory + "/" + sagaId + ".state"), 
                       data.getBytes(StandardCharsets.UTF_8));
            System.out.println("Persisted saga state: " + sagaId);
        } catch (IOException e) {
            System.err.println("Failed to persist saga state " + sagaId + ": " + e.getMessage());
        }
    }
    
    /**
     * Persists all saga states to disk.
     */
    private void persistAllStates() {
        int persistedCount = 0;
        for (Map.Entry<String, SagaSnapshot> entry : sagaSnapshots.entrySet()) {
            try {
                persistSagaState(entry.getKey(), entry.getValue());
                persistedCount++;
            } catch (Exception e) {
                System.err.println("Error persisting saga " + entry.getKey() + ": " + e.getMessage());
            }
        }
        if (persistedCount > 0) {
            System.out.println("Persisted " + persistedCount + " saga states");
        }
    }
    
    /**
     * Recovers saga states from disk on startup.
     */
    private void recoverSagaStates() {
        File directory = new File(stateDirectory);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".state"));
        
        if (files != null) {
            for (File file : files) {
                try {
                    String data = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    SagaSnapshot snapshot = deserializeSnapshot(data);
                    String sagaId = file.getName().replace(".state", "");
                    sagaSnapshots.put(sagaId, snapshot);
                    System.out.println("Recovered saga state: " + sagaId + " in state " + snapshot.getCurrentState());
                } catch (IOException e) {
                    System.err.println("Failed to recover saga state from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Shuts down the saga state manager.
     */
    public void shutdown() {
        persistenceExecutor.shutdown();
        try {
            // Final persistence before shutdown
            persistAllStates();
            
            if (!persistenceExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                persistenceExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("SagaStateManager shut down");
    }
    
    /**
     * Serializes a SagaSnapshot to a simple text format.
     */
    private String serializeSnapshot(SagaSnapshot snapshot) {
        if (snapshot == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("sagaId=").append(snapshot.getSagaId()).append("\n");
        sb.append("orderId=").append(snapshot.getOrderId()).append("\n");
        sb.append("currentState=").append(snapshot.getCurrentState()).append("\n");
        sb.append("createdAt=").append(snapshot.getCreatedAt()).append("\n");
        sb.append("lastUpdated=").append(snapshot.getLastUpdated()).append("\n");
        
        // Serialize compensation actions
        sb.append("compensationActionCount=").append(snapshot.getCompensationActions().size()).append("\n");
        for (int i = 0; i < snapshot.getCompensationActions().size(); i++) {
            CompensationActionSnapshot action = snapshot.getCompensationActions().get(i);
            sb.append("compensationAction.").append(i).append(".sellerId=").append(action.getSellerId()).append("\n");
            sb.append("compensationAction.").append(i).append(".reservationId=").append(action.getReservationId()).append("\n");
            sb.append("compensationAction.").append(i).append(".actionType=").append(action.getActionType()).append("\n");
            sb.append("compensationAction.").append(i).append(".timestamp=").append(action.getTimestamp()).append("\n");
        }
        
        // Serialize reservation IDs
        sb.append("reservationIdCount=").append(snapshot.getReservationIds().size()).append("\n");
        int resIndex = 0;
        for (Map.Entry<String, String> entry : snapshot.getReservationIds().entrySet()) {
            sb.append("reservationId.").append(resIndex).append(".key=").append(entry.getKey()).append("\n");
            sb.append("reservationId.").append(resIndex).append(".value=").append(entry.getValue()).append("\n");
            resIndex++;
        }
        
        return sb.toString();
    }
    
    /**
     * Deserializes a SagaSnapshot from text format.
     */
    private SagaSnapshot deserializeSnapshot(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }
        
        String[] lines = data.split("\n");
        
        String sagaId = null;
        String orderId = null;
        SagaState currentState = null;
        long createdAt = 0;
        long lastUpdated = 0;
        int compensationActionCount = 0;
        int reservationIdCount = 0;
        
        // Parse basic properties
        for (String line : lines) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                switch (key) {
                    case "sagaId":
                        sagaId = value;
                        break;
                    case "orderId":
                        orderId = value;
                        break;
                    case "currentState":
                        try {
                            currentState = SagaState.valueOf(value);
                        } catch (IllegalArgumentException e) {
                            currentState = SagaState.STARTED;
                        }
                        break;
                    case "createdAt":
                        createdAt = Long.parseLong(value);
                        break;
                    case "lastUpdated":
                        lastUpdated = Long.parseLong(value);
                        break;
                    case "compensationActionCount":
                        compensationActionCount = Integer.parseInt(value);
                        break;
                    case "reservationIdCount":
                        reservationIdCount = Integer.parseInt(value);
                        break;
                }
            }
        }
        
        // Parse compensation actions
        List<CompensationActionSnapshot> compensationActions = new ArrayList<>();
        for (int i = 0; i < compensationActionCount; i++) {
            String sellerId = null;
            String reservationId = null;
            String actionType = null;
            long timestamp = 0;
            
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    String prefix = "compensationAction." + i + ".";
                    if (key.startsWith(prefix)) {
                        String field = key.substring(prefix.length());
                        switch (field) {
                            case "sellerId":
                                sellerId = value;
                                break;
                            case "reservationId":
                                reservationId = value;
                                break;
                            case "actionType":
                                actionType = value;
                                break;
                            case "timestamp":
                                timestamp = Long.parseLong(value);
                                break;
                        }
                    }
                }
            }
            
            if (sellerId != null && reservationId != null && actionType != null) {
                CompensationActionSnapshot action = new CompensationActionSnapshot(sellerId, reservationId, actionType);
                compensationActions.add(action);
            }
        }
        
        // Parse reservation IDs
        Map<String, String> reservationIds = new ConcurrentHashMap<>();
        for (int i = 0; i < reservationIdCount; i++) {
            String key = null;
            String value = null;
            
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String lineKey = parts[0].trim();
                    String lineValue = parts[1].trim();
                    
                    String prefix = "reservationId." + i + ".";
                    if (lineKey.startsWith(prefix)) {
                        String field = lineKey.substring(prefix.length());
                        switch (field) {
                            case "key":
                                key = lineValue;
                                break;
                            case "value":
                                value = lineValue;
                                break;
                        }
                    }
                }
            }
            
            if (key != null && value != null) {
                reservationIds.put(key, value);
            }
        }
        
        return new SagaSnapshot(sagaId, orderId, currentState, compensationActions, reservationIds);
    }
    
    /**
     * Represents a snapshot of saga state for persistence.
     */
    public static class SagaSnapshot {
        private final String sagaId;
        private final String orderId;
        private final SagaState currentState;
        private final List<CompensationActionSnapshot> compensationActions;
        private final Map<String, String> reservationIds;
        private final long lastUpdated;
        private final long createdAt;
        
        public SagaSnapshot(String sagaId, String orderId, SagaState currentState,
                           List<CompensationActionSnapshot> compensationActions,
                           Map<String, String> reservationIds) {
            this.sagaId = sagaId;
            this.orderId = orderId;
            this.currentState = currentState;
            this.compensationActions = compensationActions != null ? compensationActions : new ArrayList<>();
            this.reservationIds = reservationIds != null ? reservationIds : new ConcurrentHashMap<>();
            this.lastUpdated = System.currentTimeMillis();
            this.createdAt = System.currentTimeMillis();
        }
        
        // Getters
        public String getSagaId() { return sagaId; }
        public String getOrderId() { return orderId; }
        public SagaState getCurrentState() { return currentState; }
        public List<CompensationActionSnapshot> getCompensationActions() { return compensationActions; }
        public Map<String, String> getReservationIds() { return reservationIds; }
        public long getLastUpdated() { return lastUpdated; }
        public long getCreatedAt() { return createdAt; }
        
        public boolean isExpired(long timeoutMs) {
            return System.currentTimeMillis() - lastUpdated > timeoutMs;
        }
        
        @Override
        public String toString() {
            return String.format("SagaSnapshot{sagaId='%s', orderId='%s', state=%s, compensations=%d, reservations=%d}", 
                               sagaId, orderId, currentState, compensationActions.size(), reservationIds.size());
        }
    }
    
    /**
     * Represents a compensation action for persistence.
     */
    public static class CompensationActionSnapshot {
        private final String sellerId;
        private final String reservationId;
        private final String actionType;
        private final long timestamp;
        
        public CompensationActionSnapshot(String sellerId, String reservationId, String actionType) {
            this.sellerId = sellerId;
            this.reservationId = reservationId;
            this.actionType = actionType;
            this.timestamp = System.currentTimeMillis();
        }
        
        // Getters
        public String getSellerId() { return sellerId; }
        public String getReservationId() { return reservationId; }
        public String getActionType() { return actionType; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("CompensationAction{sellerId='%s', reservationId='%s', actionType='%s'}", 
                               sellerId, reservationId, actionType);
        }
    }
}