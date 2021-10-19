package org.sunbird.cassandra.factory;


import org.sunbird.cassandra.CassandraImpl;
import org.sunbird.cassandra.CassandraOperation;

public class CassandraConnectionFactory implements ConnectionFactory {
  @Override
  public CassandraOperation getConnection(String hostName, String keyspaceName, String port) {
    return new CassandraImpl(hostName, keyspaceName, port);
  }
}
