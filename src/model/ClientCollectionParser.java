package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ClientCollectionParser {
    private static final String FILE_PATH = System.getProperty("user.dir") + "\\input.xml";

    public ClientCollectionParser() {
    }

    public Collection<Client> getCollection() throws ParserConfigurationException, IOException, SAXException {
        File input = new File(FILE_PATH);
        assert input.exists();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(input);

        doc.getDocumentElement().normalize();

        NodeList clientNodesList = doc.getElementsByTagName("Client");

        Collection<Client> clientCollection = new ArrayList<>();

        for (int i = 0; i < clientNodesList.getLength(); i++) {
            Node clientNode = clientNodesList.item(i);
            if (clientNode.getNodeType() == Node.ELEMENT_NODE) {
                Element clientElement = (Element) clientNode;
                int id, age;
                String name, email, phone;
                Client.Status status;

                name = clientElement.getElementsByTagName("Surname").item(0).getTextContent();
                email = clientElement.getElementsByTagName("Email").item(0).getTextContent();
                phone = clientElement.getElementsByTagName("Phone").item(0).getTextContent();

                id = Integer.parseInt(clientElement.getAttribute("id"));
                age = Integer.parseInt(clientElement.getAttribute("age"));
                String temp = clientElement.getAttribute("status");

                switch (temp.toLowerCase()) {
                    case "premium":
                        status = Client.Status.PREMIUM;
                        break;
                    case "standard":
                        status = Client.Status.STANDARD;
                        break;
                    default:
                        throw new IllegalArgumentException("status");
                }

                clientCollection.add(new Client(name, new Email(email), phone, id, age, status));
            }
        }
        return clientCollection;
    }
}
