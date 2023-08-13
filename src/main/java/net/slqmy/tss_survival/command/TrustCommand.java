package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
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
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TrustCommand {

  private final TSSSurvivalPlugin plugin;

  public TrustCommand(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;

	new CommandAPICommand("trust")
			.withArguments(new PlayerArgument("player"))
			.withArguments(
					new StringArgument("where-to-trust")
							.replaceSuggestions(
									ArgumentSuggestions.stringsWithTooltips(
											new IStringTooltip[]{
													StringTooltip.ofString("all", "Trust the player in all of your claimed chunks."),
													StringTooltip.ofString("connected", "Trust the player in all chunks connected to your current chunk."),
													StringTooltip.ofString("chunk", "Trust the player in just the current chunk.")
											}
									)
							)
			)
			.executesPlayer((Player player, CommandArguments args) -> {
			  Chunk currentChunk = player.getChunk();
			  PersistentDataContainer container = currentChunk.getPersistentDataContainer();

			  String ownerUuidString = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

			  TSSCorePlugin core = plugin.getCore();
			  MessageManager messageManager = core.getMessageManager();

			  String whereToSearch = (String) args.get("where-to-trust");
			  assert whereToSearch != null;

			  if (!whereToSearch.equals("all")) {
				if (ownerUuidString == null) {
				  messageManager.sendMessage(player, Message.CHUNK_NOT_CLAIMED);
				  return;
				}

				UUID ownerUuid = UUID.fromString(ownerUuidString);

				if (!ownerUuid.equals(player.getUniqueId())) {
				  messageManager.sendMessage(player, Message.NOT_YOUR_CHUNK);
				  return;
				}
			  }

			  Player trustedPlayer = (Player) args.get("player");
			  assert trustedPlayer != null;

			  if (trustedPlayer.getUniqueId().equals(player.getUniqueId())) {
				messageManager.sendMessage(player, Message.CANT_TRUST_YOURSELF);
				return;
			  }

			  List<int[]> chunksToTrustPlayerIn = new ArrayList<>();

			  int x = currentChunk.getX();
			  int z = currentChunk.getZ();

			  PlayerProfile profile = core.getPlayerManager().getProfile(player);
			  ArrayList<ClaimedChunk> claimedChunks = profile.getSurvivalData().getClaims().get(player.getWorld().getName());

			  switch (whereToSearch) {
				case "all" -> {
				  for (ClaimedChunk claimedChunk : claimedChunks) {
					ArrayList<UUID> trustedPlayers = claimedChunk.getTrustedPlayers();
					UUID trustedUuid = trustedPlayer.getUniqueId();

					if (trustedPlayers.contains(trustedUuid)) {
					  continue;
					}

					trustedPlayers.add(trustedUuid);
					messageManager.sendMessage(player, Message.PLAYER_SUCCESSFULLY_TRUSTED);
				  }

				  return;
				}
				case "connected" ->
				  chunksToTrustPlayerIn = getChunksToTrustPlayerIn(player.getWorld(), player.getUniqueId(), trustedPlayer.getUniqueId(),chunksToTrustPlayerIn,null,new int[] {x, z});
				case "chunk" -> {
				  chunksToTrustPlayerIn = List.of(new int[]{x, z});
				  container.set(new NamespacedKey(plugin, trustedPlayer.getUniqueId() + "_is_trusted"), PersistentDataType.BOOLEAN, true);
				}
			  }

			  for (ClaimedChunk claimedChunk : claimedChunks) {
				assert chunksToTrustPlayerIn != null;

				boolean contains = false;
				for (int[] chunk : chunksToTrustPlayerIn) {
				  if (chunk[0] == claimedChunk.getX() && chunk[1] == claimedChunk.getZ()) {
					contains = true;
				  }
				}

				if (!contains) {
				  continue;
				}

				ArrayList<UUID> trustedPlayers = claimedChunk.getTrustedPlayers();
				UUID trustedUuid = trustedPlayer.getUniqueId();

				if (trustedPlayers.contains(trustedUuid)) {
				  continue;
				}

				trustedPlayers.add(trustedUuid);
			  }

			  messageManager.sendMessage(player, Message.PLAYER_SUCCESSFULLY_TRUSTED);
			})
			.register();
  }

  @Nullable
  private ArrayList<int[]> getChunksToTrustPlayerIn(@NotNull World world, UUID claimsOwnerUuid, UUID playerToTrustUuid, @NotNull List<int[]> currentChunksToTrustPlayerIn, BlockFace currentDirection, @NotNull int[] previousChunk) {
	int newChunkX = previousChunk[0] + (currentDirection == BlockFace.EAST ? 1 : currentDirection == BlockFace.WEST ? -1 : 0);
	int newChunkZ = previousChunk[1] + (currentDirection == BlockFace.SOUTH ? 1 : currentDirection == BlockFace.NORTH ? -1 : 0);

	int[] currentChunk = new int[] {newChunkX, newChunkZ};

	if (currentChunksToTrustPlayerIn.contains(currentChunk)) {
	  return null;
	}

	Chunk newChunk = world.getChunkAt(newChunkX, newChunkZ, false);
	PersistentDataContainer chunkInfo = newChunk.getPersistentDataContainer();
	String chunkOwnerUuidString = chunkInfo.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

	if (chunkOwnerUuidString == null) {
	  return null;
	}

	UUID chunkOwnerUuid = UUID.fromString(chunkOwnerUuidString);
	if (!chunkOwnerUuid.equals(claimsOwnerUuid)) {
	  return null;
	}

	NamespacedKey playerIsTrustedKey = new NamespacedKey(plugin, playerToTrustUuid + "_is_trusted");
	Boolean playerIsTrusted = chunkInfo.get(playerIsTrustedKey, PersistentDataType.BOOLEAN);
	if (playerIsTrusted != null) {
	  return null;
	}

	ArrayList<int[]> claimsToTrustPlayerIn = new ArrayList<>();
	claimsToTrustPlayerIn.add(currentChunk);
	chunkInfo.set(playerIsTrustedKey, PersistentDataType.BOOLEAN, true);

	List<BlockFace> directionsToSearchIn = new LinkedList<>(Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST));

	if (currentDirection != null) {
	  directionsToSearchIn.remove(currentDirection.getOppositeFace());
	}

	for (BlockFace blockFace : directionsToSearchIn) {
	  ArrayList<int[]> chunksToAdd = getChunksToTrustPlayerIn(world, claimsOwnerUuid, playerToTrustUuid, currentChunksToTrustPlayerIn, blockFace, currentChunk);

	  if (chunksToAdd != null) {
		claimsToTrustPlayerIn.addAll(chunksToAdd);
	  }
	}

	return claimsToTrustPlayerIn;
  }
}
