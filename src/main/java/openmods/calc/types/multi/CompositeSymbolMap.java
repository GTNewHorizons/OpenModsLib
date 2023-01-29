package openmods.calc.types.multi;

import openmods.calc.Frame;
import openmods.calc.FrameFactory;
import openmods.calc.ISymbol;
import openmods.calc.NestedSymbolMap;
import openmods.calc.SymbolMap;

import com.google.common.base.Optional;

public class CompositeSymbolMap extends NestedSymbolMap<TypedValue> {

    private final TypedValue target;
    private final MetaObject.SlotAttr attrSlot;

    public CompositeSymbolMap(SymbolMap<TypedValue> parent, TypedValue target) {
        super(parent);
        this.target = target;
        this.attrSlot = target.getMetaObject().slotAttr;
    }

    @Override
    public void put(String name, ISymbol<TypedValue> symbol) {
        throw new IllegalStateException("Trying to set value in read-only frame");
    }

    @Override
    public ISymbol<TypedValue> get(String name) {
        final Frame<TypedValue> frame = FrameFactory.symbolsToFrame(parent);
        final Optional<TypedValue> value = attrSlot.attr(target, name, frame);
        return value.isPresent() ? super.createSymbol(value.get()) : super.get(name);
    }
}
