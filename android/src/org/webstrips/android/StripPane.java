package org.webstrips.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class StripPane extends SurfaceView implements SurfaceHolder.Callback {

	private CanvasThread thread;

	private boolean dirty = true;

	private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private static class StipPaneData { 
	
		public PointF offset = new PointF();
	
		public FloatTracker zoom = new FloatTracker(1, 1);
	
		public Bitmap comicStrip;

	}

	private FloatTracker velocityX = new FloatTracker(0, 0, 0.2f, 0.5f), velocityY = new FloatTracker(0, 0, 0.2f, 0.5f);
	
	private StipPaneData data = new StipPaneData();

	public StripPane(Context context, AttributeSet attr) {
		super(context, attr);
		getHolder().addCallback(this);
		thread = new CanvasThread(getHolder());


	}

	private RectF canvasRect = new RectF();
	
	public void setDirty() {
		dirty = true;
	}
	
	public void setVelocity(float vx, float vy) {
		
		velocityX.setValue(vx);
		velocityY.setValue(vy);
		dirty = true;
	}
	
	public Object getPersistentData() {
		return data;
	}
	
	public void setPersistentData(Object o) {
		if (o instanceof StripPane.StipPaneData) {
			data = (StripPane.StipPaneData) o;
			dirty = true;
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (data.comicStrip == null)
			return;
		canvas.drawColor(Color.BLACK);
		
		canvas.drawBitmap(data.comicStrip, null, canvasRect, paint);
	}

	public void setComicStrip(Bitmap strip) {
		synchronized (this) {
			data.comicStrip = strip;

			data.comicStrip = strip;
			
			data.offset.x = 0;
			data.offset.y = 0;
			dirty = true;
		}
	}
	
	public Bitmap getComicStrip() {
		return data.comicStrip;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		thread = new CanvasThread(getHolder());
		thread.run = true;
		thread.start();
		dirty = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.run = false;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	private static final long FRAME_LENGTH = 1000 / 20;
	
	class CanvasThread extends Thread {

		private SurfaceHolder surfaceHolder;

		private boolean run = false;
		
		public CanvasThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		@Override
		public void run() {

			Canvas c;

			while (run) {

				c = null;

				long miliseconds = System.currentTimeMillis();
				
				dirty |= !data.zoom.onGoal() || !velocityX.onGoal() || !velocityY.onGoal();
				
				if (dirty) {

					try {

						data.zoom.track();
						
						c = surfaceHolder.lockCanvas(null);
						synchronized (surfaceHolder) {
							
							if (data.comicStrip != null) {
								float width = data.zoom.getValue() * data.comicStrip.getWidth(); 
								float height = data.zoom.getValue() * data.comicStrip.getHeight();
								
								data.offset.x -= velocityX.track();
								data.offset.y -= velocityY.track();
								
								data.offset.x = c.getWidth() > width ? -((c.getWidth() - width) / 2) :
									Math.min(width - c.getWidth(), Math.max(0, data.offset.x));

								data.offset.y = c.getHeight() > height ? -((c.getHeight() - height) / 2) :
									Math.min(height - c.getHeight(), Math.max(0, data.offset.y));
								
								canvasRect.left = -data.offset.x;
								canvasRect.top = -data.offset.y;
								canvasRect.right = -data.offset.x + width;
								canvasRect.bottom = -data.offset.y + height;							
							}
							
							
							
							onDraw(c);
							dirty = false;
						}

					} finally {

						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state

						if (c != null) {

							surfaceHolder.unlockCanvasAndPost(c);

						}

					}
				}

				try {
					sleep(Math.min(FRAME_LENGTH, Math.abs(System.currentTimeMillis() - miliseconds)));
				} catch (InterruptedException e) {
					break;
				}
				
			}

		}

	}


	
	
}