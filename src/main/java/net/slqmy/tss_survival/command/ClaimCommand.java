package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.datatype.Rank;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.Claim;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ClaimCommand {

  public ClaimCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("claim")
			.executesPlayer((Player player, CommandArguments args) -> {
			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (!plugin.getSurvivalWorldNames().contains(player.getWorld().getName())) {
				messageManager.sendMessage(player, Message.CANT_CLAIM_HERE);
				return;
			  }

			  Rank playerRank = plugin.getRanksPlugin().getRankManager().getPlayerRank(player);

			  if (playerRank.getWeight() == 0) {
				messageManager.sendMessage(player, Message.RANK_REQUIRED);
				return;
			  }

			  PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

			  SurvivalPlayerData survivalData = profile.getSurvivalData();
			  // Check if they have enough allowed claims. If not, return.

			  List<Claim> claims = survivalData.getClaims();

			  Chunk chunk = player.getChunk();
			  int chunkX = chunk.getX();
			  int chunkZ = chunk.getZ();

			  Claim newClaim = new Claim(player.getWorld().getName(), chunkX, chunkZ);

			  if (claims.contains(newClaim)) {
				messageManager.sendMessage(player, Message.CHUNK_ALREADY_CLAIMED);
				return;
			  }

			  PersistentDataContainer container = chunk.getPersistentDataContainer();
			  container.set(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING, player.getUniqueId().toString());

			  claims.add(newClaim);

			  messageManager.sendMessage(player, Message.CHUNK_CLAIMED_SUCCESSFULLY);
			})
			.register();
  }
}
