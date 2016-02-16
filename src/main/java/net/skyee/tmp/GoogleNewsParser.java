package net.skyee.tmp;

/**
 * Created by SKYE on 2015/12/31.
 */
import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class GoogleNewsParser
{

    public GoogleNewsParser()
    {
        try {
            // create factory.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // using factory to create xml parser.
            DocumentBuilder db = dbf.newDocumentBuilder();

            // create connection.
            String url = "http://news.google.com/news?ned=tw&topic=b&output=rss";
            URLConnection conn = new URL(url).openConnection();

            // Google always needs an identification of the client.
            conn.setRequestProperty("User-Agent", "RSSFeed");
            InputStream in = conn.getInputStream();

            // start parsing.
            Document doc = db.parse(in);
            NodeList nl = doc.getElementsByTagName("item");

            // parent node.
            StringBuffer data = new StringBuffer();
            data.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            data.append("\r\n");
            data.append("<RSS>");
            data.append("\r\n");

            for (int i = 0; i < nl.getLength(); i++) {

                // child nodes.
                NodeList node = nl.item(i).getChildNodes();
                data.append("\t<news id='" + i + "'>");
                data.append("\r\n");
                for (int j = 0; j < node.getLength(); j++) {
                    data.append("\t\t<" + node.item(j).getNodeName() + ">");
                    if (node.item(j).getNodeName().equals("description")) {
                        if (node.item(j).getFirstChild().getNodeValue().indexOf("img src=") >= 0)
                            data.append(node.item(j).getFirstChild().getNodeValue().split("img src=")[1].split("&")[0]);
                    } else {
                        data.append(node.item(j).getFirstChild().getNodeValue());
                    }
                    data.append("</" + node.item(j).getNodeName() + ">");
                    data.append("\r\n");
                }
                data.append("\t</news>");
                data.append("\r\n");
            }
            data.append("</RSS>");
            data.append("\r\n");

            System.out.println(data.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        System.out.println("Parsing news, please wait...");
        new GoogleNewsParser();
    }
}