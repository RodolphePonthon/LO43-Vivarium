package fr.utbm.entity;

import fr.utbm.world.World;

public class AnimalLinking {

	
	public static void createEntityByID(int x, int y, World w, int ID)
	{
		switch (ID) {				
		/*Animals*/
		case 200: w.addEntity((Entity)(new EntityHellDog(x,y+1,w))); //hellHound
				break;
		
		case 208: w.addEntity((Entity)(new EntityAnimalBenenut(x,y+1,w))); //benenut
				break;
				
		case 218: w.addEntity((Entity)(new EntityAnimalDigger(x,y+1,w))); //digger
				break;
				
		case 219: w.addEntity((Entity)(new EntityAnimalCuteFlower(x,y+1,w))); //cuteFlower
				break;
		
		case 221: w.addEntity((Entity)(new EntityBeaver(x,y+1,w))); //beaver
				break;
				
		case 222: w.addEntity((Entity)(new EntityPrettyBird(x,y+1,w))); //prettyBird
				break;
		}
	}
}
