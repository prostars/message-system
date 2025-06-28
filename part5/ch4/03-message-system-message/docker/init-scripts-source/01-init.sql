CREATE USER 'replica_user'@'%' IDENTIFIED WITH 'mysql_native_password' BY 'replica_password';
GRANT REPLICATION SLAVE ON *.* TO 'replica_user'@'%';
FLUSH PRIVILEGES;
