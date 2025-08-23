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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.metadata.FixedMetadataValue;

public class AutoTNT extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private double explosionPower;
    private int fuseTicks;
    private boolean causeFire;
    private boolean causeDamage;

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();
        reloadPluginConfig();
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AutoTNT 插件已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("AutoTNT 插件已禁用！");
    }

    // 重新加载配置
    private void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();
        explosionPower = config.getDouble("explosion-power", 4.0);
        fuseTicks = config.getInt("fuse-ticks", 40);
        causeFire = config.getBoolean("cause-fire", false);
        causeDamage = config.getBoolean("cause-damage", true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // 检查放置的是TNT且玩家有权限
        if (block.getType() == Material.TNT && player.hasPermission("autotnt.use")) {
            Location loc = block.getLocation().add(0.5, 0, 0.5); // 中心位置
            World world = loc.getWorld();

            // 取消方块放置并生成点燃的TNT
            event.setCancelled(true);
            block.setType(Material.AIR);

            // 生成TNT实体
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(loc, EntityType.TNT);
            tnt.setFuseTicks(fuseTicks); // 设置自定义引信时间
            tnt.setMetadata("AutoTNT", new FixedMetadataValue(this, true)); // 添加元数据标记
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        // 检查是否为插件生成的TNT
        if (event.getEntity() instanceof TNTPrimed && event.getEntity().hasMetadata("AutoTNT")) {
            event.setRadius((float) explosionPower); // 设置爆炸威力
            event.setFire(causeFire); // 设置是否产生火焰
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // 检查是否为插件生成的TNT
        if (event.getEntity() instanceof TNTPrimed && event.getEntity().hasMetadata("AutoTNT")) {
            if (!causeDamage) {
                event.setCancelled(true); // 取消爆炸伤害
                event.getLocation().getWorld().createExplosion(
                        event.getLocation(),
                        (float) explosionPower,
                        causeFire,
                        false // 不破坏方块
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