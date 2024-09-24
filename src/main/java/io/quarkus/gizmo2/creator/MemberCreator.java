package io.quarkus.gizmo2.creator;

import java.lang.constant.ClassDesc;

import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.Annotatable;

public interface MemberCreator extends Annotatable {
    void withFlag(AccessFlag flag);

    ClassDesc owner();

    String name();
}
