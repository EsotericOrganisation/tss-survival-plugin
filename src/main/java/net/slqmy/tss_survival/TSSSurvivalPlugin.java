package net.slqmy.tss_survival;

import com.mongodb.client.MongoCursor;
import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_core.database.collection_name.PlayersCollectionName;
import net.slqmy.tss_core.datatype.player.PlayerProfile;
import net.slqmy.tss_core.datatype.player.survival.Claim;
import net.slqmy.tss_ranks.TSSRanksPlugin;
import net.slqmy.tss_survival.command.ClaimCommand;
import net.slqmy.tss_survival.listener.ClaimListener;
import net.slqmy.tss_survival.listener.ConnectionListener;
import net.slqmy.tss_survival.listener.OreMineListener;
import net.slqmy.tss_survival.listener.SkillsListener;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TSSSurvivalPlugin extends JavaPlugin {

  private final TSSCorePlugin core = (TSSCorePlugin) Bukkit.getPluginManager().getPlugin("TSS-Core");

  private final TSSRanksPlugin ranksPlugin = (TSSRanksPlugin) Bukkit.getPluginManager().getPlugin("TSS-Ranks");

  public TSSRanksPlugin getRanksPlugin() {
	return ranksPlugin;
  }

  public TSSCorePlugin getCore() {
	return core;
  }

  @Override
  public void onEnable() {
	YamlConfiguration config = (YamlConfiguration) getConfig();

	config.options().copyDefaults();
	saveDefaultConfig();

	core.getDatabase().getCursor(PlayersCollectionName.PLAYER_PROFILES, (MongoCursor<PlayerProfile> cursor) -> {
              while (cursor.hasNext()) {
                PlayerProfile profile = cursor.next();

                for (Claim claim : profile.getSurvivalData().getClaims()) {
				  World world = Bukkit.getWorld(claim.getWorldName());
				  assert world != null;

				  Chunk chunk = world.getChunkAt(claim.getChunkX(), claim.getChunkZ(), false);
				  PersistentDataContainer container = chunk.getPersistentDataContainer();

				  container.set(new NamespacedKey(this, "chunk_claim_owner"), PersistentDataType.STRING, profile.getUuid().toString());
                }
              }
            },
            PlayerProfile.class
	);

	new ClaimCommand(this);

	PluginManager pluginManager = Bukkit.getPluginManager();
	pluginManager.registerEvents(new ConnectionListener(this), this);
	pluginManager.registerEvents(new ClaimListener(this), this);
	pluginManager.registerEvents(new SkillsListener(this), this);
	pluginManager.registerEvents(new OreMineListener(this), this);
  }
}
