package com.christ.erp.services.common;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
@Component
public class DBGateway {
    private static EntityManagerFactory EntityManagerFactoryObject;
    private static final Logger LOGGER = LoggerFactory.getLogger(DBGateway.class);    
    @Autowired
    private DBGateway(EntityManagerFactory EntityManagerFactoryObject, DatabaseConfig loadDetails) {
        DBGateway.EntityManagerFactoryObject=EntityManagerFactoryObject;
        DBGateway.loadDetails=loadDetails;
    }

    private final static String READONLY_QUERY_PREFIX;
   //private final static String CONNECTION_MASTERS;
    //private final static String CONNECTION_SLAVES;
    //private final static String CONNECTION_MASTERS_PASS;
    //private final static String CONNECTION_SLAVES_PASS;
    private static DatabaseConfig loadDetails;


    static {
        READONLY_QUERY_PREFIX = AppProperties.get("mysql.readonly.query.prefix");
        //CONNECTION_MASTERS = AppProperties.get("mysql.connection.masters");
        //CONNECTION_MASTERS_PASS = AppProperties.get("mysql.connection.masters.password");
        //CONNECTION_SLAVES = AppProperties.get("mysql.connection.slaves");
        //CONNECTION_SLAVES_PASS = AppProperties.get("mysql.connection.slaves.password");
    }

    public static void runJPA(ITransactional transactional) {
        runJPA(transactional, false);
    }
    public static void runJPA(ITransactional transactional, boolean readonly) {
        if(transactional != null) {
            EntityManager jpaManager = EntityManagerFactoryObject.createEntityManager();
            jpaManager.getTransaction().begin();
            try {
                transactional.onRun(jpaManager);
                jpaManager.getTransaction().commit();
            }
            catch(Exception ignored) {
                transactional.onError(ignored);
                try { jpaManager.getTransaction().rollback(); }
                catch(Exception error) { }
            }
            finally {
                try { jpaManager.close(); }
                catch(Exception error) { }
            }
        }
    }
    public static boolean runJPA(ICommitTransactional transactional) throws Exception {
        boolean result = false;
        if(transactional != null) {
            EntityManager jpaManager = EntityManagerFactoryObject.createEntityManager();
            jpaManager.getTransaction().begin();
            try {
                result = transactional.onRun(jpaManager);
                jpaManager.getTransaction().commit();
            }
            catch(Exception ignored) {
                transactional.onError(ignored);
                try { jpaManager.getTransaction().rollback(); }
                catch(Exception error) { }
            }
            finally {
                try { jpaManager.close(); }
                catch(Exception error) { }
            }
        }
        return result;
    }

    public static<T> T runJPA(ICommitGenericTransactional<T> transactional) throws Exception {
        T result = null;
        if(transactional != null) {
            EntityManager jpaManager = EntityManagerFactoryObject.createEntityManager();
            jpaManager.getTransaction().begin();
            try {
                result = transactional.onRun(jpaManager);
                jpaManager.getTransaction().commit();
            }
            catch(Exception ignored) {
                transactional.onError(ignored);
                try { jpaManager.getTransaction().rollback(); }
                catch(Exception error) { }
            }
            finally {
                try { jpaManager.close(); }
                catch(Exception error) { }
            }
        }
        return result;
    }

    public static<T> T runJPA(ISelectGenericTransactional<T> transactional) throws Exception {
        T result = null;
        if(transactional != null) {
            EntityManager jpaManager = EntityManagerInstance.getEntityManager();
            try {
                result = transactional.onRun(jpaManager);
            }
            catch(Exception ignored) {
                transactional.onError(ignored);
            }
           /*finally {  //Commented because lazy initialization Set throwing exception ('failed to lazy initialize a collection') sometimes. Needed to check performance constraints. -- n1^1n
                try { jpaManager.close(); }
                catch(Exception error) { }
            }*/
        }
        return result;
    }

