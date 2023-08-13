package net.slqmy.tss_survival;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_ranks.TSSRanksPlugin;
import net.slqmy.tss_survival.command.ClaimCommand;
import net.slqmy.tss_survival.command.SkillsCommand;
import net.slqmy.tss_survival.command.TrustCommand;
import net.slqmy.tss_survival.command.UnTrustCommand;
import net.slqmy.tss_survival.listener.ClaimListener;
import net.slqmy.tss_survival.listener.ConnectionListener;
import net.slqmy.tss_survival.listener.OreMineListener;
import net.slqmy.tss_survival.listener.SkillsListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

	new ClaimCommand(this);
	new TrustCommand(this);
	new UnTrustCommand(this);

	new SkillsCommand();

	PluginManager pluginManager = Bukkit.getPluginManager();
	pluginManager.registerEvents(new ConnectionListener(this), this);
	pluginManager.registerEvents(new ClaimListener(this), this);
	pluginManager.registerEvents(new SkillsListener(this), this);
	pluginManager.registerEvents(new OreMineListener(this), this);
  }
}
