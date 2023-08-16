package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.datatype.Rank;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Map;

public class ClaimCommand {

  public ClaimCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("claim")
			.executesPlayer((Player player, CommandArguments args) -> {
			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  if (!plugin.getCore().getSurvivalWorldNames().contains(player.getWorld().getName())) {
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

			  double skillLevelSum = 0;
			  SkillType[] skillTypes = SkillType.values();
			  for (SkillType skillType : skillTypes) {
				skillLevelSum += SurvivalPlayerData.experienceToLevel(profile.getSurvivalData().getSkillExperience(skillType));
			  }
			  skillLevelSum /= skillTypes.length;

			  DebugUtil.log(playerRank.getName());
			  for (Rank rank : plugin.getRanksPlugin().getRankManager().getRanks()) {
				DebugUtil.log(rank.getName() + " " + rank.getInitialSurvivalClaimChunks());
			  }

			  int allowedClaimChunks = playerRank.getInitialSurvivalClaimChunks() + (int) Math.floor(skillLevelSum);

			  DebugUtil.log(allowedClaimChunks);

			  Map<String, ArrayList<ClaimedChunk>> claimMap = survivalData.getClaims();
			  ArrayList<ClaimedChunk> claims = claimMap.get(player.getWorld().getName());

			  if (allowedClaimChunks < claims.size() + 1) {
				messageManager.sendMessage(player, Message.NOT_ENOUGH_CHUNKS);
				return;
			  }

			  Chunk chunk = player.getChunk();

			  int chunkX = chunk.getX();
			  int chunkZ = chunk.getZ();

			  PersistentDataContainer container = chunk.getPersistentDataContainer();

			  NamespacedKey chunkClaimOwner = new NamespacedKey(plugin, "chunk_claim_owner");
			  String ownerUuid = container.get(chunkClaimOwner, PersistentDataType.STRING);

			  if (ownerUuid != null) {
				messageManager.sendMessage(player, Message.CHUNK_ALREADY_CLAIMED);
				return;
			  }

			  ClaimedChunk newClaim = new ClaimedChunk(chunkX, chunkZ, new ArrayList<>());

			  claims.add(newClaim);

			  container.set(chunkClaimOwner, PersistentDataType.STRING, player.getUniqueId().toString());
			  container.set(new NamespacedKey(plugin, "chunk_claim_date"), PersistentDataType.LONG, System.currentTimeMillis());

			  messageManager.sendMessage(player, Message.CHUNK_CLAIMED_SUCCESSFULLY);
			})
			.register();
  }
}
