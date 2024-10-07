package org.esoteric_organisation.tss_survival_plugin;

import org.esoteric_organisation.tss_core_plugin.TSSCorePlugin;
import org.esoteric_organisation.tss_ranks_plugin.TSSRanksPlugin;
import org.esoteric_organisation.tss_survival_plugin.command.SkillsCommand;
import org.esoteric_organisation.tss_survival_plugin.command.TradeCommand;
import org.esoteric_organisation.tss_survival_plugin.command.claim.*;
import org.esoteric_organisation.tss_survival_plugin.listener.BlockListener;
import org.esoteric_organisation.tss_survival_plugin.listener.OreMineListener;
import org.esoteric_organisation.tss_survival_plugin.listener.SurvivalConnectionListener;
import org.esoteric_organisation.tss_survival_plugin.listener.TradeListener;
import org.esoteric_organisation.tss_survival_plugin.listener.claim.ClaimListener;
import org.esoteric_organisation.tss_survival_plugin.listener.skill.SkillsListener;
import org.esoteric_organisation.tss_survival_plugin.listener.skill.SkillsMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class TSSSurvivalPlugin extends JavaPlugin {

  private final TSSCorePlugin core = (TSSCorePlugin) Bukkit.getPluginManager().getPlugin("TSS-Core");

  private final TSSRanksPlugin ranksPlugin = (TSSRanksPlugin) Bukkit.getPluginManager().getPlugin("TSS-Ranks");

  private final HashMap<UUID, UUID> outGoingTradeRequests = new HashMap<>();

  public TSSRanksPlugin getRanksPlugin() {
	return ranksPlugin;
  }

  public TSSCorePlugin getCore() {
	return core;
  }

  public HashMap<UUID, UUID> getOutGoingTradeRequests() {
	return outGoingTradeRequests;
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

	new TradeCommand(this);

	PluginManager pluginManager = Bukkit.getPluginManager();
	pluginManager.registerEvents(new SurvivalConnectionListener(this), this);
	pluginManager.registerEvents(new ClaimListener(this), this);
	pluginManager.registerEvents(new SkillsListener(this), this);
	pluginManager.registerEvents(new OreMineListener(this), this);
	pluginManager.registerEvents(new SkillsMenuListener(this), this);
	pluginManager.registerEvents(new BlockListener(this), this);
	pluginManager.registerEvents(new TradeListener(this), this);
  }
}
