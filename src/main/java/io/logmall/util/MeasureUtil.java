package io.logmall.util;


import de.fraunhofer.ccl.bo.model.entity.common.Measure;
import de.fraunhofer.ccl.bo.model.entity.common.PredefinedMeasureUnitType;

public class MeasureUtil {

	public static Measure getMeasure(String type) {
		String unit = PredefinedMeasureUnitType.getForType(type).iterator().next().getUnit();
		
		Measure measure = new Measure();
		measure.setValue(null);
		measure.setBaseUnitValue(null);
		measure.setUnitName(unit);
		measure.setType(type);
        return measure;
    }
}
