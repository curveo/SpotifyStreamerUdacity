package com.iprodev.spotifystreamer.com.iprodev.spotifystreamer.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iprodev.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by curtisashby on 6/22/15.
 */
public class ArtistsAdaper extends BaseAdapter {
    private Context mContext;
    private ArrayList<Artist> mResults;

    public ArtistsAdaper(Context c, ArrayList<Artist> results) {
        mContext = c;
        mResults = results;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public Artist getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder design patter for scroll performance.
        ViewHolder holder;
        if(convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(mContext);
            convertView = inflator.inflate(R.layout.row_result, parent, false);
            holder = new ViewHolder();
            holder.mImageV = (ImageView) convertView.findViewById(R.id.result_artist_thumb);
            holder.mArtist = (TextView) convertView.findViewById(R.id.result_artist_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Artist result = getItem(position);
        if(result.images.size() > 0 ) {
            Image thumb = null;
            for(Image thumbUrl: result.images) {
                //TODO conditionally grab the correct url based on size.
                if(thumbUrl.height >= 100 && thumbUrl.height <= 300) {
                    thumb = thumbUrl;
                }
            }
//                new AsyncTask<String, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(String... strings) {
//                        String url = strings[0];
//                        try {
//                            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//                            InputStream in = new BufferedInputStream(conn.getInputStream());
//                            byte[] buffer = new byte[1024];
//                            while(in.read() != -1) {
//
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                    }
//                }.execute(result.images.get(0).url);

            Picasso.with(mContext).load(thumb.url).into(holder.mImageV);
        } else {
            holder.mImageV.setImageResource(R.drawable.artist_placeholder);
        }
        holder.mArtist.setTag(result);
        holder.mArtist.setText(result.name);

        return convertView;
    }

    class ViewHolder {
        ImageView mImageV;
        TextView mArtist;
    }
}
