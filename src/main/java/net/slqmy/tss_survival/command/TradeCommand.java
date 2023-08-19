package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.menu.TradeMenu;
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
