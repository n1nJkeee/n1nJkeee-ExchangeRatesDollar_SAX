import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.xml.sax.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ExchangeRatesDollar {

	public static void main( String[] args ) throws ParserConfigurationException, SAXException {

		Scanner sc = new Scanner( System.in );
		System.out.print( "Введите число месяц и год в формате dd.mm.yyyy: " );

		String dateUser = sc.next();

		DateFormat dateFormat = new SimpleDateFormat( "dd.mm.yyyy" );
		DateFormat dateFormatUrl = new SimpleDateFormat( "dd/mm/yyyy" );
		Date date = null;

		try {

			date = dateFormat.parse( dateUser );

			String url = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + dateFormatUrl.format( date );
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			XMLHandler handler = new XMLHandler();

			try {
				URLConnection urlConnection = new URL( url ).openConnection();
				urlConnection.addRequestProperty( "Accept", "application/xml" );
				parser.parse( url, handler );

			} catch ( IOException e ) {
				e.printStackTrace();
			}

		} catch ( ParseException e ) {
			e.printStackTrace();
		}

		sc.close();
	}

	private static class XMLHandler extends DefaultHandler {

		private boolean flag = false, found = false;
		private String course;
		final String ID_DOLLAR = "R01235", ID_ATTR = "ID", VALUTE = "Valute", VALUE = "Value";

		@Override
		public void startElement( String uri, String localName, String qName, Attributes attributes )
				throws SAXException {

			if ( qName.equals( VALUTE ) ) {
				String id_Currency = attributes.getValue( ID_ATTR );
				if ( id_Currency.equals( ID_DOLLAR ) ) {
					flag = true;
				}
			}
		}

		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException {
			if ( flag )
				course = new String( ch, start, length );
		}

		@Override
		public void endElement( String uri, String localName, String qName ) throws SAXException {

			if ( flag && qName.equals( VALUE ) ) {

				try {
					System.out.println( course );
					found = true;
					flag = false;
					throw new SAXException();
				} catch ( SAXException e ) {
					System.exit( 0 ); // чтобы не обрабатывать весь документ
				}

			}
		}

		@Override
		public void endDocument() throws SAXException {
			if ( !found )
				System.out.println( "Извините на указанную дату курс доллара не найден!" );
		}
	}
}
