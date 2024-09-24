package io.quarkus.gizmo2.creator;

import java.lang.constant.ClassDesc;

import io.quarkus.gizmo2.FieldDesc;
import io.quarkus.gizmo2.impl.Util;

public interface FieldCreator extends MemberCreator {
    FieldDesc desc();

    void withType(ClassDesc type);

    default void withType(Class<?> type) {
        withType(Util.classDesc(type));
    }
}
