package io.quarkus.gizmo2.creator;

import java.lang.constant.ClassDesc;
import java.util.Set;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.MethodDesc;
import io.quarkus.gizmo2.StaticFieldVar;
import io.quarkus.gizmo2.impl.TypeCreatorImpl;

public sealed interface TypeCreator permits ClassCreator, InterfaceCreator, TypeCreatorImpl {
    void withFlag(AccessFlag flag);

    void withFlags(Set<AccessFlag> flags);

    void sourceFile(String name);

    ClassDesc type();

    void implements_(ClassDesc interface_);

    default void implements_(Class<?> interface_) {
        if (! interface_.isInterface()) {
            throw new IllegalArgumentException("Only interfaces may be implemented");
        }
    }

    /**
     * Add a general static initializer block to the type.
     * A type may have many static initializers;
     * they will be concatenated in the order that they are added.
     *
     * @param builder the builder (must not be {@code null})
     */
    void initializer(Consumer<BlockCreator> builder);

    MethodDesc staticMethod(String name, Consumer<StaticMethodCreator> builder);

    StaticFieldVar staticField(String name, Consumer<StaticFieldCreator> builder);
}
