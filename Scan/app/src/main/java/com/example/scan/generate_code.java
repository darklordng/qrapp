package com.example.scan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.ydcool.lib.qrmodule.encoding.QrGenerator;
import zendesk.belvedere.Belvedere;
import zendesk.belvedere.BelvedereUi;
import zendesk.belvedere.MediaIntent;
import zendesk.belvedere.MediaResult;

public class generate_code extends AppCompatActivity {

    ImageView userImage, qrCodeImage;
    EditText userId, fullName, eMail, password;
    Button generateBtn;

    Context context = this;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        final Map<String, String> userDetails = new HashMap<>();

         inflater = this.getLayoutInflater();
         dialogView= inflater.inflate(R.layout.alert_image, null);

        userImage = findViewById(R.id.user_img);
        userId = findViewById(R.id.userId_edt);
        fullName = findViewById(R.id.fullname_edt);
        eMail = findViewById(R.id.email_edt);
        password = findViewById(R.id.password_edt);
        generateBtn = findViewById(R.id.generate_btn);



        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(context);
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performChecks();
                userDetails.put("image", picturePath);
                userDetails.put("id", userId.getText().toString());
                userDetails.put("name", fullName.getText().toString());
                userDetails.put("email", eMail.getText().toString());
                userDetails.put("pass", password.getText().toString());


                try {
                    Bitmap qrCode = new QrGenerator.Builder()
                            .content(userDetails.toString())
                            .qrSize(300)
                            .margin(2)
                            .color(Color.BLACK)
                            .bgColor(Color.WHITE)
                            .ecc(ErrorCorrectionLevel.H)
                            .overlay(context,R.mipmap.ic_launcher)
                            .overlaySize(100)
                            .overlayAlpha(255)
                            .encode();


                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle("QR CODE");
                    dialogBuilder.setView(dialogView);
                    qrCodeImage = dialogView.findViewById(R.id.qrcode_img);
                    qrCodeImage.setImageBitmap(qrCode);

                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();

                    dialogBuilder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                
            }
        });




    }

    public void performChecks() {
        if(TextUtils.isEmpty(userId.getText())) {
            userId.setError("Cannot be empty");
        }

        if(TextUtils.isEmpty(fullName.getText())) {
            fullName.setError("Cannot be empty");
        }

        if(TextUtils.isEmpty(eMail.getText())) {
            eMail.setError("Cannot be empty");
        }

        if(TextUtils.isEmpty(password.getText())) {
            password.setError("Cannot be empty");
        }

    }


    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Image");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    String picturePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        userImage.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                picturePath = cursor.getString(columnIndex);
                                userImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }




}
