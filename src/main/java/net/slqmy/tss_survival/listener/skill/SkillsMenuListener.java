package net.slqmy.tss_survival.listener.skill;

import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.menu.skill.SkillExpMenu;
import net.slqmy.tss_survival.menu.skill.SkillsMenu;
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
  public void onSkillsMenuPageNavigate(@NotNull InventoryClickEvent event) {
	ItemStack clickedItem = event.getCurrentItem();
	if (clickedItem == null) {
	  return;
	}

	Player player = (Player) event.getWhoClicked();

	ItemMeta meta = clickedItem.getItemMeta();
	if (meta == null) {
	  return;
	}

	PersistentDataContainer container = meta.getPersistentDataContainer();

	if (event.getRawSlot() == 49 && container.get(new NamespacedKey(plugin, "is_skills_exp_menu_close_button"), PersistentDataType.BOOLEAN) != null) {
	  new SkillsMenu(player, plugin);
	  return;
	}

	String arrowDirection = container.get(new NamespacedKey(plugin, "skill_exp_menu_arrow_direction"), PersistentDataType.STRING);
	if (arrowDirection == null) {
	  return;
	}

	int shift = container.get(new NamespacedKey(plugin, "skill_exp_menu_shift"), PersistentDataType.INTEGER);
	String skillString = container.get(new NamespacedKey(plugin, "skill_exp_menu_skill"), PersistentDataType.STRING);
	SkillType skillType = SkillType.valueOf(skillString);

	switch (arrowDirection) {
	  case "forward" -> new SkillExpMenu(player, skillType, shift + 1, plugin);
	  case "backward" -> new SkillExpMenu(player, skillType, shift - 1, plugin);
	}
  }

  @EventHandler
  public void onSkillsMenuInteract(@NotNull InventoryClickEvent event) {
	ItemStack clickedItem = event.getCurrentItem();
	if (clickedItem == null) {
	  return;
	}

	ItemMeta meta = clickedItem.getItemMeta();
	if (meta == null) {
	  return;
	}

	PersistentDataContainer container = meta.getPersistentDataContainer();
	String skillTypeString = container.get(new NamespacedKey(plugin, "skill"), PersistentDataType.STRING);

	DebugUtil.log("SKILL TYPE: " + skillTypeString);

	if (skillTypeString == null) {
	  return;
	}

	SkillType skillType;
	try {
	  skillType = SkillType.valueOf(skillTypeString);
	} catch (IllegalArgumentException exception) {
	  return;
	}

	new SkillExpMenu((Player) event.getWhoClicked(), skillType, 0, plugin);
  }
}
