/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import commons.Database;
import constants.Constants;
import dbutil.DBUtil;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * @author              NiRRaNjAN
 * @stackoverflow       http://stackoverflow.com/users/1911941/elite
 * @playstore           https://play.google.com/store/apps/developer?id=NiRRaNjAN+RauT
 * @facebook            https://www.facebook.com/NiRRaNjAN.RauT
 * @gmail               nirranjan.raut@gmail.com
 */
@Path("/Service")
public class Service implements Constants {

    @GET
    @Path("/{db}/{table}")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Object> select(@PathParam("db") String db,
            @PathParam("table") String table, @Context UriInfo uriInfo) {
        HashMap<String, Object> records = new HashMap<String, Object>();
        try {
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                records.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return records;
            }
            String where = "";
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            int i=0;
            for(String key : queryParams.keySet()) {
                String value = queryParams.get(key).get(0);
                if(i != 0) {
                    where += " AND ";
                }
                where += key + "='" + value + "'";
                i++;
            }
            ArrayList<HashMap<String, Object>> rows = Database.selectMap(table, null, where, connection);
            records.put(STATUS, SUCCESS);
            records.put(RECORDS, rows);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            records.put(STATUS, e.getMessage());
        }
        return records;
    }

    @GET
    @Path("/{db}/{table}/like")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Object> selectLike(@PathParam("db") String db,
            @PathParam("table") String table, @Context UriInfo uriInfo) {
        HashMap<String, Object> records = new HashMap<String, Object>();
        try {
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                records.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return records;
            }
            String where = "";
            MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
            int i=0;
            for(String key : queryParams.keySet()) {
                String value = queryParams.get(key).get(0);
                if(i != 0) {
                    where += " AND ";
                }
                where += key + " LIKE '%" + value + "%'";
                i++;
            }
            ArrayList<HashMap<String, Object>> rows = Database.selectMap(table, null, where, connection);
            records.put(STATUS, SUCCESS);
            records.put(RECORDS, rows);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            records.put(STATUS, e.getMessage());
        }
        return records;
    }

    @POST
    @Path("/{db}/{table}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED})
    public HashMap insert(@PathParam("db") String db, @PathParam("table") String table,
            @Context HttpServletRequest request) {
        HashMap<String, Object> records = new HashMap<String, Object>();
        try {
            Enumeration<String> params = request.getParameterNames();
            ArrayList<String> columns = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            for(;params.hasMoreElements();) {
                String name = params.nextElement();
                String value = request.getParameter(name);
                columns.add(name);
                values.add(value);
            }
            if(columns.isEmpty() || values.isEmpty()) {
                records.put(STATUS, "Invalid input");
                return records;
            }
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                records.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return records;
            }
            if(Database.insert(table, columns, values, connection)) {
                records.put(STATUS, SUCCESS);
            } else {
                records.put(STATUS, "Failed to insert record.");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            records.put(STATUS, e.getMessage());
        }
        return records;
    }

    @PUT
    @Path("/{db}/{table}/{column}/{value}/{condition}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    public HashMap update(@PathParam("db") String db, @PathParam("table") String table,
            @PathParam("column") String column, @PathParam("value") String val,
            @PathParam("condition") String condition, @Context UriInfo uriInfo) {
        HashMap<String, Object> status = new HashMap<String, Object>();
        try {
            MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
            Set<String> keys = params.keySet();
            HashMap<String, Object> values = new HashMap<String, Object>();
            for(String key : keys) {
                String value = params.get(key).get(0);
                values.put(key, value);
            }
            if(values.isEmpty()) {
                status.put(STATUS, "No values to update. Mention query values after ? in url.");
                return status;
            }
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                status.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return status;
            }
            String where = null;
            if(column != null && !column.isEmpty() && val != null && !val.isEmpty() && condition != null && !condition.isEmpty()) {
                where = column;
                if (EQ.equalsIgnoreCase(condition)) {
                    where += "='" + val + "'";
                } else if (GT.equalsIgnoreCase(condition)) {
                    where += ">'" + val + "'";
                } else if (LT.equalsIgnoreCase(condition)) {
                    where += "<'" + val + "'";
                } else {
                    where += "='" + val + "'";
                }
            }
            if(Database.update(table, where, values, connection)) {
                status.put(STATUS, SUCCESS);
            } else {
                status.put(STATUS, "Failed to insert records.");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            status.put(STATUS, e.getMessage());
        }
        return status;
    }

    @DELETE
    @Path("/{db}/{table}/{column}/{value}/{condition}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED})
    public HashMap delete(@PathParam("db") String db, @PathParam("table") String table,
            @PathParam("column") String column, @PathParam("value") String val,
            @PathParam("condition") String condition) {
        HashMap<String, Object> status = new HashMap<String, Object>();
        try {
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                status.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return status;
            }
            String where = null;
            if(column != null && !column.isEmpty() && val != null && !val.isEmpty() && condition != null && !condition.isEmpty()) {
                where = column;
                if (EQ.equalsIgnoreCase(condition)) {
                    where += "='" + val + "'";
                } else if (GT.equalsIgnoreCase(condition)) {
                    where += ">'" + val + "'";
                } else if (LT.equalsIgnoreCase(condition)) {
                    where += "<'" + val + "'";
                } else {
                    where += "='" + val + "'";
                }
            }
            if(Database.delete(table, where, connection)) {
                status.put(STATUS, SUCCESS);
            } else {
                status.put(STATUS, "Failed to delete records.");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            status.put(STATUS, e.getMessage());
        }
        return status;
    }

    @DELETE
    @Path("/{db}/{table}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_FORM_URLENCODED})
    public HashMap deleteAll(@PathParam("db") String db, @PathParam("table") String table) {
        HashMap<String, Object> status = new HashMap<String, Object>();
        try {
            Connection connection = DBUtil.getConnection(db);
            if (connection == null || connection.isClosed()) {
                status.put(STATUS, "Failed to connect to database : " + DBUtil.getErrorMessage());
                return status;
            }
            String where = null;
            if(Database.delete(table, where, connection)) {
                status.put(STATUS, SUCCESS);
            } else {
                status.put(STATUS, "Failed to delete records.");
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            status.put(STATUS, e.getMessage());
        }
        return status;
    }

}