package net.slqmy.tss_survival.command.claim;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GiveClaimMapCommand {

  public GiveClaimMapCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("give-claim-map")
			.executesPlayer((Player player, CommandArguments args) -> {
			  ItemStack map = new ItemStack(Material.FILLED_MAP);
			  MapMeta meta = (MapMeta) map.getItemMeta();

			  PersistentDataContainer container = meta.getPersistentDataContainer();
			  container.set(new NamespacedKey(plugin, "is_claim_map"), PersistentDataType.BOOLEAN, true);

			  MapView view = Bukkit.createMap(player.getWorld());

			  Location location = player.getLocation();
			  view.setCenterX(location.getBlockX());
			  view.setCenterZ(location.getBlockZ());

			  view.setScale(MapView.Scale.CLOSEST);

			  view.setTrackingPosition(true);
			  view.setUnlimitedTracking(true);

			  meta.setMapView(view);

			  map.setItemMeta(meta);
			  player.getInventory().addItem(map);
			})
			.register();
  }
}
