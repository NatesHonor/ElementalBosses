package com.nate.boss;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.nate.boss.commands.CreateBossCommand;
import com.nate.boss.commands.ListBossesCommand;
import com.nate.boss.commands.SpawnBossCommand;

public class Boss extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("boss").setExecutor(this);

        }

    @Override
    public void onDisable() {
    }
    

    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("boss")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "Available commands:");
                sender.sendMessage(ChatColor.GOLD + "- create: Create a new boss");
                sender.sendMessage(ChatColor.GOLD + "- list: List all bosses");
                sender.sendMessage(ChatColor.GOLD + "- foobar: foobar123");
                sender.sendMessage(ChatColor.GOLD + "- foobar: foobar123");
                return true;
            } else if (args.length >= 1) {
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "create":
                        if (args.length >= 4) {
                            CreateBossCommand bossCommand = new CreateBossCommand(this);
                            bossCommand.onCommand(sender, command, label, args);
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /boss create <name> <entity_name> <health> <attack>");
                        }
                        break;
                    case "list":
                    	if (args.length >= 1) {
                    		ListBossesCommand listCommand = new ListBossesCommand(this);
                    		listCommand.onCommand(sender, command, label, args);
                    		return true;
                    	}
                    	break;
                    case "spawn":
                    	if (args.length >= 2) {
                    		SpawnBossCommand spawnCommand = new SpawnBossCommand(this);
                    		spawnCommand.onCommand(sender, command, label, args);
                    	} else {
                    		sender.sendMessage(ChatColor.RED + "Usage: /boss spawn <name>");
                    	}
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown command: " + subCommand);
                        break;
                }
                return true;
            }
        }
        return false;
    } 
}
