package org.apache.skywalking.oap.server.mask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.FileNotFoundException;
import java.io.Reader;
import lombok.Getter;
import org.apache.skywalking.oap.server.library.module.ModuleStartException;
import org.apache.skywalking.oap.server.library.util.ResourceUtils;
import org.apache.skywalking.oap.server.mask.strategy.FullStarsMaskStrategy;
import org.apache.skywalking.oap.server.mask.strategy.IpPartialMaskStrategy;
import org.apache.skywalking.oap.server.mask.strategy.MaskStrategy;

public class InfoMaskRuleLoader {

    @Getter
    private InfoMaskRule rule = new InfoMaskRule();

    InfoMaskRule loadRule() throws ModuleStartException {
        Reader applicationReader;
        try {
            applicationReader = ResourceUtils.read("info-mask-rules.yaml");
            rule = new ObjectMapper(new YAMLFactory()).readValue(applicationReader, InfoMaskRule.class);
        } catch (FileNotFoundException e) {
            throw new ModuleStartException("Cannot find the InfoMaskRule rule file [info-mask-rules.yml].", e);
        } catch (Exception e) {
            throw new ModuleStartException("Cannot read the InfoMaskRule rule file [info-mask-rules.yml].", e);
        }

        rule.getLog().getTagMaskRules().forEach(rule -> {
            rule.setStrategy(createMaskStrategy(rule.getStrategyName()));
        });
        rule.getLog().getJsonContentMaskRules().forEach(rule -> {
            rule.setStrategy(createMaskStrategy(rule.getStrategyName()));
        });
        rule.getZipkinTrace().getTagMaskRules().forEach(rule -> {
            rule.setStrategy(createMaskStrategy(rule.getStrategyName()));
        });
        return rule;
    }

    private MaskStrategy createMaskStrategy(String strategyName) {
        if (strategyName == null) {
            return new FullStarsMaskStrategy();
        }

        switch (strategyName) {
            case "FullStarsMaskStrategy":
                return new FullStarsMaskStrategy();
            case "IpPartialMaskStrategy":
                return new IpPartialMaskStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy name: " + strategyName);
        }
    }
}
