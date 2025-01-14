package com.alibaba.alink.operator.stream.tensorflow;

import com.alibaba.alink.DLTestConstants;
import com.alibaba.alink.common.utils.JsonConverter;
import com.alibaba.alink.operator.stream.StreamOperator;
import com.alibaba.alink.operator.stream.dataproc.TypeConvertStreamOp;
import com.alibaba.alink.common.dl.DLLauncherStreamOp;
import com.alibaba.alink.operator.stream.source.RandomTableSourceStreamOp;
import com.alibaba.alink.operator.stream.tensorflow.TensorFlowStreamOp;
import com.alibaba.alink.params.dataproc.HasTargetType.TargetType;
import com.alibaba.alink.testutil.categories.DLTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.HashMap;
import java.util.Map;

public class TensorFlowStreamOpTest {

	@Category(DLTest.class)
	@Test
	public void test() throws Exception {
		StreamOperator.setParallelism(3);

		StreamOperator<?> source = new RandomTableSourceStreamOp()
			.setMaxRows(1000L)
			.setNumCols(10);

		String[] colNames = source.getColNames();
		source = source.select("*, case when RAND() > 0.5 then 1. else 0. end as label");
		source = source.link(new TypeConvertStreamOp().setSelectedCols("num").setTargetType(TargetType.DOUBLE));
		String label = "label";

		Map <String, Object> userParams = new HashMap<>();
		userParams.put("featureCols", JsonConverter.toJson(colNames));
		userParams.put("labelCol", label);
		userParams.put("batch_size", 16);
		userParams.put("num_epochs", 1);

		TensorFlowStreamOp tensorFlowStreamOp = new TensorFlowStreamOp()
			.setUserFiles(new String[] {"res:///tf_dnn_stream.py"})
			.setMainScriptFile("res:///tf_dnn_stream.py")
			.setUserParams(JsonConverter.toJson(userParams))
			.setNumWorkers(2)
			.setNumPSs(1)
			.setOutputSchemaStr("model_id long, model_info string")
			.setPythonEnv(DLTestConstants.LOCAL_TF115_ENV)
			.linkFrom(source);
		tensorFlowStreamOp.print();
		StreamOperator.execute();
	}

	@Category(DLTest.class)
	@Test
	public void testWithAutoWorkersPSs() throws Exception {
		StreamOperator.setParallelism(3);
		DLLauncherStreamOp.DL_CLUSTER_START_TIME = 30 * 1000;

		StreamOperator<?> source = new RandomTableSourceStreamOp()
			.setMaxRows(1000L)
			.setNumCols(10);

		String[] colNames = source.getColNames();
		source = source.select("*, case when RAND() > 0.5 then 1. else 0. end as label");
		source = source.link(new TypeConvertStreamOp().setSelectedCols("num").setTargetType(TargetType.DOUBLE));
		String label = "label";

		Map <String, Object> userParams = new HashMap<>();
		userParams.put("featureCols", JsonConverter.toJson(colNames));
		userParams.put("labelCol", label);
		userParams.put("batch_size", 16);
		userParams.put("num_epochs", 1);

		TensorFlowStreamOp tensorFlowStreamOp = new TensorFlowStreamOp()
			.setUserFiles(new String[] {"res:///tf_dnn_stream.py"})
			.setMainScriptFile("res:///tf_dnn_stream.py")
			.setUserParams(JsonConverter.toJson(userParams))
			.setOutputSchemaStr("model_id long, model_info string")
			.setPythonEnv(DLTestConstants.LOCAL_TF115_ENV)
			.linkFrom(source);
		tensorFlowStreamOp.print();
		StreamOperator.execute();
	}
}
