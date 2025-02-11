/*
 * Copyright 2020-2021 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.nbtcrafting.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.api.RecipeUtil;
import de.siphalor.nbtcrafting.api.nbt.NbtUtil;

public class BrewingRecipe extends IngredientRecipe<Inventory> {
	public static final RecipeSerializer<BrewingRecipe> SERIALIZER = new IngredientRecipe.Serializer<>(BrewingRecipe::new);

	public BrewingRecipe(Identifier identifier, Ingredient base, Ingredient ingredient, ItemStack result) {
		super(identifier, base, ingredient, result);
	}

	@Override
	public boolean matches(Inventory inv, World world) {
		if (ingredient.test(inv.getInvStack(3))) {
			for (int i = 0; i < 3; i++) {
				if (base.test(inv.getInvStack(i)))
					return true;
			}
		}
		return false;
	}

	public ItemStack[] craftAll(Inventory inv) {
		ItemStack[] stacks = new ItemStack[3];

		Map<String, Object> reference = new HashMap<>();
		reference.put("ingredient", NbtUtil.getTagOrEmpty(inv.getInvStack(3)));

		for (int i = 0; i < 3; i++) {
			if (base.test(inv.getInvStack(i))) {
				reference.put("base", NbtUtil.getTagOrEmpty(inv.getInvStack(i)));
				stacks[i] = RecipeUtil.applyDollars(result.copy(), resultDollars, reference);
			}
		}
		return stacks;
	}

	@Override
	public DefaultedList<ItemStack> getRemainingStacks(Inventory inv) {
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(4, ItemStack.EMPTY);
		Map<String, Object> reference = new HashMap<>();
		reference.put("ingredient", inv.getInvStack(3));
		stacks.set(3, RecipeUtil.getRemainder(inv.getInvStack(3), ingredient, reference));

		for (int i = 0; i < 3; i++) {
			if (base.test(inv.getInvStack(i))) {
				reference.put("base", inv.getInvStack(i));
				stacks.set(i, RecipeUtil.getRemainder(inv.getInvStack(i), base, reference));
			}
		}
		return stacks;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return NbtCrafting.BREWING_RECIPE_TYPE;
	}
}
