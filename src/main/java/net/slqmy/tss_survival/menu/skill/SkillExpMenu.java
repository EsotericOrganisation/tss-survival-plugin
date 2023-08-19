package net.slqmy.tss_survival.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.Colour;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_core.util.InventoryUtil;
import net.slqmy.tss_core.util.NumberUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkillExpMenu {

  public SkillExpMenu(Player player, SkillType skill, int shift, @NotNull TSSSurvivalPlugin plugin) {
	TSSCorePlugin core = plugin.getCore();
	PlayerProfile profile = core.getPlayerManager().getProfile(player);

	SurvivalPlayerData survivalData = profile.getSurvivalData();
	int totalExperience = survivalData.getSkillExperience(skill);
	int level = SurvivalPlayerData.experienceToLevel(totalExperience);

	ItemStack displayItem = skill.getDisplayItem(player, core);
	Component skillMessage = displayItem.getItemMeta().displayName();
	Inventory inventory = Bukkit.createInventory(null, 54, skillMessage);

	boolean reachedRightSide = false;

	for (int i = 1; i <= 25; i++) {
	  Material displayMaterial;

	  if (i == level + 1) {
		displayMaterial = Material.LIME_STAINED_GLASS_PANE;
	  } else if (i <= level) {
		displayMaterial = Material.GREEN_STAINED_GLASS_PANE;
	  } else {
		displayMaterial = Material.GRAY_STAINED_GLASS_PANE;
	  }

	  int currentLevelRequiredExp = SurvivalPlayerData.levelToExperience(i - 1);
	  int nextLevelRequiredExp = SurvivalPlayerData.levelToExperience(i);

	  int bound = nextLevelRequiredExp - currentLevelRequiredExp;
	  int progress = totalExperience - currentLevelRequiredExp;

	  int count = (int) Math.min(Math.round(((double) progress / bound) * 10), 10);

	  TextComponent progressDisplay = Component.text(
			  "-".repeat(Math.max(count, 0)),
			  Colour.GREEN
	  ).append(
			  Component.text("-".repeat(Math.min(Math.max(10 - count, 0), 10)), Colour.WHITE).appendSpace().append(Component.text(Math.max(Math.min(progress, bound), 0), Colour.YELLOW)).append(Component.text("/", Colour.ORANGE)).append(Component.text(bound, Colour.YELLOW))
	  );

	  int mod = i % 10;
	  int floor = (int) Math.floor(i / 10.0D);

	  int horizontalDisplacement = floor * 4 + ((mod > 3) ? Math.min(mod - 3, 2) : 0) + ((mod > 8) ? mod - 8 : 0) - shift;
	  if (horizontalDisplacement > 7) {
		break;
	  }

	  if (horizontalDisplacement < -1) {
		continue;
	  }

	  int position = 10 + horizontalDisplacement + Math.min(9 * mod, 27) + (mod > 5 ? Math.max((mod - 5) * -9, -27) : 0);
	  if (position % 9 == 8) {
		reachedRightSide = true;
	  }

	  ItemStack item = new ItemStack(displayMaterial, i);
	  ItemMeta meta = item.getItemMeta();
	  meta.displayName(skillMessage.appendSpace().append(Component.text(NumberUtil.toRomanNumeral(i))));
	  meta.lore(
			  List.of(
					  progressDisplay.decoration(TextDecoration.ITALIC, false)
			  )
	  );

	  item.setItemMeta(meta);

	  inventory.setItem(position, item);
	}

	ItemStack borderItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
	for (int i = 0; i < 9; i++) {
	  inventory.setItem(i, borderItem);
	}

	for (int i = 45; i < 54; i++) {
	  inventory.setItem(i, borderItem);
	}

	ItemStack arrow = new ItemStack(Material.ARROW);
	ItemMeta meta = arrow.getItemMeta();

	NamespacedKey arrowDirection = new NamespacedKey(plugin, "skill_exp_menu_arrow_direction");
	NamespacedKey skillType = new NamespacedKey(plugin, "skill_exp_menu_skill");
	NamespacedKey shiftKey = new NamespacedKey(plugin, "skill_exp_menu_shift");

	PersistentDataContainer container = meta.getPersistentDataContainer();
	container.set(skillType, PersistentDataType.STRING, skill.name());
	container.set(shiftKey, PersistentDataType.INTEGER, shift);

	if (shift == 0) {
	  for (int i = 9; i < 45; i += 9) {
		inventory.setItem(i, borderItem);
	  }
	} else {
	  container.set(arrowDirection, PersistentDataType.STRING, "backward");
	  arrow.setItemMeta(meta);

	  inventory.setItem(48, arrow);
	}

	if (reachedRightSide) {
	  container.set(arrowDirection, PersistentDataType.STRING, "forward");
	  arrow.setItemMeta(meta);

	  inventory.setItem(50, arrow);
	} else {
	  for (int i = 17; i < 54; i += 9) {
		inventory.setItem(i, borderItem);
	  }
	}

	MessageManager messageManager = core.getMessageManager();

	ItemStack closeButton = new ItemStack(Material.BARRIER);
	ItemMeta closeMeta = closeButton.getItemMeta();
	closeMeta.displayName(messageManager.getPlayerMessage(Message.CLOSE, player).decoration(TextDecoration.ITALIC, false));

	PersistentDataContainer closeButtonContainer = closeMeta.getPersistentDataContainer();
	closeButtonContainer.set(new NamespacedKey(plugin, "is_skills_exp_menu_close_button"), PersistentDataType.BOOLEAN, true);

	closeButton.setItemMeta(closeMeta);
	inventory.setItem(49, closeButton);

	if (shift < 2) {
	  ItemStack skillItem = new ItemStack(skill.getSecondaryDisplayItemMaterial());
	  ItemMeta skillMeta = skillItem.getItemMeta();
	  skillMeta.displayName(skillMessage);
	  skillMeta.lore(List.of(
			  messageManager.getPlayerMessage(Message.START_OF_SKILL_ADVENTURE, player, skill.name()).decoration(TextDecoration.ITALIC, false)
	  ));
	  skillMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
	  skillItem.setItemMeta(skillMeta);
	  inventory.setItem(10 - shift, skillItem);
	}

	ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	ItemMeta bookMeta = book.getItemMeta();
	bookMeta.displayName(messageManager.getPlayerMessage(Message.INFO, player).decoration(TextDecoration.ITALIC, false));
	bookMeta.lore(List.of(messageManager.getPlayerMessage(skill.getInfoMessage(), player).decoration(TextDecoration.ITALIC, false)));
	bookMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
	book.setItemMeta(bookMeta);
	inventory.setItem(3, book);

	ItemStack skillItem = skill.getDisplayItem(player, core);
	inventory.setItem(4, skillItem);

	ItemStack tipsTricks = new ItemStack(Material.FILLED_MAP);
	ItemMeta tipsTricksMeta = tipsTricks.getItemMeta();
	tipsTricksMeta.displayName(messageManager.getPlayerMessage(Message.TIPS_AND_TRICKS, player).decoration(TextDecoration.ITALIC, false));
	tipsTricksMeta.lore(
			List.of(
					messageManager.getPlayerMessage(skill.getTipsTricksMessage(), player).decoration(TextDecoration.ITALIC, false)
			)
	);
	tipsTricksMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
	tipsTricks.setItemMeta(tipsTricksMeta);
	inventory.setItem(5, tipsTricks);

	InventoryUtil.makeStatic(inventory, plugin.getCore());
	player.openInventory(inventory);
  }
}
