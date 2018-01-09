package fr.utbm.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import fr.utbm.ai.AICuteFlower;
import fr.utbm.ai.Action;
import fr.utbm.texture.TextureManager;
import fr.utbm.world.World;

public abstract class EntityAnimalDwarf extends EntityAnimal {

	private boolean hasJump;
	private AICuteFlower brain;
	
	/*
	 * DWARF activity : -1 DO NOTHING, 0 WALK, 1 JUMP, 2 SPECIAL ACTIVITY 
	 */

	public EntityAnimalDwarf(float x, float y, int w, int h, World worldIn, int normalId, int walkId, int jumpId, int specialId) {
		super(x, y, w, h, worldIn);
		this.text = TextureManager.getTexture(normalId);
		anim = new Animation[3];
		anim[0] = TextureManager.getAnimation(walkId); //walk
		anim[1] = TextureManager.getAnimation(jumpId); //jump
		anim[2] = TextureManager.getAnimation(specialId); //specialActivity

		directionX = 1;
		activity = -1;
		perform = false;
		actionToPerform = -1;
		//brain = new AI(this);
	}
	
	public EntityAnimalDwarf(float x, float y, int w, int h, World worldIn, int normalId, int walkId, int jumpId, int specialId, int specialId2) {
		super(x, y, w, h, worldIn);
		this.text = TextureManager.getTexture(normalId);
		anim = new Animation[4];
		anim[0] = TextureManager.getAnimation(walkId); //walk
		anim[1] = TextureManager.getAnimation(jumpId); //jump
		anim[2] = TextureManager.getAnimation(specialId); //specialActivity1
		anim[3] = TextureManager.getAnimation(specialId2); //specialActivity2

		directionX = 1;
		activity = -1;
		perform = false;
		actionToPerform = -1;
		//brain = new AI(this);
	}

	public void update() {
		suffocating();
		
		if(!perform){
			hasJump = false;
			if (Gdx.input.isKeyPressed(Input.Keys.A)){
				actionToPerform = 1;
				directionToPerform = 1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.Z)){
				actionToPerform = 2;
				directionToPerform = 1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.E)){
				actionToPerform = 3;
				directionToPerform = 1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.R)){
				actionToPerform = 0;
				directionToPerform = 1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.Q)){
				actionToPerform = 1;
				directionToPerform = -1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.S)){
				actionToPerform = 2;
				directionToPerform = -1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.D)){
				actionToPerform = 3;
				directionToPerform = -1;
				action(actionToPerform,directionToPerform);
			}else if(Gdx.input.isKeyPressed(Input.Keys.F)){
				actionToPerform = 0;
				directionToPerform = -1;
				action(actionToPerform,directionToPerform);
			}else{
				move(0, 0, -1);
			}
		}else{
			action(actionToPerform,directionToPerform);
		}
		
		/*
		if (!perform) {
			hasJump = false;
			this.stateTime = 0;
			
			Action a = brain.updateTask();
			if (!a.isFinish()) {
				actionToPerform = a.getAction();
				directionToPerform = a.getDirection();
				action(actionToPerform, directionToPerform);
			} else {
				actionToPerform = a.getAction();
				directionToPerform = this.directionX;
				action(actionToPerform, directionToPerform);
			}

		} else {
			action(actionToPerform, directionToPerform);
		}
		*/
	}

	public void action(int actionID, int direction) {
		switch (actionID) {
		case -1: //do nothing 
			move(0, 0, -1);
			break;
		case 0: //walk
			if (isOnGround()) {
				move(0.1f*direction, 0, 0);
			} else {
				move(0, 0, activity);
			}
			break;
		case 1: //jump
			if (isOnGround() && !hasJump) {
				move(0.1f, 10f, 1);
				hasJump = true;
			} else {
				move(0.1f * direction, 0, activity);
			}
			break;
		case 2: //special Activity
			performSpecialAction(actionID, direction);
			break;
		case 3: //special Activity2 (to build for the miner)
			performSpecialAction(actionID, direction);
			break;
		}
	}
	
	protected abstract void performSpecialAction(int actionID, int direction);
	
	@Override
	public void render(SpriteBatch sp) {
		if (activity > -1) {
			stateTime += Gdx.graphics.getDeltaTime();
			TextureRegion currentFrame = anim[activity].getKeyFrame(stateTime, true);
			if (directionX == 1) {
				sp.draw(currentFrame, this.x, this.y);
			} else if (directionX == -1) {
				sp.draw(currentFrame, this.x + currentFrame.getRegionWidth(), this.y, -currentFrame.getRegionWidth(),
						currentFrame.getRegionHeight());
			}
			perform = !anim[activity].isAnimationFinished(stateTime);
		} else {
			if (directionX == 1) {
				sp.draw(this.text, this.x, this.y);
			} else if (directionX == -1) {
				sp.draw(this.text, this.x + this.text.getWidth(), this.y, -this.text.getWidth(), this.text.getHeight());
			}
		}
	}
}
