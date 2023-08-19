package net.slqmy.tss_survival.command.claim;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.util.ClaimUtil;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnTrustCommand {

  public UnTrustCommand(@NotNull TSSSurvivalPlugin plugin) {
	TSSCorePlugin core = plugin.getCore();
	MessageManager messageManager = core.getMessageManager();

	new CommandAPICommand("untrust")
			.withArguments(new PlayerArgument("player"))
			.withOptionalArguments(
					new StringArgument("where-to-trust")
							.replaceSuggestions(
									ArgumentSuggestions.stringsWithTooltips(
											(SuggestionInfo<CommandSender> info) -> {
											  Player player = (Player) info.sender();

											  return new IStringTooltip[]{
													  StringTooltip.ofString("all", messageManager.getPlayerMessage(Message.UNTRUST_PLAYER_LOCATION_ALL, player).content()),
													  StringTooltip.ofString("connected", messageManager.getPlayerMessage(Message.UNTRUST_PLAYER_LOCATION_CONNECTED, player).content()),
													  StringTooltip.ofString("chunk", messageManager.getPlayerMessage(Message.UNTRUST_PLAYER_LOCATION_CHUNK, player).content())
											  };
											}
									)
							)
			)
			.executesPlayer((Player player, CommandArguments args) -> {
			  String whereToSearch = (String) args.get("where-to-untrust");
			  if (whereToSearch == null) {
				whereToSearch = "connected";
			  }

			  Chunk currentChunk = player.getChunk();
			  PersistentDataContainer container = currentChunk.getPersistentDataContainer();

			  NamespacedKey chunkClaimOwnerKey = new NamespacedKey(plugin, "chunk_claim_owner");

			  UUID playerUuid = player.getUniqueId();

			  if (!"all".equals(whereToSearch)) {
				String ownerUuidString = container.get(chunkClaimOwnerKey, PersistentDataType.STRING);

				if (ownerUuidString == null) {
				  messageManager.sendMessage(player, Message.CHUNK_NOT_CLAIMED);
				  return;
				}

				UUID ownerUuid = UUID.fromString(ownerUuidString);

				if (!ownerUuid.equals(playerUuid)) {
				  messageManager.sendMessage(player, Message.NOT_YOUR_CHUNK);
				  return;
				}
			  }

			  Player untrustedPlayer = (Player) args.get("player");
			  assert untrustedPlayer != null;

			  UUID untrustedUuid = untrustedPlayer.getUniqueId();

			  if (untrustedUuid.equals(playerUuid)) {
				messageManager.sendMessage(player, Message.CANT_UNTRUST_YOURSELF);
				return;
			  }

			  World world = player.getWorld();

			  PlayerProfile profile = core.getPlayerManager().getProfile(player);
			  ArrayList<ClaimedChunk> claimedChunks = profile.getSurvivalData().getClaims().get(world.getName());

			  NamespacedKey trustKey = new NamespacedKey(plugin, untrustedUuid + "_is_trusted");

			  List<int[]> chunksToUntrustPlayerIn = new ArrayList<>();

			  int x = currentChunk.getX();
			  int z = currentChunk.getZ();

			  switch (whereToSearch) {
				case "all" -> {
				  for (ClaimedChunk claimedChunk : claimedChunks) {
					ArrayList<UUID> trustedPlayers = claimedChunk.getTrustedPlayers();
					trustedPlayers.remove(untrustedUuid);

					Chunk chunk = world.getChunkAt(claimedChunk.getX(), claimedChunk.getZ(), false);

					PersistentDataContainer chunkContainer = chunk.getPersistentDataContainer();
					chunkContainer.remove(trustKey);
				  }

				  messageManager.sendMessage(player, Message.PLAYER_SUCCESSFULLY_UNTRUSTED);
				  return;
				}
				case "connected" ->
						chunksToUntrustPlayerIn = ClaimUtil.getConnectedClaims(chunkClaimOwnerKey, trustKey, player, false, chunksToUntrustPlayerIn, new int[]{x, z});
				case "chunk" -> {
				  chunksToUntrustPlayerIn = List.of(new int[]{x, z});
				  container.remove(trustKey);
				}
			  }

			  for (ClaimedChunk claimedChunk : claimedChunks) {
				assert chunksToUntrustPlayerIn != null;

				boolean contains = false;
				for (int[] chunk : chunksToUntrustPlayerIn) {
				  if (chunk[0] == claimedChunk.getX() && chunk[1] == claimedChunk.getZ()) {
					contains = true;
					break;
				  }
				}

				if (!contains) {
				  continue;
				}

				claimedChunk.getTrustedPlayers().remove(untrustedUuid);
			  }

			  messageManager.sendMessage(player, Message.PLAYER_SUCCESSFULLY_UNTRUSTED);
			})
			.register();
  }
}
