package net.slqmy.tss_survival.menu.skill;

import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.util.InventoryUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
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
