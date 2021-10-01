package org.sunbird.cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.Certificate;
import org.sunbird.util.DbColumnConstants;
import org.sunbird.util.JsonKeys;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;


public class CassandraHelper {

    private static Logger logger = LoggerFactory.getLogger(CassandraHelper.class);


    /**
     * this variable is initialized so that list can easily handle the size of user..
     */
    public static final int initialCapacity = 50000;

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * these methods will be used to convert resultSet into List of User entity Object.
     *
     * @param resultSet
     * @return
     */
    public static List<Certificate> getCertificatesFromResultSet(ResultSet resultSet) {

        List<Certificate> certificateList = new ArrayList<>(initialCapacity);
        for (Row row : resultSet) {
            Certificate certificate = null;
            try {
                certificate = new Certificate(
                        row.getString(DbColumnConstants.id),
                        mapper.readValue(row.getString(DbColumnConstants.data), new TypeReference<Map<String, Object>>() {
                        }),
                        row.getString(DbColumnConstants.jsonUrl),
                        row.getTimestamp(DbColumnConstants.createdAt));
            } catch (IOException e) {
                logger.info("Exception occurred while converting row of resultSet to Certificate object ");
            }
            certificateList.add(certificate);
        }
        return certificateList;
    }


    public static Statement getUpdateQuery(String id, String jsonUrl, Map<String, Object> data,Timestamp  updatedAt) throws JsonProcessingException {
        String certData = mapper.writeValueAsString(data);
        Update.Where update = QueryBuilder.update(JsonKeys.KEYSPACE, JsonKeys.TABLE_NAME)
                .with(QueryBuilder.set(DbColumnConstants.data, certData))
                .and(QueryBuilder.set(DbColumnConstants.jsonUrl, jsonUrl))
                .and(QueryBuilder.set(DbColumnConstants.updatedat, new Timestamp(System.currentTimeMillis())))
                .where(QueryBuilder.eq(JsonKeys.id, id));
        logger.info(("update.getQueryString() " + update.getQueryString() + update.toString()));
        return update;
    }

    public static Statement getRecordQuery(String id) {
        Select.Where selectQuery = QueryBuilder.select().from(JsonKeys.KEYSPACE, JsonKeys.TABLE_NAME).where().and(eq("id", id));
        logger.info(("selectQuery.getQueryString() " + selectQuery.getQueryString() + selectQuery.toString()));
        return selectQuery;
    }
}
