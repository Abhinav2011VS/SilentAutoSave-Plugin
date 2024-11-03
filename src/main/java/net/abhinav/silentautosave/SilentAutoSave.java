package net.abhinav.silentautosave;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SilentAutoSave extends JavaPlugin implements Listener {

    private int saveInterval;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        // Suppress console output
        suppressConsoleOutput();

        // Auto Save and Backup
        new BukkitRunnable() {
            @Override
            public void run() {
                executeSaveAllSilently();
            }
        }.runTaskTimer(this, saveInterval * 20L, saveInterval * 20L);
    }

    private void loadConfig() {
        saveInterval = getConfig().getInt("save-interval", 60);
    }

    private void suppressConsoleOutput() {
        try {
            // Redirect System.out to a dummy stream to suppress console output
            PrintStream dummyPrintStream = new PrintStream(Files.newOutputStream(Path.of("dummy.log"), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            System.setOut(dummyPrintStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeSaveAllSilently() {
        try {
            // Use reflection to execute the save-all command silently
            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit.v1_21_R1.CraftServer");
            Object craftServer = craftServerClass.cast(Bukkit.getServer());
            Method saveMethod = craftServerClass.getMethod("savePlayers");
            saveMethod.invoke(craftServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
