package org.webstrips.android;

import java.io.IOException;

import org.coffeeshop.string.EscapeSequences;
import org.webstrips.core.AsynchronousOperation;
import org.webstrips.core.AsynchronousOperationListener;
import org.webstrips.core.ComicAsynchronousOperation;
import org.webstrips.core.ComicStrip;
import org.webstrips.core.ComicStripIdentifier;
import org.webstrips.core.ImageRetrievalOperation;
import org.webstrips.core.WebStrips;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ComicViewerActivity extends Activity {

	private StripPane pane;

	private RelativeLayout overlay;

	private ProgressBar progress;

	private WebStripsService.ComicControl control = null;

	private static void reportErrorAndClose(ComicViewerActivity activity, Throwable throwable) {
		Message msg = activity.errorHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString("error", throwable.getMessage());
		msg.setData(b);
		activity.errorHandler.sendMessage(msg);
		
	}
	
	private class ViewerState {

		public ViewerState(ComicStrip currentStrip,
				ComicStripIdentifier expectedStrip, Object pane) {
			this.currentStrip = currentStrip;
			this.expectedStrip = expectedStrip;
			this.pane = pane;
		}

		private ComicStrip currentStrip;

		private ComicStripIdentifier expectedStrip;

		private Object pane;

	}

	private ComicStrip currentStrip;

	private ComicStripIdentifier expectedStrip;

	final Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			boolean visible = msg.getData().getBoolean("visible");
			int p = msg.getData().getInt("progress");
			if (p < 0)
				progress.setIndeterminate(true);
			else {
				progress.setIndeterminate(false);
				progress.setProgress(p);
			}
			progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		}
	};

	final Handler errorHandler = new Handler() {
		public void handleMessage(Message msg) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(ComicViewerActivity.this);
			
			String comicName = checkConnection() ? control.getComic().getComicName() : "<unknown>";
			
			dialog.setTitle(R.string.viewer_error_title);
			dialog.setMessage(getString(R.string.viewer_error_message, comicName));
			dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,
						int which) {
					finish();
				}
				
			});
			
			dialog.show();
		}
	};
	
	private static class NavigationOperationListener implements
			AsynchronousOperationListener {

		private ComicViewerActivity act;

		public NavigationOperationListener(ComicViewerActivity act) {
			this.act = act;
		}

		@SuppressWarnings("unchecked")
		public void operationEnded(AsynchronousOperation o) {
			if (!act.checkConnection())
				return;

			ComicAsynchronousOperation<ComicStrip> operation = (ComicAsynchronousOperation<ComicStrip>) o;

			WebStrips.getLogger().report(
					"Strip returned " + operation.getComicStripIdentifier() + " " + act.expectedStrip);
			
			if (!operation.getComicStripIdentifier().equals(act.expectedStrip))
				return;

			if (operation.getResult() == null)
				return;

			if (act.control != null)
				act.control.setAnchor(operation.getResult());

			act.displayStrip(operation.getResult());

		}

		public void operationFatalError(AsynchronousOperation o, Throwable e) {
			if (!act.checkConnection())
				return;

			ComicAsynchronousOperation<?> operation = (ComicAsynchronousOperation<?>) o;

			if (!operation.getComicStripIdentifier().equals(act.expectedStrip))
				return;
			
			WebStrips.getLogger().report(e);

			reportErrorAndClose(act, e);
			
			
		}

		public void operationInterrupted(AsynchronousOperation o) {
			if (!act.checkConnection())
				return;

			ComicAsynchronousOperation<?> operation = (ComicAsynchronousOperation<?>) o;

			if (!operation.getComicStripIdentifier().equals(act.expectedStrip))
				return;

		}

		public void operationProgress(AsynchronousOperation o, float progress) {

			if (!act.checkConnection())
				return;

			ComicAsynchronousOperation<?> operation = (ComicAsynchronousOperation<?>) o;

			if (!operation.getComicStripIdentifier().equals(act.expectedStrip))
				return;

			act.setProgressIndicator(progress);
		}

		public void operationStarted(AsynchronousOperation o) {
			if (!act.checkConnection())
				return;

			ComicAsynchronousOperation<?> operation = (ComicAsynchronousOperation<?>) o;

			if (!operation.getComicStripIdentifier().equals(act.expectedStrip))
				return;

			act.setProgressIndicator(-1);
		}

		public void operationError(AsynchronousOperation o, Throwable e) {

		}

	};

	private NavigationOperationListener navigationOperationListener = new NavigationOperationListener(
			this);

	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {

			control = (WebStripsService.ComicControl) service;

			if (control == null) {
				finish();
				return;
			}

			if (currentStrip == null)
				anchor();

		}

		public void onServiceDisconnected(ComponentName className) {

			control = null;

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.comicviewer);

		pane = (StripPane) findViewById(R.id.comicPane);

		progress = (ProgressBar) findViewById(R.id.transferProgress);

		overlay = (RelativeLayout) findViewById(R.id.viewer_overlaylayout);

		overlay.setVisibility(View.INVISIBLE);

		progress.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			WebStrips.getSettings().commit();
		} catch (IOException e) {
			WebStrips.getLogger().report(e);
		}

		unbindService(connection);

		currentStrip = null;
		expectedStrip = null;

	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent i = getIntent();

		currentStrip = null;
		expectedStrip = null;

		final Object data = getLastNonConfigurationInstance();

		if (data != null) {
			ViewerState state = (ViewerState) data;
			currentStrip = state.currentStrip;
			expectedStrip = state.expectedStrip;
			pane.setPersistentData(state.pane);
		}

		bindService(new Intent("org.webstrips.COMIC", i.getData(),
				ComicViewerActivity.this, WebStripsService.class), connection,
				0);

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		ViewerState state = new ViewerState(currentStrip, expectedStrip, pane
				.getPersistentData());

		return state;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		WebStrips.getLogger().report("Create menu");

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.comicmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.comiclist:
			Intent listintent = new Intent(this, ComicListActivity.class);

			startActivity(listintent);
			finish();

			break;
		case R.id.webpage:
			if (pane.getComicStrip() == null)
				return false;
			String url = currentStrip.getSource().toString();

			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
					.parse(url));
			startActivity(intent);
			break;
		case R.id.previous:
			previous();
			break;
		case R.id.next:
			next();
			break;
		case R.id.first:
			first();
			break;
		case R.id.newest:
			newest();
			break;
		}
		return true;
	}

	private boolean dragging = false;

	private float dragX, dragY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		synchronized (this) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				dragging = true;
				dragX = event.getRawX();
				dragY = event.getRawY();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (dragging) {
					float vx = event.getRawX() - dragX;
					float vy = event.getRawY() - dragY;

					pane.setVelocity(vx, vy);

					dragX = event.getRawX();
					dragY = event.getRawY();
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				dragging = false;
			}
		}

		gestureDetector.onTouchEvent(event);

		return true;
	}

	private GestureDetector.SimpleOnGestureListener zoomDetector = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {

			float positionX = (float) e.getRawX() / (float) pane.getWidth();
			float positionY = (float) e.getRawY() / (float) pane.getHeight();

			if (positionX < 0.1) {
				previous();

				return true;
			}

			if (positionX > 0.9) {
				next();

				return true;
			}

			if (positionY < 0.1) {

				return true;
			}

			// overlay.setVisibility(View.VISIBLE);

			return false;
		}

		public boolean onDoubleTap(MotionEvent e) {
			/*
			 * if (data.zoom.getGoal() < 1.01f) data.zoom.setGoal(2); else
			 * data.zoom.setGoal(1);
			 */
			return true;
		}

	};

	private GestureDetector gestureDetector = new GestureDetector(zoomDetector);

	private void anchor() {
		if (control == null)
			return;

		expectedStrip = control.getComic().getAnchorIdentifier();
		control.anchor(navigationOperationListener);

	}

	private void next() {
		if (control == null || currentStrip == null
				|| currentStrip.next() == null)
			return;

		expectedStrip = currentStrip.next();
		control.next(navigationOperationListener, currentStrip);

	}

	private void previous() {
		if (control == null || currentStrip == null
				|| currentStrip.previous() == null)
			return;

		expectedStrip = currentStrip.previous();
		control.previous(navigationOperationListener, currentStrip);

	}

	private void first() {
		if (control == null || currentStrip == null
				|| currentStrip.getComic().getFirst() == null)
			return;

		expectedStrip = currentStrip.getComic().getFirst();
		control.first(navigationOperationListener);

	}

	private void newest() {
		if (control == null || currentStrip == null
				|| currentStrip.getComic().getNewest() == null)
			return;

		expectedStrip = currentStrip.getComic().getNewest();
		control.newest(navigationOperationListener);
	}

	private void displayStrip(ComicStrip strip) {

		if (strip == null) {
			pane.setComicStrip(null);
			currentStrip = null;
			expectedStrip = null;
			hideProgressIndicator();
			return;
		}

		if (!checkConnection())
			return;

		String title = EscapeSequences
				.stripAmpersandSequence(((strip == null) ? "" : strip
						.getTitle()
						+ " - "));

		setTitle(control.getComic().getComicName() + " - " + title);

		currentStrip = strip;
		expectedStrip = currentStrip.getIdentifier();

		// enableNavigation();

		ImageRetrievalOperation imageOperation = new ImageRetrievalOperation(
				currentStrip);

		imageOperation.addListener(new AsynchronousOperationListener() {

			public void operationEnded(AsynchronousOperation o) {

				if (!checkConnection())
					return;

				ImageRetrievalOperation operation = (ImageRetrievalOperation) o;

				if (!operation.getComicStripIdentifier().equals(expectedStrip))
					return;

				Bitmap image = BitmapFactory
						.decodeStream(operation.getResult());

				pane.setComicStrip(image);
				hideProgressIndicator();

			}

			public void operationFatalError(AsynchronousOperation o, Throwable e) {

				if (!checkConnection())
					return;

				ImageRetrievalOperation operation = (ImageRetrievalOperation) o;

				WebStrips.getLogger().report(
						"Strip returned " + operation.getComicStripIdentifier() + " " + expectedStrip);
				
				if (!operation.getComicStripIdentifier().equals(expectedStrip))
					return;
				
				reportErrorAndClose(ComicViewerActivity.this, e);
			}

			public void operationInterrupted(AsynchronousOperation o) {
				if (!checkConnection())
					return;

			}

			public void operationProgress(AsynchronousOperation o,
					float progress) {

				if (!checkConnection())
					return;

				ImageRetrievalOperation operation = (ImageRetrievalOperation) o;

				if (!operation.getComicStripIdentifier().equals(expectedStrip))
					return;

				setProgressIndicator(progress);
			}

			public void operationStarted(AsynchronousOperation o) {

				if (!checkConnection())
					return;

				setProgressIndicator(-1);

			}

			public void operationError(AsynchronousOperation o, Throwable e) {

			}

		});

		imageOperation.perform();

	}

	private void setProgressIndicator(float progress) {
		Message msg = progressHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("progress", progress < 0 ? -1 : (int) (progress * 100));
		b.putBoolean("visible", true);
		msg.setData(b);
		progressHandler.sendMessage(msg);
	}

	private void hideProgressIndicator() {
		Message msg = progressHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("progress", 0);
		b.putBoolean("visible", false);
		msg.setData(b);
		progressHandler.sendMessage(msg);
	}

	private void disableNavigation() {
		/*
		 * thi
		 * 
		 * linkAction.setEnabled(false); firstAction.setEnabled(false);
		 * previousAction.setEnabled(false); nextAction.setEnabled(false);
		 * newestAction.setEnabled(false); zoomIn.setEnabled(false);
		 * zoomOut.setEnabled(false); zoomActual.setEnabled(false);
		 */
	}

	/**
	 * Enables the navigation buttons according to the current comic
	 * capabilities.
	 * 
	 */
	private void enableNavigation() {
		/*
		 * if (currentStrip != null) {
		 * previousAction.setEnabled(currentStrip.previous() != null);
		 * nextAction.setEnabled(currentStrip.next() != null);
		 * linkAction.setEnabled(currentStrip.getSource() != null); }
		 * 
		 * if (currentComic != null) {
		 * newestAction.setEnabled(currentComic.getNewest() != null);
		 * firstAction.setEnabled(currentComic.getFirst() != null); }
		 */
	}

	private boolean checkConnection() {
		return connection != null && control != null;
	}

}
