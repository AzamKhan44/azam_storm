package com.azam.storm.azam_storm;

import java.util.Map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class ExclamationTopology {
	public static class ExclamationBolt extends BaseRichBolt{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		OutputCollector _collector;

		public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
			_collector = collector;
		}

		public void execute(Tuple tuple) {
			_collector.emit(tuple, new Values(tuple.getString(0)+"!!!"));
			_collector.ack(tuple);
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
	}
	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word", new TestWordSpout(),10);
		builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("word");
		builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");
		
		Config conf = new Config();
		conf.setDebug(true);
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		Utils.sleep(10000);
		cluster.killTopology("test");
		cluster.shutdown();
		
	}

}
