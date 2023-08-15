package net.slqmy.tss_survival.menu;

import net.kyori.adventure.text.Component;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SkillExpMenu {

  public SkillExpMenu(Player player, SkillType skill, int shift, @NotNull TSSSurvivalPlugin plugin) {
	TSSCorePlugin core = plugin.getCore();
	PlayerProfile profile = core.getPlayerManager().getProfile(player);

	SurvivalPlayerData survivalData = profile.getSurvivalData();
	int level = SurvivalPlayerData.
			experienceToLevel(
					survivalData.getSkillExperience(
							skill
					)
			);

	Component skillMessage = skill.getDisplayItem(player, core).displayName();
	Inventory inventory = Bukkit.createInventory(null, 54, skillMessage);

	boolean reachedRightSide = false;

	for (int i = 0; 25 >= i; i++) {
	  Material displayMaterial =
			  i <= level
					  ? Material.LIME_STAINED_GLASS_PANE
					  : Material.GRAY_STAINED_GLASS_PANE;

	  int mod = i % 10;
	  int floor = (int) Math.floor(i / 10.0D);

	  int horizontalDisplacement = floor * 4 + ((3 < mod) ? Math.min(mod - 3, 2) : 0) + ((8 < mod) ? mod - 8 : 0) - shift;
	  if (7 < horizontalDisplacement) {
		break;
	  }

	  if (-1 > horizontalDisplacement) {
		continue;
	  }

	  int position = 10 + horizontalDisplacement + Math.min(9 * mod, 27) + (5 < mod ? Math.max((mod - 5) * -9, -27) : 0);
	  if (8 == position % 9) {
		reachedRightSide = true;
	  }

	  try {
		inventory.setItem(position, new ItemStack(displayMaterial, i));
	  } catch (IndexOutOfBoundsException ignoredException) {
	  }
	}

	ItemStack arrow = new ItemStack(Material.ARROW);
	ItemMeta meta = arrow.getItemMeta();

	NamespacedKey arrowDirection = new NamespacedKey(plugin, "skill_exp_menu_arrow_direction");
	NamespacedKey skillType = new NamespacedKey(plugin, "skill_exp_menu_skill");
	NamespacedKey shiftKey = new NamespacedKey(plugin, "skill_exp_menu_shift");

	PersistentDataContainer container = meta.getPersistentDataContainer();
	container.set(skillType, PersistentDataType.STRING, skill.name());
	container.set(shiftKey, PersistentDataType.INTEGER, shift);

	if (0 != shift) {
	  container.set(arrowDirection, PersistentDataType.STRING, "backward");
	  arrow.setItemMeta(meta);

	  inventory.setItem(47, arrow);
	}

	if (reachedRightSide) {
	  container.set(arrowDirection, PersistentDataType.STRING, "forward");
	  arrow.setItemMeta(meta);

	  inventory.setItem(51, arrow);
	}


	player.openInventory(inventory);
  }
}
