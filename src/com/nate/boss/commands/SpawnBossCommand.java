package com.nate.boss.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.nate.boss.Boss;

public class SpawnBossCommand implements CommandExecutor, Listener {

    private Boss plugin;
    private YamlConfiguration dataConfig;
    private File dataFile;
    private Map<UUID, BossBar> bossBars;
    
    public SpawnBossCommand(Boss boss) {
        this.plugin = boss;
        this.dataFile = new File(boss.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.bossBars = new HashMap<>();
    }
    
    public Map<UUID, BossBar> getBossBars() {
        return bossBars;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {
                player.sendMessage("Invalid command format. Usage: /spawnboss bossname");
                return true;
            }

            String bossName = args[1];
            ConfigurationSection bossesSection = dataConfig.getConfigurationSection("bosses");

            if (bossesSection == null || !bossesSection.contains(bossName)) {
                player.sendMessage("Boss not found: " + bossName);
                return true;
            }

            ConfigurationSection bossSection = bossesSection.getConfigurationSection(bossName);
            String entityName = bossSection.getString("entity");
            int health = bossSection.getInt("health");
            double attackDamage = bossSection.getDouble("attack");
            ConfigurationSection itemsSection = bossSection.getConfigurationSection("items");
            List<ItemStack> items = new ArrayList<>();

            if (itemsSection != null) {
                for (String key : itemsSection.getKeys(false)) {
                    ItemStack item = itemsSection.getItemStack(key);

                    if (item != null) {
                        items.add(item);
                    }
                }
            }
            EntityType entityType = EntityType.fromName(Objects.requireNonNull(entityName).toUpperCase());

            if (entityType == null) {
                player.sendMessage("Invalid entity name for boss: " + bossName);
                return true;
            }

            LivingEntity boss = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), entityType);
            boss.setCustomName(bossName);
            boss.setCustomNameVisible(true);
            boss.setMaxHealth(health);
            boss.setHealth(health);

            boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3); // Example: Set movement speed
            boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage); // Example: Set attack damage

            boss.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            boss.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            boss.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            boss.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
            boss.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

            BossBar bossBar = Bukkit.createBossBar("Boss Health", BarColor.RED, BarStyle.SOLID);
            bossBar.setProgress(boss.getHealth() / boss.getMaxHealth());
            bossBar.addPlayer(player);

            bossBars.put(boss.getUniqueId(), bossBar);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                bossBar.setProgress(boss.getHealth() / boss.getMaxHealth());
            }, 0L, 20L);

            player.sendMessage("Boss spawned: " + bossName);
        }

        return true;
    }
    
    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        if (event.getEntity().getCustomName() != null) {
            String bossName = event.getEntity().getCustomName();
            ConfigurationSection bossesSection = dataConfig.getConfigurationSection("bosses");

            if (bossesSection != null && bossesSection.contains(bossName)) {
                ConfigurationSection bossSection = bossesSection.getConfigurationSection(bossName);
                ConfigurationSection itemsSection = bossSection.getConfigurationSection("items");
                if (itemsSection != null) {
                    for (String key : itemsSection.getKeys(false)) {
                        ItemStack item = itemsSection.getItemStack(key);

                        if (item != null) {
                            event.getDrops().add(item);
                        }
                    }
                }
            }
            
            BossBar bossBar = this.getBossBars().remove(event.getEntity().getUniqueId());
            if (bossBar != null) {
                bossBar.removeAll();
            }
        }
    }
}
