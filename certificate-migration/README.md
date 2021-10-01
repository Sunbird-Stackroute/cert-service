# certificate-migration
# Prerequisite: 
      -  Connection to Cassandra and ES
      
# How to run
 - require env variable to be set
       - sunbird_cassandra_host
       - sunbird_cassandra_keyspace
       - sunbird_cassandra_port
       - es_conn_info
       - CLOUD_STORAGE_TYPE
       - AZURE_STORAGE_SECRET
       - AZURE_STORAGE_KEY
       - sunbird_cert_domain_url

  - mvn clean install
  - cd target
  - java -jar certificate-migration-1.0-SNAPSHOT-jar-with-dependencies.jar

