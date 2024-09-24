package io.quarkus.gizmo2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.AnnotationElement;
import io.quarkus.gizmo2.creator.AnnotationCreator;
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
    void withAnnotation(RetentionPolicy retention, Annotation annotation);

    default <A extends java.lang.annotation.Annotation> void withAnnotation(Class<A> annClazz, Consumer<AnnotationCreator<A>> builder) {
        Retention ret = annClazz.getAnnotation(Retention.class);
        RetentionPolicy retention = ret == null ? RetentionPolicy.CLASS : ret.value();
        withAnnotation(retention, AnnotationCreator.makeAnnotation(annClazz, builder));
    }

    default void withAnnotation(Class<? extends java.lang.annotation.Annotation> annClazz) {
        withAnnotation(annClazz, List.of());
    }

    default void withAnnotation(Class<? extends java.lang.annotation.Annotation> annClazz, List<AnnotationElement> entries) {
        Retention ret = annClazz.getAnnotation(Retention.class);
        RetentionPolicy retention = ret == null ? RetentionPolicy.CLASS : ret.value();
        withAnnotation(retention, Annotation.of(Util.classDesc(annClazz), entries));
    }
}
