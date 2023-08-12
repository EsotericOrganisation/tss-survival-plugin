package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.datatype.Rank;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ClaimCommand {
  public ClaimCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("claim")
			.executesPlayer((Player player, CommandArguments args) -> {
			  Rank playerRank = plugin.getRanksPlugin().getRankManager().getPlayerRank(player);

			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (playerRank.getWeight() == 0) {
				messageManager.sendMessage(player, Message.RANK_REQUIRED);
				return;
			  }

			  PlayerProfile profile = plugin.getCore().getPlayerManager().getProfile(player);

			  SurvivalPlayerData survivalData = profile.getSurvivalData();
			  // Check if they have enough allowed claims. If not, return.

			  ArrayList<int[]> claims = survivalData.getClaims();

			  Chunk chunk = player.getChunk();
			  int chunkX = chunk.getX();
			  int chunkZ = chunk.getZ();
			  int[] chunkCoordinates = new int[] { chunkX, chunkZ};

			  if (claims.contains(chunkCoordinates)) {
				messageManager.sendMessage(player, Message.CHUNK_ALREADY_CLAIMED);
				return;
			  }

			  claims.add(chunkCoordinates);
			  messageManager.sendMessage(player, Message.CHUNK_CLAIMED_SUCCESSFULLY);
			})
			.register();
  }
}
