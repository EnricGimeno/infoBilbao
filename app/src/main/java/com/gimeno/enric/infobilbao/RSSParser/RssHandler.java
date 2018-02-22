package com.gimeno.enric.infobilbao.RSSParser;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Date;

import es.masterd.rss.db.FeedsDB;

public class RssHandler extends DefaultHandler implements LexicalHandler {
	// Donde iremos guardando los datos del registro a guardar
	ContentValues rssItem;

	// Flags para saber en que nodo estamos
	private boolean in_item = false;
	private boolean in_title = false;
	private boolean in_link = false;
	private boolean in_comments = false;
	private boolean in_pubDate = false;
	private boolean in_dcCreator = false;
	private boolean in_description = false;

	private boolean in_CDATA;

	// Datos de proveedor de contenidos
	private ContentResolver contentProv;
	Uri uri = Uri.parse("content://es.enric.infoeuskadi/infoeuskadi");

	/**
	 * Constructor
	 * 
	 * @param contentResolver
	 */
	public RssHandler(ContentResolver contentResolver) {
		this.contentProv = contentResolver;
	}

	/** M�todos sobreescritos ********************************/

	/**
	 * Se llama cuando se abre un tag: <tag> Puede recibir atributos cuando el
	 * xml es del estilo: <tag attribute="attributeValue">
	 * 
	 **/
	@Override
	public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
		// Nos vamos a centrar solo en los items
		if (localName.equalsIgnoreCase("item")) {
			in_item = true;
			rssItem = new ContentValues();
		} else if (localName.equalsIgnoreCase("title")) {
			in_title = true;
		} else if (localName.equalsIgnoreCase("link")) {
			in_link = true;
		} else if (localName.equalsIgnoreCase("comments")) {
			in_comments = true;
		} else if (localName.equalsIgnoreCase("pubDate")) {
			in_pubDate = true;
		} else if (localName.equalsIgnoreCase("dc:creator")) {
			in_dcCreator = true;
		} else if (localName.equalsIgnoreCase("description")) {
			in_description = true;
		}
	}

	/**
	 * Llamado cuando se cierra el tag: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		// Nos vamos a centrar solo en los items
		if (localName.equalsIgnoreCase("item")) {

			Log.d("guardado item", "" + rssItem);

			contentProv.insert(uri, rssItem);
			in_item = false;

		} else if (localName.equalsIgnoreCase("title")) {
			in_title = false;
		} else if (localName.equalsIgnoreCase("link")) {
			in_link = false;
		} else if (localName.equalsIgnoreCase("comments")) {
			in_comments = false;
		} else if (localName.equalsIgnoreCase("pubDate")) {
			in_pubDate = false;
		} else if (localName.equalsIgnoreCase("dc:creator")) {
			in_dcCreator = false;
		} else if (localName.equalsIgnoreCase("description")) {
			in_description = false;
		}
	}

	/**
	 * Se llama cuando estamos dentro de un tag: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (in_item) { // Estamos dentro de un item
			if (in_title) {
				rssItem.put(FeedsDB.Posts.TITLE, new String(ch, start, length));
			} else if (in_link) {
				rssItem.put(FeedsDB.Posts.LINK, new String(ch, start, length));
			} else if (in_description) {
				if (rssItem.get(FeedsDB.Posts.DESCRIPTION) == null) {
					rssItem.put(FeedsDB.Posts.DESCRIPTION, new String(ch,
							start, ch.length));
				} else if (rssItem.getAsString(FeedsDB.Posts.DESCRIPTION)
						.length() < ch.length) {
					rssItem.put(FeedsDB.Posts.DESCRIPTION, new String(ch,
							start, ch.length));
				}
			} else if (in_pubDate) {
				String strDate = new String(ch, start, length);
				try {
					long fecha = Date.parse(strDate);
					rssItem.put(FeedsDB.Posts.PUB_DATE, fecha);
				} catch (Exception e) {
					Log.d("RssHandler", "Error al parsear la fecha");
				}
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
	 */
	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	@Override
	public void endCDATA() throws SAXException {
		in_CDATA = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	@Override
	public void endDTD() throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	@Override
	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	@Override
	public void startCDATA() throws SAXException {
		in_CDATA = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	@Override
	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}
}
