package fr.utbm.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import fr.utbm.block.Block;
import fr.utbm.texture.TextureManager;
import fr.utbm.world.World;

public class EntityPrettyBird extends EntityAnimalFlying {

	public static final int STAYS_ON_GROUND = 10;
	private int timeOnGround;
	private int tmp;
	/*
	 * Bird activity : -1 Dont do anything 0 Idle 1 Fly horizontaly 2 Fly
	 * horizontaly downward 3 Fly horizontaly upward 4 Fly verticaly
	 */

	public EntityPrettyBird(float x, float y, World worldIn) {
		super(x, y, 16, 16, worldIn);
		text = TextureManager.getTexture(222);
		anim = new Animation[2];
		anim[0] = TextureManager.getAnimation(14);
		anim[1] = TextureManager.getAnimation(15);
		directionX = 1;
		activity = -1;
		perform = false;
		actionToPerform = 0;
		directionToPerform = 1;
		timeOnGround = 0;
		tmp = 0;
	}

	public void update() {
		if (!perform) {

			tmp++;
			if (tmp > 10) {
				actionToPerform = (actionToPerform+1)%5;
				tmp = 0;
			}

			Block upBlock = this.getWorldIn().getBlock((int) (this.x) / 16, (int) (this.y + getHeight()) / 16 + 1);
			Block leftBlock = this.getWorldIn().getBlock((int) (this.x) / 16 - 1, (int) (this.y) / 16);
			Block rightBlock = this.getWorldIn().getBlock((int) (this.x + getWidth()) / 16, (int) (this.y) / 16);
			if (((actionToPerform >= 1 && actionToPerform <= 3) && (leftBlock != null || rightBlock != null))
					|| (actionToPerform == 4 && (timeOnGround > STAYS_ON_GROUND || upBlock != null))) {
				directionToPerform = -directionToPerform;
			}

			if (actionToPerform == 3 && upBlock != null) {
				actionToPerform = 2;
			}

			if (actionToPerform == 2 && timeOnGround > STAYS_ON_GROUND) {
				actionToPerform = 3;
			}

			action(actionToPerform, directionToPerform);
		} else {
			action(actionToPerform, directionToPerform);
		}
	}

	public void action(int actionID, int direction) {
		switch (actionID) {

		case 0:
			move(0, 0, 1);
			if (isOnGround()) {
				timeOnGround++;
			}
			break;
		case 1:
			if (!isOnGround()) {
				move(0.1f * direction, 0, 0);
			} else if (timeOnGround > STAYS_ON_GROUND) {
				move(0.1f * direction, 0.1f, 0);
				timeOnGround = 0;
			} else {
				move(0, 0, 1);
				timeOnGround++;
			}
			break;
		case 2:
			if (!isOnGround()) {
				move(0.1f * direction, -0.1f, 0);
			} else {
				move(0, 0, 1);
				timeOnGround++;
			}
			break;
		case 3:
			if (!isOnGround()) {
				move(0.1f * direction, 0.1f, 0);
			} else if (timeOnGround > STAYS_ON_GROUND) {
				move(0.1f * direction, 0.1f, 0);
				timeOnGround = 0;
			} else {
				move(0, 0, 1);
				timeOnGround++;
			}
			break;
		case 4:
			if (!isOnGround()) {
				move(0, 0.1f * direction, 1);
			} else if (timeOnGround > STAYS_ON_GROUND) {
				move(0, 0.1f * direction, 1);
				timeOnGround = 0;
			} else {
				move(0, 0, 1);
				timeOnGround++;
			}
			break;
		}
	}

	@Override
	public void render(SpriteBatch sp) {
		if (activity > -1) {
			stateTime += Gdx.graphics.getDeltaTime();
			TextureRegion currentFrame = anim[activity].getKeyFrame(stateTime, true);
			if (directionX == -1) {
				sp.draw(currentFrame, this.x, this.y);
			} else if (directionX == 1) {
				sp.draw(currentFrame, this.x + currentFrame.getRegionWidth(), this.y, -currentFrame.getRegionWidth(),
						currentFrame.getRegionHeight());
			}
			perform = !anim[activity].isAnimationFinished(stateTime);
		} else {
			if (directionX == -1) {
				sp.draw(this.text, this.x, this.y);
			} else if (directionX == 1) {
				sp.draw(this.text, this.x + this.text.getWidth(), this.y, -this.text.getWidth(), this.text.getHeight());
			}
		}
	}

}