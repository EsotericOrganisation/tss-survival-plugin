package net.slqmy.tss_survival.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import net.slqmy.tss_survival.TSSSurvivalPlugin;
import net.slqmy.tss_survival.menu.SkillsMenu;
import org.bukkit.entity.Player;

public class SkillsCommand {

  public SkillsCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("skills")
			.executesPlayer((Player player, CommandArguments args) -> new SkillsMenu(player, plugin))
			.register();
  }
}
