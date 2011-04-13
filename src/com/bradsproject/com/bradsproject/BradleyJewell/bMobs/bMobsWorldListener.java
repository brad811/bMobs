package com.bradsproject.BradleyJewell.bMobs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldListener;

public class bMobsWorldListener extends WorldListener
{
	private final bMobs plugin;
	
	public bMobsWorldListener(bMobs instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onWorldLoad(WorldEvent event)
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
