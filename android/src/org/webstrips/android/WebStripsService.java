package org.webstrips.android;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Vector;

import org.webstrips.core.AsynchronousOperationListener;
import org.webstrips.core.Comic;
import org.webstrips.core.ComicManager;
import org.webstrips.core.ComicManagerListener;
import org.webstrips.core.ComicStrip;
import org.webstrips.core.WebStrips;
import org.webstrips.core.comic.ComicException;

import android.app.Service;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class WebStripsService extends Service {
	
	private ComicManager comics;
	
	public interface ComicListener {
		
		
		
	}

	
	private Vector<DataSetObserver> observers = new Vector<DataSetObserver>();
	
    public class ComicControl extends Binder {
    	
    	private Comic comic;
    	
    	private ComicControl(Comic comic) {
    		super();
    		this.comic = comic;
    	}
    	
    	public void setAnchor(ComicStrip a) {
    		
    		comic.setAnchor(a);
    		
    	}
    	
    	public void next(AsynchronousOperationListener l, ComicStrip origin) {
    		
    		comic.displayNext(l, origin);
    		
    	}
    	
    	public void previous(AsynchronousOperationListener l, ComicStrip origin) {
	
    		comic.displayPrevious(l, origin);
    	}
    	
    	public void first(AsynchronousOperationListener l) {

    		
    		comic.displayFirst(l);
    		
    	}
    	
    	public void newest(AsynchronousOperationListener l) {

    		
    		comic.displayNewest(l);
    		
    	}

    	public void anchor(AsynchronousOperationListener l) {

    		
    		comic.displayAnchor(l);
    		
    	}
    	
    	public Comic getComic() {
    		return comic;
    	}   
    	
    }

    public class ComicManagerControl extends Binder {
    	
    	public void addComicManagerObserver(DataSetObserver observer) {
    		WebStrips.getLogger().report("Add observer");
    		observers.add(observer);
    	}
    	
    	public void removeComicManagerObserver(DataSetObserver observer) {
    		WebStrips.getLogger().report("Remove observer");
    		observers.remove(observer);
    	}
    	
    	public Iterator<Comic> getComics() {
    		return comics.iterator();
    	}

    	public int getComicCount() {
    		return comics.getComicCount();
    	}
    	
    	public Comic getComic(int index) {
    		return comics.getComic(index);
    	}
    	
    	public void selectComic(Comic comic) {
    		
    	}
    	
    	public void removeComic(Comic comic) {
    		comics.removeComic(comic);
    	}
    	
    	public void reloadComic(Comic comic) {
    		comics.reloadComic(comic);
    	}
    	
    	public boolean importComic(File bundle) {
    		try {
				return comics.importBundle(bundle.toURL());
			} catch (MalformedURLException e) {
				WebStrips.getLogger().report(e);
				return false;
			}
    	}
    	
    }
    
    @Override
    public void onCreate() {

    	comics = new ComicManager();
    	
    	try {
			comics.loadComics();
			
			WebStrips.getLogger().report("Total %d comics found", comics.getComicCount());
			
			comics.addComicManagerListener(new ComicManagerListener() {

				public void comicAdded(ComicManager source, Comic newComic) {
					
					for (DataSetObserver o : observers) {
							o.onChanged();
					}
					
				}

				public void comicReloaded(ComicManager source,
					Comic removedComic, Comic newComic) {

					for (DataSetObserver o : observers) {
						o.onChanged();
					}
					
				}

				public void comicRemoved(ComicManager source, Comic removedComic) {
					for (DataSetObserver o : observers) {
						o.onChanged();
					}
				}
				
			});
			
		} catch (ComicException e) {
			WebStrips.getLogger().report(e);
		}

    }
/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.
    }
*/
    @Override
    public void onDestroy() {
		try {
			WebStrips.getSettings().commit();
		} catch (IOException e) {
			WebStrips.getLogger().report(e);
		}
    	
    	WebStrips.getLogger().report("Service destroy");
    }

    @Override
	public void onLowMemory() {
		WebStrips.getCache().flush();

		super.onLowMemory();
	}
	@Override
    public IBinder onBind(Intent intent) {

    	if ("org.webstrips.COMIC".equals(intent.getAction())) {
    		Uri uri = intent.getData();
    		
    		if (uri == null)
    			return null;
    		
    		WebStrips.getLogger().report(uri.toString());
    		
    		String comicId = uri.getHost();
    		
    		if (comicId == null)
    			return null;
    		
    		WebStrips.getLogger().report(comicId);
    		
    		Comic comic = comics.findComicByIdentifier(comicId);
    		
    		if (comic == null)
    			return null;
    		
    		return new ComicControl(comic);
    		
    	}
    	
    	if ("org.webstrips.MANAGER".equals(intent.getAction())) {
    		return managerControl;
    	}
    	
        return null;
    }

    @Override
	public boolean onUnbind(Intent intent) {
		
    	
    	
		return false;
	}

    private ComicManagerControl managerControl = new ComicManagerControl();
    
}