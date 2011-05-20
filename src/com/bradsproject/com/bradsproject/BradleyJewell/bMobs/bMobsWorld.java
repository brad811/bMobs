package com.bradsproject.BradleyJewell.bMobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;

public class bMobsWorld
{
	public final World world;
	public List<Mob> mobs = new ArrayList<Mob>();
	
	public bMobsWorld(World w)
	{
		world = w;
	}
	
	public boolean isMobEnabled(String type)
	{
		for(Mob mob : mobs)
		{
			if(mob.type.equals(type))
			{
				if(mob.enabled == false)
					return false;
				else
					return true;
			}
		}
		return true;
	}
}
