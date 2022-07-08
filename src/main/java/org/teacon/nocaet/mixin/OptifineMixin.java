package org.teacon.nocaet.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = {"net.optifine.shaders.config.ShaderPackParser", "net/optifine/shaders/Shaders"}, remap = false)
public class OptifineMixin {
}
