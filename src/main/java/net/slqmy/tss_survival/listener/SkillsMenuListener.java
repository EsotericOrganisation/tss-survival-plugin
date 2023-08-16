package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.menu.SkillExpMenu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SkillsMenuListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public SkillsMenuListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onSkillsMenuInteract(@NotNull InventoryClickEvent event) {
	ItemStack clickedItem = event.getCurrentItem();
	if (null == clickedItem) {
	  return;
	}

	ItemMeta meta = clickedItem.getItemMeta();
	if (null == meta) {
	  return;
	}

	PersistentDataContainer container = meta.getPersistentDataContainer();

	String skillTypeString = container.get(new NamespacedKey(plugin, "skill"), PersistentDataType.STRING);

	SkillType skillType;
	try {
	  skillType = SkillType.valueOf(skillTypeString);
	} catch (IllegalArgumentException | NullPointerException exception) {
	  return;
	}

	new SkillExpMenu((Player) event.getWhoClicked(), skillType, 0, plugin);
  }

  @EventHandler
  public void onSkillsMenuPageNavigate(@NotNull InventoryClickEvent event) {
	ItemStack clickedItem = event.getCurrentItem();
	if (null == clickedItem) {
	  return;
	}

	ItemMeta meta = clickedItem.getItemMeta();
	if (meta == null) {
	  return;
	}

	PersistentDataContainer container = meta.getPersistentDataContainer();

	String arrowDirection = container.get(new NamespacedKey(plugin, "skill_exp_menu_arrow_direction"), PersistentDataType.STRING);
	if (null == arrowDirection) {
	  return;
	}

	int shift = container.get(new NamespacedKey(plugin, "skill_exp_menu_shift"), PersistentDataType.INTEGER);
	String skillString = container.get(new NamespacedKey(plugin, "skill_exp_menu_skill"), PersistentDataType.STRING);
	SkillType skillType = SkillType.valueOf(skillString);

	Player player = (Player) event.getWhoClicked();

	switch (arrowDirection) {
	  case "forward" -> new SkillExpMenu(player, skillType, shift + 1, plugin);
	  case "backward" -> new SkillExpMenu(player, skillType, shift - 1, plugin);
	}
  }
}
