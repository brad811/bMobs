package com.bradsproject.BradleyJewell.bMobs;

public class Mob
{
	public final String type;
	public boolean enabled = true;
	public double probability = 1.0;
	public boolean aggressive;
	public boolean burn;
	public int health = -1;
	
	public Mob(String t)
	{
		type = t;
	}
}
