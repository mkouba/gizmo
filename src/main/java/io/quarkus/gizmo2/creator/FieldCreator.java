package io.quarkus.gizmo2.creator;

import io.quarkus.gizmo2.FieldDesc;

public interface FieldCreator extends MemberCreator {
    FieldDesc desc();
}
