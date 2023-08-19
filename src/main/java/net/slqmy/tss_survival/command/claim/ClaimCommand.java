package net.slqmy.tss_survival.command.claim;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.Rank;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.datatype.player.survival.SkillType;
import net.slqmy.tss_core.datatype.player.survival.SurvivalPlayerData;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

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

			  int allowedClaimChunks = playerRank.getInitialSurvivalClaimChunks() + (int) Math.floor(skillLevelSum);

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
			.withSubcommand(
					new CommandAPICommand("list")
							.executesPlayer((Player player, CommandArguments args) -> {
							  TSSCorePlugin core = plugin.getCore();
							  MessageManager messageManager = core.getMessageManager();

							  PlayerProfile profile = core.getPlayerManager().getProfile(player);
							  for (ClaimedChunk chunk : profile.getSurvivalData().getClaims().get(player.getWorld().getName())) {
								TextComponent unclaim = messageManager.getPlayerMessage(Message.UNCLAIM, player);
								unclaim = unclaim.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/unclaim " + chunk.getX() + " " + chunk.getZ()));

								player.sendMessage(
										Component.text(
														" [" + chunk.getX() + ", " + chunk.getZ() + "] - "
												).append(
														Component.text(
																chunk.getTrustedPlayers().size() + " "
														)
												).append(
														messageManager.getPlayerMessage(chunk.getTrustedPlayers().size() == 1 ? Message.TRUSTED_PLAYER : Message.TRUSTED_PLAYERS, player)
												)
												.appendSpace()
												.append(
														Component.text(
																"["
														).append(
																unclaim
														).append(
																Component.text(
																		"]"
																)
														)
												)
								);
							  }
							})
			)
			.withSubcommand(
					new CommandAPICommand("info")
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
			)
			.register();
  }
}
