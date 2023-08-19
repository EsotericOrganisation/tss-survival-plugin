package net.slqmy.tss_survival.command.claim;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.slqmy.tss_core.datatype.Colour;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ClaimMapCommand {

  public ClaimMapCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("claim-map")
			.executesPlayer((Player player, CommandArguments args) -> {
			  BlockFace facing = player.getFacing();

			  int xDirection = 0;
			  int zDirection = 0;

			  switch (facing) {
				case SOUTH -> {
				  xDirection = 1;
				  zDirection = 1;
				}
				case WEST -> {
				  xDirection = -1;
				  zDirection = 1;
				}
				case NORTH -> {
				  xDirection = -1;
				  zDirection = -1;
				}
				case EAST -> {
				  xDirection = 1;
				  zDirection = -1;
				}
			  }

			  UUID playerUuid = player.getUniqueId();

			  World world = player.getWorld();
			  Chunk playerChunk = player.getChunk();
			  int x0 = playerChunk.getX();
			  int z0 = playerChunk.getZ();

			  DebugUtil.log("x0: " + x0);
			  DebugUtil.log("z0: " + z0);

			  DebugUtil.log("X Direction: " + xDirection);
			  DebugUtil.log("Z Direction: " + zDirection);

			  int startX = x0 - xDirection * 5;
			  int startZ = z0 - zDirection * 5;

			  for (int x = x0 + xDirection * 5; xDirection == 1 ? x > startX : x < startX; x -= xDirection) {
				TextComponent row = Component.text("");

				for (int z = z0 + zDirection * 5; zDirection == 1 ? z > startZ : z < startZ; z -= zDirection) {
				  TextComponent chunkText = Component.text("■", Colour.GREEN);
				  Chunk chunk = world.getChunkAt(x, z, false);
				  PersistentDataContainer container = chunk.getPersistentDataContainer();

				  String ownerUuidString = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);
				  if (ownerUuidString != null) {
					UUID ownerUuid = UUID.fromString(ownerUuidString);

					if (ownerUuid.equals(playerUuid)) {
					  chunkText = chunkText.color(Colour.SLIME);
					} else {
					  Boolean isTrusted = container.get(new NamespacedKey(plugin, playerUuid + "_is_trusted"), PersistentDataType.BOOLEAN);

					  if (isTrusted == null) {
						chunkText = chunkText.color(Colour.BANANA);
					  } else {
						chunkText = chunkText.color(Colour.LIGHT_BLUE);
					  }
					}
				  }

				  row = row.append(chunkText);
				}

				player.sendMessage(row);
			  }
			})
			.register();
  }
}
