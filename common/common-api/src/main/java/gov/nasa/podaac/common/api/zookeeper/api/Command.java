package gov.nasa.podaac.common.api.zookeeper.api;

import gov.nasa.podaac.common.api.zookeeper.core.CommandResponse;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public interface Command {

	public CommandResponse execute(ZooKeeper zk) throws InterruptedException, KeeperException;
	public String getCommandName();
}
