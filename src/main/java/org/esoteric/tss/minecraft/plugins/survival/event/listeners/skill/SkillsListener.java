package org.esoteric.tss.minecraft.plugins.survival.event.listeners.skill;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.esoteric.tss.minecraft.plugins.core.TSSCorePlugin;
import org.esoteric.tss.minecraft.plugins.core.data.Colour;
import org.esoteric.tss.minecraft.plugins.core.data.player.Message;
import org.esoteric.tss.minecraft.plugins.core.data.player.PlayerProfile;
import org.esoteric.tss.minecraft.plugins.core.data.player.survival.SkillType;
import org.esoteric.tss.minecraft.plugins.core.data.player.survival.SurvivalPlayerData;
import org.esoteric.tss.minecraft.plugins.core.events.custom.SkillExperienceGainEvent;
import org.esoteric.tss.minecraft.plugins.core.events.custom.SkillLevelUpEvent;
import org.esoteric.tss.minecraft.plugins.core.managers.MessageManager;
import org.esoteric.tss.minecraft.plugins.survival.FarmingSkillExpReward;
import org.esoteric.tss.minecraft.plugins.survival.MiningSkillExpReward;
import org.esoteric.tss.minecraft.plugins.survival.TSSSurvivalPlugin;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SkillsListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public SkillsListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onMobKill(@NotNull EntityDeathEvent event) {
	LivingEntity dead = event.getEntity();
	Player killer = dead.getKiller();

	if (killer == null) {
	  return;
	}

	PlayerProfile murdererProfile = plugin.getCore().getPlayerManager().getProfile(killer);

	double gainedExp = 0;
	for (
			Attribute attribute
			: new Attribute[]{
			Attribute.GENERIC_MAX_HEALTH,
			Attribute.GENERIC_ARMOR,
			Attribute.GENERIC_ARMOR_TOUGHNESS,
			Attribute.GENERIC_ATTACK_DAMAGE,
			Attribute.GENERIC_ATTACK_KNOCKBACK,
			Attribute.GENERIC_ATTACK_SPEED,
			Attribute.GENERIC_KNOCKBACK_RESISTANCE,
			Attribute.GENERIC_MOVEMENT_SPEED,
			Attribute.GENERIC_FOLLOW_RANGE
	}
	) {
	  AttributeInstance instance = dead.getAttribute(attribute);

	  if (instance != null) {
		gainedExp += instance.getValue();
	  }
	}

	murdererProfile.getSurvivalData().incrementSkillExperience(SkillType.COMBAT, (int) gainedExp);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onMineBlock(@NotNull BlockBreakEvent event) {
	Block brokenBlock = event.getBlock();
	Material blockType = brokenBlock.getType();

	MiningSkillExpReward reward;
	try {
	  reward = MiningSkillExpReward.valueOf(blockType.name());
	} catch (IllegalArgumentException exception) {
	  return;
	}

	Player player = event.getPlayer();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.MINING, reward.getExpReward());
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onForage(@NotNull BlockBreakEvent event) {
	Block brokenBlock = event.getBlock();
	Material blockType = brokenBlock.getType();
	String blockTypeString = blockType.name();

	int gainedExp;

	if (blockTypeString.endsWith("_LOG") || blockTypeString.endsWith("_WOOD")) {
	  if (blockTypeString.contains("ACACIA_") || blockTypeString.contains("MANGROVE_") || blockTypeString.contains("CHERRY_")) {
		gainedExp = 35;
	  } else {
		gainedExp = 20;
	  }
	} else if (blockTypeString.endsWith("_STEM") || blockTypeString.endsWith("_HYPHAE")) {
	  gainedExp = 25;
	} else {
	  return;
	}

	Player player = event.getPlayer();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.FORAGING, gainedExp);
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onFarm(@NotNull BlockBreakEvent event) {
	Block brokenBlock = event.getBlock();
	Material blockType = brokenBlock.getType();
	String blockTypeString = blockType.name();

	FarmingSkillExpReward reward;
	try {
	  reward = FarmingSkillExpReward.valueOf(blockTypeString);
	} catch (IllegalArgumentException exception) {
	  return;
	}

	Player player = event.getPlayer();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.FARMING, reward.getExpReward());
  }

  @EventHandler
  public void onEnchant(@NotNull EnchantItemEvent event) {
	Player enchanter = event.getEnchanter();
	int earnedExp = event.getEnchantsToAdd().keySet().size() * 100;

	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(enchanter);

	profile.getSurvivalData().incrementSkillExperience(SkillType.ENCHANTING, earnedExp);
  }

  @EventHandler
  public void onBrewPotion(@NotNull InventoryClickEvent event) {
	int slot = event.getRawSlot();

	if (slot != 0 && slot != 1 && slot != 2) {
	  return;
	}

	ItemStack clickedItem = event.getCurrentItem();
	if (clickedItem == null) {
	  return;
	}

	InventoryType inventoryType = event.getInventory().getType();

	if (inventoryType != InventoryType.BREWING) {
	  return;
	}

	Player player = (Player) event.getWhoClicked();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.ALCHEMY, 300);
  }

  @EventHandler
  public void onForge(@NotNull InventoryClickEvent event) {
	ItemStack clickedItem = event.getCurrentItem();
	if (clickedItem == null) {
	  return;
	}

	String inventoryType = event.getInventory().getType().name();

	int requiredSlot;
	int expReward;
	switch (inventoryType) {
	  case "ANVIL" -> {
		requiredSlot = 2;
		expReward = 150;
	  }
	  case "SMITHING" -> {
		requiredSlot = 3;
		expReward = 450;
	  }
	  case "BLAST_FURNACE" -> {
		requiredSlot = 2;
		expReward = 20;
	  }
	  case "GRINDSTONE" -> {
		requiredSlot = 2;
		expReward = 50;
	  }
	  default -> {
		return;
	  }
	}

	if (event.getRawSlot() != requiredSlot) {
	  return;
	}

	Player player = (Player) event.getWhoClicked();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.FORGING, expReward);
  }

  @EventHandler
  public void onFish(@NotNull PlayerFishEvent event) {
	Player player = event.getPlayer();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.FISHING, 100);
  }

  @EventHandler
  public void onSkillLevelUp(@NotNull SkillLevelUpEvent event) {
	Player player = event.getPlayer();
	SkillType skillType = event.getSkillType();

	TSSCorePlugin core = plugin.getCore();
	MessageManager messageManager = core.getMessageManager();

	Component display = skillType.getDisplayItem(player, core).getItemMeta().displayName();

	messageManager.sendMessage(player, Message.SKILL_LEVEL_UP, display);
	player.sendTitlePart(TitlePart.TITLE, display);
	player.sendTitlePart(TitlePart.SUBTITLE, Component.text(
			event.getOldLevel(),
			Colour.GREY
	).appendSpace().append(
			Component.text(
					"->",
					Colour.YELLOW
			)
	).appendSpace().append(
			Component.text(
					event.getNewLevel(),
					Colour.SLIME
			)
	));

	player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.AMBIENT, 1.0F, 1.0F));
  }

  @EventHandler
  public void onSkillExpGain(@NotNull SkillExperienceGainEvent event) {
	Player player = event.getPlayer();

	int gainedExp = event.getGainedExp();
	int totalExp = event.getTotalExp();

	int currentLevel = SurvivalPlayerData.experienceToLevel(totalExp);
	int nextLevel = currentLevel + 1;

	int neededExp = 50 * currentLevel * currentLevel * currentLevel + 50 * currentLevel;
	int nextLevelNeededExp = 50 * nextLevel * nextLevel * nextLevel + 50 * nextLevel;

	int bound = nextLevelNeededExp - neededExp;
	player.sendActionBar(
			Component.text(
					"+" + gainedExp,
					Colour.SLIME
			).appendSpace().append(
					event.getSkillType().getDisplayItem(player, plugin.getCore()).getItemMeta().displayName()
			).appendSpace().append(
					Component.text(
							"(",
							Colour.GREY
					)
			).append(
					Component.text(
							totalExp - neededExp,
							Colour.YELLOW
					)
			).append(
					Component.text(
							"/",
							Colour.WHITE
					)
			).append(
					Component.text(
							bound,
							Colour.YELLOW
					)
			).append(
					Component.text(
							")",
							Colour.GREY
					)
			)
	);

	player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.AMBIENT, 1.0F, 1.0F));
  }
}
