package com.tszy.jbox2d.testbed.framework;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


/**
 * ���Ի���
 * 
 * @author JianbinZhu
 *
 */
public abstract class TestbedView extends SurfaceView implements Callback, Runnable {
	public static final int GAME_HEART = 1000 / 30; // ÿ��ˢ��30��
	public static final float RATE = 30.0f; // ������

	public static int screenW, screenH;

	private Thread thread;
	private SurfaceHolder holder;

	protected Paint paint;

	protected World world;
	protected MouseJointDef mouseJointDef;
	protected MouseJoint mouseJoint;
	protected Body m_ground;
	private MyDebugDraw debugDraw;	//���Ի�ͼ
	
	private float dt = 1.0f/30.0f; 	//ģ��Ƶ��
	private int velIter = 8;				//�ٶȵ������� 
	private int posIter = 3; 				//λ�õ�������
	

	public TestbedView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
/////////////////////////////////////////////////////////////////////////
	private void init() {
		holder = getHolder();
		holder.addCallback(this);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);// �޾��
		paint.setStyle(Style.FILL); // �����ʽ
		paint.setTextSize(12); // �����С
	}
	
	private void initWorld() {
		Vec2 gravity = new Vec2(0, -10f);
		world = new World(gravity, true);

		BodyDef bodyDef = new BodyDef();
		m_ground = world.createBody(bodyDef);
		
		mouseJointDef = new MouseJointDef();
		
		debugDraw = new MyDebugDraw(this);//DebugDraw.e_aabbBit |
		debugDraw.setFlags( DebugDraw.e_shapeBit 
				| DebugDraw.e_jointBit 
				| DebugDraw.e_centerOfMassBit);
		world.setDebugDraw(debugDraw);
	}
	
	private void destoryWorld(){
		Body body = world.getBodyList();
		
		for(int i=0; i<world.getBodyCount(); ++i){
			if(body != null)
				world.destroyBody(body);
			body = body.getNext();
		}
		
		Joint joint = world.getJointList();
		
		for(int i=0; i<world.getJointCount(); ++i){
			if(joint != null)
				world.destroyJoint(joint);
			joint = joint.getNext();
		}
		
		world = null;
		debugDraw = null;
		mouseJointDef = null;
		m_ground = null;
	}
	
	public World getWorld() {
		return world;
	}
	
////////////////////////////////////////////////////////////////////////////////
	/**
	 * ִ����Ϸ�߼�����
	 */
	public void _update() {
		world.step(dt, velIter, posIter);
	}

	private Rect rect = new Rect();
	public void drawDeclare(Canvas canvas) {
		String text = "��ʹ֮���ʾ��Demo";
		// ��ȡ�ı����
		paint.getTextBounds(text, 0, text.length(), rect);
		// ����Ļ����λ����ʾ�ı�
		paint.setColor(0xfff000f0); // ע�������λ ff ������͸���ȣ������õĻ�������ȫ͸���ˣ��������κ�Ч��
		canvas.drawText(text, 
				(screenW - rect.width()) / 2, screenH / 2 + rect.height() / 2, 
				paint);
	}
	
	/**
	 * ���û�����ɫ
	 * @param color
	 */
	public void setColor(int color) {
		int red = (color & 0xff0000) >> 16;
		int green = (color & 0x00ff00) >> 8;
		int blue = (color & 0x0000ff);
		paint.setColor(Color.rgb(red, green, blue));
	}

	/**
	 * ����FPS
	 * @param canvas
	 */
	private void drawFPS(Canvas canvas) {
		setColor(0x00ff00);
		if (useTime > 0){
			canvas.drawText("FPS:" + (1000 / useTime), 
					3, 3 + paint.getTextSize(), 
					paint);
		}
	}
	
	/**
	 * BOX2D ���Ի�ͼ
	 * @param canvas
	 */
	public void worldDraw(Canvas canvas) {
		debugDraw.draw(canvas);
		world.drawDebugData();
	}
	
	/**
	 * �����ؽ�
	 */
	private Vec2 v1 = new Vec2();
	private Vec2 v2 = new Vec2();
	private void mouseJointDraw(Canvas canvas) {
		// �����ؽ�
		if (mouseJoint != null) {
			paint.setColor(0xff00ff00);
			
			mouseJoint.getAnchorA(v1);
			//ת�����ӽ��е�λ��
			debugDraw.getWorldToScreenToOut(v1, v1);

			mouseJoint.getAnchorB(v2);
			//ת�����ӽ��е�λ��
			debugDraw.getWorldToScreenToOut(v2, v2);
			
			canvas.drawLine(v1.x, v1.y, v2.x, v2.y, paint);
		}
	}
	
	/**
	 * ִ����Ϸ����
	 */
	public void _draw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		worldDraw(canvas);
		mouseJointDraw(canvas);
		drawFPS(canvas);
		//drawDeclare(canvas);
	}
	
	public abstract void initTest();
	public abstract String getName();
	
	
