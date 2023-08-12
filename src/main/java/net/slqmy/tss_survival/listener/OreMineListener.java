package net.slqmy.tss_survival.listener;

import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class OreMineListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public OreMineListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onOreMine(@NotNull BlockBreakEvent event) {
	Player player = event.getPlayer();

	if (player.getGameMode() == GameMode.CREATIVE) {
	  return;
	}

	Block brokenBlock = event.getBlock();
	String blockType = brokenBlock.getType().name();

	if (!blockType.endsWith("_ORE")) {
	  return;
	}

	PlayerInventory inventory = player.getInventory();

	ItemStack heldItem = inventory.getItemInMainHand();
	ItemMeta heldItemMeta = heldItem.getItemMeta();
	if (heldItemMeta.hasEnchant(Enchantment.SILK_TOUCH)) {
	  return;
	}

	Bukkit.getScheduler().runTaskLater(plugin, () -> brokenBlock.setType(blockType.startsWith("DEEPSLATE_") ? Material.DEEPSLATE : blockType.startsWith("NETHER_") ? Material.NETHERRACK : Material.STONE), 1L);
  }
}
