package openmods.calc.parsing;

import java.util.List;

import openmods.calc.ExecutableList;
import openmods.calc.IExecutable;

import com.google.common.collect.Lists;

public class ExprUtils {

    public static <E> IExecutable<E> flattenNode(IExprNode<E> node) {
        final List<IExecutable<E>> commands = Lists.newArrayList();
        node.flatten(commands);
        return ExecutableList.wrap(commands);
    }

}
