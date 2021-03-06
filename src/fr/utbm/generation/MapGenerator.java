package fr.utbm.generation;

import java.util.ArrayList;

import fr.utbm.biome.Biome;
import fr.utbm.biome.BiomeList;
import fr.utbm.block.BlockAsh;
import fr.utbm.block.BlockGlass;
import fr.utbm.block.BlockLava;
import fr.utbm.block.BlockStone;
import fr.utbm.block.BlockWater;
import fr.utbm.entity.AnimalLinking;
import fr.utbm.render.RenderManager;
import fr.utbm.tools.Chrono;
import fr.utbm.world.Chunk;
import fr.utbm.world.Map;
import fr.utbm.world.World;

/*
 * This class combines the different sub generators to create our world at the first place, depending on the seed
 */
public class MapGenerator {
	public final static int DIRT_SURFACE = 20; //Height of the dirt at the surface
	
	public static void generate(World world, double seed)
	{
			Chrono chrono = new Chrono();
			Chrono chrono2 = new Chrono();
			
			
			long M = 42949672L;
			if (seed == 0) 
			{
				seed = Math.floor(Math.random() * M);
			}

			System.out.printf("\n---- STARTING WORLD GENERATION WITH SEED %.0f \n----  ",seed);

			chrono.reset();
			System.out.print("Generating Biomes...");
				//You can adjust minimum and maximum size of biomes
				//Care: if the biomes are too small, it wont give a good result, minimum 50 is advised
				//The biomes shall not be <20 blocks large or you will have some issues
				BiomeGenerator biomeGen = new BiomeGenerator(seed, M, Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH, 50, 100);
				ArrayList<Biome> biomeList = biomeGen.getBiomeList();
				world.setBiomeList(biomeList);
			System.out.println(" "+chrono.getTime()+"ms");
			
			RenderManager.setBiomeList(biomeList);
			
			chrono.reset();
			System.out.print("Generating Surface...");
				Surface1DGenerator noiseGen = new Surface1DGenerator(seed, M);
				//To fill the parameters: generateAndGetNoise(double amplitude, double wavelength, int octaves, double divisor)
				//=>Increase wavelength to get flat map generally ---> BETWEEN 0 & 1 <---
				//=>Decrease amplitude to get a flat map locally ---> BETWEEN 0 & 1 <---
				ArrayList<Integer> surface = noiseGen.generateAndGetNoise(1,1,15,4, biomeList);
			System.out.println(" "+chrono.getTime()+"ms");
			
			chrono.reset();
			System.out.print("Generating Caves...");
				//3rd parameter: stone ratio in caves
				//2nd parameter: ash ratio in hell caves
				Cave2DGenerator caveGen = new Cave2DGenerator(seed, M, 45, 75, Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH, Map.LIMIT_CAVE, Map.LIMIT_SURFACE-Map.LIMIT_CAVE+1);
				ArrayList<ArrayList<Integer>> caves = caveGen.generateAndGetCaves();
			System.out.println(" "+chrono.getTime()+"ms");
			
			chrono.reset();
			System.out.print("Generating Liquids...");
				LiquidGenerator liquidGen = new LiquidGenerator(seed, M);
				int[] surfaceLiquid = liquidGen.surfaceLiquidGen(surface, 70, 3, 50);
				/*caveLiquidGen(caves, minHeight, maxHeight)
				 *minHeight is the minimal height of air in the cave
				 *maxHeight is the maximal height of air in the cave
				 */
				caves = liquidGen.caveLiquidGen(caves, 5, 50);
			System.out.println(" "+chrono.getTime()+"ms");
			
			chrono.reset();
			System.out.print("Generating Vegetals...");
			VegetalGenerator vegetalGen = new VegetalGenerator(seed, M);
			//To change this generation you have to change the frequence by ID in the biomeManager.xml
			ArrayList<Integer> vegetalList = vegetalGen.surfaceVegetalGen(biomeList, surface, surfaceLiquid);
			System.out.println(" "+chrono.getTime()+"ms");
			
			chrono.reset();
			System.out.print("Generating Animals...");
			AnimalGenerator animalGen = new AnimalGenerator(seed, M);
			//To change this generation you have to change the frequence by ID in the biomeManager.xml
			ArrayList<int[]> animalSurface = animalGen.surfaceAnimalGen(biomeList, surface, surfaceLiquid);
			ArrayList<ArrayList<Integer>> animalCave = animalGen.caveEntityGen(caves, surface);
			System.out.println(" "+chrono.getTime()+"ms");
			
			chrono.reset();
			System.out.print("Placing the Blocks...");
			
			int k = 0;
			int lastSwitchI = 0;
			for(int i=0; i<Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH;i++)
			{
				if(i == lastSwitchI+biomeList.get(k).getWidth()) {
					k++;
					lastSwitchI = i;
				}
				
				/* CAVES */
				for(int j=0; j<Map.LIMIT_SURFACE+surface.get(i);j++)
				{
					if (j<Map.LIMIT_SURFACE) {
						if(caves.get(i).get(j) == 1) 
						{	
							if (j<Map.LIMIT_CAVE+surface.get(i)) 
							{
								world.getMap().setBlock(i, j, new BlockAsh(i,j,world)); 
							}
							else
							{
								world.getMap().setBlock(i, j, new BlockStone(i,j,world)); 
							}
						}
						else if(caves.get(i).get(j) == 2)
						{
							if(j<Map.LIMIT_CAVE) {
								world.getMap().setBlock(i, j, new BlockLava(i,j,0,world));
							}
							else {
								world.getMap().setBlock(i, j, new BlockWater(i,j,0,world));
							}
						}
						
						if(animalCave.get(i).get(j)>0)
						{
							AnimalLinking.createAnimalByID(i, j-1, 0, world, animalCave.get(i).get(j));
						}
					}
					else
					{
						world.getMap().setBlock(i, j, new BlockStone(i,j,world)); 
					}
				}
				
				/* SURFACE */
				for(int j=Map.LIMIT_SURFACE+surface.get(i); j<Map.LIMIT_SURFACE+MapGenerator.DIRT_SURFACE+surface.get(i)+surfaceLiquid[i]+1;j++)
				{
					if (j<Map.LIMIT_SURFACE+MapGenerator.DIRT_SURFACE+surface.get(i)) //dirt or grass
					{
						if (j==Map.LIMIT_SURFACE+MapGenerator.DIRT_SURFACE+surface.get(i)-1) { //grass
							BiomeList.createSurfaceGrassBlock(i, j, world, biomeList.get(k).getId());
							
							//if there is a vegetal, we create the vegetal
							if (vegetalList.get(i)>0) {
								BiomeList.createEntityByID(i, j, world, vegetalList.get(i));
							}
							//if there is an animal we create it
							if(animalSurface.get(i)[0]>0)
							{
								AnimalLinking.createAnimalByID(i, j, 0, world, animalSurface.get(i)[0]);
							}
							
							if(animalSurface.get(i)[1]>0)
							{
								AnimalLinking.createAnimalByID(i, j, animalSurface.get(i)[2], world, animalSurface.get(i)[1]);
							}
						}
						else { //dirt
							BiomeList.createSurfaceBlock(i, j, world, biomeList.get(k).getId());	
						}
					}
					else if (j<Map.LIMIT_SURFACE+MapGenerator.DIRT_SURFACE+surface.get(i)+surfaceLiquid[i]) //water
					{
						world.getMap().setBlock(i, j, new BlockWater(i,j,0,world));
					}
				}
			}
			
			//Placing the glass borders
			for(int i=0; i<Chunk.CHUNK_HEIGHT;i++)
			{
				world.getMap().setBlock(0, i, new BlockGlass(0,i,world,0));
				world.getMap().setBlock(Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH-1, i, new BlockGlass(Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH-1, i,world,0));
			}
			for(int i=0; i<Map.NUMBER_OF_CHUNKS*Chunk.CHUNK_WIDTH;i++)
			{
				world.getMap().setBlock(i, 0, new BlockGlass(i,0,world,1));
				world.getMap().setBlock(i, Chunk.CHUNK_HEIGHT-1, new BlockGlass(i, Chunk.CHUNK_HEIGHT-1,world,1));
			}
			System.out.println(" "+chrono.getTime()+"ms");
			System.out.println("---- GENERATION DONE, TOTAL TIME: "+chrono2.getTime()+"ms ----\n");
	}
}
