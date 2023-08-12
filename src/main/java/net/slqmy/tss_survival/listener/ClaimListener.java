package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClaimListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public ClaimListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onClaimBlockBreak(@NotNull BlockBreakEvent event) {
	Chunk chunk = event.getBlock().getChunk();
	PersistentDataContainer container = chunk.getPersistentDataContainer();
	String chunkOwnerUuidString = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

	if (chunkOwnerUuidString == null) {
	  return;
	}

	Player player = event.getPlayer();

	if (!UUID.fromString(chunkOwnerUuidString).equals(player.getUniqueId())) {
	  event.setCancelled(true);
	  plugin.getCore().getMessageManager().sendMessage(player, Message.CANT_INTERACT_BECAUSE_CHUNK_CLAIMED);
	}
  }
}
