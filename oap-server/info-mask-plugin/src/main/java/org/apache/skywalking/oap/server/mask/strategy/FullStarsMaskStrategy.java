package org.apache.skywalking.oap.server.mask.strategy;

public class FullStarsMaskStrategy implements MaskStrategy {
    private final String replaceString;

    public FullStarsMaskStrategy() {
        this.replaceString = "******";
    }

    public FullStarsMaskStrategy(String maskPattern) {
        this.replaceString = maskPattern;
    }

    @Override
    public String mask(String value) {
        return replaceString;
    }
}