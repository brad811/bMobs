package com.bradsproject.BradleyJewell.bMobs;

import java.io.FileNotFoundException;
import java.io.IOException;

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
		try
		{
			plugin.reload();
		} catch (FileNotFoundException e)
		{
			System.out.println("bMobs could not find properties file!");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
