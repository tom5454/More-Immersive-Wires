package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;

import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionalRecipeUtil {
	private List<ICondition> conditions = new ArrayList<>();
	private List<Consumer<Consumer<FinishedRecipe>>> recipes = new ArrayList<>();

	public static ConditionalRecipeUtil builder() {
		return new ConditionalRecipeUtil();
	}

	public ConditionalRecipeUtil addCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public ConditionalRecipeUtil addRecipe(Consumer<Consumer<FinishedRecipe>> callable) {
		recipes.add(callable);
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumer) {
		recipes.forEach(c -> c.accept(r -> {
			ConditionalRecipe.Builder b = ConditionalRecipe.builder();
			conditions.forEach(b::addCondition);
			b.addRecipe(r);
			b.generateAdvancement();
			b.build(consumer, r.getId());
		}));

	}
}