////////////////////////////////////////////////////////////////////////////////
	/**
	 * ����λ��תΪ��Ļλ��
	 * @param p
	 * @return
	 */
	public static Vec2 world2Scn(Vec2 p) {
		Vec2 out = new Vec2();
		world2ScnOut(p, out);
		return out;
	}
	
	public static void world2ScnOut(Vec2 p, Vec2 out) {
		out.set(p.x*RATE, p.y*RATE);
	}
	
	/**
	 * ��Ļλ��תΪ����λ��
	 * @param p
	 * @return
	 */
	public static Vec2 scn2wrold(Vec2 p) {
		Vec2 out = new Vec2();
		scn2wroldOut(p, out);
		return out;
	}
	
	public static void scn2wroldOut(Vec2 p, Vec2 out) {
		out.set(p.x/RATE, p.y/RATE);
	}
	
	
///////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean b = false;
		
		b = mouseJointEvent(event);
		if(!b)
			b = debugDraw.onTouchEvent(event);
		if(!b)
			return super.onTouchEvent(event);
		
		return b;
	}
	
	//�����ѯ
	private final AABB queryAABB = new AABB();
	private final TestQueryCallback callback = new TestQueryCallback();
	private Body mouseDragBody;	//��ǰ���קס������
	private final Vec2 mouseDownPoint = new Vec2(0, 0);

	private boolean mouseJointEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
//		mouseDownPoint.set(event.getX(), event.getY());
//		scn2wroldOut(mouseDownPoint, mouseDownPoint);
		//�ӽ��е�λ��
		debugDraw.getScreenToWorldToOut(x, y, mouseDownPoint);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN : {
				if (mouseJoint != null) {
					world.destroyJoint(mouseJoint);
					mouseJoint = null;
					mouseDragBody = null;
				}

				//��ת��Ϊ�������е�λ�ã���Ϊ�����λ��ֻ�ǿ��������λ�ö���
				queryAABB.lowerBound.set(mouseDownPoint.x - 3/RATE, mouseDownPoint.y - 3/RATE);
				queryAABB.upperBound.set(mouseDownPoint.x + 3/RATE, mouseDownPoint.y + 3/RATE);
				world.queryAABB(callback, queryAABB);

				if (mouseDragBody != null) {

					mouseJointDef.bodyA = m_ground;
					mouseJointDef.bodyB = mouseDragBody;
					mouseJointDef.target.set(mouseDownPoint); // Ŀ�ĵ�
					mouseJointDef.maxForce = 1000f * mouseDragBody.getMass(); // �������
					mouseJoint = (MouseJoint) world.createJoint(mouseJointDef); // ���ؽ�
					mouseDragBody.setAwake(true); // ����body
					
					return true;
				}
				
				break;
			}

			case MotionEvent.ACTION_MOVE : {
				if (mouseJoint != null) {
					mouseJoint.setTarget(mouseDownPoint);
					return true;
				}
				break;
			}

			case MotionEvent.ACTION_UP : {
				if (mouseJoint != null) {
					world.destroyJoint(mouseJoint);
					mouseJoint = null;
					mouseDragBody = null;
					return true;
				}
				break;
			}
		}

		return false;
	}
	
	

	//��ѯ�ص�
	class TestQueryCallback implements QueryCallback {
		/**
		 * �����ѯ���������false��ֹ��ѯ
		 */
		@Override
		public boolean reportFixture(Fixture fixture) {
			// TODO Auto-generated method stub
			
			Body body = fixture.getBody();
			AABB aabb = fixture.getAABB();

			if (body.getType() == BodyType.DYNAMIC) {
				if (AABB.testOverlap(aabb, queryAABB)) {
					mouseDragBody = body;
					return false;
				}
			}
			return true;
		}
	}
	
///////////////////////////////////////////////////////////////////////////////
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		screenW = getWidth();
		screenH = getHeight();
		
		//һ��Ҫ�ڻ�ȡ��Ļ��ߺ����
		initWorld();
		initTest();

		//���߳�
		thread = new Thread(this);
		isRun = true;
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		// ����Ļ��ת��ʱ�����»�ȡ��Ļ���
		screenW = getWidth();
		screenH = getHeight();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isRun = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thread = null;
		destoryWorld();
	}

	private boolean isRun; // �߳����б�־
	private int useTime; // ��¼ÿ��ˢ��ʹ�õ�ʱ��
	@Override
	public void run() {
		// TODO Auto-generated method stub
		long start, end;

		while (isRun) {
			start = System.currentTimeMillis();
			{
				Canvas canvas = holder.lockCanvas();
				_update(); // ˢ�½���������Ԫ��
				_draw(canvas); // ���ƽ���Ԫ��
				end = System.currentTimeMillis();
				holder.unlockCanvasAndPost(canvas);
			}
			useTime = (int) (end - start);

			if (useTime < GAME_HEART) { // ��֤ÿ��ˢ��ʱ������ͬ
				try {
					Thread.sleep(GAME_HEART - useTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
