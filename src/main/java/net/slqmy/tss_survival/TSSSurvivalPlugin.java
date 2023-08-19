package net.slqmy.tss_survival;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_ranks.TSSRanksPlugin;
import net.slqmy.tss_survival.command.SkillsCommand;
import net.slqmy.tss_survival.command.TradeCommand;
import net.slqmy.tss_survival.command.claim.*;
import net.slqmy.tss_survival.listener.BlockListener;
import net.slqmy.tss_survival.listener.OreMineListener;
import net.slqmy.tss_survival.listener.SurvivalConnectionListener;
import net.slqmy.tss_survival.listener.TradeListener;
import net.slqmy.tss_survival.listener.claim.ClaimListener;
import net.slqmy.tss_survival.listener.skill.SkillsListener;
import net.slqmy.tss_survival.listener.skill.SkillsMenuListener;
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
	new UnClaimCommand(this);

	new ClaimMapCommand(this);
	new GiveClaimMapCommand(this);

	new TrustCommand(this);
	new UnTrustCommand(this);

	new SkillsCommand(this);

	PluginManager pluginManager = Bukkit.getPluginManager();
	pluginManager.registerEvents(new SurvivalConnectionListener(this), this);
	pluginManager.registerEvents(new ClaimListener(this), this);
	pluginManager.registerEvents(new SkillsListener(this), this);
	pluginManager.registerEvents(new OreMineListener(this), this);
	pluginManager.registerEvents(new SkillsMenuListener(this), this);
	pluginManager.registerEvents(new BlockListener(this), this);
  }
}
