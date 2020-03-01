package com.example.studysimplifier01.mongoDBModel.collections;


import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MongoLesson {

    public static final String DATABASE = "ss";
    public static final String TASKS_COLLECTION = "tasks";


    private String name;
    private int date_of_task;



    private ObjectId _id;
    private String owner_id;
    private List<String> share_ids;
    private String task;

    private MongoLesson(String name, String owner_id, String task, int date_of_task, List<String> share_ids){
        this.name = name;
        this.owner_id = owner_id;
        this.date_of_task = date_of_task;
        this.share_ids = share_ids;
        this.task = task;
        _id = new ObjectId();
    }
    private MongoLesson(String name, String owner_id, String task, int date_of_task , List<String> share_ids, ObjectId id){
        this.name = name;
        this.owner_id = owner_id;
        this.date_of_task = date_of_task;
        this.share_ids = share_ids;
        this.task = task;
        _id = id;
    }
    public MongoLesson(String name, String owner_id, String task, int date_of_task){
        this.name = name;
        this.owner_id = owner_id;
        this.date_of_task = date_of_task;
        this.task = task;
        _id = new ObjectId();
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public void setShare_ids(List<String> share_ids) {
        this.share_ids = share_ids;
    }

    public int getDate_of_task() {
        return date_of_task;
    }

    public Date getDate() {
        return _id.getDate();
    }

    public String getOwner_id() {
        return owner_id;
    }

    public List<String> getShare_ids() {
        return share_ids;
    }
    public String getName(){
        return name;
    }

    public String getTask() {
        return task;
    }

    public static final class Fields {
        public static final String ID = "_id";
        public static final String OWNER_ID = "owner_id";
        public static final String NAME = "name";
        public  static final String DATE_OF_TASK = "date_of_task";
        public static final String SHARE_WITH = "share_with";
        public static final String TASK = "task";
    }
    static BsonDocument toBsonDocument(final MongoLesson lesson){
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(lesson._id));
        asDoc.put(Fields.OWNER_ID, new BsonString(lesson.owner_id));
        asDoc.put(Fields.NAME, new BsonString(lesson.name));
        asDoc.put(Fields.DATE_OF_TASK, new BsonInt32(lesson.date_of_task));
        asDoc.put(Fields.TASK, new BsonString(lesson.task));
        BsonArray shrARR = new BsonArray();
        for(String id: lesson.share_ids)
            shrARR.add(new BsonString(id));
        asDoc.put(Schedule.Fields.SHARE_WITH,shrARR);
        return asDoc;
    }
    private static MongoLesson fromBsonDocument(BsonDocument document) {
        String owner_id = document.getString(Fields.OWNER_ID).getValue();
        String name = document.getString(Fields.NAME).getValue();
        String task = document.getString(Fields.TASK).getValue();
        int date = document.getInt32(Fields.DATE_OF_TASK).getValue();
        ObjectId id = document.getObjectId(Fields.ID).getValue();
        BsonArray array = document.getArray(Fields.SHARE_WITH, new BsonArray());
        List<String> share_ids  = new LinkedList<>();
        for(BsonValue v: array) share_ids.add(v.asString().getValue());
        return new MongoLesson(name,owner_id,task, date,share_ids,id);
    }

    public static final Codec<MongoLesson> codec = new Codec<MongoLesson>() {

        @Override
        public void encode(
                final BsonWriter writer, final MongoLesson lesson, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(lesson), encoderContext);
        }

        @Override
        public Class<MongoLesson> getEncoderClass() {
            return MongoLesson.class;
        }

        @Override
        public MongoLesson decode(final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };
}
