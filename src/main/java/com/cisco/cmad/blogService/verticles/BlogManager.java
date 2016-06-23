package com.cisco.cmad.blogService.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.cisco.cmad.blogService.dto.BlogDTO;
import com.cisco.cmad.blogService.mongodb.MongoDBUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BlogManager {
	Logger logger = Logger.getLogger(BlogManager.class.getName());
	ObjectMapper mapper = new ObjectMapper();
	Datastore dataStore = MongoDBUtil.getMongoDB();

	public void getBlogs(RoutingContext routingContext) {
		List<BlogDTO> blogs = dataStore.createQuery(BlogDTO.class).asList();
		if (blogs.size() > 0) {
			routingContext
					.response()
					.setStatusCode(201)
					.putHeader("content-type",
							"application/json; charset=utf-8")
					.end(Json.encodePrettily(blogs));
		} else {
			routingContext
					.response()
					.setStatusCode(201)
					.putHeader("content-type",
							"application/json; charset=utf-8")
					.end(Json
							.encodePrettily("No blogs found!!! Go ahead & create one."));
		}
	}

	public void postBlog(RoutingContext routingContext) {

		logger.log(Level.INFO,
				"Inside postBlog API!! " + routingContext.getBodyAsString());

		try {
			BlogDTO blogDTO = Json.decodeValue(
					routingContext.getBodyAsString(), BlogDTO.class);
			blogDTO.setBlogId(UUID.randomUUID().toString());
			Client client = ClientBuilder.newClient();
			Response response = client
					.target("http://vm-srdesai-001/api/user/validateToken")
					.request("application/json")
					.header("Authorization",
							routingContext.request().getHeader("Authorization"))
					.get();
			if (response != null && response.getStatus() == 201) {
				System.out.println("Response ---->"+response.getStatus()+"::"+response.getStatusInfo());
				blogDTO.setUserId(response.readEntity(String.class)); // TODO replace dummy user with
												// actual user info
				blogDTO.setComments(null);

				Vertx.vertx().executeBlocking(
						future -> dataStore.save(blogDTO),
						res -> logger.log(Level.INFO,
								"Added New User Details Successsfully. : "
										+ res.result()));
				routingContext
						.response()
						.setStatusCode(201)
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily(blogDTO));
			} else {
				routingContext
						.response()
						.setStatusCode(403)
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily(null));
			}

		} catch (Exception e) {
			logger.log(Level.INFO,
					"Exception while posting blog " + e.getMessage());
		}

	}

	public void updateBlog(RoutingContext routingContext) {

	}

	public void getBlog(RoutingContext routingContext) {
		logger.log(Level.INFO,
				"Inside getBlog API!! " + routingContext.getBodyAsString());
		try {
			BlogDTO frontEngBlogDTO = mapper.readValue(
					routingContext.getBodyAsString(), BlogDTO.class);
			Query<BlogDTO> q = dataStore.find(BlogDTO.class, "title",
					frontEngBlogDTO.getTitle());
			logger.log(Level.INFO,
					"The given title is :" + frontEngBlogDTO.getTitle());

			if (q != null && q.get() != null)
				logger.log(Level.INFO, "The retrieved blog content is : "
						+ q.get().getContent());
		} catch (Exception e) {
			logger.log(Level.INFO,
					"Exception while retriving blog " + e.getMessage());
		}

	}

	public void addComment(RoutingContext routingContext) {
		try {
			logger.log(Level.INFO, "Inside getBlog API!! addComment"
					+ routingContext.getBodyAsString());

			String blogID = routingContext.request().getParam("blogId");

			String blogComment = java.net.URLDecoder.decode(routingContext
					.request().getParam("comment"), "UTF-8");
			Query<BlogDTO> q = dataStore.find(BlogDTO.class, "blogId", blogID);

			String commentId = UUID.randomUUID().toString();
			Map<String, String> commentMap = new HashMap<String, String>();
			Client client = ClientBuilder.newClient();
			Response response = client
					.target("http://vm-srdesai-001/api/user/validateToken")
					.request("application/json")
					.header("Authorization",
							routingContext.request().getHeader("Authorization"))
					.get();
			if (response != null && response.getStatus() == 201) {
			commentMap.put(response.readEntity(String.class), blogComment); //TODO : replace userName with actual username.
			Map<String, Map<String, String>> existingMap = new HashMap<String, Map<String, String>>();
			if (q.get().getComments() != null)
				existingMap = q.get().getComments();
			existingMap.put(commentId, commentMap);
			
			UpdateOperations<BlogDTO> ops = dataStore.createUpdateOperations(
					BlogDTO.class).set("comments", existingMap);

			Vertx.vertx().executeBlocking(
					future -> dataStore.update(q.get(),ops),
					res -> logger.log(Level.INFO,
					"Added New User Details Successsfully. : "
							+ res.result()));
			routingContext
					.response()
					.setStatusCode(201)
					.putHeader("content-type",
							"application/json; charset=utf-8")
					.end("Comment Added Successfully!!!!");
			}
			else
			{
				routingContext
				.response()
				.setStatusCode(403)
				.putHeader("content-type",
						"application/json; charset=utf-8")
				.end("Please login with valid credentials to post comment...");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void searchBlog(RoutingContext routingContext) {
		try {
			logger.log(Level.INFO, "Inside searchBlog API!!");

			String searchTag = java.net.URLDecoder.decode(routingContext
					.request().getParam("tag"), "UTF-8");
			logger.log(Level.INFO, "Search tag: " + searchTag);

			Query<BlogDTO> q = dataStore.createQuery(BlogDTO.class);
			q.or(q.criteria("title").contains(searchTag), q.criteria("tags")
					.contains(searchTag));
			List<BlogDTO> blogs = q.asList();
			logger.log(Level.INFO, "Search tag: " + blogs.size());
			if (blogs.size() > 0) {
				routingContext
						.response()
						.setStatusCode(201)
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily(blogs));
			} else {
				routingContext
						.response()
						.setStatusCode(201)
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily("No blogs found!!!"));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
