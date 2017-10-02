package com.fedex.ils;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.json.JSONException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fedex.UiApplication;
import com.fedex.lhso.ils.entity.User;

@RunWith(SpringRunner.class)
//set the profile
@ActiveProfiles(profiles = "L0") 
@SpringBootTest(classes = UiApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTestRunner {
	
	//as SpringRunner is configured to random port, 
	//let the application inject the port.
	@LocalServerPort
	private int port;
	
	//Convenient alternative of {@link RestTemplate} that is suitable for integration tests.
	//They are fault tolerant, and optionally can carry Basic authentication headers.
	@Autowired
	TestRestTemplate restTemplate;
	
	//Current sample application is enabled with Basic authentication 
	HttpHeaders headers = new HttpHeaders();
	
	@Autowired
	Environment env;
	
	@Before
	public void before() {
		headers.add("Authorization", createHttpAuthenticationHeaderValue(
				env.getProperty("security.user.name"), env.getProperty("security.user.password")));
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));		
	}
	/**
	 * Create URL with dynamic port number
	 * @param uri
	 * @return
	 */
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
	
	/**
	 * Create an entity for HTTP Basic Authentication 
	 * @param userId
	 * @param password
	 * @return
	 */
	private String createHttpAuthenticationHeaderValue(String userId,
			String password) {

		String auth = userId + ":" + password;

		byte[] encodedAuth = Base64.encode(auth.getBytes(Charset
				.forName("US-ASCII")));

		String headerValue = "Basic " + new String(encodedAuth);

		return headerValue;
	}
	
	/**
	 * Test case to retrieve all registerd users.
	 * @throws JSONException
	 */
	@Test
	public void _1testRegisterUser() throws JSONException {
		String userBody = "{\"name\": \"veladitya1\", \"email\": \"veladitya1@gmail.com\", \"password\": \"admin\", \"userRole\": \"ROLE_USER\"}";
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(userBody, headers);
		
		//using rest template to retrieve array of users 
		ResponseEntity<Object> response = restTemplate.exchange(createURLWithPort("/api/saveUser"), 
				HttpMethod.POST, entity, Object.class);
		
		//assert statement
		assertEquals(true, response.getBody());
	}
	
	
	/**
	 * Test case to retrieve all registerd users.
	 * @throws JSONException
	 */
	@Test
	public void _2testRetrieveAllRegisteredUsers() throws JSONException {

		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		//using rest template to retrieve array of users 
		ResponseEntity<User[]> response = restTemplate.exchange(createURLWithPort("/api/allUsers"), 
				HttpMethod.GET, entity, User[].class);
		
		//assert statement
		assertEquals(response.getBody().length, 3);
	}
}
