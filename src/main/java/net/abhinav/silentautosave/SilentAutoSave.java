package net.abhinav.silentautosave;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SilentAutoSave extends JavaPlugin {

    private int saveInterval;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        suppressConsoleOutput();
        startAutoSave();
    }

    private void loadConfig() {
        saveInterval = getConfig().getInt("save-interval", 60);
    }

    private void suppressConsoleOutput() {
        try {
            PrintStream dummyPrintStream = new PrintStream(Files.newOutputStream(Path.of("dummy.log"), StandardOpenOption.CREATE, StandardOpenOption.APPEND));
            System.setOut(dummyPrintStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeSaveAllSilently();
            }
        }.runTaskTimer(this, saveInterval * 20L, saveInterval * 20L);
    }

    private void executeSaveAllSilently() {
        try {
            // Use reflection to execute the save-all flush command silently
            Method saveAllMethod = Bukkit.getServer().getClass().getMethod("savePlayers");
            saveAllMethod.invoke(Bukkit.getServer());

            // Optionally, if you want to flush to disk, you may need to invoke the saveWorlds method or similar
            Method flushMethod = Bukkit.getServer().getClass().getMethod("saveWorlds");
            flushMethod.invoke(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
