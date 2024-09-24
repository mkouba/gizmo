package io.quarkus.gizmo2.impl;

import java.lang.annotation.RetentionPolicy;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.attribute.MethodParameterInfo;
import io.github.dmlloyd.classfile.attribute.MethodParametersAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.ClassMethodDesc;
import io.quarkus.gizmo2.InterfaceMethodDesc;
import io.quarkus.gizmo2.MethodDesc;
import io.quarkus.gizmo2.ParamVar;
import io.quarkus.gizmo2.creator.BlockCreator;
import io.quarkus.gizmo2.creator.StaticMethodCreator;

public final class StaticMethodCreatorImpl implements StaticMethodCreator {
    private final TypeCreatorImpl owner;
    private final String name;
    private List<ParamVarImpl> params = new ArrayList<>(4);
    private ClassDesc returnType = ConstantDescs.CD_void;
    private MethodDesc desc;
    private int flags;
    private List<Annotation> invisible;
    private List<Annotation> visible;

    public StaticMethodCreatorImpl(final TypeCreatorImpl owner, final String name) {
        this.owner = owner;
        this.name = name;
        this.flags = AccessFlag.STATIC.mask();
    }

    public MethodDesc desc() {
        MethodDesc desc = this.desc;
        if (desc == null) {
            MethodTypeDesc mtd = MethodTypeDesc.of(returnType, params.stream().map(ParamVarImpl::type).toArray(ClassDesc[]::new));
            this.desc = desc = owner instanceof InterfaceCreatorImpl ? InterfaceMethodDesc.of(owner.type(), name, mtd) : ClassMethodDesc.of(owner.type(), name, mtd);
        }
        return desc;
    }

    public void returning(final ClassDesc type) {
        this.desc = null;
        returnType = type;
    }

    public void returning(final Class<?> type) {
        returning(Util.classDesc(type));
    }

    public void body(final Consumer<BlockCreator> builder) {
        owner.zb.withMethod(name, type(), AccessFlag.STATIC.mask(), mb -> {
            mb.withFlags(flags);
            if (visible != null) {
                mb.with(RuntimeVisibleAnnotationsAttribute.of(visible));
            }
            if (invisible != null) {
                mb.with(RuntimeInvisibleAnnotationsAttribute.of(invisible));
            }
            // lock parameters
            params = List.copyOf(params);
            mb.with(MethodParametersAttribute.of(params.stream().map(pv -> MethodParameterInfo.ofParameter(Optional.of(pv.name()), pv.flags())).toList()));
            // todo: parameter annotations here
            mb.withCode(cb -> {
                BlockCreatorImpl bc = new BlockCreatorImpl(owner, cb);
                for (ParamVarImpl param : params) {
                    cb.localVariable(param.slot(), param.name(), param.type(), bc.startLabel(), bc.endLabel());
                }
                bc.accept(builder);
                bc.writeCode(cb, bc);
                if (bc.fallsOut()) {
                    throw new IllegalStateException("Outermost block of an executable member must not fall out (return or throw instead)");
                }
            });
        });
    }

    public MethodTypeDesc type() {
        return desc().type();
    }

    public ParamVar parameter(final String name, final ClassDesc type) {
        int size = params.size();
        int slot;
        if (size == 0) {
            slot = 0;
        } else {
            ParamVarImpl last = params.get(size - 1);
            slot = last.slot() + last.typeKind().slotSize();
        }
        ParamVarImpl pv = new ParamVarImpl(type, name, size, slot);
        params.add(pv);
        return pv;
    }

    public void withFlag(final AccessFlag flag) {
        switch (flag) {
            case PUBLIC, PRIVATE, PROTECTED, STATIC, SYNCHRONIZED, SYNTHETIC, BRIDGE -> flags |= flag.mask();
            default -> throw new IllegalArgumentException(flag.toString());
        }
    }

    public ClassDesc owner() {
        return owner.type();
    }

    public String name() {
        return name;
    }

    public void addAnnotation(final RetentionPolicy retention, final Annotation annotation) {
        switch (retention) {
            case CLASS -> invisible().add(annotation);
            case RUNTIME -> visible().add(annotation);
        }
    }

    private List<Annotation> visible() {
        List<Annotation> list = visible;
        if (list == null) {
            list = visible = new ArrayList<>(4);
        }
        return list;
    }

    private List<Annotation> invisible() {
        List<Annotation> list = invisible;
        if (list == null) {
            list = invisible = new ArrayList<>(4);
        }
        return list;
    }

    void accept(final Consumer<? super StaticMethodCreatorImpl> builder) {
        builder.accept(this);
    }
}
