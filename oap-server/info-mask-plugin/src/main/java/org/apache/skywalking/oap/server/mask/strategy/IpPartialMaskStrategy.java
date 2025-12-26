package org.apache.skywalking.oap.server.mask.strategy;

public class IpPartialMaskStrategy implements MaskStrategy {

    @Override
    public String mask(final String value) {
        if (value == null || !value.matches("^(\\d{1,3}\\.){3}\\d{1,3}$")) {
            return "***.***.***.***";
        }

        String[] parts = value.split("\\.");
        return parts[0] + "." + parts[1] + ".***.***";
    }
}