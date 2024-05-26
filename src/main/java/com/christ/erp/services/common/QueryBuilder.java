package com.christ.erp.services.common;

import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder {
    private Connection _connection;
    private List<QueryBuilderItem> _queries;

    public QueryBuilder() {
        this._connection = (Connection) DBGateway.getConnection(true);
    }
    public QueryBuilder(boolean isReadOnly) {
        this._connection = (Connection) DBGateway.getConnection(isReadOnly);
    }
    public QueryBuilder(Connection connectionObject) {
        Connection connection = (Connection)connectionObject;
        this._connection = connection;
    }

    public QueryBuilder addUpdateQuery(String statement) {
        this.addUpdateQuery(statement, null);
        return this;
    }
    public QueryBuilder addUpdateQuery(String statement, List<? extends Object> args) {
        if(this._queries == null) {
            this._queries = new ArrayList<>();
        }
        this._queries.add(new QueryBuilderItem(statement, true, args));
        return this;
    }
    public QueryBuilder addSelectQuery(String statement) {
        this.addSelectQuery(statement, null);
        return this;
    }
    public QueryBuilder addSelectQuery(String statement, List<? extends Object> args) {
        if(this._queries == null) {
            this._queries = new ArrayList<>();
        }
        this._queries.add(new QueryBuilderItem(statement, false, args));
        return this;
    }
    public Mono<DataSet> execute() {
        return execute(null);
    }
    public Mono<DataSet> execute(IQueryContext queryContext) {
        DataSet dataset = null;
        try {
            final Map<Integer, DataTable> results = new HashMap<>();
            this._connection.setAutoCommit(false);
            try {
                IQueryTransactionContext context = new IQueryTransactionContext() {
                    @Override
                    public void rollback() {
                        try {
                            if(QueryBuilder.this._connection != null) {
                                QueryBuilder.this._connection.close();
                                QueryBuilder.this._connection = null;
                            }
                        }
                        catch(Exception ignored) { }
                    }
                };
                if(this._queries != null && this._queries.size() > 0) {
                    for(int i = 0; i < this._queries.size(); i++) {
                        if(dataset == null) {
                            dataset = new DataSet();
                        }
                        final Integer index = i;
                        QueryBuilderItem item = this._queries.get(i);

                        QueryItem queryItem = new QueryItem();
                        queryItem.connection = this._connection;
                        queryItem.index = index;
                        queryItem.transactionContext = context;

                        if(queryContext != null) {
                            queryContext.onExecuting(queryItem);
                            if(this._connection == null) {
                                throw new Exception();
                            }
                        }

                        if(item.IsUpdate) {
                            Integer rowsEffected = DBGateway.executeUpdate(item.Query, item.Args, this._connection).block();
                            {
                                if(rowsEffected >= 0) {
                                    List<DataRow> rows = new ArrayList<>();
                                    List<String> columns = new ArrayList<>();
                                    List<Object> values = new ArrayList<>();
                                    DataTable table = new DataTable(rows, columns, 1, "");

                                    values.add(rowsEffected);
                                    columns.add("Status");
                                    rows.add(new DataRow(table, new ArrayList<Object>(rowsEffected)));
                                    results.put(index, table);
                                    queryItem.table = table;
                                }
                                else {
                                    results.put(index, null);
                                }
                            };
                        }
                        else {
                            DataTable table = DBGateway.executeQuery(item.Query, item.Args, this._connection).block();
                            {
                                results.put(index, table);
                                queryItem.table = table;
                            };
                        }

                        if(queryContext != null) {
                            queryContext.onExecuted(queryItem);
                            if(this._connection == null) {
                                throw new Exception();
                            }
                        }
                    }
                }
                for(int i = 0; i < results.size(); i++) {
                    DataTable table = results.get(i);
                    if(table == null) { throw new Exception(); }
                    else { dataset.add(table); }
                }
                this._connection.commit();
            }
            catch(Exception ex) {
                dataset.clear();
                dataset = null;
                this._connection.rollback();
            }
            finally {
                try {
                    if(this._connection != null) {
                        this._connection.close();
                        this._connection = null;
                    }
                }
                catch(Exception ignored) { }
            }
        }
        catch(Exception ignored) { }
        return Utils.monoFromObject(dataset);
    }

    class QueryBuilderItem {
        public String Query;
        public List<? extends Object> Args;
        public Boolean IsUpdate;

        public QueryBuilderItem(String query, Boolean isUpdate) {
            this.Query = query;
            this.IsUpdate = isUpdate;
        }
        public QueryBuilderItem(String query, Boolean isUpdate, List<? extends Object> args) {
            this.Query = query;
            this.IsUpdate = isUpdate;
            this.Args = args;
        }
    }
}
