package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class UnClaimCommand {

  public UnClaimCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("un-claim")
			.executesPlayer((Player player, CommandArguments args) -> {
			  Chunk currentChunk = player.getChunk();
			  PersistentDataContainer container = currentChunk.getPersistentDataContainer();

			  NamespacedKey chunkClaimOwner = new NamespacedKey(plugin, "chunk_claim_owner");
			  String ownerUuidString = container.get(chunkClaimOwner, PersistentDataType.STRING);

			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (ownerUuidString == null) {
				messageManager.sendMessage(player, Message.CHUNK_NOT_CLAIMED);
				return;
			  }

			  UUID ownerUuid = UUID.fromString(ownerUuidString);

			  if (!ownerUuid.equals(player.getUniqueId())) {
				messageManager.sendMessage(player, Message.NOT_YOUR_CHUNK);
				return;
			  }

			  NamespacedKey chunkClaimDate = new NamespacedKey(plugin, "chunk_claim_date");
			  long timeClaimedAtMilliseconds = container.get(chunkClaimDate, PersistentDataType.LONG);
			  long currentTime = System.currentTimeMillis();

			  if (currentTime - timeClaimedAtMilliseconds <= 1000L * 60L * 60L) {
				messageManager.sendMessage(player, Message.CLAIMED_TOO_RECENTLY);
				return;
			  }

			  PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);
			  ArrayList<ClaimedChunk> claimedChunks = profile.getSurvivalData().getClaims().get(player.getWorld().getName());

			  int x = currentChunk.getX();
			  int z = currentChunk.getZ();

			  claimedChunks.removeIf((ClaimedChunk claimedChunk) -> claimedChunk.getX() == x && claimedChunk.getZ() == z);

			  container.remove(chunkClaimOwner);
			  container.remove(chunkClaimDate);

			  messageManager.sendMessage(player, Message.UNCLAIMED_CHUNK);
			})
			.register();
  }
}
