package model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXAnalyzer extends DefaultHandler {
    String longestEmail, currentTag;
    double totalAge;
    int totalRecords;


    @Override
    public void startDocument() throws SAXException {
        longestEmail = "";
        currentTag = "";
        totalAge = 0d;
        totalRecords = 0;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName.toLowerCase();
        if (currentTag.equals("my:client")) {
            totalRecords++;
            totalAge += Integer.parseInt(attributes.getValue("age"));
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        switch (currentTag) {
            case "my:email":
                String inp = new String(ch, start, length);
                if (inp.length() > longestEmail.length())
                    longestEmail = inp;
                break;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        currentTag = "";
    }

    public String getResults() {
        if (totalRecords != 0)
            return "Total records : " + totalRecords + "\n Longest email : " + longestEmail + " \n Average age : " + totalAge / totalRecords;
        else
            return "No records";
    }
}
