package com.bradsproject.BradleyJewell.bMobs;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * bMobs entity listener
 * 
 * @author BradleyJewell
 */
public class bMobsEntityListener extends EntityListener
{
	private final bMobs plugin;
	
	public bMobsEntityListener(bMobs instance)
	{
		plugin = instance;
	}
	
	@Override
	public void onEntityTarget(EntityTargetEvent event)
	{
		LivingEntity entity = (LivingEntity) event.getEntity();
		plugin.handleEntity(entity, entity.getWorld());
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		//event.getEntity().getWorld();
		World world = event.getLocation().getWorld();
		bMobsWorld w = plugin.worlds.get(world.getName());
		String mobType = event.getCreatureType().name().replace("_", "").toLowerCase();
		if(!w.active.contains(mobType))
		{
			event.setCancelled(true);
		}
	}
}
