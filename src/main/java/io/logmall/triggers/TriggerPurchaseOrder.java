package io.logmall.triggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.elastic.api.ExecutionParameters;
import io.logmall.actions.CreateItemMaster;
import io.logmall.res.ResourceResolver;
import io.logmall.util.ExecutionParametersUtil;

public class TriggerPurchaseOrder {
	
	private static final Logger logger = LoggerFactory.getLogger(TriggerPurchaseOrder.class);

	/**
	 * Executes the trigger's logic by sending a request to the Logmall API and
	 * emitting response to the platform.
	 *
	 * @param parameters
	 *            execution parameters
	 */
		 
	}
