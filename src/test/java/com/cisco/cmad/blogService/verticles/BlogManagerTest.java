package com.cisco.cmad.blogService.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(VertxUnitRunner.class)
public class BlogManagerTest {
	Vertx vertx;
	HttpServer server;
	
	@Before
	public void before(TestContext context) {
		vertx = Vertx.vertx();
		server = vertx.createHttpServer()
				.requestHandler(req -> req.response().end("foo"))
				.listen(8484, context.asyncAssertSuccess());
		vertx.deployVerticle(BootStrapVerticle.class.getName());
	}

	@After
	public void after(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
		vertx.undeploy(BootStrapVerticle.class.getName());
	}

	@Test
	public void testDeployUserVerticle(TestContext context) {
		vertx.deployVerticle(BlogManager.class.getName(),
				context.asyncAssertSuccess(deploymentID -> {
					vertx.undeploy(deploymentID, context.asyncAssertSuccess());
				}));
	}


/*	@Test
	public void testValidateUserWithInValidUser(TestContext context) {
		UserDTO user = new UserDTO();
		user.setUserName("invaliduser");
		user.setPassword("invaliduser");
		final String json = Json.encodePrettily(user);
		final String length = Integer.toString(json.length());
		HttpClient client = vertx.createHttpClient();
		Async async = context.async();
		client.post(8989, "localhost", "/api/user/validateUser")
				.putHeader("content-type", "application/json")
				.putHeader("content-length", length)
				.handler(
						response -> {
							context.assertEquals(response.statusCode(), 403);
							context.assertTrue(response.headers()
									.get("content-type")
									.contains("application/json"));
							response.bodyHandler(body -> {
								System.out.println(body.toString());
								async.complete();
							});
						}).write(json).end();
	}
*/
}