package io.logmall.util;


import java.math.BigDecimal;

import de.fraunhofer.ccl.bo.model.entity.common.Measure;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;

public class MeasureUtil {

	public static Measure getMeasure(String type) {
		String unit = PredefinedMeasureUnitType.getForType(type).iterator().next().getUnit();
		
		Measure measure = new Measure();
		measure.setValue(new BigDecimal("0.0"));
		measure.setBaseUnitValue(new BigDecimal("1.0"));
		measure.setUnitName(unit);
		measure.setType(type);
        return measure;
    }
}
