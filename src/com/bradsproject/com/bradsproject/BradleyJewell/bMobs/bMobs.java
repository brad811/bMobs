package com.bradsproject.BradleyJewell.bMobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * bMobs for Bukkit
 * 
 * @author BradleyJewell
 */
public class bMobs extends JavaPlugin
{
	private final bMobsEntityListener entityListener = new bMobsEntityListener(this);
	private final bMobsWorldListener worldListener = new bMobsWorldListener(this);
	public static PermissionHandler Permissions = null;
	Server server;
	Map<String, bMobsWorld> worlds = new HashMap<String, bMobsWorld>();
	Yaml yaml;
	
	public void onEnable()
	{
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Priority.Normal, this);
		
		setupPermissions();
		
		parseConfig();
		
		long delay = 200L;
		long period = 200L;
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new bMobsKillerTask(this), delay, period);
		
		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion()
				+ " is enabled!");
	}
	
	public void onDisable()
	{
		System.out.println("bMobs has been disabled!");
	}
	
	public void setupPermissions()
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if(bMobs.Permissions == null)
		{
			if(test != null)
			{
				bMobs.Permissions = ((Permissions) test).getHandler();
			} else
			{
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void parseConfig()
	{
		yaml = new Yaml(new SafeConstructor());
		try {
			InputStream input = new FileInputStream(new File("plugins/bMobs/bMobs.yml"));
			Map<String, Object> map = (Map<String, Object>) yaml.load(input);
			Map<String, Object> worldsNode = (Map<String, Object>) map.get("worlds");
			
			for(String worldsKey: worldsNode.keySet())
			{
				Map<String, Object> worldNode = (Map<String, Object>) worldsNode.get(worldsKey);
				bMobsWorld w = new bMobsWorld(getServer().getWorld(worldsKey));
				for(String worldKey: worldNode.keySet())
				{
					Mob mob = new Mob(worldKey);
					Map<String, Object> value = (Map<String, Object>) worldNode.get(worldKey);
					
					if(value.containsKey("enabled"))
					{
						mob.enabled = Boolean.parseBoolean(value.get("enabled").toString());
					}
					
					if(value.containsKey("probability"))
					{
						mob.probability = Double.parseDouble(value.get("probability").toString());
						if(mob.probability <= 0)
							mob.enabled = false;
						else if(mob.probability > 1)
							mob.probability = 1;
					}
					
					if(value.containsKey("aggressive"))
					{
						mob.aggressive = Boolean.parseBoolean(value.get("aggressive").toString());
					}
					
					if(value.containsKey("burn"))
					{
						mob.burn = Boolean.parseBoolean(value.get("burn").toString());
					}
					
					if(value.containsKey("health"))
					{
						mob.health = Integer.parseInt(value.get("health").toString());
						if(mob.health < 0)
							mob.health = 0;
						else if(mob.health > 200)
							mob.health = 200;
					}
					
					w.mobs.add(mob);
				}
				worlds.put(worldsKey, w);
			}
		} catch(FileNotFoundException e)
		{
			System.out.println("bMobs configuration file not found.");
			this.getPluginLoader().disablePlugin(this);
		}
	}
	
	public boolean handleEntity(LivingEntity entity, World world)
	{
		bMobsWorld w = worlds.get(world.getName());
		
		if((entity instanceof Creeper && !w.isMobEnabled("creeper"))
		||(entity instanceof Skeleton && !w.isMobEnabled("skeleton"))
		||(entity instanceof Spider && !w.isMobEnabled("spider"))
		||(entity instanceof Zombie && !w.isMobEnabled("zombie"))
		||(entity instanceof Ghast && !w.isMobEnabled("ghast"))
		||(entity instanceof PigZombie && !w.isMobEnabled("pigzombie"))
		||(entity instanceof Giant && !w.isMobEnabled("giant"))
		||(entity instanceof Slime && !w.isMobEnabled("slime"))
		||(entity instanceof Chicken && !w.isMobEnabled("chicken"))
		||(entity instanceof Cow && !w.isMobEnabled("cow"))
		||(entity instanceof Sheep && !w.isMobEnabled("sheep"))
		||(entity instanceof Pig && !w.isMobEnabled("pig"))
		||(entity instanceof Squid && !w.isMobEnabled("squid"))
		||(entity instanceof Wolf && !w.isMobEnabled("wolf")))
		{
			entity.remove();
			return true;
		}
		return false;
	}
	
	public boolean isMonster(LivingEntity e)
	{
		if(!(e instanceof Player) && !(e instanceof Animals))
		{
			return true;
		}
		return false;
	}
	
	public boolean isAnimal(LivingEntity e)
	{
		if(e instanceof Animals)
		{
			return true;
		}
		return false;
	}
	
	public boolean isEntityMatch(LivingEntity e, String type)
	{
		String mobType = e.toString().toLowerCase().replace("craft", "");
		if(mobType.equals(type))
		{
			return true;
		}
		return false;
	}
	
	public void kill(World world, String type)
	{
		List<LivingEntity> mobs = world.getLivingEntities();
		for(LivingEntity m : mobs)
		{
			if(type.equals("animals") || type.equals("all"))
			{
				if(isAnimal(m))
				{
					m.remove();
				}
				else if(isMonster(m))
				{
					m.remove();
				}
			}
			else
			{
				if(isEntityMatch(m, type))
				{
					m.remove();
				}
			}
		}
	}
	
	public void reload()
	{
		worlds.clear();
		parseConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		String commandName = cmd.getName().toLowerCase();
		
		if(commandName.equalsIgnoreCase("bkill"))
		{
			if(!(bMobs.Permissions == null || bMobs.Permissions
					.has(player, "bmobs.bkill")))
			{
				player.sendMessage("You do not have permission to use that command.");
				return false;
			}
			try {
				if(args[0].equalsIgnoreCase("monsters") || args[0].equalsIgnoreCase("mobs"))
				{
					kill(player.getWorld(), "monsters");
					player.sendMessage("All monsters have been killed!");
					return true;
				}
				else if(args[0].equalsIgnoreCase("animals"))
				{
					kill(player.getWorld(), "animals");
					player.sendMessage("All animals have been killed!");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{
					kill(player.getWorld(), "all");
					player.sendMessage("All creatures have been killed!");
					return true;
				}
				else
				{
					kill(player.getWorld(), args[0].toLowerCase());
					player.sendMessage("All "+ args[0].toLowerCase() +"(s) have been killed!");
					return true;
				}
			}
			catch(CommandException e)
			{
				kill(player.getWorld(), "monsters");
				player.sendMessage("All monsters have been killed!");
				return true;
			}
			catch(NullPointerException e)
			{
				kill(player.getWorld(), "monsters");
				player.sendMessage("All monsters have been killed!");
				return true;
			}
		}
		else if(commandName.equalsIgnoreCase("bmobsreload"))
		{
			if(!(bMobs.Permissions == null || bMobs.Permissions
					.has(player, "bmobs.reload")))
			{
				player.sendMessage("You do not have permission to use that command.");
				return false;
			}
			
			reload();
			player.sendMessage("bMobs properties have been reloaded!");
			return true;
		}
		return false;
	}
}
