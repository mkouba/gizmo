package io.quarkus.gizmo2.creator;

import io.quarkus.gizmo2.Constant;

public interface StaticFieldCreator extends FieldCreator {
    void withInitial(Constant initial);
}
