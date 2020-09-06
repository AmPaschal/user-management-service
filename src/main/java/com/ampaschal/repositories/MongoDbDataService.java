package com.ampaschal.repositories;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Amusuo Paschal
 * @since 20 July 2020, 15:16:47
 */

public class MongoDbDataService {


	private static final String MONGO_ID = "_id";

	@Inject MongoClient mongoClient;



	public MongoCollection<Document> getCollection(String collection) {
		return getDatabaseConnection().getCollection(collection);
	}


	private MongoDatabase getDatabaseConnection() {

		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		return mongoClient.getDatabase(getDatabaseName()).withCodecRegistry(pojoCodecRegistry);
	}

	protected String getDatabaseName() {
		return "base_db";
	}


	protected  <T> MongoCollection<T> getCollection(String collection, Class<T> type) {
		return getDatabaseConnection().getCollection(collection, type);
	}


	public <T> InsertOneResult save(T doc, String collectionName, Class<T> type) {
		MongoCollection<T> collection = getCollection(collectionName, type);
		return collection.insertOne(doc);
	}


	public InsertManyResult saveMultiple(List<Document> docs, MongoCollection<Document> collection) {
		return collection.insertMany(docs);
	}


	public <T> UpdateResult replaceDocumentById(String collectionName, Class<T> type, T document, ObjectId id) {
		MongoCollection<T> collection = getCollection(collectionName, type);
		Bson filter = Filters.eq(MONGO_ID, id);
		return collection.replaceOne(filter, document);

	}


	public <T> UpdateResult updateDocumentById(String collectionName, Class<T> type, Bson document, ObjectId id) {
		MongoCollection<T> collection = getCollection(collectionName, type);
		Bson filter = Filters.eq(MONGO_ID, id);
		return collection.updateOne(filter, document);

	}


	public <T> FindIterable<T> find(Bson filter, MongoCollection<T> collection) {
		return collection.find(filter);
	}


	protected <T> FindIterable<T> find(Bson filter, String collectionName, Class<T> type) {
		MongoCollection<T> collection = getCollection(collectionName, type);
		return collection.find(filter);
	}


	protected Document replace(Document doc, Document replacement, MongoCollection<Document> collection) {
		Bson filter = new Document(MONGO_ID, doc.getObjectId(MONGO_ID));
		return collection.findOneAndReplace(filter, replacement);
	}


	public void delete(Document doc, MongoCollection<Document> collection) {
		Bson filter = new Document(MONGO_ID, doc.getObjectId(MONGO_ID));
		collection.findOneAndDelete(filter);
	}

}