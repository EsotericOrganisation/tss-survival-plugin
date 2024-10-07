package org.esoteric_organisation.tss_survival_plugin.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.esoteric_organisation.tss_survival_plugin.TSSSurvivalPlugin;
import org.esoteric_organisation.tss_survival_plugin.menu.skill.SkillsMenu;
import org.bukkit.entity.Player;

public class SkillsCommand {

  public SkillsCommand(TSSSurvivalPlugin plugin) {
	new CommandAPICommand("skills")
			.executesPlayer((Player player, CommandArguments args) -> new SkillsMenu(player, plugin))
			.register();
  }
}