    public static Mono<? extends Object> execute(IDBTransaction<Mono<? extends Object>> transaction, Boolean isReadOnly) {
        final Connection connection = (Connection) DBGateway.getConnection(isReadOnly);
        final IDBSession session = new IDBSession() {
            @Override
            public Object getConnection() {
                return connection;
            }
            @Override
            public boolean isOpen() {
                try { return (connection != null && connection.isClosed() == false); }
                catch (SQLException e) { return false; }
            }
            @Override
            public void rollback() {
                try { connection.rollback(); }
                catch (SQLException e) { }
                finally {
                    try { connection.close(); }
                    catch (SQLException e) { }
                }
            }
        };

        try { connection.setAutoCommit(false); }
        catch (SQLException e) { }

        return transaction.onExecute(session).map(result -> {
            try {
                if(session.isOpen() == true) {
                    connection.commit();
                }
            }
            catch(SQLException ignored) { }
            finally {
                try { connection.close(); }
                catch(SQLException ignored) { }
            }
            return result;
        });
    }
    public static Mono<DataTable> query(String statement, IDBSession session) {
        return Mono.justOrEmpty(null);
    }
    public static Mono<DataTable> update(String statement, IDBSession session) {
        return Mono.justOrEmpty(null);
    }
    public static Mono<DataTable> query(String statement, List<? extends Object> args, IDBSession session) {
        return Mono.justOrEmpty(null);
    }
    public static Mono<DataTable> update(String statement, List<? extends Object> args, IDBSession session) {
        return Mono.justOrEmpty(null);
    }

    public static Object getConnection(String statement) {
        return getConnection((statement != null &&
                statement.trim().length() > 0 &&
                statement.trim().toUpperCase().startsWith(READONLY_QUERY_PREFIX)));
    }
    public static Object getConnection(Boolean isReadOnly) {
        Connection connection = null;
        try {
            //String connectionString = CONNECTION_MASTERS;
            //String connectionString = CONNECTION_MASTERS + URLEncoder.encode(CONNECTION_MASTERS_PASS, StandardCharsets.UTF_8);
            //System.out.println("bean"+loadDetails.getUrl());
            String connectionString = loadDetails.getUrl() + "?user=" + loadDetails.getUsername() + "&password=" + URLEncoder.encode(loadDetails.getPassword(), StandardCharsets.UTF_8);
            if(isReadOnly == true) {
                //connectionString = CONNECTION_SLAVES;
                //connectionString = CONNECTION_SLAVES + URLEncoder.encode(CONNECTION_SLAVES_PASS, StandardCharsets.UTF_8);
                connectionString =  loadDetails.getUrl() + "?user=" + loadDetails.getUsername() + "&password=" + URLEncoder.encode(loadDetails.getPassword(), StandardCharsets.UTF_8);
            }
            connection = DriverManager.getConnection(connectionString);
        }
        catch(Exception ex) { Utils.log(ex.getMessage()); }
        return connection;
    }

