package com.feanaro.lindale_api.utils;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;
import java.util.ArrayList;
import java.util.Iterator;


public class JsonDoc {

	public static String ObjJavaToJsonString(Object obj){
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	public static JSONObject ObjJavaToJson(Object obj){
		return new JSONObject(ObjJavaToJsonString(obj));
	}
    public static Document jsonToDoc(JSONObject objToDoc) {
		List<String> keys_ = new ArrayList<String>(objToDoc.keySet());
		Document res = new Document();
		Iterator<String> iteKey = keys_.iterator();
		while (iteKey.hasNext()) {
			String keyNow = iteKey.next();
			if (keyNow == "_id") {
				// String id = (String) objToDoc.get(keyNow);
				// res.append(keyNow, new ObjectId(id));
			} else {
				res.put(keyNow, objToDoc.get(keyNow));
			}
		}
		return res;
	}

	public static JSONArray strToJsons(JSONString obj) {
		return (JSONArray) new JSONTokener(obj.toJSONString()).nextValue();
	}

	public static JSONObject strToJson(JSONString obj) {
		return new JSONObject(obj);
	}

	public static List<Document> jsonsToDoc(JSONArray jArray) {
		List<Document> listDoc = new ArrayList<Document>();
		for (int i = 0; i < jArray.length(); i++) {
			listDoc.add(jsonToDoc(jArray.getJSONObject(i)));
		}
		return listDoc;
	}

	public static HashMap<String, ?> strToJavaMap(String json){
		return (HashMap<String, ?>) new Gson().fromJson(json, HashMap.class);
	}
}
