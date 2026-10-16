package com.example.library.client;

import com.example.library.dto.ExternalBook;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenLibraryClient {

    private final RestClient restClient;
    private final String query;
    private final int limit;

    public OpenLibraryClient(@Value("${openlibrary.base-url}") String baseUrl,
                             @Value("${openlibrary.query}") String query,
                             @Value("${openlibrary.limit}") int limit) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);   // 5 seconds to connect
        factory.setReadTimeout(20000);     // 20 seconds to read
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("User-Agent", "LibraryDataSync/1.0 (interview assignment)")
                .build();
        this.query = query;
        this.limit = limit;
    }

    /** Calls the Search API and converts valid records into ExternalBook objects. */
    public List<ExternalBook> fetchBooks() {
        JsonNode root;
        try {
            root = restClient.get()
                    .uri(uri -> uri.path("/search.json")
                            .queryParam("q", query)
                            .queryParam("fields", "key,title,author_name,first_publish_year")
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException e) {
            throw new ExternalApiException("Open Library request failed: " + e.getMessage(), e);
        }

        if (root == null || !root.has("docs") || !root.get("docs").isArray()) {
            throw new ExternalApiException("Open Library returned an unexpected response format.");
        }

        List<ExternalBook> books = new ArrayList<>();
        for (JsonNode doc : root.get("docs")) {
            String key = doc.path("key").asText("").replace("/works/", "").trim();
            String title = doc.path("title").asText("").trim();
            if (key.isEmpty() || title.isEmpty()) {
                continue; // validation: skip records without an id or title
            }
            List<String> names = new ArrayList<>();
            for (JsonNode a : doc.path("author_name")) {
                names.add(a.asText());
            }
            String authors = String.join(", ", names);
            Integer year = doc.hasNonNull("first_publish_year") ? doc.get("first_publish_year").asInt() : null;

            books.add(new ExternalBook(
                    limitLength(key, 50), limitLength(title, 500), limitLength(authors, 1000), year));
        }
        return books;
    }

    private static String limitLength(String text, int max) {
        return text.length() > max ? text.substring(0, max) : text;
    }
}