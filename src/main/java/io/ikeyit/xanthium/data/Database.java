package io.ikeyit.xanthium.data;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private Map<String, String> base = new HashMap<>();

    public void set(String key, String value) {
        base.put(key, value);
    }

    public String get(String key) {
        return base.get(key);
    }

    public void rename(String key, String newKey) {
        String value = base.remove(key);
        base.put(newKey, value);
    }
}
