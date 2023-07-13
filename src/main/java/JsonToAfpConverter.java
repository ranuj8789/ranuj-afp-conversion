import org.apache.fop.apps.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class JsonToAfpConverter {

    public static void main(String[] args) {
        String jsonFilePath = getResourceFilePath("input.json");
        String xsltFilePath = getResourceFilePath("json-to-xslfo.xsl");
        String fopConfigPath = getResourceFilePath("fop.xconf");
        String outputFilePath = getResourceFilePath("output.afp");
        convertJsonToAfp(jsonFilePath, xsltFilePath, fopConfigPath, outputFilePath);
    }

    public static void convertJsonToAfp(String jsonFilePath, String xsltFilePath, String fopConfigPath, String outputFilePath) {
        try {
            // Load XSLT template
            File xsltFile = new File(xsltFilePath);
            StreamSource xsltSource = new StreamSource(xsltFile);

            // Load JSON file
            File jsonFile = new File(jsonFilePath);
            StreamSource jsonSource = new StreamSource(jsonFile);

            // Configure FOP
            FopFactory fopFactory = FopFactory.newInstance(new File(fopConfigPath));
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();


            // Create the output file
            File outputFile = new File(outputFilePath);
            OutputStream outputStream = new FileOutputStream(outputFile);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_AFP, foUserAgent, outputStream);

// Create the output file


            // Apply XSL-FO transformation
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Templates templates = transformerFactory.newTemplates(xsltSource);
            Transformer transformer = templates.newTransformer();
            Source xslFoSource = new SAXSource(new InputSource(jsonSource.getInputStream()));
            Result result = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xslFoSource, result);

            // Close the output file
            outputStream.close();

            System.out.println("Conversion completed successfully!");
        } catch (IOException | TransformerException | FOPException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public static String getResourceFilePath(String resourceFileName) {
        ClassLoader classLoader = JsonToAfpConverter.class.getClassLoader();
        File resourceFile = new File(classLoader.getResource(resourceFileName).getFile());
        return resourceFile.getPath();
    }
}
