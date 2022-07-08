package org.teacon.nocaet;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.teacon.nocaet.client.compat.optifine.ShaderPackParserTransformer;
import org.teacon.nocaet.client.compat.optifine.ShadersTransformer;

import java.util.List;
import java.util.Set;

public class GarlicMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClassName.equals("net.optifine.shaders.config.ShaderPackParser")) {
            ShaderPackParserTransformer.transform(targetClass);
        } else if (targetClassName.equals("net.optifine.shaders.Shaders")) {
            ShadersTransformer.transform(targetClass);
        }
    }
}
