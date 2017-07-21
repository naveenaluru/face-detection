package it.polito.teaching.cv;

import it.polito.elite.teaching.cv.utils.PropertyLoader;
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

   public static final String COLLECTION_ID = "nnaircollection2";
   private static final String PATH_TO_PROCESS = System.getProperty("user.dir") + "/faces";
   private static final Float THRESHOLD = 70F;
   private static final int MAX_FACES = 2;

   AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1)
       .withCredentials(new AWSStaticCredentialsProvider(
           new BasicAWSCredentials(PropertyLoader.getAmazonKey(), PropertyLoader.getAmazonSecretKey()))).build();

   public List<FaceMatch> search() throws Exception {
      long start = System.currentTimeMillis();

      List<FaceMatch> matches = null;

      File dir = new File(PATH_TO_PROCESS);
      if (dir.isDirectory()) {
         matches = readPicturesAndFindMatch(amazonRekognition, dir);
      } else {
         throw new RuntimeException("Provided path is not a directory");
      }
      long stop = System.currentTimeMillis();
      System.out.println("Time in ms for search:" + (stop - start));
      return matches;
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
         FaceMatch highMatchFace = null;
         Float similarity = 0F;
         if (searchFacesByImageResult != null) {
            List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
            for (FaceMatch face : faceImageMatches) {
               if (face.getSimilarity() > similarity) {
                  similarity = face.getSimilarity();
                  highMatchFace = face;
                  System.out.println("FOund other high match: " + face.getFace().toString());
               }
               System.out.println(face.getFace().toString());
               System.out.println();
            }

            if (highMatchFace != null) {
               faceMatches.add(highMatchFace);
            }
         }

         photo.delete();
      }

      return faceMatches;
   }

   private SearchFacesByImageResult callSearchFacesByImage(String collectionId, Image image,
       AmazonRekognition amazonRekognition) {
      SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
          .withCollectionId(collectionId).withImage(image).withFaceMatchThreshold(THRESHOLD).withMaxFaces(MAX_FACES);
      try {
         long start = System.currentTimeMillis();
         SearchFacesByImageResult searchFacesByImage = amazonRekognition.searchFacesByImage(searchFacesByImageRequest);
         long stop = System.currentTimeMillis();
         System.out.println("Time in ms for amazon call:" + (stop - start));
         return searchFacesByImage;
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }
}
