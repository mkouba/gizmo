package io.quarkus.gizmo2.impl;

import java.lang.constant.ClassDesc;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.ClassBuilder;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.ConstructorDesc;
import io.quarkus.gizmo2.Expr;
import io.quarkus.gizmo2.FieldDesc;
import io.quarkus.gizmo2.MethodDesc;
import io.quarkus.gizmo2.creator.AbstractMethodCreator;
import io.quarkus.gizmo2.creator.ClassCreator;
import io.quarkus.gizmo2.creator.ConstructorCreator;
import io.quarkus.gizmo2.creator.FieldCreator;
import io.quarkus.gizmo2.creator.InstanceMethodCreator;
import io.quarkus.gizmo2.creator.MethodCreator;

public final class ClassCreatorImpl extends TypeCreatorImpl implements ClassCreator {
    public ClassCreatorImpl(final ClassDesc type, final ClassBuilder zb) {
        super(type, zb);
    }

    public void withFlag(final AccessFlag flag) {
        if (flag == AccessFlag.INTERFACE) {
            throw new IllegalArgumentException("Flag " + flag + " not allowed here");
        }
        super.withFlag(flag);
    }

    public void withFlags(final Set<AccessFlag> flags) {
        if (flags.contains(AccessFlag.INTERFACE)) {
            throw new IllegalArgumentException("Flag " + AccessFlag.INTERFACE + " not allowed here");
        }
        super.withFlags(flags);
    }

    public void extends_(final ClassDesc desc) {
        zb.withSuperclass(desc);
    }

    public FieldDesc field(final String name, final Consumer<FieldCreator> builder) {
        return null;
    }

    public MethodDesc method(final String name, final Consumer<InstanceMethodCreator> builder) {
        return null;
    }

    public MethodDesc abstractMethod(final String name, final Consumer<AbstractMethodCreator> builder) {
        return null;
    }

    public MethodDesc nativeMethod(final String name, final Consumer<AbstractMethodCreator> builder) {
        return null;
    }

    public MethodDesc staticNativeMethod(final String name, final Consumer<MethodCreator> builder) {
        return null;
    }

    public ConstructorDesc constructor(final Consumer<ConstructorCreator> builder) {
        return null;
    }

    void accept(final Consumer<ClassCreator> builder) {
        builder.accept(this);
    }
}
