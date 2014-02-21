package com.tszy.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import android.content.Context;

import com.tszy.jbox2d.testbed.framework.TestbedView;

public class Domino extends TestbedView {

	public Domino(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initTest() {
		// TODO Auto-generated method stub
		{ // Floor
			FixtureDef fd = new FixtureDef();
			PolygonShape sd = new PolygonShape();
			sd.setAsBox(20.0f, 1.0f);
			fd.shape = sd;

			BodyDef bd = new BodyDef();
			bd.position = new Vec2(0.0f, -.5f);
			getWorld().createBody(bd).createFixture(fd);

		}

		{ // Platforms
			for (int i = 0; i < 4; i++) {
				FixtureDef fd = new FixtureDef();
				PolygonShape sd = new PolygonShape();
				sd.setAsBox(15.0f, 0.125f);
				fd.shape = sd;

				BodyDef bd = new BodyDef();
				bd.position = new Vec2(0.0f, 5f + 5f * i);
				getWorld().createBody(bd).createFixture(fd);
			}
		}

		{
			FixtureDef fd = new FixtureDef();
			PolygonShape sd = new PolygonShape();
			sd.setAsBox(0.125f, 2f);
			fd.shape = sd;
			fd.density = 25.0f;

			BodyDef bd = new BodyDef();
			bd.type = BodyType.DYNAMIC;
			float friction = .5f;
			int numPerRow = 25;

			for (int i = 0; i < 4; ++i) {
				for (int j = 0; j < numPerRow; j++) {
					fd.friction = friction;
					bd.position = new Vec2(-14.75f + j * (29.5f / (numPerRow - 1)), 7.3f + 5f * i);
					if (i == 2 && j == 0) {
						bd.angle = -0.1f;
						bd.position.x += .1f;
					} else if (i == 3 && j == numPerRow - 1) {
						bd.angle = .1f;
						bd.position.x -= .1f;
					} else
						bd.angle = 0f;
					Body myBody = getWorld().createBody(bd);
					myBody.createFixture(fd);
				}
			}
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Dominos";
	}

}
