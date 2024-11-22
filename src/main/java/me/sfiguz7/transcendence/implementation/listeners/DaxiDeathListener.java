package me.sfiguz7.transcendence.implementation.listeners;

import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.sfiguz7.transcendence.TranscEndence;
import me.sfiguz7.transcendence.implementation.items.items.Daxi;
import me.sfiguz7.transcendence.implementation.utils.SaveUtils;
import me.sfiguz7.transcendence.lists.TEItems;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.bukkit.event.EventPriority.LOWEST;

public class DaxiDeathListener implements Listener {

    public static final NamespacedKey TINKER_PROTECTION = new NamespacedKey(TranscEndence.getInstance(), "tinker");
    private static final Log log = LogFactory.getLog(DaxiDeathListener.class);

    public DaxiDeathListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        /*
         * Tinker adds a PDC long to players if they have a full set of Daxi gear. This long is 5 seconds
         * worth of Daxi-loss-on-death protection.
         */
        if (PersistentDataAPI.getLong(p, TINKER_PROTECTION) >= System.currentTimeMillis()) {
            return;
        }

        UUID uuid = p.getUniqueId();
        Map<UUID, Set<Daxi.Type>> activePlayers = TranscEndence.getRegistry().getDaxiEffectPlayers();
        if (activePlayers.get(uuid) != null) {
            for (Daxi.Type type : activePlayers.get(uuid)) {
                e.getDrops().add(new CustomItemStack(TEItems.STABLE_BLOCK, 8));
            }
            activePlayers.remove(uuid);

            SaveUtils.writeData();
        }
    }

}