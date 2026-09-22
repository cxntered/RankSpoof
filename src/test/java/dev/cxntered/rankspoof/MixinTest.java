package dev.cxntered.rankspoof;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

/**
 * This code was adapted from <a href="https://modrinth.com/mod/skyblocker-liap">Skyblocker</a> under the LGPLv3 license.
 * <p>
 * <a href="https://github.com/SkyblockerMod/Skyblocker/blob/main/src/test/java/de/hysky/skyblocker/MixinsTest.java"><code>MixinsTest.java</code></a>
 * <p>
 * SPDX-License-Identifier: LGPL-3.0-only
 */
public class MixinTest {
    @BeforeAll
    static void setupEnvironment() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testMixins() {
        var environment = MixinEnvironment.getCurrentEnvironment();
        Assertions.assertInstanceOf(IMixinTransformer.class, environment.getActiveTransformer());
        environment.setOption(MixinEnvironment.Option.REFMAP_REMAP, false);
        environment.audit();
    }
}
