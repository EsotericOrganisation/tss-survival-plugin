package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.ClaimedChunk;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.entity.Player;

public class ClaimsListCommand {

  public ClaimsListCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("claims-list")
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
			.register();
  }
}
