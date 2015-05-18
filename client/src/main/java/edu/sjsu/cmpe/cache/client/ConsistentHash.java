package edu.sjsu.cmpe.cache.client;

import com.google.common.hash.HashFunction;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by rajat on 5/8/15.
 */
public class ConsistentHash<T> {

    private final HashFunction hashFunc;
    private final int replicas;
    private final SortedMap<Long, T> ring = new TreeMap<Long, T>();

    public ConsistentHash(HashFunction hashFunc, int replicas,
                          Collection<T> nodes) {
        this.hashFunc = hashFunc;
        this.replicas = replicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < replicas; i++) {
            Long l=hashFunc.hashString(node.toString() + i).asLong();
            ring.put(l, node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < replicas; i++) {
            ring.remove(hashFunc.hashString(node.toString() + i).asLong());
        }
    }

    public T get(Object key) {
        if (ring.isEmpty()) {
            return null;
        }
        long hash = hashFunc.hashString(key.toString()).asLong();
        if (!ring.containsKey(hash)) {
            SortedMap<Long, T> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }
}
