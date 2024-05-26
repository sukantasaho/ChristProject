package com.christ.erp.services.common;

import reactor.core.publisher.Mono;

public interface IQueryContext {
    Mono<Void> onExecuting(QueryItem item);
    Mono<Void> onExecuted(QueryItem item);
}
