package com.alibaba.alink.operator.stream.nlp;

import com.alibaba.alink.testutil.categories.DLTest;
import org.apache.flink.types.Row;

import com.alibaba.alink.DLTestConstants;
import com.alibaba.alink.common.MLEnvironmentFactory;
import com.alibaba.alink.operator.stream.StreamOperator;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class BertTextEmbeddingStreamOpTest {

	@Category(DLTest.class)
	@Test
	public void linkFrom() throws Exception {
		Row[] rows1 = new Row[] {
			Row.of(1L, "An english sentence."),
			Row.of(2L, "这是一个中文句子"),
		};

		MLEnvironmentFactory.getDefault().getStreamExecutionEnvironment().setParallelism(1);

		StreamOperator <?> data = StreamOperator.fromTable(
			MLEnvironmentFactory.getDefault().createStreamTable(rows1, new String[] {"sentence_id", "sentence_text"}));

		BertTextEmbeddingStreamOp bertEmb = new BertTextEmbeddingStreamOp()
			.setSelectedCol("sentence_text").setOutputCol("embedding").setLayer(-2)
			.setModelPath(DLTestConstants.BERT_CHINESE_SAVED_MODEL_PATH);
		data.link(bertEmb).print();

		StreamOperator.execute();
	}
}
