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
 * 测试基类
 * 
 * @author JianbinZhu
 *
 */
public abstract class TestbedView extends SurfaceView implements Callback, Runnable {
	public static final int GAME_HEART = 1000 / 30; // 每秒刷新30次
	public static final float RATE = 30.0f; // 比例尺

	public static int screenW, screenH;

	private Thread thread;
	private SurfaceHolder holder;

	protected Paint paint;

	protected World world;
	protected MouseJointDef mouseJointDef;
	protected MouseJoint mouseJoint;
	protected Body m_ground;
	private MyDebugDraw debugDraw;	//调试绘图
	
	private float dt = 1.0f/30.0f; 	//模拟频率
	private int velIter = 8;				//速度迭代次数 
	private int posIter = 3; 				//位置迭代次数
	

	public TestbedView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
/////////////////////////////////////////////////////////////////////////
	private void init() {
		holder = getHolder();
		holder.addCallback(this);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);// 无锯齿
		paint.setStyle(Style.FILL); // 填充样式
		paint.setTextSize(12); // 字体大小
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
	 * 执行游戏逻辑方法
	 */
	public void _update() {
		world.step(dt, velIter, posIter);
	}

	private Rect rect = new Rect();
	public void drawDeclare(Canvas canvas) {
		String text = "天使之翼的示例Demo";
		// 获取文本宽高
		paint.getTextBounds(text, 0, text.length(), rect);
		// 在屏幕中央位置显示文本
		paint.setColor(0xfff000f0); // 注意最高两位 ff 代表画笔透明度，不设置的画就是完全透明了，看不到任何效果
		canvas.drawText(text, 
				(screenW - rect.width()) / 2, screenH / 2 + rect.height() / 2, 
				paint);
	}
	
	/**
	 * 设置画笔颜色
	 * @param color
	 */
	public void setColor(int color) {
		int red = (color & 0xff0000) >> 16;
		int green = (color & 0x00ff00) >> 8;
		int blue = (color & 0x0000ff);
		paint.setColor(Color.rgb(red, green, blue));
	}

	/**
	 * 绘制FPS
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
	 * BOX2D 调试绘图
	 * @param canvas
	 */
	public void worldDraw(Canvas canvas) {
		debugDraw.draw(canvas);
		world.drawDebugData();
	}
	
	/**
	 * 画鼠标关节
	 */
	private Vec2 v1 = new Vec2();
	private Vec2 v2 = new Vec2();
	private void mouseJointDraw(Canvas canvas) {
		// 画鼠标关节
		if (mouseJoint != null) {
			paint.setColor(0xff00ff00);
			
			mouseJoint.getAnchorA(v1);
			//转换成视角中的位置
			debugDraw.getWorldToScreenToOut(v1, v1);

			mouseJoint.getAnchorB(v2);
			//转换成视角中的位置
			debugDraw.getWorldToScreenToOut(v2, v2);
			
			canvas.drawLine(v1.x, v1.y, v2.x, v2.y, paint);
		}
	}
	
	/**
	 * 执行游戏绘制
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
	 * 世界位置转为屏幕位置
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
	 * 屏幕位置转为世界位置
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
	
	//点击查询
	private final AABB queryAABB = new AABB();
	private final TestQueryCallback callback = new TestQueryCallback();
	private Body mouseDragBody;	//当前鼠标拽住的物体
	private final Vec2 mouseDownPoint = new Vec2(0, 0);

	private boolean mouseJointEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
//		mouseDownPoint.set(event.getX(), event.getY());
//		scn2wroldOut(mouseDownPoint, mouseDownPoint);
		//视角中的位置
		debugDraw.getScreenToWorldToOut(x, y, mouseDownPoint);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN : {
				if (mouseJoint != null) {
					world.destroyJoint(mouseJoint);
					mouseJoint = null;
					mouseDragBody = null;
				}

				//得转化为在世界中的位置，因为点击的位置只是看到的相对位置而已
				queryAABB.lowerBound.set(mouseDownPoint.x - 3/RATE, mouseDownPoint.y - 3/RATE);
				queryAABB.upperBound.set(mouseDownPoint.x + 3/RATE, mouseDownPoint.y + 3/RATE);
				world.queryAABB(callback, queryAABB);

				if (mouseDragBody != null) {

					mouseJointDef.bodyA = m_ground;
					mouseJointDef.bodyB = mouseDragBody;
					mouseJointDef.target.set(mouseDownPoint); // 目的点
					mouseJointDef.maxForce = 1000f * mouseDragBody.getMass(); // 最大拉力
					mouseJoint = (MouseJoint) world.createJoint(mouseJointDef); // 鼠标关节
					mouseDragBody.setAwake(true); // 唤醒body
					
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
	
	

	//查询回调
	class TestQueryCallback implements QueryCallback {
		/**
		 * 报告查询结果，返回false终止查询
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
		
		//一定要在获取屏幕宽高后调用
		initWorld();
		initTest();

		//起线程
		thread = new Thread(this);
		isRun = true;
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		// 当屏幕旋转的时候重新获取屏幕宽高
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

	private boolean isRun; // 线程运行标志
	private int useTime; // 记录每次刷屏使用的时间
	@Override
	public void run() {
		// TODO Auto-generated method stub
		long start, end;

		while (isRun) {
			start = System.currentTimeMillis();
			{
				Canvas canvas = holder.lockCanvas();
				_update(); // 刷新界面上所有元素
				_draw(canvas); // 绘制界面元素
				end = System.currentTimeMillis();
				holder.unlockCanvasAndPost(canvas);
			}
			useTime = (int) (end - start);

			if (useTime < GAME_HEART) { // 保证每次刷屏时间间隔相同
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
