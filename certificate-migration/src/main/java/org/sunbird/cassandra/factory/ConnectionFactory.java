package org.sunbird.cassandra.factory;


import org.sunbird.cassandra.CassandraOperation;

public interface ConnectionFactory {

  CassandraOperation getConnection(String hostName, String keyspaceName, String port);
}
