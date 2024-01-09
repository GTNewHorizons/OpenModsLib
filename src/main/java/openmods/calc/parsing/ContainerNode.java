package openmods.calc.parsing;

import java.util.List;

import com.google.common.collect.ImmutableList;

import openmods.calc.IExecutable;

public class ContainerNode<E> implements IExprNode<E> {

    public final List<IExprNode<E>> args;

    public ContainerNode(List<IExprNode<E>> args) {
        this.args = ImmutableList.copyOf(args);
    }

    @Override
    public void flatten(List<IExecutable<E>> output) {
        throw new UnsupportedOperationException(); // should be captured before serialization;
    }

    @Override
    public Iterable<IExprNode<E>> getChildren() {
        return args;
    }
}
