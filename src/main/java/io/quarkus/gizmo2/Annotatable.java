package io.quarkus.gizmo2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.AnnotationElement;
import io.quarkus.gizmo2.impl.Util;

/**
 * An element that can be annotated.
 */
public interface Annotatable {

    /**
     * Add an annotation.
     *
     * @param retention the retention policy for the annotation (must not be {@code null})
     * @param annotation the annotation (must not be {@code null})
     */
    void addAnnotation(RetentionPolicy retention, Annotation annotation);

    default void addAnnotation(Class<? extends java.lang.annotation.Annotation> annClazz, List<AnnotationElement> entries) {
        Retention ret = annClazz.getAnnotation(Retention.class);
        RetentionPolicy retention = ret == null ? RetentionPolicy.CLASS : ret.value();
        addAnnotation(retention, Annotation.of(Util.classDesc(annClazz), entries));
    }
}
