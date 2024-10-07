package org.esoteric_organisation.tss_survival_plugin.listener;

import org.esoteric_organisation.tss_core_plugin.datatype.player.Message;
import org.esoteric_organisation.tss_core_plugin.manager.MessageManager;
import org.esoteric_organisation.tss_survival_plugin.TSSSurvivalPlugin;
import org.esoteric_organisation.tss_survival_plugin.menu.TradeMenu;
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
