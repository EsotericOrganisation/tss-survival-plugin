package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ViewTrustedPlayersCommand {

  public ViewTrustedPlayersCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("view-trusted-players")
			.executesPlayer((Player player, CommandArguments args) -> {
			  Chunk chunk = player.getChunk();
			  PersistentDataContainer container = chunk.getPersistentDataContainer();

			  String ownerUuidString = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

			  TSSCorePlugin core = plugin.getCore();
			  MessageManager messageManager = core.getMessageManager();
			  if (ownerUuidString == null) {
				messageManager.sendMessage(player, Message.CHUNK_NOT_CLAIMED);
				return;
			  }

			  UUID ownerUuid = UUID.fromString(ownerUuidString);
			  if (!player.getUniqueId().equals(ownerUuid)) {
				messageManager.sendMessage(player, Message.NOT_YOUR_CHUNK);
				return;
			  }

			  int x = chunk.getX();
			  int z = chunk.getZ();

			  PlayerProfile profile = core.getPlayerManager().getProfile(player);
			  for (ClaimedChunk claimedChunk : profile.getSurvivalData().getClaims().get(player.getWorld().getName())) {
				if (claimedChunk.getX() == x && claimedChunk.getZ() == z) {
				  for (UUID uuid : claimedChunk.getTrustedPlayers()) {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
					player.sendMessage(Component.text(
							offlinePlayer.getName()
					));
				  }

				  return;
				}
			  }
			})
			.register();
  }
}
