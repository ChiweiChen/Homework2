package com.chiwei.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check if OpenCV is available for use
        if(!OpenCVLoader.initDebug()){
            Log.d("test","No OpenCV");
        }
        else{
            Log.d("test", "OpenCV Ok");
        }

        //Set the elements needed
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            //Records whether or not the lines have been drawn
            boolean drawn;
            @Override
            public void onClick(View view) {

                //If the boolean value is still false, do stuff
                if(drawn!=true){
                    //Get the QRCode, decode it, and store the results in a String
                    Bitmap orig = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    String result = decodeQRCode(orig);

                    //Display the results in textView
                    textView.setText(result);

                    //Create a copy of the QRCode that is a Matrix object
                    Mat img = new Mat(orig.getWidth(), orig.getHeight(), CvType.CV_8UC4);
                    Utils.bitmapToMat(orig, img);

                    //Set the other variables needed to draw the lines
                    Scalar lineColor = new Scalar(255, 0, 0, 255);
                    int lineWidth = 18;

                    //Split each String representing a line in the original result string and store them in a String array
                    String [] lines = result.split(";");

                    //Iterate over each line in the lines array
                    for(String line: lines){

                        //Create a new array that stores the start and end points of the line
                        Point[] points = {new Point(), new Point()};

                        //Split the coordinates of the two points in each line
                        String [] coords = line.split(" ");

                        //A variable that controls which Point object is being constructed
                        int count = 0;

                        //For each set of coordinates, separate the x and y axis
                        for(String coord: coords){
                            String [] xy = coord.split(",");

                            //Construct the point using the x and y values
                            points[count] = new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1]));
                            count++;
                        }

                        //Draw the line
                        Imgproc.line(img, points[0], points[1], lineColor, lineWidth);
                    }

                    //Convert the Matrix object back to a Bitmap object and set the new image
                    Bitmap bitmap = Bitmap.createBitmap(img.width(), img.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(img, bitmap);
                    imageView.setImageBitmap(bitmap);
                }

                //Set the boolean variable to true
                drawn = true;

            }
        });
    }
    String decodeQRCode(Bitmap bitmap){
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap,mat);
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        String result = qrCodeDetector.detectAndDecode(mat);
        return result;
    }
}
