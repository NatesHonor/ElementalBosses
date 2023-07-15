package com.nate.boss.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.nate.boss.Boss;

import java.io.File;
import java.io.IOException;

public class CreateBossCommand implements CommandExecutor {

    @SuppressWarnings("unused")
	private Boss plugin;
    private YamlConfiguration dataConfig;
    private File dataFile;

    public CreateBossCommand(Boss boss) {
        this.plugin = boss;
        this.dataFile = new File(boss.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 4) {
                player.sendMessage("Invalid command format. Usage: /createboss bossname entityname health attack");
                return true;
            }

            String bossName = args[1];
            String entityName = args[2];
            int health = Integer.parseInt(args[3]);
            double attackDamage = Double.parseDouble(args[4]);

            EntityType entityType = EntityType.fromName(entityName.toUpperCase());

            if (entityType == null) {
                player.sendMessage("Invalid entity name: " + entityName);
                return true;
            }

            // Save the boss data to the YAML file
            dataConfig.set("bosses." + bossName + ".entity", entityName);
            dataConfig.set("bosses." + bossName + ".health", health);
            dataConfig.set("bosses." + bossName + ".attack", attackDamage);
            dataConfig.set("bosses." + bossName + ".items", "diamond");

            try {
                dataConfig.save(dataFile);
                player.sendMessage("Boss created: " + bossName);
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage("Failed to create boss.");
            }
        }

        return true;
    }
}
