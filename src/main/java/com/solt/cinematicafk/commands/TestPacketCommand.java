package com.solt.cinematicafk.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class TestPacketCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        try {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
            sender.sendMessage("Ints: " + packet.getIntegers().size());
            sender.sendMessage("Bytes: " + packet.getBytes().size());
            sender.sendMessage("Shorts: " + packet.getShorts().size());
            sender.sendMessage("Strings: " + packet.getStrings().size());
            sender.sendMessage("Float: " + packet.getFloat().size());
            sender.sendMessage("Mods: " + packet.getModifier().size());
            
            for (int i = 0; i < packet.getModifier().size(); i++) {
                Object obj = packet.getModifier().read(i);
                sender.sendMessage("Mod " + i + ": " + (obj == null ? "null" : obj.getClass().getName()));
            }
        } catch (Exception e) {
            sender.sendMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
