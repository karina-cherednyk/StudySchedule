package com.example.studysimplifier01.mongoDBModel.collections;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.LinkedList;
import java.util.List;

public class User {

    public static final String DATABASE = "ss";
    public static final String USERS_COLLECTION = "usernames";

    private final ObjectId _id;
    private final String owner_id;
    private  String username;
    private  String email;
    private List<String> friendsIds;


    public User(
            final ObjectId id,
            final String owner_id,
            final String username,
            final String email
    ) {
        this._id = id;
        this.owner_id = owner_id;
        this.username = username;
        this.email = email;
        this.friendsIds = new LinkedList<>();
    }
    public User(
            final ObjectId id,
            final String owner_id,
            final String username,
            final String email,
            List<String> friendsIds
    ) {
        this._id = id;
        this.owner_id = owner_id;
        this.username = username;
        this.email = email;
        this.friendsIds = friendsIds;
    }


    public ObjectId get_id() {
        return _id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
    public List<String> getFriendsIds(){return friendsIds;}


    static BsonDocument toBsonDocument(final User user) {
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(user._id));
        asDoc.put(Fields.OWNER_ID, new BsonString(user.owner_id));
        asDoc.put(Fields.USERNAME, new BsonString(user.username));
        asDoc.put(Fields.EMAIL, new BsonString(user.email));

        BsonArray friends = new BsonArray();
        for(String id : user.friendsIds)
            friends.add(new BsonString(id));

        asDoc.put(Fields.FRIENDS,friends);
        return asDoc;
    }



    static User fromBsonDocument(final BsonDocument doc) {
        BsonArray frARR = doc.getArray(Fields.FRIENDS,new BsonArray());

        List<String> ids = new LinkedList<>();
        for (BsonValue id : frARR)
            ids.add(id.asString().getValue());

        return new User(
                doc.getObjectId(Fields.ID).getValue(),
                doc.getString(Fields.OWNER_ID).getValue(),
                doc.getString(Fields.USERNAME).getValue(),
                doc.getString(Fields.EMAIL).getValue(),
                ids
        );
    }

    public static final class Fields {
        public static final String ID = "_id";
        public static final String OWNER_ID = "owner_id";
        public static final String USERNAME = "username";
        public  static final String EMAIL = "email";
        public static final String FRIENDS = "friends_ids";
    }

    public static final Codec<User> codec = new Codec<User>() {

        @Override
        public void encode(
                final BsonWriter writer, final User value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<User> getEncoderClass() {
            return User.class;
        }

        @Override
        public User decode(
                final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
