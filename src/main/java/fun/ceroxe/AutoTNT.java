package fun.ceroxe;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.metadata.FixedMetadataValue;

public class AutoTNT extends JavaPlugin implements Listener {

    private double explosionPower;
    private int fuseTicks;
    private boolean causeFire;
    private boolean causeDamage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AutoTNT 插件已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoTNT 插件已禁用！");
    }

    private void reloadPluginConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();
        explosionPower = config.getDouble("explosion-power", 4.0);
        fuseTicks = config.getInt("fuse-ticks", 40);
        causeFire = config.getBoolean("cause-fire", false);
        causeDamage = config.getBoolean("cause-damage", true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.TNT && player.hasPermission("autotnt.use")) {
            Location loc = block.getLocation().add(0.5, 0, 0.5);
            World world = loc.getWorld();

            event.setCancelled(true); // 取消方块放置

            // 修复：减少玩家手持物品的数量
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.TNT) {
                // 非创造模式才减少物品
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    int amount = itemInHand.getAmount();
                    if (amount > 1) {
                        itemInHand.setAmount(amount - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                }
            }

            // 生成点燃的TNT
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.TNT);
            tnt.setFuseTicks(fuseTicks);
            tnt.setMetadata("AutoTNT", new FixedMetadataValue(this, true));
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed && event.getEntity().hasMetadata("AutoTNT")) {
            event.setRadius((float) explosionPower);
            event.setFire(causeFire);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed && event.getEntity().hasMetadata("AutoTNT")) {
            if (!causeDamage) {
                event.setCancelled(true);
                event.getLocation().getWorld().createExplosion(
                        event.getLocation(),
                        (float) explosionPower,
                        causeFire,
                        false
                );
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("autotnt") && args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("autotnt.reload")) {
                    reloadPluginConfig();
                    sender.sendMessage(ChatColor.GREEN + "AutoTNT 配置已重新加载！");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令！");
                    return false;
                }
            }
        }
        return false;
    }
}