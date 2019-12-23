package io.github.chiver.util;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import io.github.chiver.Constants;
import io.github.chiver.model.Gallery;
import io.github.chiver.model.GalleryItem;

public class SitemapSAXParser extends DefaultHandler {

    private final Consumer<Gallery> callback;
    private StringBuilder buffer;
    private String currentLoc;
    private String currentGalleryLoc;
    private List<GalleryItem> currentGalleryItems;

    public SitemapSAXParser(Consumer<Gallery> callback) {
        this.callback = callback;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("url".equals(localName)) {
            startUrl();
        } else if ("loc".equals(localName)) {
            startLoc();
        } else if ("image".equals(localName)) {
            startImage();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("url".equals(localName)) {
            endUrl();
        } else if ("loc".equals(localName)) {
            endLoc();
        } else if ("image".equals(localName)) {
            endImage();
        }
    }

    private void startUrl() {
        currentGalleryItems = new LinkedList<>();
    }

    private void endUrl() {
        if (!currentGalleryItems.isEmpty()) {
            callback.accept(new Gallery(parseTitle(currentGalleryLoc), currentGalleryItems.get(0).imageSource, currentGalleryLoc, new LinkedList<>(currentGalleryItems)));
        }

        currentGalleryLoc = null;
        currentGalleryItems.clear();
    }

    private void startImage() {
        if (currentGalleryItems.isEmpty()) {
            currentGalleryLoc = currentLoc;
        }
    }

    private void endImage() {
        currentGalleryItems.add(new GalleryItem(currentLoc));
    }

    private void startLoc() {
        currentLoc = null;
        buffer = new StringBuilder();
    }

    private void endLoc() {
        currentLoc = buffer.toString();
        buffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (buffer != null) {
            buffer.append(ch, start, length);
        }
    }

    private String parseTitle(String url) {
        try {
            String path = new URL(url).getPath();

            String[] split = path.split("/");
            if (split.length > 0) {
                return split[split.length - 1].replaceAll("-", " ");
            }
        } catch (Throwable t) {
            Log.e(Constants.TAG, "Can't get item type from url", t);
        }
        return "";
    }
}