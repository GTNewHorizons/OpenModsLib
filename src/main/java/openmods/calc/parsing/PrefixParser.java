package openmods.calc.parsing;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import openmods.calc.BinaryOperator;
import openmods.calc.BinaryOperator.Associativity;
import openmods.calc.OperatorDictionary;
import openmods.calc.UnaryOperator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

public class PrefixParser<E> implements IAstParser<E> {

    private final OperatorDictionary<E> operators;

    private final IExprNodeFactory<E> exprNodeFactory;

    public PrefixParser(OperatorDictionary<E> operators, IExprNodeFactory<E> exprNodeFactory) {
        this.operators = operators;
        this.exprNodeFactory = exprNodeFactory;
    }

    private static Token next(Iterator<Token> input) {
        try {
            return input.next();
        } catch (NoSuchElementException e) {
            throw new UnfinishedExpressionException();
        }
    }

    protected IExprNode<E> parseNode(ICompilerState<E> state, PeekingIterator<Token> input) {
        final Token token = next(input);
        return parseNode(state, input, token);
    }

    private IExprNode<E> parseNode(ICompilerState<E> state, PeekingIterator<Token> input, final Token firstToken) {
        if (firstToken.type.isValue()) return exprNodeFactory.createValueNode(firstToken);

        switch (firstToken.type) {
            case SYMBOL:
                return exprNodeFactory.createSymbolGetNode(firstToken.value);
            case MODIFIER:
                return parseModifierNode(firstToken.value, state, input);
            case LEFT_BRACKET:
                return parseNestedNode(firstToken.value, state, input);
            default:
                throw new IllegalArgumentException("Unexpected token: " + firstToken);
        }
    }

    private IExprNode<E> parseNestedNode(String openingBracket, ICompilerState<E> state, PeekingIterator<Token> input) {
        final String closingBracket = TokenUtils.getClosingBracket(openingBracket);

        if (openingBracket.equals("(")) {
            final Token operationToken = next(input);

            final String operationName = operationToken.value;
            if (operationToken.type == TokenType.SYMBOL) {
                final ISymbolCallStateTransition<E> stateTransition = state.getStateForSymbolCall(operationName);
                final List<IExprNode<E>> args = collectArgs(
                        openingBracket,
                        closingBracket,
                        input,
                        stateTransition.getState());
                return stateTransition.createRootNode(args);
            } else if (operationToken.type == TokenType.OPERATOR) {
                final List<IExprNode<E>> args = collectArgs(openingBracket, closingBracket, input, state);
                if (args.size() == 1) {
                    final UnaryOperator<E> unaryOperator = operators.getUnaryOperator(operationName);
                    Preconditions.checkState(unaryOperator != null, "Invalid unary operator '%s'", operationName);
                    return exprNodeFactory.createUnaryOpNode(unaryOperator, args.get(0));
                } else if (args.size() > 1) {
                    final BinaryOperator<E> binaryOperator = operators.getBinaryOperator(operationName);
                    Preconditions.checkState(binaryOperator != null, "Invalid binary operator '%s'", operationName);
                    return compileBinaryOpNode(binaryOperator, args);
                } else {
                    throw new IllegalArgumentException("Called operator " + operationName + " without any arguments");
                }
            } else {
                // bit non-standard, but meh
                final IExprNode<E> target = parseNode(state, input, operationToken);
                final List<IExprNode<E>> args = collectArgs(openingBracket, closingBracket, input, state);
                return exprNodeFactory.createBinaryOpNode(
                        operators.getDefaultOperator(),
                        target,
                        exprNodeFactory.createBracketNode(openingBracket, closingBracket, args));
            }
        } else {
            // not parenthesis, so probably data structure
            final List<IExprNode<E>> args = collectArgs(openingBracket, closingBracket, input, state);
            return exprNodeFactory.createBracketNode(openingBracket, closingBracket, args);
        }
    }

    private List<IExprNode<E>> collectArgs(String openingBracket, String closingBracket, PeekingIterator<Token> input,
            ICompilerState<E> state) {
        final List<IExprNode<E>> args = Lists.newArrayList();
        while (true) {
            final Token argToken = input.peek();
            if (argToken.type == TokenType.SEPARATOR) {
                // comma is whitespace
                next(input);
            } else if (argToken.type == TokenType.RIGHT_BRACKET) {
                Preconditions.checkState(
                        argToken.value.equals(closingBracket),
                        "Unmatched brackets: '%s' and '%s'",
                        openingBracket,
                        argToken.value);
                next(input);
                break;
            } else {
                final IAstParser<E> newParser = state.getParser();
                final IExprNode<E> parsedNode = newParser.parse(state, input);
                args.add(parsedNode);
            }
        }
        return args;
    }

    private IExprNode<E> parseModifierNode(String modifier, ICompilerState<E> state, PeekingIterator<Token> input) {
        final IModifierStateTransition<E> stateTransition = state.getStateForModifier(modifier);
        final ICompilerState<E> newState = stateTransition.getState();
        final IAstParser<E> newParser = newState.getParser();
        final IExprNode<E> parsedNode = newParser.parse(newState, input);
        return stateTransition.createRootNode(parsedNode);
    }

    private IExprNode<E> compileBinaryOpNode(BinaryOperator<E> op, List<IExprNode<E>> args) {
        if (op.associativity == Associativity.LEFT) {
            IExprNode<E> left = args.get(0);
            IExprNode<E> right = args.get(1);

            for (int i = 2; i < args.size(); i++) {
                left = exprNodeFactory.createBinaryOpNode(op, left, right);
                right = args.get(i);
            }

            return exprNodeFactory.createBinaryOpNode(op, left, right);
        } else {
            final int lastArg = args.size() - 1;
            IExprNode<E> left = args.get(lastArg - 1);
            IExprNode<E> right = args.get(lastArg);

            for (int i = lastArg - 2; i >= 0; i--) {
                right = exprNodeFactory.createBinaryOpNode(op, left, right);
                left = args.get(i);
            }

            return exprNodeFactory.createBinaryOpNode(op, left, right);
        }

    }

    @Override
    public IExprNode<E> parse(ICompilerState<E> state, PeekingIterator<Token> input) {
        return parseNode(state, input);
    }

}
