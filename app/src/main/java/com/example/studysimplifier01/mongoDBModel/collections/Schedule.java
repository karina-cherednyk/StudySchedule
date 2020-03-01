package com.example.studysimplifier01.mongoDBModel.collections;

import com.example.studysimplifier01.roomDBModel.entities.Lesson;

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

public class Schedule {
    public static final String DATABASE = "ss";
    public static final String SCHEDULE_COLLECTION = "schedules";

    private final ObjectId _id;
    private final String owner_id;
    private final String name;
    private List<Lesson> lessons;
    private List<String> sharedIds;

    public Schedule(
            final String owner_id,
            final String name,
            final List<Lesson> lessons
    ){
        _id = new ObjectId();
        this.owner_id = owner_id;
        this.name = name;
        this.lessons = lessons;
        sharedIds = new LinkedList<>();
    }
    public Schedule(
            final String owner_id,
            final String name,
            final List<Lesson> lessons,
            List<String> sharedIds
    ){
        _id = new ObjectId();
        this.owner_id = owner_id;
        this.name = name;
        this.lessons = lessons;
        this.sharedIds = sharedIds;
    }

    public ObjectId get_id(){
        return _id;
    }
    public String getOwner_id() {
        return owner_id;
    }

    public String getName() {
        return name;
    }

    public List<Lesson> getLessons(){
        return lessons;
    }
    public static final class Fields {
        public static final String ID = "_id";
        public static final String OWNER_ID = "owner_id";
        public static final String NAME = "name";
        public  static final String LESSONS = "lessons";
        public static final String SHARE_WITH = "share_with";
    }
    static BsonDocument toBsonDocument(final Schedule schedule){
        final BsonDocument asDoc = new BsonDocument();
        asDoc.put(Fields.ID, new BsonObjectId(schedule._id));
        asDoc.put(Fields.OWNER_ID, new BsonString(schedule.owner_id));
        asDoc.put(Fields.NAME, new BsonString(schedule.name));
        BsonArray lessARR = new BsonArray();
        for(Lesson lesson : schedule.lessons)
            lessARR.add(Lesson.convert(lesson));
        asDoc.put(Fields.LESSONS,lessARR);
        BsonArray shrARR = new BsonArray();
        for(String id: schedule.sharedIds)
            shrARR.add(new BsonString(id));
        asDoc.put(Fields.SHARE_WITH,shrARR);

        return asDoc;
    }
    private static Schedule fromBsonDocument(BsonDocument document) {
       String owner_id = document.getString(Fields.OWNER_ID).getValue();
       String name = document.getString(Fields.NAME).getValue();


       BsonArray lessARR = document.getArray(Fields.LESSONS,new BsonArray());
       BsonArray shrARR = document.getArray(Fields.SHARE_WITH,new BsonArray());

       List<Lesson> lessons = new LinkedList<>();
       for (BsonValue lessDoc : lessARR)
           lessons.add(Lesson.convert(lessDoc.asDocument()));

        List<String> sharedIds = new LinkedList<>();
        for (BsonValue id : shrARR)
            sharedIds.add(id.asString().getValue());

       return new Schedule(owner_id,name,lessons,sharedIds);
    }

    public static final Codec<Schedule> codec = new Codec<Schedule>() {

        @Override
        public void encode(
                final BsonWriter writer, final Schedule value, final EncoderContext encoderContext) {
            new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
        }

        @Override
        public Class<Schedule> getEncoderClass() {
            return Schedule.class;
        }

        @Override
        public Schedule decode(final BsonReader reader, final DecoderContext decoderContext) {
            final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
            return fromBsonDocument(document);
        }
    };



}
