package org.webstrips.android;

import java.io.File;
import java.util.HashMap;

import org.webstrips.core.Comic;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ComicListActivity extends ListActivity {

	private WebStripsService.ComicManagerControl managerBinding;

	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

	private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {

			managerBinding = (WebStripsService.ComicManagerControl) service;

			if (managerBinding == null) {
				finish();
				return;
			}

			setListAdapter(new ComicListActivity.ComicListAdapter());

		}

		public void onServiceDisconnected(ComponentName className) {

			managerBinding = null;

		}
	};

	class ComicListAdapter implements ListAdapter {

		private LayoutInflater inflater;

		public ComicListAdapter() {

			inflater = getLayoutInflater();

		}

		public boolean areAllItemsEnabled() {
			return true;
		}

		public boolean isEnabled(int position) {

			if (managerBinding != null && position >= 0
					&& position < managerBinding.getComicCount())
				return true;

			return false;
		}

		public int getCount() {
			if (managerBinding != null)
				return managerBinding.getComicCount();
			return 0;
		}

		public Object getItem(int position) {
			if (managerBinding != null)
				return managerBinding.getComic(position);
			return null;
		}

		public long getItemId(int position) {
			if (managerBinding != null && position > 0
					&& position < managerBinding.getComicCount())
				return managerBinding.getComic(position).hashCode();
			return 0;
		}

		public int getItemViewType(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (managerBinding == null || position < 0
					|| position >= managerBinding.getComicCount())
				return null;

			View view = null;

			if (convertView != null) {
				if (convertView.findViewById(R.id.comiclist_name) != null)
					view = convertView;

			}

			if (view == null)
				view = inflater.inflate(R.layout.comiclistitem, parent, false);

			TextView name = (TextView) view.findViewById(R.id.comiclist_name);
			TextView author = (TextView) view
					.findViewById(R.id.comiclist_author);
			ImageView image = (ImageView) view
					.findViewById(R.id.comiclist_image);

			Comic comic = managerBinding.getComic(position);

			Bitmap bitmap = imageForComic(comic);

			if (bitmap != null)
				image.setImageBitmap(bitmap);
			else
				image.setImageResource(R.drawable.noimage);

			name.setText(comic.getComicName());
			author.setText(comic.getComicAuthor());
			return view;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isEmpty() {
			if (managerBinding != null)
				return managerBinding.getComicCount() == 0;
			return true;
		}

		public void registerDataSetObserver(DataSetObserver observer) {
			if (managerBinding != null)
				managerBinding.addComicManagerObserver(observer);
		}

		public void unregisterDataSetObserver(DataSetObserver observer) {
			if (managerBinding != null)
				managerBinding.removeComicManagerObserver(observer);

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {

		super.onResume();

		getListView().setLongClickable(true);

		registerForContextMenu(getListView());

		bindService(new Intent("org.webstrips.MANAGER", null, this,
				WebStripsService.class), connection, 0);
	}

	@Override
	protected void onPause() {

		setListAdapter(null);

		unbindService(connection);

		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == getListView().getId()) {

			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

			if (managerBinding == null)
				return;
			Comic comic = managerBinding.getComic(info.position);
			if (comic == null)
				return;

			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.comiclistmenu, menu);
			menu.setHeaderTitle(comic.getComicName());

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		if (managerBinding == null)
			return true;
		Comic comic = managerBinding.getComic(info.position);
		if (comic == null)
			return true;

		// FIXME: comic indetification has to be done using ids. Implement this
		// using some random long numbers.

		Intent intent = null;

		switch (item.getItemId()) {
		case R.id.list_browse:
			Uri uri = Uri.parse("comic://" + comic.getComicIdentifier()
					+ "/anchor");

			intent = new Intent("org.android.VIEW", uri, this, ComicViewerActivity.class);

			startActivity(intent);
			break;
		case R.id.list_info:
			intent = new Intent("org.android.INFO", Uri.parse("comic://" + comic.getComicIdentifier()), this, ComicDetailsActivity.class);

			startActivity(intent);
			break;
		case R.id.list_webpage:
			String url = comic.getComicHomapage();

			intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
					.parse(url));
			startActivity(intent);
			break;
		case R.id.list_remove:
			
			managerBinding.removeComic(comic);
			
			
			break;
		}
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		if (managerBinding == null)
			return;

		Comic comic = managerBinding.getComic(position);

		if (comic == null)
			return;

		Uri uri = Uri
				.parse("comic://" + comic.getComicIdentifier() + "/anchor");

		Intent intent = new Intent("COMIC", uri, this,
				ComicViewerActivity.class);

		this.startActivity(intent);

	}

	private Bitmap imageForComic(Comic comic) {

		Bitmap bitmap = imageCache.get(comic.getComicIdentifier());

		if (bitmap != null)
			return bitmap;

		File file = comic.getImageFile();

		if (file == null)
			return null;

		bitmap = BitmapFactory.decodeFile(file.toString());

		imageCache.put(comic.getComicIdentifier(), bitmap);

		return bitmap;
	}
}
