package org.Kademlia.Storage;
import org.Kademlia.utils.Utils;

import java.math.BigInteger;
public class StorageValue {
    private final BigInteger value;
    private final long timestamp;

    public StorageValue(BigInteger value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public StorageValue(byte[] value, long timestamp) {
        this(Utils.byteToBigInteger(value), timestamp);
    }

    public BigInteger getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "StorageValue{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
