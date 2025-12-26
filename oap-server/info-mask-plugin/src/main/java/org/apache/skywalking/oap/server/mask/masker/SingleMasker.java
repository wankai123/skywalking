package org.apache.skywalking.oap.server.mask.masker;

import java.util.HashSet;
import java.util.Set;
import org.apache.skywalking.oap.server.mask.strategy.MaskStrategy;

public class SingleMasker extends InfoMasker {
    private MaskStrategy maskStrategy;
    public SingleMasker(MaskStrategy strategy) {
        this.maskStrategy = strategy;
    }
    //todo: configure different strategies for different keys
    private final Set<String> maskKeys = new HashSet<>();

    public SingleMasker() {
    }

    public boolean isMaskKey(String key) {
        return maskKeys.contains(key);
    }

    public void addMaskKey(String key) {
        maskKeys.add(key.toLowerCase());
    }

    public String maskValue(String value) {
        return maskStrategy.mask(value);
    }

    public String maskValue(String key, String value) {
        if (isMaskKey(key)) {
            return maskStrategy.mask(value);
        }
        return value;
    }
}
