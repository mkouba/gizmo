package io.quarkus.gizmo2.impl;

import java.lang.annotation.RetentionPolicy;
import java.lang.constant.ClassDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.ClassBuilder;
import io.github.dmlloyd.classfile.attribute.SourceFileAttribute;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.MethodDesc;
import io.quarkus.gizmo2.StaticFieldVar;
import io.quarkus.gizmo2.creator.BlockCreator;
import io.quarkus.gizmo2.creator.StaticFieldCreator;
import io.quarkus.gizmo2.creator.StaticMethodCreator;
import io.quarkus.gizmo2.creator.TypeCreator;

public abstract sealed class TypeCreatorImpl implements TypeCreator permits ClassCreatorImpl, InterfaceCreatorImpl {
    private final ClassDesc type;
    final ClassBuilder zb;
    private final List<Consumer<BlockCreator>> inits = new ArrayList<Consumer<BlockCreator>>(0);

    TypeCreatorImpl(final ClassDesc type, final ClassBuilder zb) {
        this.type = type;
        this.zb = zb;
    }

    public void withFlag(final AccessFlag flag) {
        zb.withFlags(flag);
    }

    public void withFlags(final Set<AccessFlag> flags) {
        zb.withFlags(flags.toArray(AccessFlag[]::new));
    }

    public void sourceFile(final String name) {
        zb.with(SourceFileAttribute.of(name));
    }

    public ClassDesc type() {
        return type;
    }

    public void implements_(final ClassDesc interface_) {
        zb.withInterfaceSymbols(interface_);
    }

    public void initializer(final Consumer<BlockCreator> builder) {
        inits.add(Objects.requireNonNull(builder, "builder"));
    }

    public MethodDesc staticMethod(final String name, final Consumer<StaticMethodCreator> builder) {
        Objects.requireNonNull(builder, "builder");
        StaticMethodCreatorImpl smc = new StaticMethodCreatorImpl(this, name);
        smc.accept(builder);
        return smc.desc();
    }

    public StaticFieldVar staticField(final String name, final Consumer<StaticFieldCreator> builder) {
        Objects.requireNonNull(builder, "builder");
        return null;
    }

    public void addAnnotation(final RetentionPolicy retention, final Annotation annotation) {

    }
}
