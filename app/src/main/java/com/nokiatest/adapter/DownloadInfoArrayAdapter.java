/*
package com.nokiatest.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nokiatest.R;
import com.nokiatest.model.DownloadInfo;

import java.util.List;


public class DownloadInfoArrayAdapter extends ArrayAdapter<DownloadInfo> {
  // Simple class to make it so that we don't have to call findViewById frequently
  private static class ViewHolder {
    TextView tvFileName, tvTimer, tvTTFB, tvMD5, tvSync;
    ProgressBar progressBar;
    DownloadInfo info;
    public ViewHolder(View vi){
      tvFileName = (TextView) vi.findViewById(R.id.tv_file_name);
      tvTimer = (TextView) vi.findViewById(R.id.tv_timer);
      tvTTFB = (TextView) vi.findViewById(R.id.tv_ttfb);
      tvMD5 = (TextView) vi.findViewById(R.id.tv_md5);
      tvSync = (TextView)vi.findViewById(R.id.tv_sync);
      progressBar = (ProgressBar)vi.findViewById(R.id.progress_bar);
    }
  }
  
  
  private static final String TAG = DownloadInfoArrayAdapter.class.getSimpleName();

  public DownloadInfoArrayAdapter(Context context, int textViewResourceId,
      List<DownloadInfo> objects) {
    super(context, textViewResourceId, objects);
  }
  
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    //View row = convertView;
    final DownloadInfo info = getItem(position);
    // We need to set the convertView's progressBar to null.

    ViewHolder holder = null;
    
    if(null == convertView) {
      LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.row_app_sync, parent, false);
      holder = new ViewHolder(convertView);
      holder.info = info;
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
      holder.info.setProgressBar(null);
      holder.info = info;
      holder.info.setProgressBar(holder.progressBar);
    }

    holder.textView.setText(info.getFilename());
    holder.progressBar.setProgress(info.getProgress());
    holder.progressBar.setMax(info.getFileSize());
    info.setProgressBar(holder.progressBar);
    
    holder.button.setEnabled(info.getDownloadState() == DownloadState.NOT_STARTED);
    final Button button = holder.button;
    holder.button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        info.setDownloadState(DownloadState.QUEUED);
        button.setEnabled(false);
        button.invalidate();
        FileDownloadTask task = new FileDownloadTask(info);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      }
    });
    
    
    //TODO: When reusing a view, invalidate the current progressBar.
    
    return row;
  }

}
*/
