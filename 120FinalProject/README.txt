=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: ZHOUMIC
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays. The Game uses 2D arrays to create the menu design for the upgrade screen.
  This 2D array of rectangles generated upon giving it the parameters of length, width, and size of the 2D array
  gives us an easy way to store the location data for each button on the upgrade menu. The only work that needs to be 
  done is to see that, while on the upgrade screen, if the point clicked is within the bounds of any of our rectangles,
  allowing us to bypass the need to find the exact pixel borders and also in case of future changes or additions.

  2. Collections. I use in particular treeSets to store much of the needed data regarding the state of the game, including 
  what projectiles, enemies, stars, and drops need to be drawn and in what order (in this case I created the arbitrary ordering
  of drawing from the bottom up using the comparable interface). I did not end up implementing different enemies/an enemy dictionary
  and instead had the enemies scale with the number of ticks, i.e. every line of enemies occurs every 42 ticks and every 18 lines, the
  enemies go up one level and the wave number increases. 

  3. I/O. Implementation of a highscore and statistics tracker that is persistent between closing and reopening the game. 
  Between rounds the game also automatically saves, allowing the user to load from the main starting screen the next time they play which
  is also persistent between sessions of the application. Saves/Loads all applicable information except those particular to a specific
  round of playing the game, if a player quits before the round ends it will only load the saved data from the last round ended.

  4. Inheritance/Subtyping and Dynamic Dispatch. The class structure in the game extends the one given from Mushroom of Doom. It allows for 
  a generic ImageObj which allows for any image to implemented as a GameObj. ImageObj extends GameObj, and is itself extended by 
  multiple other classes with more specific needs such as PlayableObj, EnemyObj, PlayerProjectile, and Drops. For example, EnemyObj has the
  specific need of a health and level field, while PlayableObj and PlayerProjectile need a damage field. PlayableObj and PlayerProjectile are
  not the same class, such as some more generic PlayerRelatedObj because they differ in that the movement of one has a need to be clipped while
  the other does not. Drops also varies in this way since it is the only object which experiences acceleration in its movement.


=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
  Game.java - necessary main class which the program runs from.
  
  GameScreen.java - Carries gamestate, updates game on tick cycle every 35 ms and repaints and the same rate. Also
  handles player interaction, such as keyboard and mouse inputs.
  
  GameObj.java - Generic GameObj class which sets up all the necessary fields an element of the game would need.
  
  ImageObj.java - Extends GameObj, generic image class which allows for the GameObj to be represented as an image.
  
  PlayableObj.java - Extends ImageObj. This class is mainly used for the player itself, I made it generic as a 
  PlayableObj however because I was considering implementing multiple different playable characters or having
  multiple PlayableObj on the screen at once. Regardless, PlayableObj simply carries a damage field which allows
  us to know how much damage its projectiles should do. 
  
  EnemyObj.java - Extends ImageObj. Carries a health and level field which allows us to subtract health when a projectile
  intersects and enemyObj, also allows us to know when to remove the EnemyObj (When health <= 0) and also automatically
  scales healthy with enemy level. 
  
  PlayerProjectile.java - Extends ImageObj. Also carries a damage field, similar to the playable object. However, since
  we want projectiles to leave the screen, we do not clip the move function. And we thus simply override the method from 
  GameObj and replace it with one that doesn't. 
  
  Drops.java - Includes an acceleration (a_y) field, since I wanted the drops to move at a different speed as the rest of the enemies to
  add more variety to the game screen. This is accomplished by simply adding the a_y field and overriding the move method to
  change the y velocity every move tick .
  
  Star.java - Implements a static field image which is always a star, used in the making of the "scrolling" background. 
  Comet1 to Comet4.java - Similar to Star.java but used for comets. 
  
  Direction.java - enumeration from Mushroom of Doom.
  
- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  Nothing in particular. The biggest obstacles were mostly learning how to implement things we hadn't yet learned in class, such
  as using a custom font. 

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
  The design could be more modular. I found that as I wanted to add more GameMode enum's that I would have to implement a repetitive structure
  to accommodate the new GameMode in multiple different parts of the program, in particular the tick() and repaint() methods. A larger program
  would have demanded a more modular way of handling new GameModes and it would be good style to implement it that way regardless. 

========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  Github and the Javadocs were referenced on multiple occasions for how to use implement things we haven't done in class, such as custom fonts.
  All images were downloaded opensource from OpenGameArt.Org. 
