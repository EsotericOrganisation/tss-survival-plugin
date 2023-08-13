package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.event.custom_event.SkillLevelUpEvent;
import net.slqmy.tss_survival.FarmingSkillExpReward;
import net.slqmy.tss_survival.MiningSkillExpReward;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
			Attribute.GENERIC_MOVEMENT_SPEED
	}
	) {
	  AttributeInstance instance = dead.getAttribute(attribute);

	  if (instance != null) {
		gainedExp += instance.getValue();
	  }
	}

	murdererProfile.getSurvivalData().incrementSkillExperience(SkillType.COMBAT, (int) gainedExp);
  }

  @EventHandler
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

  @EventHandler
  public void onForage(@NotNull BlockBreakEvent event) {
	Block brokenBlock = event.getBlock();
	Material blockType = brokenBlock.getType();
	String blockTypeString = blockType.name();

	int gainedExp = 0;

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

  @EventHandler
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

  public void onFish(@NotNull PlayerFishEvent event) {
	Player player = event.getPlayer();
	PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

	profile.getSurvivalData().incrementSkillExperience(SkillType.FISHING, 100);
  }

  @EventHandler
  public void onSkillLevelUp(@NotNull SkillLevelUpEvent event) {
	plugin.getCore().getMessageManager().sendMessage(event.getPlayer(), Message.SKILL_LEVEL_UP,event.getSkillType().name());
  }
}
