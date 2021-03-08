package io.github.chiver.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.function.Consumer;

import io.github.chiver.model.Gallery;
import io.github.chiver.model.GalleryItem;

public class FeedSAXParser extends DefaultHandler {

    private final Consumer<Gallery> callback;
    private StringBuilder buffer;
    private String currentTitle;
    private String currentLink;
    private String currentCategory;
    private String currentMediaUrl;
    private GalleryItem currentGalleryItem;
    private boolean inItem = false;

    public FeedSAXParser(Consumer<Gallery> callback) {
        this.callback = callback;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("item".equals(qName)) {
            startItem();
        } else if (inItem && "link".equals(qName)) {
            startLink();
        } else if (inItem && "title".equals(qName)) {
            startTitle();
        } else if (inItem && "media:category".equals(qName)) {
            startCategory();
        } else if (inItem && "media:content".equals(qName)) {
            startImage(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if ("item".equals(qName)) {
            endItem();
        } else if (inItem && "link".equals(qName)) {
            endLink();
        } else if (inItem && "title".equals(qName)) {
            endTitle();
        } else if (inItem && "media:category".equals(qName)) {
            endCategory();
        } else if (inItem && "media:content".equals(qName)) {
            endImage();
        }
    }

    private void startItem() {
        inItem = true;
    }

    private void endItem() {
        inItem = false;
        if (currentGalleryItem != null) {
            callback.accept(new Gallery(currentTitle, currentGalleryItem.imageSource, currentLink));
        }

        currentGalleryItem = null;
    }

    private void startImage(Attributes attributes) {
        currentMediaUrl = attributes.getValue("url");
    }


    private void endImage() {
        if (!"author".equals(currentCategory)) {
            currentGalleryItem = new GalleryItem(currentMediaUrl);
        }
        currentCategory = null;
    }

    private void startTitle() {
        currentTitle = null;
        buffer = new StringBuilder();
    }

    private void endTitle() {
        currentTitle = buffer.toString();
        buffer = null;
    }

    private void startLink() {
        currentLink = null;
        buffer = new StringBuilder();
    }

    private void endLink() {
        currentLink = buffer.toString();
        buffer = null;
    }

    private void startCategory() {
        currentCategory = null;
        buffer = new StringBuilder();
    }

    private void endCategory() {
        currentCategory = buffer.toString();
        buffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (buffer != null) {
            buffer.append(ch, start, length);
        }
    }
}
