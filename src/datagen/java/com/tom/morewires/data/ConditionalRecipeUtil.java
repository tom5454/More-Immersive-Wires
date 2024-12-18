package com.tom.morewires.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

public class ConditionalRecipeUtil {
	private List<ICondition> conditions = new ArrayList<>();
	private List<Consumer<RecipeOutput>> recipes = new ArrayList<>();

	public static ConditionalRecipeUtil builder() {
		return new ConditionalRecipeUtil();
	}

	public ConditionalRecipeUtil addCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	public ConditionalRecipeUtil addRecipe(Consumer<RecipeOutput> callable) {
		recipes.add(callable);
		return this;
	}

	public void build(RecipeOutput consumer) {
		recipes.forEach(c -> c.accept(new RecipeOutput() {

			@Override
			public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement,
					ICondition... conditions) {
				consumer.accept(id, recipe, advancement, ConditionalRecipeUtil.this.conditions.toArray(ICondition[]::new));
			}

			@Override
			public Builder advancement() {
				return consumer.advancement();
			}
		}));
	}
}
