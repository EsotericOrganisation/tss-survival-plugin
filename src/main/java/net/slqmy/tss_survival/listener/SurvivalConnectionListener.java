package net.slqmy.tss_survival.listener;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.ChatFormatting;
import net.slqmy.tss_core.datatype.player.Message;
import net.slqmy.tss_core.manager.MessageManager;
import net.slqmy.tss_core.util.DebugUtil;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import org.bukkit.*;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurvivalConnectionListener implements Listener {

  private final Map<UUID, BukkitTask> claimMessageTasks = new HashMap<>();

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

	DebugUtil.log("FROM: " + from.getWorld().getName());
	DebugUtil.log("TO: " + to.getWorld().getName());

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

	claimMessageTasks.put(player.getUniqueId(), claimMessageTask);
  }

  @EventHandler
  public void onSurvivalQuit(@NotNull PlayerTeleportEvent event) {
	List<String> survivalWorlds = plugin.getCore().getSurvivalWorldNames();
	Location to = event.getTo();

	DebugUtil.log("TO: " + to.getWorld().getName());

	if (survivalWorlds.contains(to.getWorld().getName())) {
	  return;
	}

	handlePlayerSurvivalQuit(event);
  }

  private void handlePlayerSurvivalQuit(@NotNull PlayerEvent event) {
	Player player = event.getPlayer();

	DebugUtil.log("PLAYER WORLD: " + player.getWorld().getName());

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

	player.getScoreboard().getObjective("survival_scoreboard").unregister();
  }

  @EventHandler
  public void onSurvivalQuit(@NotNull PlayerQuitEvent event) {
	handlePlayerSurvivalQuit(event);
  }

  @EventHandler
  public void onPiglinCrossBowCharge(@NotNull EntityLoadCrossbowEvent event) {
	if (!(event.getEntity() instanceof Piglin)) {
	  return;
	}

	ItemStack crossbow = event.getCrossbow();
	CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();

	ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
	FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();

	FireworkEffect orangeExplosion = FireworkEffect.builder()
			.with(FireworkEffect.Type.BALL)
			.withColor(Color.ORANGE)
			.withFade(Color.WHITE)
			.withTrail()
			.build();

	FireworkEffect redExplosion = FireworkEffect.builder()
			.with(FireworkEffect.Type.BALL)
			.withColor(Color.RED)
			.withFade(Color.WHITE)
			.withTrail()
			.build();

	fireworkMeta.addEffect(orangeExplosion);
	fireworkMeta.addEffect(redExplosion);
	fireworkMeta.setPower(1);

	firework.setItemMeta(fireworkMeta);

	meta.setChargedProjectiles(null);
	meta.setChargedProjectiles(List.of(firework));
	crossbow.setItemMeta(meta);
  }
}
