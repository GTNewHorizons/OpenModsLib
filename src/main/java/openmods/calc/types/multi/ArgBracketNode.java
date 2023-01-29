package openmods.calc.types.multi;

import java.util.List;

import openmods.calc.IExecutable;
import openmods.calc.parsing.BracketContainerNode;
import openmods.calc.parsing.IExprNode;

import com.google.common.base.Preconditions;

public class ArgBracketNode extends BracketContainerNode<TypedValue> {

    public ArgBracketNode(List<IExprNode<TypedValue>> args) {
        super(args, "(", ")");
    }

    @Override
    public void flatten(List<IExecutable<TypedValue>> output) {
        // Multivalued brackets should be handled by default operator
        Preconditions.checkState(args.size() == 1, "Invalid number of expressions in bracket: %s", args);
        args.get(0).flatten(output);
    }
}
