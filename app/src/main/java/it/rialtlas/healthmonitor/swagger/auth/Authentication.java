package it.rialtlas.healthmonitor.swagger.auth;

/*
 * DataFlow API
 * Some custom description of API.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@datagrafservizi.it
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


import java.util.List;
import java.util.Map;

import it.rialtlas.healthmonitor.swagger.Pair;

public interface Authentication {
    /**
     * Apply authentication settings to header and query params.
     *
     * @param queryParams List of query parameters
     * @param headerParams Map of header parameters
     */
    void applyToParams(List<Pair> queryParams, Map<String, String> headerParams);
}