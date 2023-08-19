package net.slqmy.tss_survival.util;

import net.slqmy.tss_core.util.DebugUtil;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ClaimUtil {

  @Nullable
  public static List<int[]> getConnectedClaims(NamespacedKey chunkClaimOwnerKey, NamespacedKey trustKey, Player claimOwner, boolean trustPlayer, @NotNull List<int[]> currentChunks, BlockFace currentDirection, @NotNull int[] previousChunk) {
	int newChunkX = previousChunk[0] + (currentDirection == BlockFace.EAST ? 1 : currentDirection == BlockFace.WEST ? -1 : 0);
	int newChunkZ = previousChunk[1] + (currentDirection == BlockFace.SOUTH ? 1 : currentDirection == BlockFace.NORTH ? -1 : 0);

	int[] currentChunk = {newChunkX, newChunkZ};

	for (int[] chunk : currentChunks) {
	  if (chunk[0] == currentChunk[0] && chunk[1] == currentChunk[1]) {
		DebugUtil.log("Chunk already contained!");
		return null;
	  }
	}

	Chunk newChunk = claimOwner.getWorld().getChunkAt(newChunkX, newChunkZ, false);
	PersistentDataContainer chunkInfo = newChunk.getPersistentDataContainer();
	String chunkOwnerUuidString = chunkInfo.get(chunkClaimOwnerKey, PersistentDataType.STRING);

	if (chunkOwnerUuidString == null) {
	  return null;
	}

	UUID chunkOwnerUuid = UUID.fromString(chunkOwnerUuidString);
	UUID claimsOwnerUuid = claimOwner.getUniqueId();
	if (!chunkOwnerUuid.equals(claimsOwnerUuid)) {
	  return null;
	}

	Boolean playerIsTrusted = chunkInfo.get(trustKey, PersistentDataType.BOOLEAN);
	if ((playerIsTrusted != null) == trustPlayer) {
	  return null;
	}

	currentChunks.add(currentChunk);

	if (trustPlayer) {
	  chunkInfo.set(trustKey, PersistentDataType.BOOLEAN, true);
	} else {
	  chunkInfo.remove(trustKey);
	}

	List<BlockFace> directionsToSearchIn = new LinkedList<>(Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST));

	if (currentDirection != null) {
	  directionsToSearchIn.remove(currentDirection.getOppositeFace());
	}

	for (BlockFace blockFace : directionsToSearchIn) {
	  List<int[]> chunksToAdd = getConnectedClaims(chunkClaimOwnerKey, trustKey, claimOwner, trustPlayer, currentChunks, blockFace, currentChunk);

	  if (chunksToAdd != null) {
		currentChunks.addAll(chunksToAdd);
	  }
	}

	return currentChunks;
  }

  @Nullable
  public static List<int[]> getConnectedClaims(NamespacedKey chunkClaimOwnerKey, NamespacedKey trustKey, Player claimOwner, boolean trustPlayer, @NotNull List<int[]> currentChunks, @NotNull int[] previousChunk) {
	return getConnectedClaims(chunkClaimOwnerKey, trustKey, claimOwner, trustPlayer, currentChunks, null, previousChunk);
  }
}
