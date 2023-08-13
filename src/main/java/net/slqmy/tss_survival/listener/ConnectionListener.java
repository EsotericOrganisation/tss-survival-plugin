package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

  private final Map<UUID, BukkitTask> claimMessageTasks = new HashMap<>();

  private final TSSSurvivalPlugin plugin;

  public ConnectionListener(TSSSurvivalPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onJoin(@NotNull PlayerJoinEvent event) {
    Player player = event.getPlayer();

    BukkitTask claimMessageTask = Bukkit.getScheduler().runTaskTimer(
            plugin, () -> {
              if (!plugin.getCore().getSurvivalWorldNames().contains(player.getWorld().getName())) {
                return;
              }

              Chunk playerChunk = player.getChunk();
              PersistentDataContainer container = playerChunk.getPersistentDataContainer();
              String ownerChunkUuid = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

              MessageManager messageManager = plugin.getCore().getMessageManager();
              if (ownerChunkUuid == null) {
                player.sendActionBar(
                        messageManager.getPlayerMessage(Message.WILDERNESS, player)
                );
                return;
              }

              UUID ownerUuid = UUID.fromString(ownerChunkUuid);
              OfflinePlayer chunkOwner = Bukkit.getOfflinePlayer(ownerUuid);

              player.sendActionBar(
                      messageManager.getPlayerMessage(Message.PLAYER_CLAIM, player, chunkOwner.getName())
              );
            },
            0L,
            30L
    );

    claimMessageTasks.put(player.getUniqueId(), claimMessageTask);
  }

  @EventHandler
  public void onQuit(@NotNull PlayerQuitEvent event) {
    claimMessageTasks.remove(event.getPlayer().getUniqueId());
  }
}
