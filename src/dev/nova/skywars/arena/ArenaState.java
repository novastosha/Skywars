package dev.nova.skywars.arena;

import org.bukkit.ChatColor;

public enum ArenaState {

    WAITING(ChatColor.GREEN),
    STARTING(ChatColor.GOLD),
    FULL(ChatColor.DARK_RED),
    RESETTING(ChatColor.BLUE),
    INGAME(ChatColor.AQUA),
    END(ChatColor.DARK_PURPLE);


    private final ChatColor color;

    ArenaState(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
