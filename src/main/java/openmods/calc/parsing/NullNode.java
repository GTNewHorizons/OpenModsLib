package openmods.calc.parsing;

import java.util.List;

import openmods.calc.IExecutable;

import com.google.common.collect.ImmutableList;

public class NullNode<E> implements IExprNode<E> {

    @Override
    public void flatten(List<IExecutable<E>> output) {}

    @Override
    public Iterable<IExprNode<E>> getChildren() {
        return ImmutableList.of();
    }

}
