package com.brett.console.commands;

import java.io.File;

import org.lwjgl.util.vector.Vector3f;

import com.brett.console.Command;
import com.brett.renderer.Loader;
import com.brett.renderer.datatypes.TexturedModel;
import com.brett.tools.MousePicker;
import com.brett.world.World;
import com.brett.world.entities.Entity;

/**
*
* @author brett
*
*/

public class SpawnCommand extends Command {

	private MousePicker pick;
	private World world;
	private Loader loader;
	
	public SpawnCommand(MousePicker picker, World world, Loader loader) {
		this.pick = picker;
		this.world = world;
		this.loader = loader;
	}
	
	@Override
	public String run(String data) {
		try {
			String[] d = data.split(" ");
			if (d.length == 1)
				return "Please enter more varaiables! \nspawn $ENTITY $NUMOFENTS $X $Y $Z \n^Last 3 are not needed. will spawn at mouse pos.";
			
			int a = 1;
			Vector3f pos = null;
			if (d.length > 2 && d[2] != null)
				a = Integer.parseInt(d[2]);
			if (d.length >= 6)
				pos = new Vector3f(Float.parseFloat(d[3]), Float.parseFloat(d[4]), Float.parseFloat(d[5]));
			if (pos == null) {
				pos = pick.getCurrentTerrainPoint();
				if (pos == null)
					pos = new Vector3f(0, 0, 0);
			}
			if (world.getSpawnableEntities().get(d[1].toLowerCase()) != null) {
				for(int i = 0; i < a; i++) {
					world.spawnEntity(new Entity(world.getSpawnableEntities().get(d[1].toLowerCase())).setPosition(pos));
				}
				return "Spawned #" + a + " Entities";
			}
			if (new File("resources/models/" + d[1] + ".obj").exists()) {
				String texture = "";
				if (new File("resources/textures/" + d[1] + ".png").exists())
					texture = d[1];
				else
					texture = "error";
				// create a master entity to copy
				Entity e = new Entity(TexturedModel.createTexturedModel(loader, d[1], texture), pos, 0, 0, 0, 1);
				for(int i = 0; i < a; i++) {
					world.spawnEntity(new Entity(e).setPosition(pos));
				}
				world.addSpawnableEntitiy(d[1], e);
				// also add the lower case version just in case.
				world.addSpawnableEntitiy(d[1].toLowerCase(), e);
				return "Spawned #" + a + "Entities";
			}
		} catch (Exception e) {
			return "Spawn Failed";
		}
		return "Spawn Failed";
	}
	
}
