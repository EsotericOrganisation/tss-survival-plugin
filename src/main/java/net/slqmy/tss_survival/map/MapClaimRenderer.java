package net.slqmy.tss_survival.map;

import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class MapClaimRenderer extends MapRenderer {

  private final TSSSurvivalPlugin plugin;

  public MapClaimRenderer(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @Override
  public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
	int xDirection = -1;
	int zDirection = -1;

	UUID playerUuid = player.getUniqueId();

	int centerX = map.getCenterX();
	int centerZ = map.getCenterZ();

	for (int x = -1; x <= 8; x++) {
	  for (int y = -1; y <= 8; y++) {
		int chunkX = (centerX + xDirection * (64 - x * 16)) / 16;
		int chunkZ = (centerZ + zDirection * (64 - y * 16)) / 16;

		World world = player.getWorld();
		Chunk chunk = world.getChunkAt(chunkX, chunkZ, false);
		PersistentDataContainer container = chunk.getPersistentDataContainer();

		NamespacedKey ownerKey = new NamespacedKey(plugin, "chunk_claim_owner");
		String ownerUuidString = container.get(ownerKey, PersistentDataType.STRING);
		if (ownerUuidString == null) {
		  continue;
		}

		Color color;
		UUID ownerUuid = UUID.fromString(ownerUuidString);

		NamespacedKey trustKey = new NamespacedKey(plugin, playerUuid + "_is_trusted");
		Boolean isTrusted = container.get(trustKey, PersistentDataType.BOOLEAN);

		if (ownerUuid.equals(playerUuid)) {
		  color = Color.GREEN;
		} else {
		  if (isTrusted == null) {
			color = Color.YELLOW;
		  } else {
			color = Color.CYAN;
		  }
		}

		Chunk northChunk = world.getChunkAt(chunkX, chunkZ - 1);
		Chunk northEastChunk = world.getChunkAt(chunkX + 1, chunkZ - 1);

		Chunk eastChunk = world.getChunkAt(chunkX + 1, chunkZ);

		Chunk southEastChunk = world.getChunkAt(chunkX + 1, chunkZ + 1);
		Chunk southChunk = world.getChunkAt(chunkX, chunkZ + 1);
		Chunk southWestChunk = world.getChunkAt(chunkX - 1, chunkZ + 1);

		Chunk westChunk = world.getChunkAt(chunkX - 1, chunkZ);
		Chunk northWestChunk = world.getChunkAt(chunkX - 1, chunkZ - 1);

		Chunk[] chunks = {northChunk, northEastChunk, eastChunk, southEastChunk, southChunk, southWestChunk, westChunk, northWestChunk};

		int length = chunks.length; // length = 8

		int bitField = 0b00000000;
		for (int i = 0; i < length; i += 2) {
		  Chunk orthogonalChunk = chunks[i];
		  Chunk diagonalChunk = chunks[i + 1];
		  Chunk horizontalChunk = chunks[(i + 2) % length];

		  PersistentDataContainer orthogonalChunkContainer = orthogonalChunk.getPersistentDataContainer();
		  PersistentDataContainer diagonalChunkContainer = diagonalChunk.getPersistentDataContainer();
		  PersistentDataContainer horizontalChunkContainer = horizontalChunk.getPersistentDataContainer();

		  String orthogonalChunkOwnerUuidString = orthogonalChunkContainer.get(ownerKey, PersistentDataType.STRING);
		  String diagonalChunkOwnerUuidString = diagonalChunkContainer.get(ownerKey, PersistentDataType.STRING);
		  String horizontalChunkOwnerUuidString = horizontalChunkContainer.get(ownerKey, PersistentDataType.STRING);

		  Boolean orthogonalChunkIsTrusted = orthogonalChunkContainer.get(trustKey, PersistentDataType.BOOLEAN);
		  Boolean diagonalChunkIsTrusted = diagonalChunkContainer.get(trustKey, PersistentDataType.BOOLEAN);
		  Boolean horizontalChunkIsTrusted = horizontalChunkContainer.get(trustKey, PersistentDataType.BOOLEAN);

		  if (Objects.equals(orthogonalChunkOwnerUuidString, horizontalChunkOwnerUuidString) && (!Objects.equals(diagonalChunkOwnerUuidString, orthogonalChunkOwnerUuidString) || !Objects.equals(diagonalChunkIsTrusted, orthogonalChunkIsTrusted))) {
			bitField |= (byte) Math.pow(2, 4.0D + i / 2.0D);
		  }

		  if (ownerUuidString.equals(orthogonalChunkOwnerUuidString) && Objects.equals(isTrusted, orthogonalChunkIsTrusted)) {
			bitField |= (byte) Math.pow(2, i / 2.0D);
		  }

//		  if (ownerUuidString.equals(horizontalChunkOwnerUuidString)) {
//			bitField |= (byte) Math.pow(2, i / 3.0D + 1.0D);
//		  }
		}

		int mapX = x * 16 + 8;
		int mapY = y * 16 - 8;

		byte isNorthConnectedFlag = 0b00000001;
		boolean isNorthConnected = (bitField & isNorthConnectedFlag) != 0;
		if (!isNorthConnected) {
		  for (int x_x = Math.max(mapX, 0); x_x < Math.min(mapX + 16, 128); x_x++) {
			canvas.setPixelColor(x_x, mapY, color);
		  }
		}

		byte isEastConnectedFlag = 0b00000010;
		boolean isEastConnected = (bitField & isEastConnectedFlag) != 0;
		if (!isEastConnected) {
		  for (int y_y = Math.max(mapY, 0); y_y < Math.min(mapY + 16, 128); y_y++) {
			canvas.setPixelColor(mapX + 15, y_y, color);
		  }
		}

		byte isSouthConnectedFlag = 0b00000100;
		boolean isSouthConnected = (bitField & isSouthConnectedFlag) != 0;
		if (!isSouthConnected) {
		  for (int x_x = Math.max(mapX, 0); x_x < Math.min(mapX + 16, 128); x_x++) {
			canvas.setPixelColor(x_x, mapY + 15, color);
		  }
		}

		byte isWestConnectedFlag = 0b00001000;
		boolean isWestConnected = (bitField & isWestConnectedFlag) != 0;
		if (!isWestConnected) {
		  for (int y_y = Math.max(mapY, 0); y_y < Math.min(mapY + 16, 128); y_y++) {
			canvas.setPixelColor(mapX, y_y, color);
		  }
		}

		byte shouldFillTopRightCornerFlag = 0b00010000;
		boolean shouldFillTopRightCorner = (bitField & shouldFillTopRightCornerFlag) != 0;
		if (shouldFillTopRightCorner) {
		  canvas.setPixelColor(mapX + 15, mapY, color);
		}

		byte shouldFillBottomRightCornerFlag = 0b00100000;
		boolean shouldFillBottomRightCorner = (bitField & shouldFillBottomRightCornerFlag) != 0;
		if (shouldFillBottomRightCorner) {
		  canvas.setPixelColor(mapX + 15, mapY + 15, color);
		}

		byte shouldFillBottomLeftCornerFlag = 0b01000000;
		boolean shouldFillBottomLeftCorner = (bitField & shouldFillBottomLeftCornerFlag) != 0;
		if (shouldFillBottomLeftCorner) {
		  canvas.setPixelColor(mapX, mapY + 15, color);
		}

		int shouldFillTopLeftCornerFlag = 0b10000000;
		boolean shouldFillTopLeftCorner = (bitField & shouldFillTopLeftCornerFlag) != 0;
		if (shouldFillTopLeftCorner) {
		  canvas.setPixelColor(mapX, mapY, color);
		}
	  }
	}
  }
}
