package com.vpipl.mmtfranchisee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.WHITE;

/**
 * Created by admin on 20-12-2017.
 */

public class QrGenerate extends AppCompatActivity {

    TextView textView;
    Bitmap bitmap_qr;
    Button button_print;
    LinearLayout LL_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppUtils.setActionbarTitle(getSupportActionBar(), this);

        textView = (TextView) findViewById(R.id.textView);
        button_print = (Button) findViewById(R.id.button_print);
        LL_view = (LinearLayout) findViewById(R.id.LL_view);

        textView.setText("Vendor Code : " + com.vpipl.mmtfranchisee.AppController.getSpUserInfo().getString(SPUtils.USER_FCode, ""));

        qrGenerator(com.vpipl.mmtfranchisee.AppController.getSpUserInfo().getString(SPUtils.USER_FCode, ""));

        button_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button_print.setVisibility(View.GONE);
                loadBitmapFromView(LL_view);
            }
        });
    }

    public void qrGenerator(String qrCodeData) {
        try {

            //setting size of qr code
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallestDimension = width < height ? width : height;

//            int width =300, height = 300;
//            int smallestDimension = width < height ? width : height;

            //setting parameters for qr code
            String charset = "UTF-8"; // or "ISO-8859-1"
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            createQRCode(qrCodeData, charset, hintMap, smallestDimension, smallestDimension);

        } catch (Exception ex) {
            Log.e("QrGenerate", ex.getMessage());
        }
    }

    public void createQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {

        try {
//            //generating qr code in bitmatrix type
//            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
//            //converting bitmatrix to bitmap
//            int width = matrix.getWidth();
//            int height = matrix.getHeight();
//            int[] pixels = new int[width * height];
//            // All are 0, or black, by default
//            for (int y = 0; y < height; y++) {
//                int offset = y * width;
//                for (int x = 0; x < width; x++) {
//                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
//                }
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            //setting bitmap to image view
//            ImageView myImage = (ImageView) findViewById(R.id.imageView1);
//            myImage.setImageBitmap(bitmap);

            //generating qr code.
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //for black and white
                    //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    //for custom color
                    pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(), R.color.colorB, null) : WHITE;
                }
            }
            //creating bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            //getting the logo
            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            //setting bitmap to image view
            ImageView myImage = (ImageView) findViewById(R.id.imageView1);
//            myImage.setImageBitmap(bitmap);


            myImage.setImageBitmap(mergeBitmaps(overlay, bitmap));


        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        bitmap_qr = combined;
        return combined;
    }


    public void loadBitmapFromView(View v) {

        int specWidth = View.MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED);
        v.measure(specWidth, specWidth);
        int questionWidth = v.getMeasuredWidth();
        int questionHeight = v.getMeasuredHeight();

        Bitmap b = Bitmap.createBitmap( questionWidth, questionHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, questionWidth, questionHeight);
        v.draw(c);
        handleclick(b);
    }


    public void handleclick(Bitmap screen) {

//        Bitmap screen = bitmap_qr;

        try {
            String path = Environment.getExternalStorageDirectory() + "/Android/SC_QR_code/";

            File root = new File(path);
            if (!root.exists()) {
                root.mkdirs();
            }

            String filename = "Img_" + AppController.getSpUserInfo().getString(SPUtils.USER_FCode, "") + ".png";

            File f = new File(path + filename);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();


            FileOutputStream stream = new FileOutputStream(f);
            screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            AppController.URI = Uri.fromFile(f);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//      Toast.makeText(this, "Receipt Exported Successfully", Toast.LENGTH_SHORT).show();

       // startActivity(new Intent(QrGenerate.this, MainActivity.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}