package com.bradsproject.BradleyJewell.bMobs;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.entity.LivingEntity;

public class bMobsKillerTask implements Runnable
{
	private final bMobs plugin;
	
	public bMobsKillerTask(bMobs instance)
	{
		plugin = instance;
	}
	
	@Override
	public void run()
	{
		Set<Entry<String, bMobsWorld>> set = plugin.worlds.entrySet();
		for(Entry<String, bMobsWorld> world : set)
		{
			List<LivingEntity> entities = world.getValue().world.getLivingEntities();
			for(LivingEntity entity : entities)
			{
				plugin.handleEntity(entity, world.getValue().world);
			}
		}
	}
	
}