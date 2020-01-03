package de.siphalor.nbtcrafting.dollar.part.unary;

import de.siphalor.nbtcrafting.dollar.DollarEvaluationException;
import de.siphalor.nbtcrafting.dollar.part.DollarPart;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;

public abstract class UnaryDollarOperator implements DollarPart {
	DollarPart dollarPart;

	public UnaryDollarOperator(DollarPart dollarPart) {
		this.dollarPart = dollarPart;
	}

	@Override
	public final Object evaluate(Map<String, CompoundTag> reference) throws DollarEvaluationException {
		return evaluate(dollarPart.evaluate(reference));
	}

	public abstract Object evaluate(Object value) throws DollarEvaluationException;
}
