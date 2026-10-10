package de.sunclient.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Ruestung und Item in der Hand, mit Haltbarkeit. */
public class ArmorModule extends HudModule {
	private static final EquipmentSlot[] SLOTS = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
	};
	private static final int ROW = 18;
	private static final int WIDTH = 48;

	public ArmorModule() {
		// x = 100000: wird automatisch an den rechten Rand geklemmt
		super("armor", "Ruestung", 100000, 4);
	}

	private List<ItemStack> stacks(Minecraft mc) {
		List<ItemStack> list = new ArrayList<>();
		if (mc.player != null) {
			for (EquipmentSlot slot : SLOTS) {
				ItemStack stack = mc.player.getItemBySlot(slot);
				if (!stack.isEmpty()) list.add(stack);
			}
		}
		if (list.isEmpty() && HudManager.editing) {
			list.add(new ItemStack(Items.DIAMOND_HELMET));
			list.add(new ItemStack(Items.DIAMOND_CHESTPLATE));
			list.add(new ItemStack(Items.DIAMOND_LEGGINGS));
			list.add(new ItemStack(Items.DIAMOND_BOOTS));
			list.add(new ItemStack(Items.DIAMOND_SWORD));
		}
		return list;
	}

	@Override
	public boolean hasContent(Minecraft mc) {
		return !stacks(mc).isEmpty();
	}

	@Override
	public int width(Minecraft mc) {
		return WIDTH;
	}

	@Override
	public int height(Minecraft mc) {
		return Math.max(1, stacks(mc).size()) * ROW;
	}

	@Override
	public void draw(GuiGraphics g, Minecraft mc, int x, int y) {
		int row = 0;
		for (ItemStack stack : stacks(mc)) {
			int ry = y + row * ROW;
			g.renderItem(stack, x + WIDTH - 18, ry);
			g.renderItemDecorations(mc.font, stack, x + WIDTH - 18, ry);
			if (stack.isDamageableItem()) {
				String left = String.valueOf(stack.getMaxDamage() - stack.getDamageValue());
				g.drawString(mc.font, left, x + WIDTH - 20 - mc.font.width(left), ry + 4, 0xFFFFFFFF, true);
			}
			row++;
		}
	}
}
