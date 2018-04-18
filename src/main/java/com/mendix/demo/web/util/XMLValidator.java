package com.mendix.demo.web.util;

import com.mendix.demo.web.response.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;

/**
 * Created by naseers on 18/04/2018.
 */
public class XMLValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(XMLValidator.class);

    private final Schema schema;

    public XMLValidator(String schemaPath) throws IOException {
        this.schema = loadSchema(schemaPath);
    }

    public static Schema loadSchema(String schemaPath) throws IOException {
        File schemaDir = new File(schemaPath);
        if (!schemaDir.exists()) {
            throw new IOException("XSD [ " + schemaPath + " ] cannot be found");
        }
        try (InputStream in = new FileInputStream(schemaPath)) {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(in));
            return schema;
        } catch (SAXException e) {
            throw new IOException("Failed to parse schema file!!");
        }
    }


    @Override
    public ResponseEnvelope validate(String str) throws IOException {
        try (InputStream in = new ByteArrayInputStream(str.getBytes())) {
            schema.newValidator().validate(new StreamSource(in));
            return new ResponseEnvelope(null,true);
        } catch (SAXException e) {
            LOG.debug("XML Validation Failed!!");
            ResponseEnvelope response = new ResponseEnvelope(null,false);
            response.addErrorMessage(e.getMessage());
            return response;
        }
    }
}
