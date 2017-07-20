package it.polito.teaching.cv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.util.IOUtils;

public class SearchFacesService {

   public static final String COLLECTION_ID = "nnaircollection";
   private static final String CREDENTIALS_KEY = "";
   private static final String CREDENTIALS_SECRET = "";
   private static final String PATH_TO_PROCESS = System.getProperty("user.dir") + "/faces";
   private static final Float THRESHOLD = 70F;
   private static final int MAX_FACES = 2;

   public List<FaceMatch> search() throws Exception {

      AWSCredentials credentials = new BasicAWSCredentials(CREDENTIALS_KEY, CREDENTIALS_SECRET);
      AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1)
          .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

      File dir = new File(PATH_TO_PROCESS);
      if (dir.isDirectory()) {
         return readPicturesAndFindMatch(amazonRekognition, dir);
      } else {
         throw new RuntimeException("Provided path is not a directory");
      }
   }

   private List<FaceMatch> readPicturesAndFindMatch(AmazonRekognition amazonRekognition, File dir)
       throws IOException, FileNotFoundException {
      List<FaceMatch> faceMatches = new ArrayList<>();
      for (final File photo : dir.listFiles()) {

         ByteBuffer imageBytes;
         try (InputStream inputStream = new FileInputStream(photo)) {
            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
         }

         // Read image from folder and create image object
         Image image = new Image().withBytes(imageBytes);

         // Search collection for faces similar to the largest face in
         // the image.
         SearchFacesByImageResult searchFacesByImageResult = callSearchFacesByImage(COLLECTION_ID, image,
             amazonRekognition);

         System.out.println("Faces matching largest face in image  " + photo.getName());
         List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
         for (FaceMatch face : faceImageMatches) {
            System.out.println(face.getFace().toString());
            System.out.println();
         }

         faceMatches.addAll(searchFacesByImageResult.getFaceMatches());

         photo.delete();
      }

      return faceMatches;
   }

	private SearchFacesByImageResult callSearchFacesByImage(String collectionId, Image image,
			AmazonRekognition amazonRekognition) {
		SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
				.withCollectionId(collectionId).withImage(image).withFaceMatchThreshold(THRESHOLD)
				.withMaxFaces(MAX_FACES);
		try {
			return amazonRekognition.searchFacesByImage(searchFacesByImageRequest);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
