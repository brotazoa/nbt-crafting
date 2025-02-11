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

package de.siphalor.nbtcrafting.mixin;

import java.util.concurrent.ExecutionException;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.siphalor.nbtcrafting.NbtCrafting;
import de.siphalor.nbtcrafting.api.nbt.NbtUtil;
import de.siphalor.nbtcrafting.util.duck.IItemStack;

@SuppressWarnings("ALL")
@Mixin(RecipeFinder.class)
public abstract class MixinRecipeFinder {
	@Shadow
	public abstract void addItem(final ItemStack stack);

	@Shadow
	@Final
	public Int2IntMap idToAmountMap;

	@Unique
	private static Pair<Integer, CompoundTag> getStackPair(ItemStack stack) {
		return new Pair<Integer, CompoundTag>(Registry.ITEM.getRawId(stack.getItem()), NbtUtil.getTagOrEmpty(stack));
	}

	@Inject(method = "findRecipe(Lnet/minecraft/recipe/Recipe;Lit/unimi/dsi/fastutil/ints/IntList;I)Z", at = @At("HEAD"))
	public void onFindRecipe(@SuppressWarnings("rawtypes") Recipe recipe, IntList ints, int int_1, CallbackInfoReturnable<Boolean> ci) {
		NbtCrafting.lastRecipeFinder = (RecipeFinder) (Object) this;
	}

	@Inject(method = "countRecipeCrafts(Lnet/minecraft/recipe/Recipe;ILit/unimi/dsi/fastutil/ints/IntList;)I", at = @At("HEAD"))
	public void onCountCrafts(@SuppressWarnings("rawtypes") Recipe recipe, int int_1, IntList ints, CallbackInfoReturnable<Integer> ci) {
		NbtCrafting.lastRecipeFinder = (RecipeFinder) (Object) this;
	}

	/**
	 * @reason Fixes nbt items to be excluded from matching sometimes? Shouldn't break anything.
	 * @author Siphalor
	 */
	@Overwrite
	public void addNormalItem(final ItemStack stack) {
		addItem(stack);
	}

	/**
	 * @reason Makes this function nbt dependent
	 * @author Siphalor
	 */
	@Overwrite
	public static int getItemId(ItemStack stack) {
		int id = -1;
		if (stack.isEmpty()) {
			id = 0;
		} else {
			Pair<Integer, CompoundTag> stackPair = getStackPair(stack);
			try {
				id = NbtCrafting.stack2IdMap.get(stackPair);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	/**
	 * @reason Makes this function nbt dependent
	 * @author Siphalor
	 */
	@Overwrite
	public static ItemStack getStackFromId(final int id) {
		synchronized (NbtCrafting.id2StackMap) {
			if (NbtCrafting.id2StackMap.containsKey(id)) {
				ItemStack result = new ItemStack(Item.byRawId(NbtCrafting.id2StackMap.get(id).getFirst()));
				((IItemStack) (Object) result).nbtCrafting$setRawTag(NbtCrafting.id2StackMap.get(id).getSecond());
				return result;
			}
		}
		return ItemStack.EMPTY;
	}
}
