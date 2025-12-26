package org.apache.skywalking.oap.server.mask.service;

import org.apache.skywalking.oap.server.mask.InfoMaskRule;
import org.apache.skywalking.oap.server.library.module.Service;

public class InfoMaskRuleService implements Service {
    private final InfoMaskRule infoMaskRule;

    public InfoMaskRuleService(final InfoMaskRule infoMaskRule) {
        this.infoMaskRule = infoMaskRule;
    }

    public InfoMaskRule getInfoMaskRule() {
        return infoMaskRule;
    }
}
