package com.bradsproject.BradleyJewell.bMobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

public class bMobsWorld
{
	public final bMobs plugin;
	public List<String> active = new ArrayList<String>();
	public final World world;
	
	public bMobsWorld(bMobs instance, World w)
	{
		plugin = instance;
		world = w;
		active.add("nothing");
	}
}
