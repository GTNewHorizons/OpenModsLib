package openmods.stencil;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.google.common.base.Throwables;

import openmods.Log;
import openmods.api.IResultListener;
import openmods.asm.MethodMatcher;

public class CapabilitiesHookInjector extends ClassVisitor {

    private final Type hookType;

    private final Method hookMethod;

    private final MethodMatcher targetMethod;

    private final IResultListener listener;

    private class MethodInjector extends MethodVisitor {

        public MethodInjector(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.RETURN) {
                Log.debug("Injecting call into OpenGLHelper.init()");
                super.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        hookType.getInternalName(),
                        hookMethod.getName(),
                        hookMethod.getDescriptor(),
                        false);
                listener.onSuccess();
            }

            super.visitInsn(opcode);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
        return targetMethod.match(name, desc) ? new MethodInjector(parent) : parent;
    }

    public CapabilitiesHookInjector(String rawCls, ClassVisitor cv, IResultListener listener) {
        super(Opcodes.ASM5, cv);

        this.listener = listener;

        Type type = Type.getMethodType(Type.VOID_TYPE);
        this.targetMethod = new MethodMatcher(
                rawCls.replace('.', '/'),
                type.getDescriptor(),
                "initializeTextures",
                "func_77474_a");

        this.hookType = Type.getType(FramebufferHooks.class);

        try {
            this.hookMethod = Method.getMethod(FramebufferHooks.class.getMethod("init"));
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

}
