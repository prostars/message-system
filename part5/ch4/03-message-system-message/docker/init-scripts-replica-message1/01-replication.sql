CHANGE REPLICATION SOURCE TO
  SOURCE_HOST = 'mysql-source-message1',
  SOURCE_USER = 'replica_user',
  SOURCE_PASSWORD = 'replica_password',
  SOURCE_AUTO_POSITION = 1;

START REPLICA;
