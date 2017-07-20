package it.polito.teaching.cv;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Created on 7/20/2017.
 */
@DynamoDBTable(tableName = "tagImageMapping")
public class TagImageMapping {
    String tagName;
    byte[] imageBytes;

    @DynamoDBHashKey(attributeName = "tagName")
    public String getTagName() {
	return tagName;
    }

    public void setTagName(String tagName) {
	this.tagName = tagName;
    }

    @DynamoDBAttribute(attributeName = "imageBytes")
    public byte[] getImageBytes() {
	return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
	this.imageBytes = imageBytes;
    }
}
