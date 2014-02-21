package com.tszy.jbox2d.testbed.framework;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;


/**
 * DebugDraw ʵ����
 * ����BOX2D�ڲ���ͼ
 * 
 * @author JianbinZhu
 *
 */
public class MyDebugDraw extends DebugDraw {
	private OBBViewportTransform transform;
	private Paint paint;
	private Canvas canvas;
	private Path path;
	@SuppressWarnings("unused")
	private TestbedView testbedView;
	
	public MyDebugDraw(TestbedView testbedView) {
		super(new OBBViewportTransform());

		this.testbedView = testbedView;
		
		path = new Path();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		transform = (OBBViewportTransform) viewportTransform;
		
		//Ĭ���ӽ�λ�ã���Ļ����λ������������λ���غϣ�
		scale = 10;
		camera_x = -(TestbedView.screenW/2) / scale;
		camera_y = (TestbedView.screenH - 10) / scale;
		
		//
		setCamera();
	}
	
	private float camera_x, camera_y, scale;
	public void setCamera() {
		transform.setYFlip(true);
		/**
		 * �����ӽ�λ��
		 * �������ĵ���Զ��(0,0)
		 * ������������������setYFlip(false)�� ����-  ����+  ����-  ����+ 
		 * ��������ֻ���Ļ��ȥ�ı���������λ�� �����Ͻǣ�camera_x, camera_y ��׽���ӷ�Χ
		 * ����3���Ӿ����ű�����1�� = �������أ����������30
		 */
		transform.setCamera(camera_x, camera_y, scale);
	}
	
	public void setCamera(float x, float y) {
		camera_x = x;
		camera_y = y;
		setCamera();
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		setCamera();
	}

	/**
	 * �ƶ��ӽ�
	 */
	private float down_x, down_y;
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		
		switch(action){
			case MotionEvent.ACTION_DOWN:{
				System.out.println("ACTION_DOWN");
				down_x = x;
				down_y = y;
				break;
			}
			
			case MotionEvent.ACTION_MOVE:{
				System.out.println("ACTION_MOVE");
				camera_x -= (x-down_x)/TestbedView.RATE;
				camera_y += (y-down_y)/TestbedView.RATE;
				down_x = x;
				down_y = y;
				setCamera();
				break;
			}
			
			//case MotionEvent.
		}
		
		return true;
	}
	
	/**
	 * ���û�����ɫ
	 * 
	 * @param color
	 */
	public void setColor(Color3f color) {
		int r = (int) (0xff * color.x);
		int g = (int) (0xff * color.y);
		int b = (int) (0xff * color.z);

		paint.setColor(Color.rgb(r, g, b));
	}

	private Rect r_text = new Rect();
	public void draw(Canvas canvas) {
		this.canvas = canvas;
		
		String text = "cx="+camera_x + " cy=" + camera_y;
		paint.getTextBounds(text, 0, text.length(), r_text);
		paint.setColor(0xff00ff00);
		canvas.drawText(text, TestbedView.screenW-r_text.width(), 3+r_text.height(), paint);
	}

	private final Vec2 sp1 = new Vec2();
	private final Vec2 sp2 = new Vec2();

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
		// TODO Auto-generated method stub
		getWorldToScreenToOut(argPoint, sp1);

		setColor(argColor);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(sp1.x, sp1.y, argRadiusOnScreen, paint);
	}

	/**
	 * ��ʵ�Ķ����
	 */
	private final Vec2 temp = new Vec2();
	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		// TODO Auto-generated method stub
		setColor(color);
		paint.setStyle(Style.FILL);

		path.reset();
		for (int i = 0; i < vertexCount; i++) {
			getWorldToScreenToOut(vertices[i], temp);

			if (i == 0)
				path.moveTo(temp.x, temp.y);
			else
				path.lineTo(temp.x, temp.y);
		}
		path.close();

		canvas.drawPath(path, paint);
	}

	/**
	 * ������Բ
	 */
	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		// TODO Auto-generated method stub
		getWorldToScreenToOut(center, sp1);

		setColor(color);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(sp1.x, sp1.y, radius*scale, paint);
	}

	/**
	 * ��ʵ��Բ
	 */
	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
		// TODO Auto-generated method stub
		getWorldToScreenToOut(center, sp1);

		setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(sp1.x, sp1.y, radius*scale, paint);
	}

	/**
	 * ���߶�
	 */
	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		// TODO Auto-generated method stub
		getWorldToScreenToOut(p1, sp1);
		getWorldToScreenToOut(p2, sp2);

		setColor(color);
		paint.setStyle(Style.STROKE);
		canvas.drawLine(sp1.x, sp1.y, sp2.x, sp2.y, paint);
	}

	private final Vec2 temp2 = new Vec2();
	@Override
	public void drawTransform(Transform xf) {
		// TODO Auto-generated method stub
		getWorldToScreenToOut(xf.position, temp);
		temp2.setZero();
		float k_axisScale = 0.4f;
		temp2.x = xf.position.x + k_axisScale * xf.R.col1.x;
		temp2.y = xf.position.y + k_axisScale * xf.R.col1.y;
		getWorldToScreenToOut(temp2, temp2);

		paint.setColor(0xffff0000);
		canvas.drawLine(temp.x, temp.y, temp2.x, temp2.y, paint);

		temp2.x = xf.position.x + k_axisScale * xf.R.col2.x;
		temp2.y = xf.position.y + k_axisScale * xf.R.col2.y;
		getWorldToScreenToOut(temp2, temp2);

		paint.setColor(0xff00ff00);
		canvas.drawLine(temp.x, temp.y, temp2.x, temp2.y, paint);
	}

	/**
	 * ���ַ���
	 */
	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		// TODO Auto-generated method stub
		setColor(color);
		canvas.drawText(s, x, y, paint);
	}
}
