package org.esoteric.tss.minecraft.plugins.survival.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.esoteric.tss.minecraft.plugins.core.data.player.Message;
import org.esoteric.tss.minecraft.plugins.core.managers.MessageManager;
import org.esoteric.tss.minecraft.plugins.survival.TSSSurvivalPlugin;
import org.esoteric.tss.minecraft.plugins.survival.menus.TradeMenu;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TradeCommand {

  public TradeCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("trade")
			.withArguments(new PlayerArgument("player"))
			.executesPlayer((Player player, CommandArguments args) -> {
			  Player target = (Player) args.get("player");

			  MessageManager messageManager = plugin.getCore().getMessageManager();
			  UUID playerUuid = player.getUniqueId();

			  if (target.getUniqueId().equals(playerUuid)) {
				messageManager.sendMessage(player, Message.CANT_TRADE_WITH_YOURSELF);
				return;
			  }

			  UUID targetUuid = target.getUniqueId();

			  Map<UUID, UUID> outGoingTradeRequests = plugin.getOutGoingTradeRequests();
			  if (outGoingTradeRequests.containsKey(playerUuid) && outGoingTradeRequests.get(playerUuid).equals(targetUuid)) {
				new TradeMenu(target, player, plugin);
			  } else {
				outGoingTradeRequests.put(targetUuid, playerUuid);
				messageManager.sendMessage(player, Message.SENT_TRADE_REQUEST, target.getName());
			  }
			})
			.register();
  }
}
