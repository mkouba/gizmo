package io.quarkus.gizmo2.impl;

import java.lang.constant.ClassDesc;
import java.util.ListIterator;

import io.github.dmlloyd.classfile.CodeBuilder;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import io.quarkus.gizmo2.AccessMode;
import io.quarkus.gizmo2.ParamVar;

public final class ParamVarImpl extends LValueExprImpl implements ParamVar {
    private final ClassDesc type;
    private final String name;
    private final int index;
    private final int slot;
    private int flags = 0;

    public ParamVarImpl(final ClassDesc type, final String name, final int index, final int slot) {
        this.type = type;
        this.name = name;
        this.index = index;
        this.slot = slot;
    }

    int flags() {
        return flags;
    }

    public void withFlag(AccessFlag flag) {
        if (flag.locations().contains(AccessFlag.Location.METHOD_PARAMETER)) {
            flags |= flag.mask();
        } else {
            throw new IllegalArgumentException("Flag " + flag + " is not applicable to method parameters");
        }
    }

    public int slot() {
        return slot;
    }

    public int index() {
        return index;
    }

    ExprImpl emitGet(final BlockCreatorImpl block, final AccessMode mode) {
        return asBound();
    }

    Item emitSet(final BlockCreatorImpl block, final ExprImpl value, final AccessMode mode) {
        return new Item() {
            protected void processDependencies(final BlockCreatorImpl block, final ListIterator<Item> iter, final boolean verifyOnly) {
                value.process(block, iter, verifyOnly);
            }

            public void writeCode(final CodeBuilder cb, final BlockCreatorImpl block) {
                cb.storeLocal(typeKind(), slot);
            }
        };
    }

    public ClassDesc type() {
        return type;
    }

    public boolean bound() {
        return false;
    }

    public void writeCode(final CodeBuilder cb, final BlockCreatorImpl block) {
        cb.loadLocal(typeKind(), slot);
    }

    public String name() {
        return name;
    }
}
