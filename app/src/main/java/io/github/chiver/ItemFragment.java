package io.github.chiver;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.github.chiver.model.GalleryItem;

@SuppressWarnings("WeakerAccess")
public class ItemFragment extends Fragment {

    private static final String HTML = "<html>" +
            "   <head>" +
            "       <style type=\"text/css\">body{margin:0 auto;text-align:center;} img{width:100%%;}</style>" +
            "   </head>" +
            "   <body>" +
            "       <table style=\"width:100%%; height:100%%;\">" +
            "           <tr>" +
            "               <td style=\"vertical-align:middle;\">" +
            "                   <img src=\"%s\">" +
            "               </td>" +
            "           </tr>" +
            "       </table>" +
            "   </body>" +
            "</html>";
    private final String url;
    private final ImageLoader imageLoader;
    private View rootView;

    public ItemFragment(String url, ImageLoader imageLoader) {
        this.url = url;
        this.imageLoader = imageLoader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.fragment_item, container, false);
        loadItem(rootView.findViewById(R.id.niv_gallery_item));

        if (isAGif(url)) {
            ImageView ivPlay = rootView.findViewById(R.id.iv_play);
            ivPlay.setVisibility(View.VISIBLE);
            ivPlay.setOnClickListener(this::onPlay);
        }

        return rootView;
    }

    private void onPlay(@SuppressWarnings("unused") View view) {
        WebView webView = rootView.findViewById(R.id.wv_gallery_item);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageFinished(view, url);
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.transparent));
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        });

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        webView.setBackgroundResource(android.R.color.transparent);
        rootView.findViewById(R.id.ll_gallery_item).setVisibility(View.VISIBLE);
        String html = String.format(HTML, url);
        webView.loadData(html, "text/html", "utf8");

    }

    private boolean isAGif(String url) {
        return GalleryItem.Type.forURL(url) == GalleryItem.Type.ANIMATED;
    }

    private void loadItem(NetworkImageView nivItem) {
        imageLoader.get(url, ImageLoader.getImageListener(nivItem, 0, 0));
        nivItem.setImageUrl(url, imageLoader);

    }

}