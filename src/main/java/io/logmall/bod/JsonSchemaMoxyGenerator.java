package io.logmall.bod;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fraunhofer.ccl.bo.converter.xml.oagis.BusinessObjectContextResolver;
import de.fraunhofer.ccl.bo.model.entity.common.BusinessObjectReferencable;

/**
 * Genrates json schema from moxy binding
 * 
 * @author andreas.trautmann
 *
 */
public class JsonSchemaMoxyGenerator {

	private static final Logger logger = LoggerFactory.getLogger(JsonSchemaMoxyGenerator.class);

	final JAXBContext jaxbContext;

	public JsonSchemaMoxyGenerator() {
		try {
			jaxbContext = new BusinessObjectContextResolver().getContext();
		} catch (Exception e) {
			logger.error("Cannot initialze JAXB context: " + e.getMessage(), e);
			throw new IllegalStateException("Cannot initialze JAXB context: " + e.getMessage());
		}
	}

	public String generate(Class<? extends BusinessObjectReferencable> boType) throws JAXBException {
		try {

			StringWriter sw = new StringWriter();
			final StreamResult sr = new StreamResult(sw);

			jaxbContext.generateSchema(new SchemaOutputResolver() {
				@Override
				public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
					return sr;
				}
			});

			return sw.toString();

		} catch (IOException ioe) {
			throw new JAXBException(ioe);
		}

		// XmlBindings bindings;
		// try {
		// bindings = resolveBinding(boType);
		// JsonObject root;
		// for (JavaType javaType : bindings.getJavaTypes().getJavaType()) {
		// JsonObject jsonObject = toJsonObject(javaType);
		// logger.info("jsonObject " + jsonObject);
		// return jsonObject.toString();
		// }
		// return "nix";
		// } catch (FileNotFoundException e) {
		// logger.error("Could not read xml binding for " + boType.getSimpleName() + ":
		// " + e.getMessage(), e);
		// throw new JAXBException(e.getMessage());
		// }
	}

	private JsonObject toJsonObject(JavaType javaType) {
		JsonSchemaProperty property = new JsonSchemaProperty();
		property.setName(javaType.getName());
		property.setTitle(javaType.getName());
		property.setRequired(false);
		property.setType(JsonSchemaProperty.TYPE.STRING);
		String json = new Gson().toJson(property);
		JsonReader jsonReader = Json.createReader(new StringReader(json));
		try {
			return jsonReader.readObject();
		} finally {
			jsonReader.close();
		}
	}

	private XmlBindings resolveBinding(Class<? extends BusinessObjectReferencable> boType)
			throws JAXBException, FileNotFoundException {
		FileReader bindingsFile = null;
		try {
			String fileName = "oagis_" + boType.getSimpleName().toLowerCase() + ".xml";
			bindingsFile = new FileReader(fileName);
			return (XmlBindings) jaxbContext.createUnmarshaller().unmarshal(bindingsFile);
		} finally {
			if (bindingsFile != null)
				try {
					bindingsFile.close();
				} catch (IOException e) {
					logger.error("Failed to close: " + e.getMessage());
				}
		}
	}

}
