package org.webstrips.core;

import org.webstrips.core.data.ObjectCache;
import org.webstrips.core.WebStrips;

public abstract class Preloader implements AsynchronousOperationListener {

	protected enum Action {EXPAND_BACK, EXPAND_FORTH, HALT}

	private Comic comic;
	
	private Object mutex = new Object();
	
	private ComicStrip forth, back;
	
	private ObjectCache<ComicStrip> storage;
	
	private ComicStripIdentifier waitingFor = null;
	
	
	public Preloader(Comic comic, int size) {
		this.comic = comic;
		
		this.storage = new ObjectCache<ComicStrip>(size);
	}
	
	public ComicStrip getPreloaded(ComicStripIdentifier s) {
		
		return (storage == null) ? null : (ComicStrip) storage.query(s.getId());
		
	}
	
	public void setPreloadAnchor(ComicStrip anchor) {
		
		synchronized (mutex) {
		
			WebStrips.getLogger().report(WebStrips.PRELOADER, "Anchor shift to: " + anchor);
			
			resetPlotting();
			
			forth = anchor;
			back = anchor;
			
			waitingFor = null;
			
			doNextStep();
			
		}
	}
	
	private void doNextStep() {
		
		WebStrips.getLogger().report(WebStrips.PRELOADER, "NextStep");
		
		switch (plotNextStep()) {
		case EXPAND_BACK: {
			
			waitingFor = back.previous();
			
			WebStrips.getLogger().report(WebStrips.PRELOADER, "Moving back to: %s", waitingFor);
			
			if (waitingFor != null)
				comic.displayPrevious(this, back);
			else
				doNextStep();
			
			break;
		}
		case EXPAND_FORTH: {
			
			waitingFor = forth.next();
			
			WebStrips.getLogger().report(WebStrips.PRELOADER, "Moving forth to: %s", waitingFor);
			
			if (waitingFor != null)
				comic.displayNext(this, forth);
			else
				doNextStep();
			
			break;
		}
		case HALT: {
			
			WebStrips.getLogger().report(WebStrips.PRELOADER, "Halting");
			
			break;
		}
		
		}
		
	}
	
	public int size() {
		return storage.size();
	}
	
	public int capacity() {
		return storage.capacity();
	}
	
	protected abstract void resetPlotting();
	
	protected abstract Action plotNextStep();
	
	@SuppressWarnings("unchecked")
	public void operationEnded(AsynchronousOperation o) {
		
		synchronized (mutex) {
			
			if (waitingFor == null)
				return;
			
			ComicAsynchronousOperation<ComicStrip> operation = (ComicAsynchronousOperation<ComicStrip>) o;
			
			ComicStrip comicStrip = operation.getResult();
			
			WebStrips.getLogger().report(WebStrips.PRELOADER, "Got: %s", comicStrip);
			
			if (comicStrip != null && comicStrip.equals(waitingFor)) {
				
				ImageRetrievalOperation preloadImage = new ImageRetrievalOperation(comicStrip);
				preloadImage.perform();
				
				WebStrips.getLogger().report(WebStrips.PRELOADER, "Expect: %s", waitingFor);
				
				storage.insert(comicStrip.getIdentifier().getId(), comicStrip);
				
				if (comicStrip.equals(back)) {
					WebStrips.getLogger().report(WebStrips.PRELOADER, "Putting to storage front: %s", comicStrip.getImageSource());
					
					back = comicStrip;
					
				} else {
					WebStrips.getLogger().report(WebStrips.PRELOADER, "Putting to storage back: %s", comicStrip.getImageSource());
					
					forth = comicStrip;
				}
				
				WebStrips.getLogger().report(WebStrips.PRELOADER, "Preloader status: %d/%d", size(), capacity());
				
				doNextStep();
			}
			
		}
		
	}

	public void operationFatalError(AsynchronousOperation o, Throwable e) {}

	public void operationInterrupted(AsynchronousOperation o) {}

	public void operationProgress(AsynchronousOperation o, float progress) {}

	public void operationStarted(AsynchronousOperation o) {}

	@Override
	public void operationError(AsynchronousOperation o, Throwable e) {}

}
