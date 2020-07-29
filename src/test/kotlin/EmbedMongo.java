//import com.mongodb.BasicDBObject;
//import com.mongodb.client.MongoClient;
//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodProcess;
//import de.flapdoodle.embed.mongo.MongodStarter;
//import de.flapdoodle.embed.mongo.config.IMongodConfig;
//import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.distribution.Version;
//import de.flapdoodle.embed.process.runtime.Network;
//
//import java.io.IOException;
//
//public class EmbedMongo {
//
//    /**
//     *
//     */
//    public static void main(String[] args) throws IOException {
//        MongodStarter starter = MongodStarter.getDefaultInstance();
//        String bindIp = "localhost";
//        int port = 12345;
//        IMongodConfig mongodConfig = new MongodConfigBuilder()
//                .version(Version.Main.PRODUCTION)
//                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
//                .build();
//
//        MongodExecutable mongodExecutable = null;
//        try {
//            mongodExecutable = starter.prepare(mongodConfig);
//            MongodProcess mongod = mongodExecutable.start();
//
//            MongoClient mongo = new MongoClient(bindIp, port);
//            DB db = mongo.getDB("test");
//            DBCollection col = db.createCollection("testCol", new BasicDBObject());
//            col.save(new BasicDBObject("testDoc", new Date()));
//
//        } finally {
//            if (mongodExecutable != null)
//                mongodExecutable.stop();
//        }
//    }
//}
