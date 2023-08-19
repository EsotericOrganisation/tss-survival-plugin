package net.slqmy.tss_survival.listener;

import net.kyori.adventure.text.TextComponent;
import net.minecraft.ChatFormatting;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.map.MapClaimRenderer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurvivalConnectionListener implements Listener {

  private final Map<UUID, BukkitTask> claimMessageTasks = new HashMap<>();
  private final Map<UUID, BukkitTask> claimMapUpdateTasks = new HashMap<>();

  private final TSSSurvivalPlugin plugin;

  public SurvivalConnectionListener(TSSSurvivalPlugin plugin) {
	this.plugin = plugin;
  }

  @EventHandler
  public void onJoinSurvival(@NotNull PlayerTeleportEvent event) {
	Player player = event.getPlayer();

	List<String> survivalWorlds = plugin.getCore().getSurvivalWorldNames();
	Location from = event.getFrom();
	Location to = event.getTo();

	if (!survivalWorlds.contains(to.getWorld().getName()) || survivalWorlds.contains(from.getWorld().getName())) {
	  return;
	}

	Scoreboard scoreboard = player.getScoreboard();
	TextComponent component = plugin.getCore().getMessageManager().getPlayerMessage(Message.SURVIVAL_SCOREBOARD_TITLE, player);

	Objective objective = scoreboard.registerNewObjective("survival_scoreboard", Criteria.DUMMY, component);
	objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	objective.displayName(component);

	Team currentClaim = scoreboard.registerNewTeam("current_claim");
	currentClaim.addEntry(ChatFormatting.RESET.toString());

	objective.getScore(ChatFormatting.RESET.toString()).setScore(1);

	BukkitTask claimMessageTask = Bukkit.getScheduler().runTaskTimer(
			plugin, new Runnable() {

			  private String chunkOwnerName;

			  @Override
			  public void run() {
				Chunk playerChunk = player.getChunk();
				PersistentDataContainer container = playerChunk.getPersistentDataContainer();
				String ownerChunkUuid = container.get(new NamespacedKey(plugin, "chunk_claim_owner"), PersistentDataType.STRING);

				MessageManager messageManager = plugin.getCore().getMessageManager();

				String newChunkOwnerName;
				TextComponent message;
				if (ownerChunkUuid == null) {
				  message = messageManager.getPlayerMessage(Message.WILDERNESS, player);
				  newChunkOwnerName = "Wilderness";
				} else {
				  UUID ownerUuid = UUID.fromString(ownerChunkUuid);
				  OfflinePlayer chunkOwner = Bukkit.getOfflinePlayer(ownerUuid);

				  newChunkOwnerName = chunkOwner.getName();
				  message = messageManager.getPlayerMessage(Message.PLAYER_CLAIM, player, newChunkOwnerName);
				}

				if (newChunkOwnerName.equals(chunkOwnerName)) {
				  return;
				}

				chunkOwnerName = newChunkOwnerName;

				player.sendActionBar(message);
				currentClaim.suffix(message);
			  }
			},
			0L,
			30L
	);

	Chunk playerChunk = player.getChunk();

	BukkitTask claimMapUpdateTask = new BukkitRunnable() {

	  int chunkX = playerChunk.getX();
	  int chunkZ = playerChunk.getZ();

	  @Override
	  public void run() {
		PlayerInventory inventory = player.getInventory();
		ItemStack heldItem = inventory.getItemInMainHand();

		if (heldItem.getType() != Material.FILLED_MAP) {
		  heldItem = inventory.getItemInOffHand();

		  if (heldItem.getType() != Material.FILLED_MAP) {
			return;
		  }
		}

		MapMeta meta = (MapMeta) heldItem.getItemMeta();
		PersistentDataContainer container = meta.getPersistentDataContainer();

		Boolean isClaimMap = container.get(new NamespacedKey(plugin, "is_claim_map"), PersistentDataType.BOOLEAN);

		if (isClaimMap == null) {
		  return;
		}

		Chunk newChunk = player.getChunk();
		if (newChunk.getX() == chunkX && newChunk.getZ() == chunkZ) {
		  return;
		}

		chunkX = newChunk.getX();
		chunkZ = newChunk.getZ();

		World world = player.getWorld();
		MapView newView = Bukkit.createMap(world);

		Location center = newChunk.getBlock(8, 0, 8).getLocation();
		newView.setCenterX(center.getBlockX());
		newView.setCenterZ(center.getBlockZ());

		newView.setScale(MapView.Scale.CLOSEST);

		newView.setTrackingPosition(true);
		newView.setUnlimitedTracking(true);

		boolean contains = false;
		for (MapRenderer renderer : newView.getRenderers()) {
		  if (renderer instanceof MapClaimRenderer) {
			contains = true;
			break;
		  }
		}

		if (!contains) {
		  newView.addRenderer(new MapClaimRenderer(plugin));
		}

		meta.setMapView(newView);
		heldItem.setItemMeta(meta);
	  }
	}.runTaskTimer(plugin, 0L, 30L);

	UUID playerUuid = player.getUniqueId();
	claimMessageTasks.put(playerUuid, claimMessageTask);
	claimMapUpdateTasks.put(playerUuid, claimMapUpdateTask);
  }

  @EventHandler
  public void onSurvivalQuit(@NotNull PlayerTeleportEvent event) {
	List<String> survivalWorlds = plugin.getCore().getSurvivalWorldNames();
	Location to = event.getTo();

	if (survivalWorlds.contains(to.getWorld().getName())) {
	  return;
	}

	handlePlayerSurvivalQuit(event);
  }

  private void handlePlayerSurvivalQuit(@NotNull PlayerEvent event) {
	Player player = event.getPlayer();

	if (!plugin.getCore().getSurvivalWorldNames().contains(player.getWorld().getName())) {
	  return;
	}

	UUID playerUuid = player.getUniqueId();

	BukkitTask task = claimMessageTasks.get(playerUuid);
	if (task == null) {
	  return;
	}

	task.cancel();
	claimMessageTasks.remove(playerUuid);

	claimMapUpdateTasks.get(playerUuid).cancel();
	claimMapUpdateTasks.remove(playerUuid);

	player.getScoreboard().getObjective("survival_scoreboard").unregister();
  }

  @EventHandler
  public void onSurvivalQuit(@NotNull PlayerQuitEvent event) {
	handlePlayerSurvivalQuit(event);
  }
}
