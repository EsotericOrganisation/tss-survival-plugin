package net.slqmy.tss_survival.menu;

import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.util.InventoryUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TradeMenu {

  private final TSSSurvivalPlugin plugin;

  public TradeMenu(@NotNull Player tradesMan, @NotNull Player merchant, @NotNull TSSSurvivalPlugin plugin) {
	this.plugin = plugin;

	tradesMan.openInventory(createTradeInventory(tradesMan));
	merchant.openInventory(createTradeInventory(merchant));
  }

  @NotNull
  private Inventory createTradeInventory(Player trader) {
	Inventory tradeInventory = Bukkit.createInventory(null, 54, plugin.getCore().getMessageManager().getPlayerMessage(Message.TRADING_GUI_TITLE, trader));

	for (int i = 4; i <= 49; i += 9) {
	  tradeInventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
	}

	InventoryUtil.makeStatic(tradeInventory, plugin.getCore());
	return tradeInventory;
  }
}
