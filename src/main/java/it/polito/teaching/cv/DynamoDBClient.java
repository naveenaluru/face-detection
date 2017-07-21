package it.polito.teaching.cv;

import it.polito.elite.teaching.cv.utils.PropertyLoader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.util.Pair;

/**
 * Created on 7/20/2017.
 */
public class DynamoDBClient {

    static AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(new BasicAWSCredentials(PropertyLoader.getAmazonKey(), PropertyLoader.getAmazonSecretKey()));

    public static Pair<String, WritableImage> getPreference(String faceId) throws IOException {
        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
        FaceIdMapping faceIdMapping = (FaceIdMapping)mapper.load(FaceIdMapping.class,faceId);
        if(faceIdMapping !=null) {
        	String tagsString = faceIdMapping.getTags();
            String tag = Arrays.asList(tagsString.split(",")).get(0);
            InputStream in = new ByteArrayInputStream(((TagImageMapping)mapper.load(TagImageMapping.class,tag)).getImageBytes());
            BufferedImage bImage = ImageIO.read(in);
            WritableImage fxImage = SwingFXUtils.toFXImage(bImage, null);
            return new Pair<String, WritableImage>(tagsString, fxImage);
        }
        
        return null;
    }
}
