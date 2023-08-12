package net.slqmy.tss_survival;

import net.slqmy.tss_core.TSSCorePlugin;
import net.slqmy.tss_ranks.TSSRanksPlugin;
import net.slqmy.tss_survival.command.ClaimCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TSSSurvivalPlugin extends JavaPlugin {

  TSSCorePlugin core = (TSSCorePlugin) Bukkit.getPluginManager().getPlugin("TSS-Core");

  TSSRanksPlugin ranksPlugin = (TSSRanksPlugin) Bukkit.getPluginManager().getPlugin("TSS-Ranks");

  public TSSRanksPlugin getRanksPlugin() {
    return ranksPlugin;
  }

  public TSSCorePlugin getCore() {
    return core;
  }

  @Override
  public void onEnable() {
    new ClaimCommand(this);
  }
}
