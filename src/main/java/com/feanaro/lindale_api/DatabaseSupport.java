package com.feanaro.lindale_api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

import com.feanaro.lindale_api.models.MethodHttp;
import com.feanaro.lindale_api.models.RoleAuth;
import com.feanaro.lindale_api.utils.JsonDoc;

import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.bson.Document;
import org.json.JSONObject;

public class DatabaseSupport {

    public static final String ENV_RUN = "local";

    public static final HashMap<String, HashMap<String, HashMap<String, List<String>>>> getMongoStructExpected() {
        HashMap<String, HashMap<String, HashMap<String, List<String>>>> AllDbStruct = new HashMap<>();

        HashMap<String, HashMap<String, List<String>>> LindaleColStruct = new HashMap<>();
        HashMap<String, HashMap<String, List<String>>> FeanaroColStruct = new HashMap<>();

        HashMap<String, List<String>> USER_ROLE = new HashMap<>();
        HashMap<String, List<String>> HISTORY_ROLE = new HashMap<>();
        HashMap<String, List<String>> GENERIC_ROLE = new HashMap<>();

        // _______________
        USER_ROLE.put(MethodHttp.GET_ALL.toString(), List.of(RoleAuth.ALL.toString()));
        USER_ROLE.put(MethodHttp.GET_ONE.toString(), List.of(RoleAuth.ALL.toString()));
        USER_ROLE.put(MethodHttp.CREATE_MANY.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        USER_ROLE.put(MethodHttp.CREATE_ONE.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        USER_ROLE.put(MethodHttp.UPDATE_ONE.toString(), List.of(RoleAuth.ALL.toString()));
        USER_ROLE.put(MethodHttp.DELETE_MANY.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        USER_ROLE.put(MethodHttp.DELETE_ONE.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        USER_ROLE.put(MethodHttp.SEARCH.toString(),
                List.of(RoleAuth.ADMINISTRATOR.toString(), RoleAuth.TEAM.toString()));

        HISTORY_ROLE.put(MethodHttp.GET_ALL.toString(), List.of(RoleAuth.ALL.toString()));
        HISTORY_ROLE.put(MethodHttp.GET_ONE.toString(), List.of(RoleAuth.ALL.toString()));
        HISTORY_ROLE.put(MethodHttp.CREATE_MANY.toString(), List.of(RoleAuth.NONE.toString()));
        HISTORY_ROLE.put(MethodHttp.CREATE_ONE.toString(), List.of(RoleAuth.NONE.toString()));
        HISTORY_ROLE.put(MethodHttp.UPDATE_ONE.toString(), List.of(RoleAuth.NONE.toString()));
        HISTORY_ROLE.put(MethodHttp.DELETE_MANY.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        HISTORY_ROLE.put(MethodHttp.DELETE_ONE.toString(), List.of(RoleAuth.NONE.toString()));
        HISTORY_ROLE.put(MethodHttp.SEARCH.toString(), List.of(RoleAuth.ALL.toString()));

        GENERIC_ROLE.put(MethodHttp.GET_ALL.toString(), List.of(RoleAuth.ALL.toString()));
        GENERIC_ROLE.put(MethodHttp.GET_ONE.toString(), List.of(RoleAuth.ALL.toString()));
        GENERIC_ROLE.put(MethodHttp.CREATE_MANY.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        GENERIC_ROLE.put(MethodHttp.CREATE_ONE.toString(),
                List.of(RoleAuth.ADMINISTRATOR.toString(), RoleAuth.TEAM.toString()));
        GENERIC_ROLE.put(MethodHttp.UPDATE_ONE.toString(),
                List.of(RoleAuth.MODERATOR.toString(), RoleAuth.TEAM.toString(), RoleAuth.ADMINISTRATOR.toString()));
        GENERIC_ROLE.put(MethodHttp.DELETE_MANY.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        GENERIC_ROLE.put(MethodHttp.DELETE_ONE.toString(), List.of(RoleAuth.ADMINISTRATOR.toString()));
        GENERIC_ROLE.put(MethodHttp.SEARCH.toString(), List.of(RoleAuth.ALL.toString()));

        FeanaroColStruct.put("User", USER_ROLE);
        FeanaroColStruct.put("History", HISTORY_ROLE);
        FeanaroColStruct.put("BiblioRef", GENERIC_ROLE);
        FeanaroColStruct.put("Website", GENERIC_ROLE);
        FeanaroColStruct.put("ReactFaberDocs", GENERIC_ROLE);
        FeanaroColStruct.put("Component", GENERIC_ROLE);

        LindaleColStruct.put("Composer", GENERIC_ROLE);
        LindaleColStruct.put("Performer", GENERIC_ROLE);
        LindaleColStruct.put("InstrumentMaker", GENERIC_ROLE);
        LindaleColStruct.put("Instrument", GENERIC_ROLE);
        LindaleColStruct.put("Artwork", GENERIC_ROLE);
        LindaleColStruct.put("MusicSheet", GENERIC_ROLE);
        LindaleColStruct.put("Opus", GENERIC_ROLE);
        LindaleColStruct.put("SaasStreaming", GENERIC_ROLE);
        LindaleColStruct.put("Radio", GENERIC_ROLE);
        LindaleColStruct.put("MusicScientist", GENERIC_ROLE);
        LindaleColStruct.put("News", GENERIC_ROLE);
        LindaleColStruct.put("Museum", GENERIC_ROLE);
        LindaleColStruct.put("MusicSchool", GENERIC_ROLE);
        LindaleColStruct.put("Press", GENERIC_ROLE);
        LindaleColStruct.put("DiscHouse", GENERIC_ROLE);

        // LindaleColStruct.put("s","");
        AllDbStruct.put("Feanaro", FeanaroColStruct);
        AllDbStruct.put("Lindale", LindaleColStruct);
        return AllDbStruct;
    }

    public static HashMap<String, List<String>> getMongoStructCurrent() {
        HashMap<String, List<String>> res = new HashMap<>();
        MongoCursor<String> itDbs = getInstance().listDatabaseNames().iterator();
        while (itDbs.hasNext()) {

            String dbCurrent = itDbs.next();
            if (isDB(dbCurrent)) {
                List<String> allCol = new ArrayList<String>();
                MongoCursor<String> itCol = getInstance().getDatabase(dbCurrent).listCollectionNames().iterator();
                while (itCol.hasNext()) {
                    allCol.add(itCol.next());
                }
                res.put(dbCurrent, allCol);
            } else {
            }
        }
        return res;
    }

    public static HashMap<String, String> getMongoConfig() {
        HashMap<String, String> CONNECTION_MONGODB = new HashMap<String, String>();
        // CONNECTION_MONGODB.put("prod","");
        CONNECTION_MONGODB.put("local",
                "mongodb://localhost:27017/?readPreference=primary&appname=MongoDB%20Compass%20Community&ssl=false");
        return CONNECTION_MONGODB;
    }

    public static HashMap<String, String> getUrlConfig() {
        HashMap<String, String> CONNECTION_URL = new HashMap<String, String>();
        // CONNECTION_URL.put("prod", "");
        CONNECTION_URL.put("local", "http://localhost:8080");
        return CONNECTION_URL;
    }

    private static class LazyHolder {
        static final MongoClient MONGO_CONFIG = MongoClients.create(getMongoConfig().get(ENV_RUN));
        static final String URL_CONFIG = getUrlConfig().get(ENV_RUN);
    }

    public static JsonDoc jsonDoc = new JsonDoc();

    // END CONFIG
    public static MongoClient getInstance() {
        return LazyHolder.MONGO_CONFIG;
    }

    public static MongoCollection<Document> gotCollection(String db, String col) {
        return getInstance().getDatabase(db).getCollection(col);
    }

    public static boolean isDB(String str) {
        return getMongoStructExpected().keySet().contains(str);
    }

    private static boolean isCol(String str) {
        List<String> Cols = new ArrayList<String>();
        Iterator<String> itDb = getMongoStructExpected().keySet().iterator();
        while (itDb.hasNext()) {
            String currentDb = itDb.next();
            Iterator<String> itCol = getMongoStructExpected().get(currentDb).keySet().iterator();
            while (itCol.hasNext()) {
                String currentCol = itCol.next();
                Cols.add(currentCol);
            }
        }
        return Cols.contains(str);
    }

    public static boolean isColByDb(String db, String col) {
        if (isCol(col) == false || isDB(db) == false) {
            return false;
        } else {
            return getMongoStructExpected().get(db).keySet().contains(col);
        }
    }

    public static Date dateByObjIdHexa(String id) {
        return new Date(Long.parseLong(id.substring(0, 8), 16) * 1000);
    }

    public static Document queryById(String id) {
        Document query = new Document();
        query.put("_id", new ObjectId(id));
        return query;
    }

    public Document getOne(String db, String col, String id) {
        List<Document> cur = gotCollection(db, col).find(queryById(id)).into(new ArrayList<Document>());
        return cur.get(0);
    }

    public DeleteResult deleteOne(String db, String col, String id) {
        return gotCollection(db, col).deleteOne(queryById(id));
    }

    public DeleteResult deleteMany(String db, String col) {
        return gotCollection(db, col).deleteMany(new Document());
    }

    public UpdateResult updateOne(String db, String col, String id, Document doc) {
        return gotCollection(db, col).replaceOne(queryById(id), doc);
    }

    public boolean isIdExists(String db, String col, String id) {
        FindIterable<Document> iterable = gotCollection(db, col).find(queryById(id));
        return iterable.first() != null;
    }

    public InsertOneResult createOne(String db, String col, Document createObj) {
        return gotCollection(db, col).insertOne(createObj);
    }

    public InsertManyResult createMany(String db, String col, List<Document> listObj) {
        return gotCollection(db, col).insertMany(listObj);
    }

    public FindIterable<Document> search(String db, String col, Map<String, ?> query) {
        Gson gson = new Gson();
        JSONObject json = new JSONObject(gson.toJson(query));
        return gotCollection(db, col).find(jsonDoc.jsonToDoc(json));
    }

    public HashMap<String, HashMap<String, List<Document>>> getAllDatabases() {
        MongoClient mongoClient = getInstance();
        Iterator<String> itDb = getMongoStructExpected().keySet().iterator();
        HashMap<String, HashMap<String, List<Document>>> DATABASES = new HashMap<String, HashMap<String, List<Document>>>();
        while (itDb.hasNext()) {
            String currentDb = itDb.next();
            HashMap<String, List<Document>> COLLECTIONS = new HashMap<String, List<Document>>();

            Iterator<String> itCol = getMongoStructExpected().get(currentDb).keySet().iterator();
            while (itCol.hasNext()) {
                List<Document> temp = new ArrayList<Document>();
                String currentCol = itCol.next();
                MongoCursor<Document> docCuror = mongoClient.getDatabase(currentDb).getCollection(currentCol).find()
                        .cursor();
                while (docCuror.hasNext()) {
                    temp.add(docCuror.next());
                }
                COLLECTIONS.put(currentCol, temp);
            }
            DATABASES.put(currentDb, COLLECTIONS);
        }
        return DATABASES;

    }

}
