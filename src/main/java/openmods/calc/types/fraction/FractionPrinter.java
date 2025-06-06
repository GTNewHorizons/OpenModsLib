package openmods.calc.types.fraction;

import org.apache.commons.lang3.math.Fraction;

import openmods.calc.IValuePrinter;
import openmods.config.simpler.Configurable;

public class FractionPrinter implements IValuePrinter<Fraction> {

    @Configurable
    public boolean properFractions;

    @Configurable
    public boolean expand;

    @Override
    public String str(Fraction value) {
        if (expand) return Double.toString(value.doubleValue());
        return properFractions ? value.toProperString() : value.toString();
    }

    @Override
    public String repr(Fraction value) {
        return str(value);
    }

}
