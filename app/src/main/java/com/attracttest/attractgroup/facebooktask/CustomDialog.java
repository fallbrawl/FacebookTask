package com.attracttest.attractgroup.facebooktask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by nexus on 08.10.2017.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private Dialog dialog;
    private Button share, cancel;
    private ImageView imageToShare;
    private EditText textToShare;
    private Uri imageUri;

    ShareButton shareButton;

    public CustomDialog(Activity a, Uri uri) {
        super(a);
        this.activity = a;
        this.imageUri = uri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sharedialog);
        share = (Button) findViewById(R.id.btn_share);
        cancel = (Button) findViewById(R.id.btn_cancel);
        imageToShare = (ImageView) findViewById(R.id.image_for_share);
        textToShare = (EditText) findViewById(R.id.text_to_share);

        Picasso.with(getContext()).load(imageUri).fit().into(imageToShare);
        share.setOnClickListener(this);
        cancel.setOnClickListener(this);
        ShareDialog shareDialog = new ShareDialog(activity);
            // this part is optional
//        shareDialog.registerCallback(callbackManager, callback);
//
//
//        FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                Log.v(TAG, "Successfully posted");
//                // Write some code to do some operations when you shared content successfully.
//            }
//
//            @Override
//            public void onCancel() {
//                Log.v("staty", "Sharing cancelled");
//                // Write some code to do some operations when you cancel sharing content.
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.v(TAG, error.getMessage());
//                // Write some code to do some operations when some error occurs while sharing content.
//            }
//        };


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                //activity.finish();
                Toast.makeText(getContext(), textToShare.getText(), Toast.LENGTH_LONG).show();

                try {
                    sharePhotoToFacebook();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void sharePhotoToFacebook() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(textToShare.getText().toString())
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareButton shareButton = (ShareButton) findViewById(R.id.fb_share_button);
// Set the content you want to share.
        shareButton.setShareContent(content);
        shareButton.performClick();


    }
}
