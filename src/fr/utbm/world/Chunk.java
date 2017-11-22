package fr.utbm.world;

import fr.utbm.block.Block;

public class Chunk {
	public final static int CHUNK_WIDTH=50;
	public final static int CHUNK_HEIGHT=400;
	private int chunkID;
	private Block[][] blocks;
	//private Biome biome;
	
	
	public Chunk()
	{
		chunkID = 0;
		blocks = new Block[CHUNK_HEIGHT][CHUNK_WIDTH];
		//biome = new Biome()
	}
	
	public Chunk(int ID /*Biome b*/)
	{
		chunkID = ID;
		blocks = new Block[CHUNK_HEIGHT][CHUNK_WIDTH];
		//biome = b;
	}
	
	public int getID()
	{
		return this.chunkID;
	}
	
	public Block getBlock(int i, int j)
	{
		return this.blocks[i][j];
	}
	
	public void setBlock(int i, int j, Block block)
	{
		this.blocks[i][j] = block;
	}
	
	public void render()
	{
		for(int i=0;i<CHUNK_HEIGHT;i++)
		{
			for(int j=0;j<CHUNK_WIDTH;j++)
			{
				//TODO RenderManager.blabla
			}
		}
	}
	
}
