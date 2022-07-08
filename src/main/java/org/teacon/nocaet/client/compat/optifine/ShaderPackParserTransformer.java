package org.teacon.nocaet.client.compat.optifine;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ShaderPackParserTransformer {

    public static void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if (method.name.equals("loadShader")) {
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode call && call.name.equals("remap")) {
                        var list = new InsnList();
                        list.add(new InsnNode(Opcodes.POP));
                        list.add(new VarInsnNode(Opcodes.ALOAD, 0)); // program
                        list.add(new VarInsnNode(Opcodes.ALOAD, 1)); // shader type
                        list.add(new VarInsnNode(Opcodes.ALOAD, 8)); // writer
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/teacon/nocaet/client/compat/optifine/OptifineHooks", "injectShader", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false));
                        list.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/optifine/util/LineBuffer"));
                        method.instructions.insertBefore(instruction, list);
                    }
                }
            }
        }
    }
}
