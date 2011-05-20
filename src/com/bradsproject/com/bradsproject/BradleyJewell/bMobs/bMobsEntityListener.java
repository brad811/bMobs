package com.bradsproject.BradleyJewell.bMobs;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
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
		if(!plugin.handleEntity(entity, entity.getWorld()))
		{ // if the entity was not removed...
			World world = entity.getLocation().getWorld();
			bMobsWorld w = plugin.worlds.get(world.getName());
			w.mobs = w.mobs;
			String mobType = entity.toString().replace("Craft", "").toLowerCase();
			
			for(Mob mob : w.mobs)
			{
				if(mob.type.equals(mobType))
				{
					if(mob.aggressive == false)
					{
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
	@Override
	public void onEntityCombust(EntityCombustEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof Player)
			return;
		if(!plugin.canBurn(entity))
		{
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		World world = event.getLocation().getWorld();
		bMobsWorld w = plugin.worlds.get(world.getName());		
		String mobType = event.getCreatureType().name().replace("_", "").toLowerCase();
		
		try {
			if(!w.isMobEnabled(mobType))
			{
				event.setCancelled(true);
				return;
			}
			else
			{
				for(Mob mob : w.mobs)
				{
					if(mob.type.equals(mobType))
					{
						Random rand = new Random();
						int x = rand.nextInt(10) + 1;
						if(x > mob.probability * 10)
						{
							event.setCancelled(true);
							return;
						}
						else if(mob.health != -1)
						{
							LivingEntity entity = (LivingEntity) event.getEntity();
							entity.setHealth(mob.health);
						}
					}
				}
			}
		}catch(NullPointerException e)
		{
			// the world hasn't finished loading yet!
		}
	}
}
