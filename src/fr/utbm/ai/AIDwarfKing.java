package fr.utbm.ai;

import java.util.ArrayList;

import fr.utbm.entity.EntityAnimalDwarfKing;
import fr.utbm.entity.EntityAnimalDwarfMiner;
import fr.utbm.entity.EntityAnimalDwarfWarrior;
import fr.utbm.world.Chunk;
import fr.utbm.world.Map;

public class AIDwarfKing extends AIAnimal {
	public final int NB_MINER = 1;
	public final int NB_WARRIOR = 3;
	public final int HOME_SPAWN_FACTOR = 15*16;
	
	private float homeCenter; 
	
	private EntityAnimalDwarfKing animal;
	private AIGoTo pathFinder;
	
	private ArrayList<EntityAnimalDwarfMiner> miners;
	private ArrayList<EntityAnimalDwarfWarrior> warriors;
	
	private int blockerInteger;
	private int noInfiniteTimer;
	
	private boolean hasAnObjective;
	
	public AIDwarfKing(EntityAnimalDwarfKing e) {
		super(e);
		this.animal = e;
		
		this.pathFinder = new AIGoTo(e);
		this.pathFinder.setControls(1, 0); //jump, walk
		
		this.miners = new ArrayList<>();
		this.warriors = new ArrayList<>();
		
		this.homeCenter = -1;
		this.hasAnObjective = false;
		
		this.blockerInteger = 0;
		this.noInfiniteTimer = 0;
		
		
		EntityAnimalDwarfWarrior tempWarrior = new EntityAnimalDwarfWarrior(animal.getX()/16, (animal.getY())/16, animal.getWorldIn(), animal);
		animal.getWorldIn().addEntity(tempWarrior);
		warriors.add(tempWarrior);
		
		EntityAnimalDwarfMiner tempMiner = new EntityAnimalDwarfMiner(animal.getX()/16, (animal.getY())/16, animal.getWorldIn(), animal);
		animal.getWorldIn().addEntity(tempMiner);
		miners.add(tempMiner);
	}
	
	@Override
	public Action updateTask() {
		
		/* Je suis le roi des nains !
		 * Je peux faire pop NB_WARRIOR warriors et NB_MINER miner.
		 * Je d�finis une maison qui me plait sur la map et j'agite mes bras pour que les nains fassent le travail.
		 * Je reste toujours � c�t� de ma maison.
		 */
		Action actionDecided = null;
		if(blockerInteger <= 0) {
			if(!hasAnObjective) {
				if(this.warriors.size() < NB_WARRIOR && Math.random()<0.005) { //si il n'y a pas le maxi on a une chance sur 500 d'en faire pop un
					EntityAnimalDwarfWarrior tempWarrior = new EntityAnimalDwarfWarrior(animal.getX()/16, (animal.getY())/16, animal.getWorldIn(), animal);
					warriors.add(tempWarrior);
					animal.getWorldIn().addEntity(tempWarrior);
				}
				else if(this.miners.size() < NB_MINER && Math.random()<0.005) { //si il n'y a pas le maxi on a une chance sur 500 d'en faire pop un
					EntityAnimalDwarfMiner tempMiner = new EntityAnimalDwarfMiner(animal.getX()/16, (animal.getY())/16, animal.getWorldIn(), animal);
					miners.add(tempMiner);
					animal.getWorldIn().addEntity(tempMiner);
				}
				else {
					if(this.homeCenter == -1) { //if the dwarf has no home
						int putHomeAtXFromPos = (int)(Math.random()*(HOME_SPAWN_FACTOR));
						if (Math.random()<0.5 && !((animal.getX()-putHomeAtXFromPos)/16 <= 1)) { //we put the home left if possible
							homeCenter = animal.getX()-putHomeAtXFromPos;
						} 
						else if (!((animal.getX()+putHomeAtXFromPos)/16 >= Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH-1)) { //we put the home right if possible
							homeCenter = animal.getX()+putHomeAtXFromPos;
						}
						else {
							homeCenter = -1;
						}
					}
					else //the king has a home, he needs to turn around and command
					{
						double whatToDo = Math.random();
						if(whatToDo < 0.1) { //he turns around
							int leftToTheHome;
							if (Math.random() < 0.5) {
								leftToTheHome = -1;
							} else {
								leftToTheHome = 1;
							}
							
							
							float toGo = (float)(homeCenter+Math.random()*HOME_SPAWN_FACTOR*leftToTheHome);
							if((int)((toGo+animal.getWidth())/16)+1>Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH-1) //si jamais on risque d'aller sur le bord droit
							{
								toGo = toGo -32;
							}
							this.pathFinder.setObjective(toGo);
							this.hasAnObjective = true;
							
							if(toGo/16 <= 1) { //we're to close to the left
								this.hasAnObjective = false;
							} else if((toGo+animal.getWidth())/16 >= (Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH-1)) { //we're to close to the right
								this.hasAnObjective = false;
							}
							this.noInfiniteTimer = 20;
						} else if (whatToDo < 0.8){ //he commands
							if(Math.random() < 0.5) { //he commands to the right
								actionDecided = new Action(1,2, false);
							} else { //he commands to the left
								actionDecided = new Action(-1,2, false);
							}
						}
						else { //do nothing
							this.blockerInteger = (int)(Math.random()*100);
							actionDecided = new Action(0,-1,false);
						}
					}
				}	
			} else {
				actionDecided = this.pathFinder.updateTask();
				if(actionDecided.isFinish() || noInfiniteTimer <= 0) {
					this.hasAnObjective = false;
				}
				noInfiniteTimer--;
			}
		}
		else {
			blockerInteger --;
		}
		
		if(actionDecided == null) {
			actionDecided = new Action(0,-1,false);
		}
		
		return actionDecided;
	}
	
	public float getHomeCenter() {
		return this.homeCenter;
	}
	
	public ArrayList<EntityAnimalDwarfMiner> getMiners() {
		return this.miners;
	}
	
	public ArrayList<EntityAnimalDwarfWarrior> getWarriors() {
		return this.warriors;
	}
}