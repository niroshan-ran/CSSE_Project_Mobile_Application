package com.csse.mobileapp.utilities;

import android.os.AsyncTask;

import com.csse.mobileapp.models.Payment;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;


import org.bson.BSON;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.Map;

public class DBConnection {

    private static final String password = "admin";
    private static final String URI = "mongodb://admin:" + password + "@cluster0-shard-00-00.qggrt.mongodb.net:27017,cluster0-shard-00-01.qggrt.mongodb.net:27017,cluster0-shard-00-02.qggrt.mongodb.net:27017/Online_Ticketing_System?ssl=true&replicaSet=atlas-14i991-shard-0&authSource=admin&retryWrites=true&w=majority";


    public MongoCursor<Document> GetUserData(String username) {

        MongoCursor<Document> results = null;


        try {

            MongoClient client = new MongoClient(new MongoClientURI(URI));

            results = client.getDatabase("Online_Ticketing_System").getCollection("users").find((Bson) JSON.parse("{'email' : '" + username + "'}")).iterator();

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }



        return results;

    }

    public int SavePayment(Payment payment) {

        MongoCursor<Document> results = null;

        Document doc = new Document();

        doc.append("name", payment.getCardHolderName());
        doc.append("email", payment.getUserEmail());
        doc.append("cardNo", payment.getCardNumber());
        doc.append("expDate" , payment.getExpiryDate());
        doc.append("CCV", payment.getSecurityCode());
        doc.append("amount", payment.getDepositAmount());

        try {

            MongoClient client = new MongoClient(new MongoClientURI(URI));

            client.getDatabase("Online_Ticketing_System").getCollection("payments").insertOne(doc);

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();

            return -1;
        }

        return 0;


    }

    public double GetBalanceAmount(String userEmail) {

        MongoCursor<Document> paymentResults = null;
        MongoCursor<Document> expenseResults = null;

        double payments = 0;
        double expenses = 0;

        try {

            Document doc = new Document();
            doc.append("email", userEmail);

            MongoClient client = new MongoClient(new MongoClientURI(URI));

            paymentResults = client.getDatabase("Online_Ticketing_System").getCollection("payments").find((Bson) JSON.parse("{'email' : '" + userEmail + "'}")).iterator();
            expenseResults = client.getDatabase("Online_Ticketing_System").getCollection("trips").find((Bson) JSON.parse("{'username' : '" + userEmail + "'}")).iterator();



            while (paymentResults.hasNext()) {
                Document document = paymentResults.next();

                payments += document.getDouble("amount");
            }

            while (expenseResults.hasNext()) {
                Document document = expenseResults.next();

                expenses += document.getDouble("fair");
            }


            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return payments - expenses;

    }


}
