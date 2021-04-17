# !! April 2021 Update !!
The game and releases are currently working. I've merged my grade 12 final project (Майнкрафт³ (Minecraft3)) into TotalCrafter. What is this means is that I have to manually merge the code from TotalCrafter. Currently the only code to have this done is updating to LWJGL3.<br>
###Whats left?
* Cubic chunks
* rescaleable windows
* better UI system
# OpenCL
Currently, the server in the main branch uses OpenCL to generate its chunks on the GPU. This is part of the MinecraftCL project, and serves as the testing grounds for further OpenCL integration. Why? Because it is easier to implement with a codebase you understand. <br>
<br>
#Old readme below:
---------------------------- <br>
<br>
# How to play
Майнкрафт³ (Minecraft3) doesn’t have a goal to it. It is just like early versions of Minecraft in which you can well mine and craft blocks. There is no final boss for you to defeat and your only ‘goal’ is to have fun! Through the use of console commands (see wiki) you are able to give yourself items if you don’t want to mine for them. Build what you’d like as the only limitation is your imagination!<br>
<br>
## Controls
Currently there is no way change the controls currently but the basic controls are as follows; **W** for forward movement, **S** for backwards movement, **A** for left movement, **D** for right movement, **space** to jump, and **control** to crouch. To open the console you can press **grave** or **tilde** on your keyboard.<br>
<br>
Moving your mouse allows you to look around, left click is to mine blocks, and right click is to interact with blocks or place blocks. Whether you are holding a block or not the game will also try to interact with a block first and if there is no block to interact with it will place a block. Pressing the middle mouse button 4 times within 5 seconds will create an explosion at your player position. WARNING: if you do this inside or within about 7 blocks of your build you will destroy it and NOT get the blocks back. **Explosions do not drop any blocks.**<br>
<br>
## How to start a world
Starting a world in Майнкрафт³ is not hard. When you load the game up press single player, then press any of the 5 world spots, then type in the seed you want or leave it empty and press generate world. Its just that easy! <br>
<br>
## How to play multiplayer
First you will need a server. You can download the server file from the release page of the github. Make sure you open port 1337. Once you have the server started press the multiplayer tab on the main menu. Type in the username you wish to use and the IP of your server (leave blank if you are playing on a localhost).

# Game Features
* Singleplayer
* Multiplayer
* Infinite Terrain
* Inventory
* Blocks and Items
* Block Placing
* Block Mining
* Crafting / Smelting
* Crouching
* Random Ore Generation
* Random Tree Generation
* Flowers

# Technical Features
* Custom 2D Renderer
  * UI Buttons
  * UI Textures
  * UI Sliders
  * UI Text Input
  * Menus
* Custom 3D Renderer
    * Voxel Renderer
      * Chunk builder
      * Compression on chunk meshes
      * 1023 possible blocks
      * 16 block light states and 16 sun light states
      * 16 block states 
  * Player Renderer
     * Basic linear interpolation
* Explosions
* UDP Based Server
* Plus other things probably I'm forgetting

# Bugs
Please report any bugs by submitting an issue through github. https://github.com/Tri11Paragon/Grade-12-Java-Library/issues

Trello: https://trello.com/b/SagWdprR/minecraft-3# <br>
WKJGX
