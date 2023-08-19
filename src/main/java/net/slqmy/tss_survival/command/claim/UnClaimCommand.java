package net.slqmy.tss_survival.command.claim;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.Location2DArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.TextComponent;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_core.manager.PlayerManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnClaimCommand {

  public UnClaimCommand(@NotNull TSSSurvivalPlugin plugin) {
	TSSCorePlugin core = plugin.getCore();
	MessageManager messageManager = core.getMessageManager();
	PlayerManager playerManager = core.getPlayerManager();

	new CommandAPICommand("unclaim")
			.withOptionalArguments(new Location2DArgument("claim", LocationType.BLOCK_POSITION)
					.replaceSuggestions(ArgumentSuggestions.stringsWithTooltips((SuggestionInfo<CommandSender> info) -> {
					  Player player = (Player) info.sender();
					  PlayerProfile profile = playerManager.getProfile(player);

					  String worldName = player.getWorld().getName();
					  List<ClaimedChunk> claimedChunks = profile.getSurvivalData().getClaims().get(worldName);

					  return claimedChunks.stream().map((ClaimedChunk chunk) -> {
						int size = chunk.getTrustedPlayers().size();

						TextComponent text = messageManager.getPlayerMessage(size == 1 ? Message.TRUSTED_PLAYER : Message.TRUSTED_PLAYERS, player);

						return StringTooltip.ofString(chunk.getX() + " " + chunk.getZ(), chunk.getTrustedPlayers().size() + " " + text.content());
					  }).toArray(IStringTooltip[]::new);
					}))
			)
			.withOptionalArguments(
					new StringArgument("where-to-unclaim")
							.replaceSuggestions(
									ArgumentSuggestions.stringsWithTooltips(
											(SuggestionInfo<CommandSender> info) -> {
											  Player player = (Player) info.sender();

											  return new IStringTooltip[]{
													  StringTooltip.ofString("all", messageManager.getPlayerMessage(Message.UNCLAIM_CHUNK_LOCATION_ALL, player).content()),
													  StringTooltip.ofString("connected", messageManager.getPlayerMessage(Message.UNCLAIM_CHUNK_LOCATION_CONNECTED, player).content()),
													  StringTooltip.ofString("chunk", messageManager.getPlayerMessage(Message.UNCLAIM_CHUNK_LOCATION_CHUNK, player).content())
											  };
											}
									)
							)
			)
			.executesPlayer((Player player, CommandArguments args) -> {
			  Chunk currentChunk = player.getChunk();
			  PersistentDataContainer container = currentChunk.getPersistentDataContainer();

			  NamespacedKey chunkClaimOwner = new NamespacedKey(plugin, "chunk_claim_owner");
			  String ownerUuidString = container.get(chunkClaimOwner, PersistentDataType.STRING);

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

			  PlayerProfile profile = playerManager.getProfile(player);
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
