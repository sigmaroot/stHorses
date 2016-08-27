package com.shepherdjerred.sthorses.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class DismountEvent {

    @EventHandler
    public void onClickEvent(VehicleExitEvent event) {

        if (event.getVehicle().getType() == EntityType.HORSE) {

        }

    }

}
