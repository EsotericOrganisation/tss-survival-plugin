package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClaimListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public ClaimListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onClaimInteract(@NotNull PlayerInteractEvent event) {
	Chunk chunk = null;
	Player player = event.getPlayer();

	switch (event.getAction()) {
	  case LEFT_CLICK_AIR, RIGHT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
		return;
	  }
	  case RIGHT_CLICK_BLOCK -> {
		Block block = event.getClickedBlock();
		DebugUtil.log("IS CONTAINER? " + (block.getState() instanceof Container));
		DebugUtil.log("IS OPENABLE? " + (block.getState() instanceof Openable));
		DebugUtil.log("BLOCK: " + (block.getState()));
		DebugUtil.log("BLOCK CLASS: " + (block.getState().getClass()));

		if (!(block.getState() instanceof Container)) {
		  return;
		}

		chunk = block.getChunk();
	  }
	  case PHYSICAL -> chunk = event.getPlayer().getChunk();
	}

	event.setCancelled(handleClaimedChunkInteract(chunk, player));
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onClaimedBlockBreak(@NotNull BlockBreakEvent event) {
	event.setCancelled(handleClaimedChunkInteract(event.getBlock().getChunk(), event.getPlayer()));
  }

  @EventHandler
  public void onClaimedChunkBlockPlace(BlockPlaceEvent event) {
	event.setCancelled(handleClaimedChunkInteract(event.getBlock().getChunk(), event.getPlayer()));
  }

  private boolean handleClaimedChunkInteract(@NotNull Chunk chunk, Player player) {
	PersistentDataContainer container = chunk.getPersistentDataContainer();
	String chunkOwnerUuidString = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

	if (chunkOwnerUuidString == null) {
	  return false;
	}

	UUID playerUuid = player.getUniqueId();
	if (UUID.fromString(chunkOwnerUuidString).equals(playerUuid)) {
	  return false;
	}

	Boolean playerIsTrusted = container.get(new NamespacedKey(plugin, playerUuid + "_is_trusted"), PersistentDataType.BOOLEAN);
	if (playerIsTrusted != null) {
	  return false;
	}

	plugin.getCore().getMessageManager().sendMessage(player, Message.CANT_INTERACT_BECAUSE_CHUNK_CLAIMED, Bukkit.getOfflinePlayer(UUID.fromString(chunkOwnerUuidString)).getName());
	return true;
  }
}
