package org.teacon.nocaet.client.compat.optifine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ShadersTransformer {

    public static void transform(ClassNode node) {
        var field = new FieldNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "NOCAET_PROGRESS", "Lnet/optifine/shaders/uniform/ShaderUniform1f;", null, null);
        node.fields.add(field);
        for (MethodNode method : node.methods) {
            if (method.name.equals("<clinit>")) {
                var list = new InsnList();
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, node.name, "shaderUniforms", "Lnet/optifine/shaders/uniform/ShaderUniforms;"));
                list.add(new LdcInsnNode("nocaetProgress"));
                list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/optifine/shaders/uniform/ShaderUniforms", "make1f", "(Ljava/lang/String;)Lnet/optifine/shaders/uniform/ShaderUniform1f;", false));
                list.add(new FieldInsnNode(Opcodes.PUTSTATIC, node.name, field.name, field.desc));
                method.instructions.insertBefore(method.instructions.getLast(), list);
            } else if (method.name.equals("setProgramUniforms")) {
                var list = new InsnList();
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, node.name, field.name, field.desc));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/teacon/nocaet/client/compat/optifine/OptifineHooks", "getProgress", "()F"));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, node.name, "setProgramUniform1f", "(Lnet/optifine/shaders/uniform/ShaderUniform1f;F)V", false));
                method.instructions.insertBefore(method.instructions.getFirst(), list);
            }
        }
    }
}
