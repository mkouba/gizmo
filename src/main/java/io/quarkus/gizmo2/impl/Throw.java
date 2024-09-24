package io.quarkus.gizmo2.impl;

import java.util.ListIterator;

import io.github.dmlloyd.classfile.CodeBuilder;
import io.quarkus.gizmo2.Expr;

final class Throw extends Item {
    final ExprImpl thrown;

    Throw(final Expr val) {
        thrown = (ExprImpl) val;
    }

    protected void insert(final BlockCreatorImpl block, final ListIterator<Item> iter) {
        super.insert(block, iter);
        block.cleanStack(iter);
    }

    protected void processDependencies(final BlockCreatorImpl block, final ListIterator<Item> iter, final boolean verifyOnly) {
        thrown.process(block, iter, verifyOnly);
    }

    public boolean exitsAll() {
        return true;
    }

    public void writeCode(final CodeBuilder cb, final BlockCreatorImpl block) {
        block.exitAll(cb);
        cb.athrow();
    }
}
