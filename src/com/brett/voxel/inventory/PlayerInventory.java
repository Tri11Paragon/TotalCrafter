package com.brett.voxel.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.brett.IInventoryDisable;
import com.brett.IKeyState;
import com.brett.console.Console;
import com.brett.renderer.gui.GUIRenderer;
import com.brett.renderer.gui.UIMaster;
import com.brett.voxel.inventory.recipe.PlayerCrafting;
import com.brett.voxel.tools.LevelLoader;
import com.brett.voxel.world.blocks.BlockCrafting;
import com.brett.voxel.world.items.ItemStack;

/**
*
* @author brett
* @date Mar. 12, 2020
*/
//TODO: make this a extension of inventory.
public class PlayerInventory implements IKeyState, Serializable {

	private static final long serialVersionUID = -629690982706594815L;
	
	private Inventory i;
	private Hotbar h;
	@SuppressWarnings("unused")
	private GUIRenderer rend;
	private PlayerCrafting craft;
	private static List<IInventoryDisable> disableKeyState = new ArrayList<IInventoryDisable>();
	
	public PlayerInventory(UIMaster ui) {
		float sizeX = 48*15;
		float sizeY = 48*7;
		float x = Display.getWidth()/2 - sizeX/2;
		float y = Display.getHeight()/2 - sizeY/2 + 100;
		// crates a ne ivenorty
		i = new Inventory((int)LevelLoader.seed, "player");
		// maek some backhtound
		// not needed and kinda ugly
		i.setBackground(ui.createCenteredTexture(UIMaster.inventoryTexture, -1, -1, 0, 100, sizeX + 30, sizeY + 30));
		h = new Hotbar(i, ui);
		for (int j = 0; j < 15; j++) {
			for (int k = 0; k < 7; k++) {
				i.addSlot(new Slot(x + (j*48),y + (k*48), 48, 48));
			}
		}
		// load some stuff
		i.loadInventory();
		ui.addMenu(i);
		ui.addMenu(h);
		this.rend = ui.getRenderer();
		this.craft = new PlayerCrafting();
		craft.loadInventory();
		ui.addMenu(craft);
	}
	
	/**
	 * adds an item to the player's inventory
	 */
	public void addItemToInventory(ItemStack i) {
		if (!h.addItemToInventorySimilar(i))
			if (!this.i.addItemToInventorySimilar(i))
				if (!this.h.addItemToInventory(i))
					this.i.addItemToInventory(i);
				
	}
	
	public ItemStack getItemInSelectedSlot() {
		return h.getItemSelected();
	}
	
	public Slot getSelectedSlot() {
		return h.getSelectedSlot();
	}
	
	public void update() {
		craft.update();
	}
	
	public List<Slot> getSlots(){
		return i.getSlots();
	}
	
	public void saveInventory() {
		i.saveInventory();
	}
	
	public void enable() {
		i.enable();
		craft.enable();
	}
	
	public void enableIOnly() {
		i.enable();
	}
	
	public void disable() {
		i.disable();
		craft.disable();
	}
	
	public void disableIOnly() {
		i.disable();
	}
	
	public void toggleEnabled() {
		i.toggleEnabled();
		craft.toggleEnabled();
	}
	
	public void toggleEnabledIOnly() {
		i.toggleEnabled();
	}

	@Override
	public void onKeyPressed() {
		// toggles the inventories for the player
		if (Keyboard.isKeyDown(Keyboard.KEY_E) && !Console.getIsOpen()) {
			boolean inved = false;
			// the all disable keystate listeners
			for (int i = 0; i < disableKeyState.size(); i++) {
				if (disableKeyState.get(i).disableInventory()) {
					this.i.disable();
					this.craft.disable();
					inved = true;
					Mouse.setGrabbed(true);
					continue;
				}
			}
			// closed close from close
			if (inved)
				return;
			// i am sotired
			//craft time make sure we got the goof dust
			if (BlockCrafting.craft != null) {
				if (BlockCrafting.craft.isEnabled()) {
					BlockCrafting.craft.disable();
					this.i.disable();
					this.craft.disable();
					Mouse.setGrabbed(true);
				} else {
					this.i.toggleEnabled();
					this.craft.toggleEnabled();
				}
			} else {
				this.i.toggleEnabled();
				this.craft.toggleEnabled();
			}
		}
	}

	@Override
	public void onKeyReleased() {
	}
	
	public static void registerDisableState(IInventoryDisable state) {
		disableKeyState.add(state);
	}
	
	public static void removeDisableState(IInventoryDisable state) {
		disableKeyState.remove(state);
	}
	
	public void cleanup() {
		h.saveInventory();
		i.saveInventory();
		craft.saveInventory();
	}
	
}
