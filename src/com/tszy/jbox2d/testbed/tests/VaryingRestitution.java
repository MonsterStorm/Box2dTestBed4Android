package com.tszy.jbox2d.testbed.tests;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import android.content.Context;

import com.tszy.jbox2d.testbed.framework.TestbedView;

public class VaryingRestitution extends TestbedView {
	public VaryingRestitution(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initTest() {
		// TODO Auto-generated method stub
		{
			BodyDef bd = new BodyDef();
			Body ground = getWorld().createBody(bd);

			PolygonShape shape = new PolygonShape();
			shape.setAsEdge(new Vec2(-40.0f, 0.0f), new Vec2(40.0f, 0.0f));
			ground.createFixture(shape, 0.0f);
		}

		{
			CircleShape shape = new CircleShape();
			shape.m_radius = 1.0f;

			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 1.0f;

			float restitution[] = {0.0f, 0.1f, 0.3f, 0.5f, 0.75f, 0.9f, 1.0f};

			for (int i = 0; i < 7; ++i) {
				BodyDef bd = new BodyDef();
				bd.type = BodyType.DYNAMIC;
				bd.position.set(-10.0f + 3.0f * i, 20.0f);

				Body body = getWorld().createBody(bd);

				fd.restitution = restitution[i];
				body.createFixture(fd);
			}
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Varying Restitution";
	}
}
