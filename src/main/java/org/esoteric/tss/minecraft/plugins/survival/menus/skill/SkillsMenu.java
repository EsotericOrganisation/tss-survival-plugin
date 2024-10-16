package org.esoteric.tss.minecraft.plugins.survival.menus.skill;

import org.esoteric.tss.minecraft.plugins.core.data.player.survival.SkillType;
import org.esoteric.tss.minecraft.plugins.core.util.InventoryUtil;
import org.esoteric.tss.minecraft.plugins.survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SkillsMenu {
  public SkillsMenu(Player player, TSSSurvivalPlugin plugin) {
	Inventory inventory = Bukkit.createInventory(null, 45);

	for (SkillType skillType : SkillType.values()) {
	  ItemStack displayItem = skillType.getDisplayItem(player, plugin.getCore());
	  ItemMeta meta = displayItem.getItemMeta();

	  PersistentDataContainer container = meta.getPersistentDataContainer();
	  container.set(new NamespacedKey(plugin, "skill"), PersistentDataType.STRING, skillType.name());

	  displayItem.setItemMeta(meta);

	  inventory.addItem(displayItem);
	}

	InventoryUtil.makeStatic(inventory, plugin.getCore());
	player.openInventory(inventory);
  }
}
