package org.Kademlia.Storage;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class StorageManager {
    private final Map<BigInteger, StorageValue> storage;

    public StorageManager() {
        this.storage = new HashMap<BigInteger, StorageValue>();
    }

    public synchronized boolean addValue(BigInteger key, StorageValue value) {
        // If the key is already present, check the timestamp and update only if newer.
        if (storage.containsKey(key)) {
            StorageValue aux = storage.get(key);
            if (aux.getTimestamp() < value.getTimestamp()) {
                storage.put(key, value);
                return true;
            } else {
                return false;
            }
        }
        // If the key was not previously present just add it.
        storage.put(key, value);
        return true;
    }

    public StorageValue getValue(BigInteger key) {
        StorageValue value = storage.get(key);
        return value;
    }

    public synchronized boolean put(BigInteger key, BigInteger value, long timestamp) {
        StorageValue Value = new StorageValue(value, timestamp);
        return this.addValue(key, Value);
    }


    @Override
    public String toString() {
        return "StorageManager{" +
                "storage=" + storage +
                '}';
    }
}
