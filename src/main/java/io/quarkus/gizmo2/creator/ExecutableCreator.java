package io.quarkus.gizmo2.creator;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

import io.quarkus.gizmo2.ParamVar;
import io.quarkus.gizmo2.impl.Util;

public interface ExecutableCreator extends MemberCreator {
    MethodTypeDesc type();

    /**
     * Add a parameter.
     * The method type is changed to include the new parameter.
     *
     * @param name the parameter name (must not be {@code null})
     * @param type the parameter type (must not be {@code null})
     * @return the parameter variable (not {@code null})
     */
    ParamVar parameter(String name, ClassDesc type);

    default ParamVar parameter(String name, Class<?> type) {
        return parameter(name, Util.classDesc(type));
    }
}
