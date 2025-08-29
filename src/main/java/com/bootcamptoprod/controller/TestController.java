package com.bootcamptoprod.controller;

import com.bootcamptoprod.service.MongoServiceClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mongo")
public class TestController {
	
	private final MongoServiceClient mongoServiceClient;

    @Autowired
    public TestController(MongoServiceClient mongoServiceClient) {
        this.mongoServiceClient = mongoServiceClient;
    }

    @GetMapping("/databases")
    public List<String> listDatabases() {
        return mongoServiceClient.listDatabases();
    }

    @GetMapping("/collections/{dbName}")
    public List<String> listCollections(@PathVariable String dbName) {
        return mongoServiceClient.listCollections(dbName);
    }

    @GetMapping("/query/simple")
    public List<Document> simpleQuery(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestParam String field,
            @RequestParam String value) {
        return mongoServiceClient.simpleQuery(dbName, collectionName, field, value);
    }

    @PostMapping("/query/complex")
    public List<Document> complexQuery(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String jsonQuery) {
        return mongoServiceClient.complexQuery(dbName, collectionName, jsonQuery);
    }

    @GetMapping("/indexes")
    public List<Document> listIndexes(
            @RequestParam String dbName,
            @RequestParam String collectionName) {
        return mongoServiceClient.listIndexes(dbName, collectionName);
    }

    @PostMapping("/collections")
    public String createCollection(
            @RequestParam String dbName,
            @RequestParam String collectionName) {
        return mongoServiceClient.createCollection(dbName, collectionName);
    }

    @PostMapping("/documents")
    public String insertDocument(
            @RequestParam String dbName,
            @RequestParam String collectionName,
            @RequestBody String jsonDocument) {
        return mongoServiceClient.insertDocument(dbName, collectionName, jsonDocument);
    }

}
