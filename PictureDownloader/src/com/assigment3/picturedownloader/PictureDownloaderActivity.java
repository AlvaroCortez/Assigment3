package com.assigment3.picturedownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PictureDownloaderActivity extends Activity implements
		OnClickListener, LoaderCallbacks<String> {

	//private static AsyncPictureDownloader loader;
	private TextView statusText;
	private ProgressBar downloadProgress;
	private Button downloaderButton;
	private static String url;
	private static final int MAX = 100;
	private static final int ZERO = 0;
	private static final int LOADER_ID = 1;
	private static final String pathSave = "/sdcard/downloadedPicture.jpg";
	private static boolean error = false;

	//@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_downloader_layout);
		statusText = (TextView) findViewById(R.id.status_text);
		downloaderButton = (Button) findViewById(R.id.picture_button);
		downloadProgress = (ProgressBar) findViewById(R.id.progress_downloader);
		downloadProgress.setVisibility(View.INVISIBLE);
		downloadProgress.setMax(MAX);
		downloadProgress.setProgress(ZERO);
		url = getString(R.string.image_url);
		LoaderManager loaderManager = getLoaderManager();
		Bundle bundle = new Bundle();
		bundle.putString(LoaderPictureDownloader.ARGS, url);
		loaderManager.initLoader(LOADER_ID, bundle, this);
	}
	
	public void onStart(){
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		Loader<String> loader;
		if (downloaderButton.getText().equals("Open")) {
			Intent image = new Intent();
			image.setAction(Intent.ACTION_VIEW);
			File file = new File(pathSave);
			image.setDataAndType(Uri.fromFile(file), "image/*");
			startActivity(image);
		} else {
			statusText.setText("Status: Downloading");
			downloaderButton.setVisibility(View.INVISIBLE);
			downloadProgress.setVisibility(View.VISIBLE);
			downloadProgress.setProgress(ZERO);
			loader = getLoaderManager().getLoader(LOADER_ID);
			loader.forceLoad();
		}
	}
	
	@Override
	public Loader<String> onCreateLoader(int id, Bundle args) {
		Loader<String> loader = null;
		if (id == LOADER_ID){
			loader = new LoaderPictureDownloader(this, args);
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data) {
		statusText.setText("Status: Downloaded");
		downloaderButton.setVisibility(View.VISIBLE);
		downloaderButton.setText("Open");
		downloadProgress.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onLoaderReset(Loader<String> loader) {		
	}

	private static class LoaderPictureDownloader extends Loader<String>{
		
		public final static String ARGS = "picture";
		String url;

		public LoaderPictureDownloader(Context context, Bundle args) {
			super(context);
			if (args != null){
				url = args.getString(ARGS);
			}
		}

		@Override
		protected void onForceLoad() {
			super.onForceLoad();
			int count = ZERO;
			URLConnection urlConnection;
			URL url;
			InputStream input;
			OutputStream output;
			try {
				url = new URL(this.url);
				urlConnection = url.openConnection();
				urlConnection.connect();
				int lenghtFile = urlConnection.getContentLength();
				input = new BufferedInputStream(url.openStream(),
						8192);
				output = new FileOutputStream(pathSave);
				byte data[] = new byte[1024];
				long total = ZERO;
				while ((count = input.read(data)) != -1) {
					total += count;
					//activity.downloadProgress.setProgress(((int) ((total * MAX) / lenghtFile)));
					output.write(data, ZERO, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (IOException e) {
				PictureDownloaderActivity.error = true;
			} finally{
				
			}
			deliverResult(ARGS);
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();
		}

		@Override
		protected void onStopLoading() {
			super.onStopLoading();
		}

		@Override
		protected void onReset() {
			super.onReset();
		}
	}
}
