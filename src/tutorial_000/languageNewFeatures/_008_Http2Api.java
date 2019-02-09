package tutorial_000.languageNewFeatures;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class _008_Http2Api {
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void main(String[] args) throws IOException, InterruptedException {
		/*
		 * Java 11 finally introduce the HTTP 2 API. This API was incubating in "jdk.incubator.httpclient" module in java 9, and is
		 * now part of the "java.net.http" module. His main types are HttpClient, HttpRequest, and HttpResponse. It fulle supports
		 * HTTP/2, allow to handle responses asynchronously and can send and receive bodies in a reactive manner.
		 * 
		 * NEW API PRINCIPLES
		 * 
		 * Sending a request and receiving a response with the new HTTP/2 API mainly follow these steps :
		 * 
		 * - use a builder to create an immutable, reusable HttpClient
		 * - use a builder to create an immutable, reusable HttpRequest
		 * - pass the request to the client to receive an HttpResponse
		 * 
		 * The main advantage of immutability of theses classes is that you can configure clients an requests then reuse them without 
		 * worrying about negative interactions between different requests or threads.
		 * 
		 * CONFIGURING AN HTTP CLIENT
		 * 
		 * To create an HttpClient, we just have to use HttpClient.newBuilder(), configure it, then finish with build() :
		 */
		HttpClient client = HttpClient.newBuilder()
			    // Used only for tutorial purposes. HTTP/2 is the default choice.
			    .version(HttpClient.Version.HTTP_2)
			    .connectTimeout(Duration.ofSeconds(5))
			    .followRedirects(Redirect.NORMAL)
			    .build();
		
		/*
		 * Besides what we've done here, we also may configure a proxy, SSL context and parameters, an authenticator, and cookie handler. 
		 * There also exist an executor-method. As mentioned below, the client is immutable and thus thread-safe, so we can configure it 
		 * once then use everywhere.
		 * 
		 * CONFIGURING AN HTTP REQUEST
		 * 
		 * An HttpRequest may be created by following the same pattern as for Httpclient :
		 */
		HttpRequest request = HttpRequest.newBuilder()
			    .GET()
			    .uri(URI.create("http://codefx.org"))
			    .header("Accept-Language", "en-US,en;q=0.5")
			    .build();
		
		/*
		 * We don't have to set the URL in uri(URI) and can instead pass it directly to newBuilder(URI). With header(String, String), we 
		 * add a name/value pair to the request's header. If we want to override existing values for a header name, we can use setHeader. 
		 * If we have many header entries and don't want to repeat header a lot, it exist the headers(String...) method where we alternate 
		 * between names and values :
		 */
		HttpRequest request2 = HttpRequest.newBuilder()
			    .GET()
			    .uri(URI.create("http://codefx.org"))
			    .headers(
			        "Accept-Language", "en-US,en;q=0.5",
			        "Accept-Encoding", "gzip, deflate, br")
			    .build();
		
		/*
		 * Besides headers and more HTTP methods ( PUT, POST, and the generic method), we can request a "100 CONTINUE" before sending the 
		 * request body (if there is one), as well as override the client's preferred HTTP version and timeout.
		 * 
		 * If we use other request than GET, we need to include a BodyPublisher when configuring the HTTP method. The goal of BodyPublisher 
		 * is to handle the request body reactively (we will see this principle in another tutorial class). When we get an instance of it 
		 * via BodyPublishers, depending on the form of incoming body we can call some static methods on it. For example (few more exists) :
		 * 
		 * - ofByteArray(byte[])
		 * - ofFile(Path)
		 * - ofString(String)
		 * - ofInputStream(Supplier<InputStream>)
		 * 
		 * We can then pass the returned BodyPublisher to a request builder's method like PUT, POST :
		 */
		BodyPublisher requestBody = BodyPublishers.ofString("{ request body }");
		
		HttpRequest request3 = HttpRequest.newBuilder()
				.POST(requestBody)
				.uri(URI.create("http://codefx.org"))
				.build();
		
		/*
		 * RECEIVING AN HTTP RESPONSE
		 * 
		 * In order to receive an http response, we just have to call HttpClient.send() method with the request and a  BodyHandler<T>, which 
		 * is in charge of handling the received response's bytes and transform them into the specified type.
		 * 
		 * For example, if we want to receive a response interpreted as a String :
		 */
		HttpResponse<String> response = client.send(
				request,
				BodyHandlers.ofString());
		
		String responseBody = response.body();
		
		/*
		 * Note that beesides the body, the response also contains the status code, headers, SSL session, a reference to the request, as well 
		 * as intermediate responses that handled redirection or authentication.
		 * 
		 * SYNCHRONOUS HTTP REQUEST HANDLING
		 * 
		 * For demonstrating synchronous http request handling whith this new http/2 api, we will search a given term in ten long wikipedia
		 * articles.
		 */
		final HttpClient CLIENT = HttpClient.newBuilder().build();
		
		final List<URI> URLS = Stream.of(
					"https://en.wikipedia.org/wiki/List_of_compositions_by_Franz_Schubert",
					"https://en.wikipedia.org/wiki/2018_in_American_television",
					"https://en.wikipedia.org/wiki/List_of_compositions_by_Johann_Sebastian_Bach",
					"https://en.wikipedia.org/wiki/List_of_Australian_treaties",
					"https://en.wikipedia.org/wiki/2016%E2%80%9317_Coupe_de_France_Preliminary_Rounds",
					"https://en.wikipedia.org/wiki/Timeline_of_the_war_in_Donbass_(April%E2%80%93June_2018)",
					"https://en.wikipedia.org/wiki/List_of_giant_squid_specimens_and_sightings",
					"https://en.wikipedia.org/wiki/List_of_members_of_the_Lok_Sabha_(1952%E2%80%93present)",
					"https://en.wikipedia.org/wiki/1919_New_Year_Honours",
					"https://en.wikipedia.org/wiki/List_of_International_Organization_for_Standardization_standards"
				)
				.map(URI::create)
				.collect(Collectors.toList());
		
		final String SEARCH_TERM = "Foo";
		
		/*
		 * With the HTTP client, URLs, and search term, we can build our requests (one per URL), send them out, wait for the response and 
		 * then check the body for the search term :
		 */
	    URLS.forEach(url -> {
	    	boolean founded = false;
	    	
	    	HttpRequest wiki_request = HttpRequest.newBuilder(url).GET().build();
	    	
	    	try {
				HttpResponse<String> wiki_response = CLIENT.send(wiki_request, BodyHandlers.ofString());
				founded = wiki_response.body().contains(SEARCH_TERM);
			} catch (IOException | InterruptedException e) {
				System.out.println("Exception occured during call to " + url);
				e.printStackTrace();
			}
	    	
	        System.out.println("Completed " + url + " / found: " + founded);
	    });
	    
	    /*
	     * This example isn't perfect. It blocks on each of the ten requests, wasting time and resources. There are three places where the 
	     * code can be changed to become non-blocking :
	     * 
	     * - send request asynchronously
	     * - provide request body as reactive stream
	     * - process response body as reactive stream
	     * 
	     * We will see an asynchronous request in the following example. Other points will be treated in another tutorial class.
	     */
	    
	    System.out.println("=====================================");
	    
	    /* 
	     * ASYNCHRONOUS HTTP REQUEST HANDLING
	     * 
	     * The simplest way to non-blocking calss is to send them asynchronously. HttpClient has a method to do it : sendAsync() sends the 
	     * request and immediately returns a CompletableFuture<HttpResponse<T>>.
	     * 
	     * By default, the request is handled by an executor service holded by the JVM. If we call HttpClient.Builder::executor while building 
	     * the client, we can define a custom Executor for these calls. Whichever executor takes care of the request/response, we can use our thread 
	     * to continue with more important stuff. For instance for the previous example, requesting the next nine Wikipedia pages.
	     */
	    CompletableFuture[] futures = URLS.stream()
	    		// Compute the CompletableFutures to outpute the result.
	    		.map(url -> {
	    			HttpRequest wiki_request = HttpRequest.newBuilder(url).GET().build();
	    			// Return a CompletableFuture.
	    			return CLIENT.sendAsync(wiki_request, BodyHandlers.ofString())
	    					// thenApply() is analogous to Optional.map and Stream.map.
	    					.thenApply(HttpResponse::body)
	    					.thenApply(body -> body.contains(SEARCH_TERM))
	    					// Used to map any errors.
	    					.exceptionally( e -> false)
	    					// thenAccept() is analogous to Optional::ifPresent.
	    					.thenAccept(found -> System.out.println("Completed " + url + " / found: " + found));
	    		})
	    		.toArray(CompletableFuture[]::new);

	    CompletableFuture.allOf(futures).join();
	    
	    /*
	     * As mentioned, HttpClient::sendAsync returns a CompletableFuture<HttpResponse<T>> that eventually completes with the response. We then extract 
	     * the request body (a String), check whether it contains the search term (thus transforming to a Boolean) and finally print that to standard out. 
	     * We use exceptionally to map any errors that may occur while handling the request or response to a "not found" result. Note that thenAccept() 
	     * method returns a CompletableFuture<Void> :
	     * 
	     * - Void because we are expected to have finished processing the content in the specified Consumer.
	     * - it's still a CompletableFuture, so we can wait for it to finish
	     * 
	     * The threads running our requests are daemon threads, which means they don't keep our program alive. If main sends ten asynchronous requests without 
	     * waiting for them to complete, the program ends immediately after the ten sends and we never see any results. So, in order to get our outputs, we
	     * call the CompletableFuture.allOf() method, that await all the CompletableFutures passed in complete.
	     */
	}

}
