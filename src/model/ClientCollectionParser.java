package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class ClientCollectionParser {
    public static final String FILE_PATH = System.getProperty("user.dir") + "\\input.xml";
    private static final String FILE_PATH_BINARY = System.getProperty("user.dir") + "\\input.bin";
    private static final String OUT_FILE_PATH = System.getProperty("user.dir") + "\\output.xml";
    private static final String OUT_FILE_PATH_BINARY = System.getProperty("user.dir") + "\\output.bin";
    private static final String SCHEMA_PATH = "schema.xsd";

    public ClientCollectionParser() {
    }

    public Collection<Client> getCollection(boolean validate) throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        return getCollection(FILE_PATH, validate);
    }

    public Collection<Client> getCollection(String path, boolean validate) throws ParserConfigurationException, IOException, SAXException, URISyntaxException {
        File input = new File(path);
        assert input.exists();
        if (validate) {
            if (!isValidXML(input))
                throw new SAXException("Invalid xml file");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(input);

        doc.getDocumentElement().normalize();

        NodeList clientNodesList = doc.getElementsByTagName("my:Client");

        Collection<Client> clientCollection = new ArrayList<>();

        for (int i = 0; i < clientNodesList.getLength(); i++) {
            Node clientNode = clientNodesList.item(i);
            if (clientNode.getNodeType() == Node.ELEMENT_NODE) {
                Element clientElement = (Element) clientNode;
                int id, age;
                String name, email, phone;
                Client.Status status;

                name = clientElement.getElementsByTagName("my:Surname").item(0).getTextContent();
                email = clientElement.getElementsByTagName("my:Email").item(0).getTextContent();
                phone = clientElement.getElementsByTagName("my:Phone").item(0).getTextContent();

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

    public void save(Collection<Client> clientCollection) throws TransformerException, ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();
        Element root = document.createElementNS(
                "http://my.namespace.com", // namespace
                "my:Collection" // node name including prefix
        );
        document.appendChild(root);

        clientCollection.forEach((x) -> {
            Element elem = document.createElement("my:Client");

            elem.setAttribute("id", String.valueOf(x.getId()));
            elem.setAttribute("age", String.valueOf(x.getAge()));
            elem.setAttribute("status", String.valueOf(x.getStatus()));

            Element name = document.createElement("my:Surname");
            name.appendChild(document.createTextNode(x.getName()));

            Element email = document.createElement("my:Email");
            email.appendChild(document.createTextNode(x.getEmail().toString()));

            Element phone = document.createElement("my:Phone");
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

    public void serialize(Collection<Client> clientCollection) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OUT_FILE_PATH_BINARY))) {
            for (Client c : clientCollection)
                oos.writeObject(c);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public Collection<Client> deserialize() {
        return deserialize(FILE_PATH_BINARY);
    }

    public Collection<Client> deserialize(String path) {
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

    public boolean isValidXML(File xmlFile) throws IOException, URISyntaxException {
        File file = null;
        String resource = "/schema.xsd";
        URL res = getClass().getResource(resource);
        if (res.toString().startsWith("jar:")) {
            try {
                InputStream input = getClass().getResourceAsStream(resource);
                file = File.createTempFile("tempfile", ".tmp");
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                file.deleteOnExit();
            } catch (IOException ex) {
                throw ex;
            }
        } else {
            file = new File(res.getFile());
        }

        if (!file.exists()) {
            System.out.println(file.getAbsolutePath());
            throw new FileNotFoundException("Schema file not found");
        }

        Source xmlSource = new StreamSource(xmlFile);
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            Schema schema = sf.newSchema(file);
            Validator v = schema.newValidator();
            v.validate(xmlSource);
        } catch (SAXException | IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
