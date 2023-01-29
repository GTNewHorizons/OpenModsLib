package openmods.calc.parsing;

import java.util.List;

import openmods.calc.IExecutable;

import com.google.common.collect.ImmutableList;

public class DummyNode<E> implements IExprNode<E> {

    private final IExprNode<E> child;

    public DummyNode(IExprNode<E> child) {
        this.child = child;
    }

    @Override
    public void flatten(List<IExecutable<E>> output) {
        child.flatten(output);
    }

    @Override
    public Iterable<IExprNode<E>> getChildren() {
        return ImmutableList.of(child);
    }

}
