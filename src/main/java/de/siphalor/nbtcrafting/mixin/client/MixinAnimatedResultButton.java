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

package de.siphalor.nbtcrafting.mixin.client;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnimatedResultButton.class)
public abstract class MixinAnimatedResultButton extends AbstractButtonWidget {

	public MixinAnimatedResultButton(int int_1, int int_2, String string_1) {
		super(int_1, int_2, string_1);
	}

	@Inject(
			method = "renderButton",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItem(Lnet/minecraft/item/ItemStack;II)V", shift = Shift.AFTER),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void drawButton(int mouseX, int mouseY, float delta, CallbackInfo ci, MinecraftClient minecraftClient, int int_3, int int_4, boolean boolean_1, @SuppressWarnings("rawtypes") List list_1, ItemStack stack, int int_5) {
		minecraftClient.getItemRenderer().renderGuiItemOverlay(minecraftClient.textRenderer, stack, this.x + int_5, this.y + int_5);
	}
}