    public static Mono<DataTable> executeQuery(String query, List<? extends Object> args, Boolean isReadOnly) {
        try {
            Connection connection = (Connection) DBGateway.getConnection(isReadOnly);
            try { return DBGateway.executeQuery(query, args, connection); }
            catch(Exception ex) { Utils.log(ex.getMessage()); }
            finally {
                try { connection.close(); } catch(Exception ex) { }
            }
        }
        catch(Exception ex) { Utils.log(ex.getMessage()); }
        return Mono.justOrEmpty(null);
    }
    public static Mono<DataTable> executeQuery(String query, List<? extends Object> args, Object connectionObject) {
        LOGGER.info("inside executeQuery");
        DataTable datatable = null;
        try {
            Connection connection = (Connection) connectionObject;
            PreparedStatement statement = connection.prepareStatement(query);
            if(args != null && args.size() > 0) {
                for(int i = 1; i <= args.size(); i++) {
                    Object value = args.get(i - 1);
                    if(value.getClass() == int.class || value.getClass() == Integer.class) {
                        statement.setInt(i, (Integer) value);
                    }
                    else if(value.getClass() == double.class || value.getClass() == Double.class) {
                        statement.setDouble(i, (double) value);
                    }
                    else if(value.getClass() == boolean.class || value.getClass() == Boolean.class) {
                        statement.setBoolean(i, (Boolean) value);
                    }
                    else {
                        statement.setString(i, (value == null ? "" : value.toString()));
                    }
                }
            }
            try {
            	LOGGER.info("before executeQuery");
                ResultSet result = statement.executeQuery();
                LOGGER.info("after executeQuery");
                if(result != null) {
                    List<String> columns = new ArrayList<>();
                    List<DataRow> rows = new ArrayList<>();
                    ResultSetMetaData metadata = result.getMetaData();
                    for(int i = 1; i <= metadata.getColumnCount(); i++) {
                        columns.add(metadata.getColumnLabel(i));
                    }
                    datatable = new DataTable(rows, columns, 0, "");
                    int columnCount = metadata.getColumnCount();
                    while(result.next()) {
                        List<Object> items = new ArrayList<>();
                        for(int i = 1; i <= columnCount; i++) {
                            items.add(result.getObject(i));
                        }
                        rows.add(new DataRow(datatable, items));
                    }
                } else {
                    LOGGER.info("Result is null");
                }
            }
            catch(Exception ex) {
                LOGGER.error("from query execution in DBGateway" + ex.getMessage());
                Utils.log(ex.getMessage());
            }
        }
        catch(Exception ex) {
            LOGGER.error("from connection in DBGateway" + ex.getMessage());
            Utils.log(ex.getMessage());
        }
        return Mono.justOrEmpty(datatable);
    }
    public static Mono<Integer> executeUpdate(String query, List<? extends Object> args, Boolean isReadOnly) {
        try {
            Connection connection = (Connection) DBGateway.getConnection(isReadOnly);
            try { return DBGateway.executeUpdate(query, args, connection); }
            catch(Exception ex) { Utils.log(ex.getMessage()); }
            finally {
                try { connection.close(); } catch(Exception ex) { }
            }
        }
        catch(Exception ex) { Utils.log(ex.getMessage()); }
        return Mono.justOrEmpty(-1);
    }
    public static Mono<Integer> executeUpdate(String query, List<? extends Object> args, Object connectionObject) {
        Integer status = -1;
        try {
            Connection connection = (Connection) connectionObject;
            PreparedStatement statement = connection.prepareStatement(query);
            if(args != null && args.size() > 0) {
                for(int i = 1; i <= args.size(); i++) {
                    Object value = args.get(i - 1);
                    if(value.getClass() == int.class || value.getClass() == Integer.class) {
                        statement.setInt(i, (Integer) value);
                    }
                    else if(value.getClass() == double.class || value.getClass() == Double.class) {
                        statement.setDouble(i, (double) value);
                    }
                    else if(value.getClass() == boolean.class || value.getClass() == Boolean.class) {
                        statement.setBoolean(i, (Boolean) value);
                    }
                    else {
                        statement.setString(i, (value == null ? "" : value.toString()));
                    }
                }
            }
            try { status = statement.executeUpdate(); }
            catch(Exception ex) { Utils.log(ex.getMessage()); }
        }
        catch(Exception ex) { Utils.log(ex.getMessage()); }
        return Mono.justOrEmpty(status);
    }

    public static Mono<DataTable> executeReadonlyQuery(String statement) {
        return DBGateway.executeReadonlyQuery(statement, null);
    }
    public static Mono<DataTable> executeReadonlyQuery(String statement, List<? extends Object> args) {
        return DBGateway.executeQuery(statement, args, true);
    }
    public static Mono<DataTable> executeWritableQuery(String statement) {
        return DBGateway.executeWritableQuery(statement, null);
    }
    public static Mono<DataTable> executeWritableQuery(String statement, List<? extends Object> args) {
        return DBGateway.executeQuery(statement, args, false);
    }
    public static Mono<Integer> executeWritableUpdate(String statement, List<? extends Object> args) {
        return DBGateway.executeUpdate(statement, args, false);
    }

    public static Mono<DataTable> executeQuery(String statement) {
        return DBGateway.executeQuery(statement, null);
    }
    public static Mono<DataTable> executeQuery(String statement, List<? extends Object> args) {
        Boolean isReadOnly = (statement != null &&
                statement.trim().length() > 0 &&
                statement.trim().toUpperCase().startsWith(READONLY_QUERY_PREFIX));
        String params = "";
        if(args != null) {
            for (int i = 0; i < args.size(); i++) {
                params += (i > 0 ? "," : "") + "?";
            }
        }
        return executeQuery("CALL " + statement + "(" + params + ");", args, isReadOnly);
    }
}