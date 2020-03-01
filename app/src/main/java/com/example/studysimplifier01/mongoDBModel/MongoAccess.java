package com.example.studysimplifier01.mongoDBModel;

import com.example.studysimplifier01.mongoDBModel.collections.MongoLesson;
import com.example.studysimplifier01.mongoDBModel.collections.Schedule;
import com.example.studysimplifier01.mongoDBModel.collections.User;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchAuth;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.core.auth.providers.userpassword.UserPasswordAuthProviderClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;

import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.or;

public class MongoAccess {

    public static StitchAppClient client = Stitch.getDefaultAppClient();
    public static StitchAuth auth = client.getAuth();


    public static final RemoteMongoClient mongoClient = client.getServiceClient(
            RemoteMongoClient.factory,"mongodb-atlas");

    public static RemoteMongoCollection<User> users = mongoClient
            .getDatabase(User.DATABASE)
            .getCollection(User.USERS_COLLECTION, User.class)
            .withCodecRegistry(CodecRegistries.fromRegistries(
                BsonUtils.DEFAULT_CODEC_REGISTRY, CodecRegistries.fromCodecs(User.codec)
                ));

    public static RemoteMongoCollection<Schedule> schedules = mongoClient
            .getDatabase(Schedule.DATABASE)
            .getCollection(Schedule.SCHEDULE_COLLECTION,Schedule.class)
            .withCodecRegistry(CodecRegistries.fromRegistries(
                BsonUtils.DEFAULT_CODEC_REGISTRY, CodecRegistries.fromCodecs(Schedule.codec)
                 ));

    public static  RemoteMongoCollection<MongoLesson> tasks = mongoClient
            .getDatabase(MongoLesson.DATABASE)
            .getCollection(MongoLesson.TASKS_COLLECTION,MongoLesson.class)
            .withCodecRegistry(CodecRegistries.fromRegistries(
                    BsonUtils.DEFAULT_CODEC_REGISTRY, CodecRegistries.fromCodecs(MongoLesson.codec)
            ));


    public static Task<User> getUser(Document filter){
        return users.find(filter).first();
    }
    public static Task<User> findUpdateUser(Document filter, Document update){
        return users.findOneAndUpdate(filter,update,User.class);

    }
    public static Task<User> getMyself(){
        return users.findOne(new Document(User.Fields.OWNER_ID, getId()));
    }

    public static Task<User> getFriend(String username){
        return getMyself().onSuccessTask(me->{
            List<String> ids = me.getFriendsIds();
            return users.findOne(and(eq(User.Fields.USERNAME, username), in(User.Fields.OWNER_ID,ids)));
        });
    }

    public static void logout(){
        if(auth.isLoggedIn()) auth.logout();
    }

    public static String getId(){
        return auth.getUser().getId();
    }

    public static UserPasswordAuthProviderClient getEmailPasswordClient(){
        return auth.getProviderClient(UserPasswordAuthProviderClient.factory);
    }

    public static Task<StitchUser> accessAnon(){
        logout();
        return auth.loginWithCredential(new AnonymousCredential());
    }

    public static Task<StitchUser> logIn(String email, String password){
        logout();
        return auth.loginWithCredential(new UserPasswordCredential(email, password));
    }

    public static StitchUser getStitchUser(){
        return auth.getUser();
    }

    public static Task<RemoteInsertOneResult> insertOneUser(User user){
        return users.insertOne(user);
    }
    public static Task<RemoteInsertOneResult> insertTask(MongoLesson task){
        return getMyself().onSuccessTask( me -> {
            task.setShare_ids(me.getFriendsIds());
            return tasks.insertOne(task);
        });
    }

    public static Task<LinkedList<MongoLesson>> fetchFriendsTasks(String lessonName){
        return getMyself().onSuccessTask(task->{
            List<String> ids = task.getFriendsIds();
            return  tasks.find(and( eq(MongoLesson.Fields.NAME, lessonName),or( eq(MongoLesson.Fields.OWNER_ID, task.getOwner_id()) ,in(MongoLesson.Fields.SHARE_WITH,ids)))).into(new LinkedList<>());
            });
    }

    public static Task insertSchedule(Schedule schedule){
        return schedules.insertOne(schedule);
    }

    public static Task<RemoteUpdateResult> updateOneUser(String id, Document updates){
        return users.updateOne(new Document(User.Fields.OWNER_ID,id),updates);
    }

    public static Task<User> getUsername(String id){
        return users.find(new Document(User.Fields.OWNER_ID,id)).first();
    }

    public static Task<LinkedList<User>> getFriends(){
        return getMyself().onSuccessTask(me ->
                users.find(in(User.Fields.OWNER_ID, me.getFriendsIds())).into(new LinkedList<User>())
                );
    }
    public static Task<LinkedList<User>> getFriends(Bson filter){
        return getMyself().onSuccessTask(me ->
                users.find(filter).into(new LinkedList<User>())
        );
    }
    public static Task<LinkedList<Schedule>> getFriendsSchedules(){

        Document projection = new Document();
        projection.put(Schedule.Fields.NAME,1);
        projection.put(Schedule.Fields.OWNER_ID,1);
        projection.put(Schedule.Fields.ID,0);

        return schedules.find().into(new LinkedList<>());
    }


}
