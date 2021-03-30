package com.opofa.xmlparser;

import com.opofa.file.FileHandler;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder documentBuilder;

    private static List<Dependency> dependencies = new ArrayList<>();

    public static void merge(String source, String target) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        parse(source, null, document, 0);
        write(target, document);
        writeToFile();
    }

    private static void parse(String pathName, String parent, Document targetDocument, int level) throws IOException, SAXException {
        dependencies.add(new Dependency(pathName, level));

        level = level + 1;

        Document document = documentBuilder.parse(new File(pathName));

        if (targetDocument.getDocumentElement() == null) {
            Element rootElement = targetDocument.createElement(document.getFirstChild().getNodeName());
            targetDocument.appendChild(rootElement);
        }

        NodeList childNodes = document.getFirstChild().getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);

            if (item.getNodeName().equals("import")) {
                String dependency = item.getAttributes().getNamedItem("resource").getNodeValue();

                parse(dependency, pathName, targetDocument, level);
            } else {
                Node newNode = targetDocument.importNode(item, true);

                if (item.getNodeType() == 1) {
                    Element newElement = (Element) newNode;
                    newElement.setAttribute("source", pathName);

                    if (parent != null) {
                        newElement.setAttribute("target", parent);
                    }

                    targetDocument.getDocumentElement().appendChild(newElement);
                }
            }
        }
    }

    private static void write(String targetPath, Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        DOMSource domSource = new DOMSource(document);

        File file = new File(targetPath);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
    }

    private static void writeToFile() throws IOException {
        String filePath = "data/stats.txt";
        String line = "";

        FileHandler.write(filePath, line, false);

        for (Dependency dependency : dependencies) {
            line = " ".repeat(dependency.getLevel()) + dependency.getName() + " " + dependencies.stream().filter(a -> a.getName().equals(dependency.getName())).count();

            FileHandler.write(filePath, line, true);
        }
    }
}
