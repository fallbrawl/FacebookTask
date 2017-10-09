package com.attracttest.attractgroup.facebooktask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Суть, чтобы получить данные юзера и его посты.
    // Потом, нужно сделать выбор картинок из галереи и камеры  - загружать их в listView .
    // Потом по клику элементов в этом списке открывается диалог,
    // там уже видно эту картинку + поле для ввода текста и кнопка постить в ФБ.

    LoginButton loginButton;
    CallbackManager callbackManager;
    ImageView avatar;
    TextView profileName;
    GridView gallery;
    private ArrayList<Uri> bitmaps;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        avatar = (ImageView) findViewById(R.id.avatar);
        profileName = (TextView) findViewById(R.id.profilename);
        gallery = (GridView) findViewById(R.id.gridview);
// gettin hash!
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.attracttest.attractgroup.facebooktask",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
//
//        }
        bitmaps = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, bitmaps);
        gallery.setAdapter(imageAdapter);

        loginButton.setReadPermissions("public_profile");

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                                    loginButton.setVisibility(View.GONE);

                                    Picasso.with(MainActivity.this).load(currentProfile.getProfilePictureUri(100, 100)).into(avatar);
                                    avatar.setVisibility(View.VISIBLE);
                                    profileName.setText(currentProfile.getFirstName() + currentProfile.getLastName());
                                    profileName.setVisibility(View.VISIBLE);
                                    gallery.setVisibility(View.VISIBLE);

                                    mProfileTracker.stopTracking();
                                }
                            };
                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        } else {
                            Profile profile = Profile.getCurrentProfile();

                            Log.v("facebook - profile", profile.getId());
                            loginButton.setVisibility(View.GONE);

                            Picasso.with(MainActivity.this).load(profile.getProfilePictureUri(100, 100)).into(avatar);
                            avatar.setVisibility(View.VISIBLE);
                            profileName.setText(profile.getFirstName() + profile.getLastName());
                            profileName.setVisibility(View.VISIBLE);
                            gallery.setVisibility(View.VISIBLE);

                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.v("facebook - onCancel", "cancelled");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.v("facebook - onError", e.getMessage());
                    }
                });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Source:")
                        .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        Intent galleryPickerIntent = new Intent(Intent.ACTION_PICK);
                                        galleryPickerIntent.setType("image/*");
                                        startActivityForResult(galleryPickerIntent, 607);
                                        break;
                                    case 1:
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                        startActivityForResult(takePictureIntent, 608);
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        });

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomDialog cdd = new CustomDialog(MainActivity.this, bitmaps.get(position));
                cdd.show();

            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            Log.e("staty", "called callback!");
        }
        switch (requestCode) {
            case 607:

                if (data != null) {
                    final Uri imageUri = data.getData();
                    bitmaps.add(imageUri);
                    imageAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
                break;

            case 608:

                if (resultCode != RESULT_CANCELED) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    bitmaps.add(getImageUri(MainActivity.this, imageBitmap));
                    imageAdapter.notifyDataSetChanged();
                }

                break;

        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
