package org.sunbird.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraImpl implements CassandraOperation {

    private static Cluster cluster;
    private static Session session;
    private String keyspaceName;
    private String host;
    private String port;
    static Logger logger = LoggerFactory.getLogger(CassandraImpl.class);

    public CassandraImpl(String host, String keyspaceName, String port) {
        this.keyspaceName = keyspaceName;
        this.host = host;
        this.port = port;
        initializeConnection();
    }

    @Override
    public ResultSet getRecords(String query) {
        ResultSet resultSet = session.execute(query);
        return resultSet;
    }

    @Override
    public ResultSet getRecord(Statement query) {
        ResultSet resultSet = session.execute(query);
        return resultSet;
    }

    @Override
    public void closeConnection() {
        cluster.close();
    }

    @Override
    public boolean updateRecord(Statement query, String id) {
        try {
            ResultSet resultSet = session.execute(query);
            return resultSet.wasApplied();
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "exception occurred in updating record %s : %s ", id, e.getMessage()));
            return false;
        }
    }

    /**
     * this method will initialize the cassandra connection
     */
    public void initializeConnection() {
        String[] hostsArray = host.split(",");
        cluster =
                Cluster.builder()
                        .addContactPoints(hostsArray)
                        .withRetryPolicy(DefaultRetryPolicy.INSTANCE)
                        .build();
        session = cluster.connect(keyspaceName);
        session.execute("USE ".concat(keyspaceName));
        logger.info(String.format("cassandra connection created %s", session));
    }

    @Override
    public Session getSession() {
        return session;
    }
}
