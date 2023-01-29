package openmods.calc.parsing;

import openmods.calc.IExecutable;

import com.google.common.collect.PeekingIterator;

public interface ITokenStreamCompiler<E> {

    public IExecutable<E> compile(PeekingIterator<Token> input);
}
