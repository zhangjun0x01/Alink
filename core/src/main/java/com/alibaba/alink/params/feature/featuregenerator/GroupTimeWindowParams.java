package com.alibaba.alink.params.feature.featuregenerator;

import org.apache.flink.ml.api.misc.param.ParamInfo;
import org.apache.flink.ml.api.misc.param.ParamInfoFactory;

public interface GroupTimeWindowParams<T> extends
	BaseWindowParams <T> {

	ParamInfo <String[]> PARTITION_COLS = ParamInfoFactory
		.createParamInfo("partitionCols", String[].class)
		.setDescription("partition col names")
		.setAlias(new String[] {"partitionColNames"})
		.setHasDefaultValue(new String[0])
		.build();

	default String[] getPartitionCols() {return get(PARTITION_COLS);}

	default T setPartitionCols(String... colNames) {return set(PARTITION_COLS, colNames);}
}
