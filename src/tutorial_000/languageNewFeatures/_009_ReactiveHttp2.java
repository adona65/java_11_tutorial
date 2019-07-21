package tutorial_000.languageNewFeatures;

import java.io.File;
import java.io.IOException;
import java.lang.StackWalker.StackFrame;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class _009_ReactiveHttp2 {

	public static void main(String[] args) throws IOException, InterruptedException {
		/*
		 * With Http2 API introduced in java 11, we can also handle request and response bodies in a reactive manner. With it, we can can throttle, 
		 * stream, and expose a result as soon as we found it (instead of waiting for the entire body to arrive).
		 * 
		 * STREAMING THE REQUEST BODY
		 * 
		 * If a request has a large body, we may not want to load it into memory in its entirety.to avoid it, we may use reactive HTTP API. When creating 
		 * a POST request for example, we need to provide the body, but we don’t have to do that in the form of a String or byte[]. Formally, we have to 
		 * hand over a BodyPublisher, which is essentially a Publisher<ByteBuffer>. The HTTP request will then subscribe to that publisher and request bytes 
		 * to send over the wire.
		 * 
		 * We can observe that behavior by creating decorators for the interfaces BodyPublisher, Subscriber, and Subscription that log to standard out and 
		 * then inject them into the HTTP request builder :
		 */
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest post = HttpRequest
				.newBuilder(URI.create("https://postman-echo.com/post"))
				// In this POST method happened an interesting thing. We retrieve the Path of ou larg_json file, the the BodyPublishers::ofFile creates a 
				// BodyPublisher that walks the file as needed. We wrap it into the logging decorator and pass the request to the client. Once the streaming 
				// starts, we can see corresponding outputs. The interesting part is that the BodyPublisher returned by BodyPublishers::ofFile is lazy (it never 
				// reads more than it has to fulfill the next request), and that the HTTP client will only request new bytes once the last ones were send over the 
				// wire. That means no matter how large the file, we never need to store more than 16kb of it in memory.
				// It’s easy to integrate with that logic and, as a more elaborate example, create a publisher that connects to a database and uses pagination to, 
				// at all times, only hold a little window of the entire result in memory while transforming it to a sensible representation and streaming it as part 
				// of a request body.
				.POST(new LoggingBodyPublisher(BodyPublishers.ofFile(
															new File(_009_ReactiveHttp2.class.getResource("large.json").getPath()).toPath()))
																	)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse<String> response = client
				.send(post, BodyHandlers.ofString());

		System.out.println("Status: " + response.statusCode());
		System.out.println(response.body());
		
		System.out.println("=====================================");
		
		/*
		 * In the following, we will perform again the wikipedia search example of the Http2Api tutorial, but in a reactive way. Note that compared to the previous
		 * example, where a BodyPublisher is in charge of publishing bytes that are sent over the wire, a BodySubscriber<T> subscribes to the bytes received as part 
		 * of the response and collects them into an instance of type T. The bytes come in the form of lists of byte buffers, meaning BodySubscriber extends 
		 * Subscriber<List<ByteBuffer>>. Implementing that means extracting bytes from buffers, being aware of charsets, deciding where to split the resulting string, 
		 * etc etc. It's for that reason we will not do it in the next example.
		 * 
		 * Instead we can implement a Subscriber<String> and pass it to BodyHandlers::fromLineSubscriber (look inside inner class "ReactivePageSearch" in the search()
		 * method).
		 */
		List<String> URLS = List.of(
				"https://en.wikipedia.org/wiki/List_of_compositions_by_Franz_Schubert",
				"https://en.wikipedia.org/wiki/2018_in_American_television",
				"https://en.wikipedia.org/wiki/List_of_compositions_by_Johann_Sebastian_Bach",
				"https://en.wikipedia.org/wiki/List_of_Australian_treaties",
				"https://en.wikipedia.org/wiki/2016%E2%80%9317_Coupe_de_France_Preliminary_Rounds",
				"https://en.wikipedia.org/wiki/Timeline_of_the_war_in_Donbass_(April%E2%80%93June_2018)",
				"https://en.wikipedia.org/wiki/List_of_giant_squid_specimens_and_sightings",
				"https://en.wikipedia.org/wiki/List_of_members_of_the_Lok_Sabha_(1952%E2%80%93present)",
				"https://en.wikipedia.org/wiki/1919_New_Year_Honours",
				"https://en.wikipedia.org/wiki/List_of_International_Organization_for_Standardization_standards");
		
		HttpClient httpClient = HttpClient.newBuilder().build();
		
		String SEARCH_STRING = "Foo";
		
		List<Search> searches = URLS.stream()
				.map(url -> URI.create(url))
				.map(url -> new Search(url, SEARCH_STRING))
				.collect(Collectors.toList());
		
		ReactivePageSearch reactiveSearch = new ReactivePageSearch(httpClient);
		
		// Following perform search action.
		long startTime = System.currentTimeMillis();
		
		long successCount = reactiveSearch.search(searches)
											.stream()
											.filter(Result::contains)
											.count();
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Search terms found " + successCount + "/" + searches.size());
		System.out.println("Took " + elapsedTime + " ms.");
	}

	private static class LoggingBodyPublisher implements BodyPublisher {

		private final BodyPublisher publisher;

		private LoggingBodyPublisher(BodyPublisher publisher) {
			this.publisher = publisher;
		}

		@Override
		public long contentLength() {
			var contentLength = publisher.contentLength();
			log("Content length queried: " + contentLength);
			return contentLength;
		}

		@Override
		public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
			log("Subscriber registered: " + subscriber);
			publisher.subscribe(new LoggingSubscriber(subscriber));
		}

	}
	
	private static class LoggingSubscriber implements Subscriber<ByteBuffer> {

		private final Subscriber<? super ByteBuffer> subscriber;
		private long totalBytesPassed = 0;

		private LoggingSubscriber(Subscriber<? super ByteBuffer> subscriber) {
			this.subscriber = subscriber;
		}

		@Override
		public void onSubscribe(Subscription subscription) {
			log("Subscription registered: " + subscription);
			subscriber.onSubscribe(new LoggingSubscription(subscription));
		}

		@Override
		public void onNext(ByteBuffer item) {
			int passed = item.array().length;
			totalBytesPassed += passed;
			log("Bytes passed: " + passed + " ↺ " + totalBytesPassed);
			subscriber.onNext(item);
		}

		@Override
		public void onError(Throwable throwable) {
			log("Error occured: " + throwable);
			subscriber.onError(throwable);
		}

		@Override
		public void onComplete() {
			log("Publishing completed");
			subscriber.onComplete();
		}

	}
	
	private static class LoggingSubscription implements Subscription {

		private final Subscription subscription;
		private long totalRequestedItems = 0;

		private LoggingSubscription(Subscription subscription) {
			this.subscription = subscription;
		}

		@Override
		public void request(long n) {
			totalRequestedItems += n;
			log("Items requested: " + n + " ↺ " + totalRequestedItems);
			subscription.request(n);
		}

		@Override
		public void cancel() {
			log("Subscription canceled.");
			subscription.cancel();
		}

	}
	
	private static void log(String message) {
		StackFrame caller = StackWalker.getInstance().walk(frames -> frames.skip(1).findFirst().orElseThrow());
		System.out.printf("    [DEBUG | %-12s] %s%n", caller.getMethodName(), message);
	}
	
	private static class Search {

		private final URI url;
		private final String term;

		public Search(URI url, String term) {
			this.url = url;
			this.term = term;
		}

		public URI url() {
			return url;
		}

		public String urlEnd() {
			String url = this.url.toString();
			int lastSlashIndex = url.lastIndexOf('/');
			return url.substring(lastSlashIndex + 1);
		}

		public String term() {
			return term;
		}

		@Override
		public String toString() {
			return String.format("'%s' in '%s'?", term, urlEnd());
		}

	}
	
	private static class Result {

		private final Search search;
		private final boolean contains;

		private Result(Search search, boolean contains) {
			this.search = search;
			this.contains = contains;
		}

		public static Result completed(Search search, boolean contains) {
			Result result = new Result(search, contains);
			System.out.println("   [DEBUG] Completed " + result);
			return result;
		}

		public static Result failed(Search search, Throwable exception) {
			Result result = new Result(search, false);
			System.err.println("   [ERROR] Error while searching " + search.url() + ": " + exception.getMessage());
			return result;
		}

		@SuppressWarnings("unused")
		public Search search() {
			return search;
		}

		@SuppressWarnings("unused")
		public boolean contains() {
			return contains;
		}

		@Override
		public String toString() {
			return String.format("'%s' in '%s': %s", search.term(), search.urlEnd(), contains);
		}

	}
	
	private static interface PageSearch {

		List<Result> search(List<Search> searches);

	}
	
	public static class ReactivePageSearch implements PageSearch {

		private final HttpClient client;

		public ReactivePageSearch(HttpClient client) {
			this.client = client;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List<Result> search(List<Search> searches) {
			CompletableFuture[] futures = searches.stream()
					.map(search -> search(search))
					.toArray(CompletableFuture[]::new);

			CompletableFuture.allOf(futures).join();

			return Stream.of(futures)
					.map(this::getUnsafely)
					.collect(Collectors.toList());
		}

		// Perform a search in a reactive way.
		private CompletableFuture<Result> search(Search search) {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(search.url())
					.build();
			
			/*
			 * We implement a Subscriber<String> and pass it to BodyHandlers::fromLineSubscriber.
			 * 
			 * A BodyHandler<T> is in charge of evaluating the response’s status code, HTTP version, and header lines and to create a BodySubscriber<T> 
			 * for the response’s bytes. The generic type T indicates what these bytes will eventually be transformed to and determines the T in 
			 * HttpResponse<T> and thus the return type of HttpResponse::body. 
			 * 
			 * In the first example in this _009_ReactiveHttp2 class, we called BodyHandlers::ofString to get a BodyHandler<String>, which represents the
			 * entire response body as a single string, and passed it to the client’s send methods.
			 */
			StringFinder finder = new StringFinder(search);
			
			/*
			 * Here we are going to call BodyHandlers.fromLineSubscriber(Subscriber<String>) which gives us more to do but also more freedom : It wraps our 
			 * subscriber into a BodySubscriber<Void> (Void will be explained later) that aggregates the lists of byte buffers to strings, takes them apart 
			 * on newlines, and then expects our subscriber to handle these individual response lines. In return, we don’t need to wait for the entire body 
			 * to arrive before we can process it.
			 * 
			 * We pass the StringFinder to fromLineSubscriber, which wraps it into a BodyHandler, and then return the CompletableFuture our finder exposes. 
			 * The provided subscriber processes the body however it pleases and without having to make it available afterwards. It hence returns a 
			 * BodyHandler<Void>, leading to an HttpResponse<Void>, meaning sendAsync returns a CompletableFuture that completes when the response is fully 
			 * processed but never exposes the body.
			 */
			client.sendAsync(request, BodyHandlers.fromLineSubscriber(finder))
				/*
				 * While StringFinder properly handles errors by exposing them via its CompletaleFuture, these aren’t all errors that can occur. On the contrary, 
				 * these are just the small subset of errors that may happen while the body is streamed from server to client (e.g. loss of connection, not being 
				 * able to establish the connection etc).
				 * 
				 * In which case StringFinder is never subscribed to anything, its CompletableFuture never completes, and waiting for it blocks forever. Where did 
				 * we go wrong? Where do those kinds of errors surface? Here’s where the CompletableFuture that sendAsync returns comes back in. It’s the thing that 
				 * exposes such errors ! And so we need to hook into its exception handling and make our finder’s future complete with the same exception.
				 * 
				 * This way, the CompletableFuture<Boolean> returned by StringFinder surfaces all possible outcomes that can occur while fielding the HTTP request :
				 * - it will complete with true as soon as the term is found
				 * - it will complete with false if the entire body was scanned
				 * - it will complete with an exception if there is any problem (including those that occur before the body is streamed)
				 */
				.exceptionally(ex -> {
					finder.onError(ex);
					// We return null because we wait a CompletableFuture<Void>
					return null;
				});
			return finder
					.found()
					.exceptionally(ex -> Result.failed(search, ex));
		}

		private Result getUnsafely(CompletableFuture<Result> result) {
			// this is unsafe because the future's exceptions are not properly handled;
			// it makes sense in this case because:
			//  (a) exceptional completion is covered by the the call to
			//      `exceptionally(...)` in `search(Search)`
			//  (b) the line `CompletableFuture.allOf(futures).join()` in
			//      `search(List<Search>)` means `get()` below can never block
			try {
				return result.get();
			} catch (ExecutionException | InterruptedException ex) {
				throw new IllegalStateException("Future should have completed and handled errors.", ex);
			}
		}

		/*
		 *  StringFinder implements the reactive subscriber contract by storing the subscription, requesting items (in this case lines, one by one), 
		 *  and processing them.
		 */
		private static class StringFinder implements Subscriber<String> {

			private final Search search;
			private final CompletableFuture<Result> found;
			private Subscription subscription;

			private StringFinder(Search search) {
				this.search = search;
				this.found = new CompletableFuture<>();
			}

			@Override
			public void onSubscribe(Subscription subscription) {
				this.subscription = subscription;
				requestLine();
			}

			private void requestLine() {
				subscription.request(1);
			}

			@Override
			public void onNext(String line) {
				// no cancelation
//				if (!found.isDone() && line.contains(search.term()))
//					found.complete(Result.completed(search, true));
				// with cancelation
				
				/*
				 * If we don't perform any specific treatment, We will always stream the entire body, even after we found the search term. To abort the stream
				 *  once we’re done, all we need to do is add one line to StringFinder::onNext : "subscription.cancel();".
				 *  
				 *  By canceling the subscription, we won’t receive any more lines, and this is really a performance upgrade. But keep in mind that this speedup 
				 *  highly depends on how soon the search term is found ! If you search for "Foobar" instead of "Foo", none of the ten sites contains it, and 
				 *  performance is back to the runtime without cancellation (because we search into the entire articles).
				 *  
				 *  Regarding cancellation, we need to note something. Canceling the subscription leads to the client calling onError with an exception like :
				 *  "java.util.concurrent.CompletionException: java.io.IOException: Stream 47 cancelled"
				 *  
				 *  Since onError calls found.completeExceptionally, the future must already have been completed by then (or the result is always an error instead 
				 *  of true). That’s why found.complete(true) must come before subscription.cancel() !
				 */
				if (line.contains(search.term())) {
					found.complete(Result.completed(search, true));
					subscription.cancel();
				}

				requestLine();
			}

			@Override
			public void onError(Throwable ex) {
				found.completeExceptionally(ex);
			}

			@Override
			public void onComplete() {
				found.complete(Result.completed(search, false));
			}

			public CompletableFuture<Result> found() {
				return found;
			}

		}

	}
}
