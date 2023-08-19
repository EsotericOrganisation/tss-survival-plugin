package net.slqmy.tss_survival.listener;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.menu.TradeMenu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TradeListener implements Listener {

  private final TSSSurvivalPlugin plugin;

  public TradeListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onTrade(@NotNull PlayerInteractAtEntityEvent event) {
	Player player = event.getPlayer();

	if (!player.isSneaking()) {
	  return;
	}

	Entity clicked = event.getRightClicked();

	if (!(clicked instanceof Player target)) {
	  return;
	}

	UUID playerUuid = player.getUniqueId();
	UUID targetUuid = target.getUniqueId();

	Map<UUID, UUID> outGoingTradeRequests = plugin.getOutGoingTradeRequests();

	MessageManager messageManager = plugin.getCore().getMessageManager();

	if (outGoingTradeRequests.containsKey(playerUuid) && outGoingTradeRequests.get(playerUuid).equals(targetUuid)) {
	  new TradeMenu(target, player, plugin);
	} else {
	  outGoingTradeRequests.put(targetUuid, playerUuid);
	  messageManager.sendMessage(player, Message.SENT_TRADE_REQUEST, target.getName());
	}
  }
}
