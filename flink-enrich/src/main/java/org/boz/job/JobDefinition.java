package org.boz.job;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

public interface JobDefinition extends Serializable {

    void setup(StreamExecutionEnvironment environment) throws Exception;
}
