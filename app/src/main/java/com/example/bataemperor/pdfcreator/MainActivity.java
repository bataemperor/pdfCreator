package com.example.bataemperor.pdfcreator;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {
    EditText etName, etSurname, etDate;
    Bitmap bitmap;
    ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etDate = (EditText) findViewById(R.id.etDate);
        ivImage = (ImageView) findViewById(R.id.ivImage);
    }

    public void generate(View view) {
        //reference to EditText
        //create document object
        Document doc = new Document();
        //output file path
        String outpath = Environment.getExternalStorageDirectory() + "/mypdf.pdf";
        try {
            //create pdf writer instance
            PdfWriter.getInstance(doc, new FileOutputStream(outpath));
            //open the document for writing
            doc.open();
            //add paragraph to the document

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap = scaleCenterCrop(bitmap, 200, 200);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image img = Image.getInstance(stream.toByteArray());
            doc.add(img);
            Paragraph p = new Paragraph(etName.getText().toString());
            p.setSpacingAfter(5);
            doc.add(new Paragraph(p));
            doc.add(new LineSeparator());
            p = new Paragraph(etSurname.getText().toString());
            p.setSpacingAfter(5);
            doc.add(p);
            doc.add(new LineSeparator());
            p = new Paragraph(etDate.getText().toString());
            p.setSpacingAfter(5);
            doc.add(p);
            doc.add(new LineSeparator());

            //close the document
            doc.close();
            Toast.makeText(this, "Pdf path : " + Environment.getExternalStorageDirectory() + "/mypdf.pdf", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException | DocumentException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void pick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data == null) {
                //Display an error
                return;
            } else {
                Uri uri = data.getData();
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));

                    ivImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public void send(View view) {
        String filename = "mypdf.pdf";
        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"nekiEmail@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cv");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}


