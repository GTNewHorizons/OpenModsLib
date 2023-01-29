package openmods.calc.parsing;

import java.util.List;

import openmods.calc.IExecutable;
import openmods.calc.SymbolGet;

import com.google.common.collect.ImmutableList;

public class SymbolGetNode<E> extends SymbolOpNode<E> {

    public SymbolGetNode(String symbol) {
        super(symbol);
    }

    @Override
    public void flatten(List<IExecutable<E>> output) {
        output.add(new SymbolGet<E>(symbol));
    }

    @Override
    public Iterable<IExprNode<E>> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "<get: " + symbol + ">";
    }
}
