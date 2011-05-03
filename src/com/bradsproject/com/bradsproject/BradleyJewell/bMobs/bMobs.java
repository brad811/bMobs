package com.bradsproject.BradleyJewell.bMobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;

/**
 * bMobs for Bukkit
 * 
 * @author BradleyJewell
 */
public class bMobs extends JavaPlugin
{
	private final bMobsEntityListener entityListener = new bMobsEntityListener(this);
	private final bMobsWorldListener worldListener = new bMobsWorldListener(this);
	public static AnjoPermissionsHandler Permissions = null;
	Server server;
	Map<String, bMobsWorld> worlds = new HashMap<String, bMobsWorld>();
	
	public void onEnable()
	{
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Priority.Normal, this);
		
		try
		{
			parseProperties();
		} catch (FileNotFoundException e)
		{
			System.out.println("bMobs.properties file not found. Generating...");
			FileWriter fstream;
			try
			{
				try {
					fstream = new FileWriter("plugins/bMobs/bMobs.properties");
				} catch(FileNotFoundException e1)
				{
					File f = new File("plugins/bMobs");
					f.mkdir();
					System.out.println(f.getAbsolutePath());
					fstream = new FileWriter("plugins/bMobs/bMobs.properties");
				}
				BufferedWriter out = new BufferedWriter(fstream);
		        out.write("# List of creatures that will not be removed (start line with # to disable)\n");
		        
		        List<World> worldss = getServer().getWorlds();
		        for(World w : worldss)
		        {
		        	out.write("world:"+w.getName() + "\n");
			        out.write("#creeper\n");
			        out.write("skeleton\n");
			        out.write("#spider\n");
			        out.write("zombie\n");
			        out.write("#ghast\n");
			        out.write("#giant\n");
			        out.write("pigzombie\n");
			        out.write("#slime\n");
			        out.write("chicken\n");
			        out.write("cow\n");
			        out.write("sheep\n");
			        out.write("pig\n");
			        out.write("squid\n");
			        out.write("wolf\n");
			        out.write("\n");
		        }
		        
		        out.close();
		        parseProperties();
			} catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
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
		// NOTE: All registered events are automatically unregistered when a
		// plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		System.out.println("bMobs has been disabled!");
	}
	
	public void parseProperties() throws FileNotFoundException, IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("plugins/bMobs/bMobs.properties"));
		String str;
		bMobsWorld w = null;
		while((str = in.readLine()) != null)
		{
			if(str.isEmpty() || str.substring(0, 1).equals("#"))
				continue;
			
			String[] split = str.split(":");
			if(split[0].equals("world"))
			{
				if(w != null)
				{
					try {
						System.out.println("bMobs got world: " + w.world.getName());
						worlds.put(w.world.getName(), w);
					} catch(NullPointerException e)
					{
						System.out.println("Problem getting worlds from server! Try running \"/bmobsreload\" once server is finished starting up.");
						return;
					}
				}
				w = new bMobsWorld(this, getServer().getWorld(split[1]));
				continue;
			}
			if(w != null)
				w.active.add(str.toLowerCase());
		}
		try {
			worlds.put(w.world.getName(), w);
		}
		catch (NullPointerException e){
			System.out.println("bMobs could not process one of your worlds properly! Check that your properties file is properly formatted.");
		}
		in.close();
	}
	
	public void reload() throws FileNotFoundException, IOException
	{
		BufferedReader in = new BufferedReader(new FileReader("plugins/bMobs/bMobs.properties"));
		String str;
		bMobsWorld w = null;
		worlds.clear();
		while((str = in.readLine()) != null)
		{
			if(str.isEmpty() || str.substring(0, 1).equals("#"))
				continue;
			
			String[] split = str.split(":");
			if(split[0].equals("world"))
			{
				try {
				if(w != null)
					worlds.put(w.world.getName(), w);
				
				w = new bMobsWorld(this, getServer().getWorld(split[1]));
				System.out.println("bMobs processing world: " + w.world.getName());
				continue;
				} catch(NullPointerException e)
				{
					//
				}
			}
			try {
				w.active.add(str.toLowerCase());
			} catch(NullPointerException e)
			{
				System.out.println("Improperly formatted bMobs.properties file!");
			}
		}
		try {
			worlds.put(w.world.getName(), w);
		}catch(NullPointerException e)
		{
			System.out.println("bMobs could not access a world yet! It may still be loading.");
		}
		in.close();
		
		for(World world : getServer().getWorlds())
		{
			if(!worlds.containsKey(world.getName()))
			{
				addWorld(world);
			}
		}
		
	}
	
	public void addWorld(World w) throws IOException
	{
		System.out.println("bMobs adding world: " + w.getName());
		
		FileWriter fstream = new FileWriter("plugins/bMobs/bMobs.properties", true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write("world:"+w.getName() + "\n");
        out.write("#creeper\n");
        out.write("skeleton\n");
        out.write("#spider\n");
        out.write("zombie\n");
        out.write("#ghast\n");
        out.write("#giant\n");
        out.write("pigzombie\n");
        out.write("#slime\n");
        out.write("chicken\n");
        out.write("cow\n");
        out.write("sheep\n");
        out.write("pig\n");
        out.write("squid\n");
        out.write("wolf\n");
        out.write("\n");
        
        worlds.put(w.getName(), new bMobsWorld(this,w));
        
        out.close();
        fstream.close();
	}
	
	public void handleEntity(LivingEntity entity, World world)
	{
		bMobsWorld w = worlds.get(world.getName());
		if((entity instanceof Creeper && !w.active.contains("creeper"))
		||(entity instanceof Skeleton && !w.active.contains("skeleton"))
		||(entity instanceof Spider && !w.active.contains("spider"))
		||(entity instanceof Zombie && !w.active.contains("zombie"))
		||(entity instanceof Ghast && !w.active.contains("ghast"))
		||(entity instanceof PigZombie && !w.active.contains("pigzombie"))
		||(entity instanceof Giant && !w.active.contains("giant"))
		||(entity instanceof Slime && !w.active.contains("slime"))
		||(entity instanceof Chicken && !w.active.contains("chicken"))
		||(entity instanceof Cow && !w.active.contains("cow"))
		||(entity instanceof Sheep && !w.active.contains("sheep"))
		||(entity instanceof Pig && !w.active.contains("pig"))
		||(entity instanceof Squid && !w.active.contains("squid"))
		||(entity instanceof Wolf && !w.active.contains("wolf")))
		{
			entity.remove();
		}
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
	
	public void kill(World world, String type)
	{
		List<LivingEntity> mobs = world.getLivingEntities();
		for(LivingEntity m : mobs)
		{
			if(isAnimal(m) && (type.equals("animals") || type.equals("all")))
			{
				m.remove();
			}
			else if(isMonster(m) && (type.equals("monsters") || type.equals("all")))
			{
				m.remove();
			}
		}
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
			}
			catch(NullPointerException e)
			{
				kill(player.getWorld(), "monsters");
				player.sendMessage("All monsters have been killed! (e)");
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
			try
			{
				reload();
				player.sendMessage("bMobs properties have been reloaded!");
				return true;
			} catch (FileNotFoundException e)
			{
				System.out.println("bMobs Error: properties file not found!");
				return false;
			} catch (IOException e)
			{
				System.out.println("bMobs Error: problem reloading properties file!");
				return false;
			}
		}
		return false;
	}
}
