package com.connection.pool.demo;

import java.sql.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.postgresql.ds.PGSimpleDataSource;

import lombok.SneakyThrows;

public class PooledDataSource extends PGSimpleDataSource {

    private final Queue<Connection> connectionPool;

    public PooledDataSource(String url, String userName, String password) {
        setURL(url);
        setUser(userName);
        setPassword(password);
        this.connectionPool = new ConcurrentLinkedQueue<>();
        initDataSource();
    }

    @SneakyThrows
    public void initDataSource() {
        for (int i = 0; i < 10; i++) {
            var physicalConnection = super.getConnection();
            var connectionProxy = new ConnectionProxy(physicalConnection, connectionPool);
            this.connectionPool.offer(connectionProxy);
        }
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }
}