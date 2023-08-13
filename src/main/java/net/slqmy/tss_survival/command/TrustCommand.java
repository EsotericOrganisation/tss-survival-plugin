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
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class TrustCommand {

  public TrustCommand(TSSSurvivalPlugin plugin) {
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
			  if (ownerUuidString == null) {
				messageManager.sendMessage(player, Message.CHUNK_NOT_CLAIMED);
				return;
			  }

			  UUID ownerUuid = UUID.fromString(ownerUuidString);

			  if (!ownerUuid.equals(player.getUniqueId())) {
				messageManager.sendMessage(player, Message.NOT_YOUR_CHUNK);
				return;
			  }

			  Player trustedPlayer = (Player) args.get("player");
			  assert trustedPlayer != null;

			  if (trustedPlayer.getUniqueId().equals(player.getUniqueId())) {
				messageManager.sendMessage(player, Message.CANT_TRUST_YOURSELF);
				return;
			  }

			  PlayerProfile profile = core.getPlayerManager().getProfile(player);

			  int x = currentChunk.getX();
			  int z = currentChunk.getZ();

			  ArrayList<ClaimedChunk> claimedChunks = profile.getSurvivalData().getClaims().get(player.getWorld().getName());
			  for (ClaimedChunk claimedChunk : claimedChunks) {
				if (claimedChunk.getX() == x && claimedChunk.getZ() == z) {
				  ArrayList<UUID> trustedPlayers = claimedChunk.getTrustedPlayers();
				  UUID trustedUuid = trustedPlayer.getUniqueId();

				  if (trustedPlayers.contains(trustedUuid)) {
					messageManager.sendMessage(player, Message.PLAYER_ALREADY_TRUSTED);
					return;
				  }

				  trustedPlayers.add(trustedUuid);
				  messageManager.sendMessage(player, Message.PLAYER_SUCCESSFULLY_TRUSTED);
				}
			  }
			})
			.register();
  }
}
