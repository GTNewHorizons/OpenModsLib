package openmods.calc.parsing;

import java.util.List;

import openmods.calc.ExecutableList;
import openmods.calc.IExecutable;

import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class AstCompiler<E> implements ITokenStreamCompiler<E> {

    private final ICompilerState<E> initialCompilerState;

    public AstCompiler(ICompilerState<E> initialCompilerState) {
        this.initialCompilerState = initialCompilerState;
    }

    @Override
    public IExecutable<E> compile(PeekingIterator<Token> input) {
        final IAstParser<E> parser = initialCompilerState.getParser();
        final IExprNode<E> rootNode = parser.parse(initialCompilerState, input);

        final List<IExecutable<E>> output = Lists.newArrayList();
        rootNode.flatten(output);
        return ExecutableList.wrap(output);
    }

}
