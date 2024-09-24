package io.quarkus.gizmo2.impl;

import java.io.Serializable;
import java.lang.constant.ClassDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Set;

import sun.reflect.ReflectionFactory;

public final class Util {
    private Util() {}

    private static final ClassValue<ClassDesc> constantCache = new ClassValue<ClassDesc>() {
        protected ClassDesc computeValue(final Class<?> type) {
            return type.describeConstable().orElseThrow(IllegalArgumentException::new);
        }
    };

    public static ClassDesc classDesc(Class<?> clazz) {
        return constantCache.get(clazz);
    }

    private static final ClassValue<MethodHandle> writeReplaces = new ClassValue<MethodHandle>() {
        private final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();

        protected MethodHandle computeValue(final Class<?> type) {
            return rf.writeReplaceForSerialization(type);
        }
    };

    public static SerializedLambda serializedLambda(final Serializable lambda) {
        MethodHandle handle = writeReplaces.get(lambda.getClass());
        if (handle == null) {
            throw new IllegalArgumentException("Cannot interpret method handle");
        }
        try {
            return (SerializedLambda) (Object) handle.invoke(lambda);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    private static final StackWalker SW = StackWalker.getInstance(Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE, StackWalker.Option.SHOW_HIDDEN_FRAMES, StackWalker.Option.SHOW_REFLECT_FRAMES));

    public static String detailedStackTrace() {
        StringBuilder b = new StringBuilder(1000);
        SW.walk(stream -> {
            stream.forEachOrdered(
                fr -> b.append("at ").append(fr.toStackTraceElement()).append(" bci=").append(fr.getByteCodeIndex()).append('\n')
            );
            return null;
        });
        return b.toString();
    }
}
