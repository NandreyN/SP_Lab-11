package model;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class ClientCollectionParser {
    public static final String FILE_PATH = System.getProperty("user.dir") + "\\input.xml";
    private static final String FILE_PATH_BINARY = System.getProperty("user.dir") + "\\input.bin";
    private static final String OUT_FILE_PATH = System.getProperty("user.dir") + "\\output.xml";
    private static final String OUT_FILE_PATH_BINARY = System.getProperty("user.dir") + "\\output.bin";

    public ClientCollectionParser() {
    }

    public static Collection<Client> getCollection() throws IOException, SAXException, ParserConfigurationException {
        return getCollection(FILE_PATH);
    }

    public static Collection<Client> getCollection(String path) throws ParserConfigurationException, IOException, SAXException {
        File input = new File(path);
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

    public static void save(Collection<Client> clientCollection) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElement("Collection");
        document.appendChild(root);

        clientCollection.forEach((x) -> {
            Element elem = document.createElement("Client");

            elem.setAttribute("id", String.valueOf(x.getId()));
            elem.setAttribute("age", String.valueOf(x.getAge()));
            elem.setAttribute("status", String.valueOf(x.getStatus()));

            Element name = document.createElement("Surname");
            name.appendChild(document.createTextNode(x.getName()));

            Element email = document.createElement("Email");
            email.appendChild(document.createTextNode(x.getEmail().toString()));

            Element phone = document.createElement("Phone");
            phone.appendChild(document.createTextNode(x.getPhone()));

            elem.appendChild(name);
            elem.appendChild(email);
            elem.appendChild(phone);
            root.appendChild(elem);
        });

        /////////////////
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // additional spaces while outputting document tree

        DOMSource source = new DOMSource(document);
        try {
            // location and name of XML file you can change as per need
            FileWriter fos = new FileWriter(OUT_FILE_PATH);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);

        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void serialize(Collection<Client> clientCollection) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OUT_FILE_PATH_BINARY))) {
            for (Client c : clientCollection)
                oos.writeObject(c);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static Collection<Client> deserialize() {
        return deserialize(FILE_PATH_BINARY);
    }

    public static Collection<Client> deserialize(String path) {
        Collection<Client> clientCollection = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            while (true) {
                clientCollection.add((Client) ois.readObject());
            }
        } catch (EOFException ex) {
            return clientCollection;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return clientCollection;
    }
}
