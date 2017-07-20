package it.polito.teaching.cv;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created on 7/19/2017.
 */
@DynamoDBTable(tableName = "faceIdMapping")
public class FaceIdMapping {
    String faceId;
    String tags;

    @DynamoDBHashKey(attributeName = "faceId")
    public String getFaceId() {
	return faceId;
    }

    public void setFaceId(String faceId) {
	this.faceId = faceId;
    }

    @DynamoDBAttribute(attributeName = "tags")
    public String getTags() {
	return tags;
    }

    public void setTags(String tags) {
	this.tags = tags;
    }
}
