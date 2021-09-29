package com.feanaro.lindale_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feanaro.lindale_api.DatabaseSupport;
import com.feanaro.lindale_api.models.MethodHttp;
import com.feanaro.lindale_api.models.RoleAuth;
import com.feanaro.lindale_api.models.VerbHttp;
import com.feanaro.lindale_api.utils.JsonDoc;
import com.feanaro.lindale_api.utils.PasswordUtils;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.http.ResponseEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.bson.Document;
import org.json.JSONString;
import org.json.JSONObject;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;

@RestController
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class LindaleApiApplication {

	static JsonDoc jsonDoc = new JsonDoc();
	static PasswordUtils pws = new PasswordUtils();

	static DatabaseSupport db = new DatabaseSupport();
	static HashMap<String, HashMap<String, List<Document>>> dbThings = db.getAllDatabases();

	public static void main(String[] args) {
		SpringApplication.run(LindaleApiApplication.class, args);
		// Must have create Database first according to the setting : /
	}

	// User-Agent
	static String _HEADER_CUSTOM = "xx__feanaro__xx_____";
	static String _HEADER_NAME = _HEADER_CUSTOM + "name";
	static String _HEADER_HTAG = _HEADER_CUSTOM + "htag";
	static String _HEADER_CURRENT_ROLE = _HEADER_CUSTOM + "current_role";
	// String _HEADER_UserAgent = "User-Agent";

	public static boolean haveheader(Map<String, String> headers, String selectHead) {
		return headers.keySet().contains(selectHead);
	}

	// EXCEPT AUTHENTIFICATE AND SUBSCRIBE
	public static boolean verifyBeforeEachRequest(Map<String, String> headers, String db, String col, String method) {
		if (DatabaseSupport.isColByDb(db, col)) {
			return true;
		} else {
			return false;
		}
		// if (DatabaseSupport.isColByDb(db, col) == false || haveheader(headers,
		// _HEADER_NAME) == false
		// || haveheader(headers, _HEADER_HTAG) == false || haveheader(headers,
		// _HEADER_CURRENT_ROLE) == false
		// || (haveheader(headers, _HEADER_CURRENT_ROLE) == true
		// && headers.get(_HEADER_CURRENT_ROLE) == RoleAuth.NONE.toString())) {
		// return false;
		// } else {
		// List<String> roleRequired =
		// DatabaseSupport.getMongoStructExpected().get(db).get(col).get(method);
		// return roleRequired.contains(RoleAuth.ALL.toString()) ||
		// roleRequired.contains(headers.get(_HEADER_CURRENT_ROLE));
		// }
	}


	// GET
	@RequestMapping(value = "/{project}/{object}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> getAllUni(@RequestHeader Map<String, String> headers, @PathVariable String project,
			@PathVariable String object) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.GET_ALL.toString())) {

			MongoCollection<Document> col = DatabaseSupport.gotCollection(project, object);
			MongoCursor<Document> data = col.find().iterator();
			List<Document> arrayDoc = new ArrayList<Document>();
			while (data.hasNext()) {
				arrayDoc.add(data.next());
			}
			return ResponseEntity.ok(arrayDoc);

		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	@RequestMapping(value = "/{project}/{object}/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> getOneUni(@RequestHeader Map<String, String> headers, @PathVariable String project,
			@PathVariable String object, @PathVariable String id) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.GET_ONE.toString())) {
			Document doc = db.getOne(project, object, id);

			return ResponseEntity.ok(doc);
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	@RequestMapping(value = "/{project}/{object}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> postManyUni(@RequestHeader Map<String, String> headers,
			@RequestBody(required = false) JSONString objs, @PathVariable String project, @PathVariable String object) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.CREATE_MANY.toString())) {
			List<Document> manyObj = JsonDoc.jsonsToDoc(JsonDoc.strToJsons(objs));
			InsertManyResult res = db.createMany(project, object, manyObj);

			return ResponseEntity.ok(manyObj);
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	@RequestMapping(value = "/{project}/{object}/New", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> postOneUni(@RequestHeader Map<String, String> headers,
			@RequestBody(required = false) JSONString obj, @PathVariable String project, @PathVariable String object) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.CREATE_ONE.toString())) {
			Document objDoc = JsonDoc.jsonToDoc(JsonDoc.strToJson(obj));
			InsertOneResult doc = db.createOne(project, object, objDoc);

			return ResponseEntity.ok(objDoc);
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	// Update
	@RequestMapping(value = "/{project}/{object}/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> updateOneUni(@RequestHeader Map<String, String> headers,
			@RequestBody(required = false) JSONString obj, @PathVariable String project, @PathVariable String object,
			@PathVariable String id) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.UPDATE_ONE.toString())) {
			Document doc = JsonDoc.jsonToDoc(JsonDoc.strToJson(obj));
			HashMap<String, Object> mapRes = new HashMap<>();
			mapRes.put("_id", id);
			mapRes.put("data", doc);

			UpdateResult res = db.updateOne(project, object, id, doc);
			return ResponseEntity.ok(doc);
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	// SEARCH ...
	@RequestMapping(value = "/{project}/{object}/Search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> searchUniversal(@RequestHeader Map<String, String> headers,
			@RequestBody(required = false) JSONString obj, @PathVariable String project, @PathVariable String object) {
		if (verifyBeforeEachRequest(headers, project, object, MethodHttp.SEARCH.toString())) {
			try {
				HashMap<String, ?> query = new ObjectMapper().readValue(obj.toString(), HashMap.class);

				List<Document> res = new ArrayList<Document>();
				MongoCursor<Document> itDocument = db.search(project, object, query).iterator();
				while (itDocument.hasNext()) {
					res.add(itDocument.next());
				}
				return ResponseEntity.ok(res);
			} catch (Exception e) {
				return ResponseEntity.badRequest().body(e);
			}
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	@CrossOrigin
	@GetMapping("/")
	public ResponseEntity<?> getIsAlive() {
		return ResponseEntity.ok("API is Alive");
	}

	@CrossOrigin
	@GetMapping("/Database/Config/Current")
	public ResponseEntity<?> getDabaseConfigCurrent() {
		return ResponseEntity.ok(DatabaseSupport.getMongoStructCurrent());
	}

	@CrossOrigin
	@GetMapping("/Database/Config/Expected")
	public ResponseEntity<?> getDabaseConfigExpe() {
		return ResponseEntity.ok(DatabaseSupport.getMongoStructExpected());
	}

	@CrossOrigin
	@GetMapping("/Database/Download")
	public ResponseEntity<?> downloadDB(@RequestHeader Map<String, String> headers) {
		if (haveheader(headers, _HEADER_CURRENT_ROLE) == true
				&& headers.get(_HEADER_CURRENT_ROLE) == RoleAuth.ADMINISTRATOR.toString()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				byte[] bytesDb_ = mapper.writeValueAsBytes(db.getAllDatabases());
				return ResponseEntity.ok().contentLength(bytesDb_.length)
						.contentType(MediaType.parseMediaType("application/octet-stream"))
						.body(new InputStreamResource(new ByteArrayInputStream(bytesDb_)));
			}

			catch (Exception e) {
				return ResponseEntity.badRequest().body("");
			}
		} else {
			return ResponseEntity.badRequest().body("");
		}
	}

	@RequestMapping(value = "/Feanaro/User/subscribe", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	public ResponseEntity<?> subscribe(@RequestBody(required = false) Object obj) {
		Gson gson = new Gson();
		String objJson = gson.toJson(obj);
		JSONObject objJObject = new JSONObject(objJson);

		MongoCursor<Document> usersIt = db.search("Feanaro", "User", (Map<String, ?>) obj).iterator();
		List<JSONObject> users_ = new ArrayList<JSONObject>();
		List<String> htags = new ArrayList<String>();
		if (users_.size() > 0) {
			while (usersIt.hasNext()) {
				JSONObject jsonUser = new JSONObject(usersIt.next().toJson());
				users_.add(jsonUser);
				htags.add(jsonUser.getString("htag"));
			}
		}
		JSONObject res = new JSONObject();
		res.put("name", (String) objJObject.get("name"));
		res.put("email", (String) objJObject.get("email"));
		res.put("currentRole", "USER");
		res.put("role", objJObject.get("role"));
		res.put("isConnected", true);
		res.put("status", "EN LIGNE");
		res.put("pws", PasswordUtils.generateSecurePassword((String) objJObject.get("pws")));
		res.put("htag", (String) PasswordUtils.uniqGenRdmHtag(htags));
		db.createOne("Feanaro", "User", JsonDoc.jsonToDoc(res));
		try {
			HashMap<String, ?> f = new ObjectMapper().readValue(res.toString(), HashMap.class);
			return ResponseEntity.ok(f);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

	@RequestMapping(value = "/Feanaro/User/authenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@CrossOrigin
	// name currentRole pws htag
	public ResponseEntity<?> authenticate(@RequestBody(required = false) Object obj) {
		Gson gson = new Gson();
		String objJsonStr = gson.toJson(obj);

		JSONObject FullObjUser = new JSONObject(objJsonStr);
		JSONObject objUser = new JSONObject();
		objUser.put("name", FullObjUser.getString("name"));
		objUser.put("htag", FullObjUser.getString("htag"));

		try {
			HashMap<String, ?> objSearch = new ObjectMapper().readValue(objUser.toString(), HashMap.class);
			MongoCursor<Document> usersIt = db.search("Feanaro", "User", (Map<String, ?>) objSearch).iterator();

			if (usersIt.hasNext()) {
				Document user = usersIt.next();
				boolean userIsCorrect = (PasswordUtils.verifyUserPassword(FullObjUser.getString("pws"),
						user.getString("pws"))
				// && ((boolean) user.get("isConnected") == false)
				);
				if (userIsCorrect == true) {
					try {
						user.put("isConnected", true);
						db.updateOne("Feanaro", "User", user.getObjectId("_id").toString(), user);
						// HashMap<String, ?> fe = new ObjectMapper().readValue(user.toJson(),
						// HashMap.class);
						return ResponseEntity.ok(user.toJson());
					} catch (Exception e) {
						return ResponseEntity.badRequest().body(e);
					}
				} else {
					return ResponseEntity.badRequest().body("Bad password");
				}
			} else {
				return ResponseEntity.badRequest().body("");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e);
		}
	}

}
