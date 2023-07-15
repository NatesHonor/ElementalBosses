package com.nate.boss.commands;

import java.io.File;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.nate.boss.Boss;

public class ListBossesCommand implements CommandExecutor {

    @SuppressWarnings("unused")
	private Boss plugin;
    private YamlConfiguration dataConfig;
    private File dataFile;

    public ListBossesCommand(Boss boss) {
        this.plugin = boss;
        this.dataFile = new File(boss.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ConfigurationSection bossesSection = dataConfig.getConfigurationSection("bosses");

            if (bossesSection == null) {
                player.sendMessage("No bosses found.");
                return true;
            }

            Set<String> bossNames = bossesSection.getKeys(false);
            player.sendMessage("Bosses:");

            for (String bossName : bossNames) {
                player.sendMessage("- " + bossName);
            }
        }

        return true;
    }
}