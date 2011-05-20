package com.bradsproject.BradleyJewell.bMobs;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class bMobsWorldListener extends WorldListener
{
	private final bMobs plugin;
	
	public bMobsWorldListener(bMobs instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onWorldLoad(WorldLoadEvent event)
	{
		plugin.reload();
	}
}
