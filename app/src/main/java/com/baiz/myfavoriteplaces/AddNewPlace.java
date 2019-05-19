package com.baiz.myfavoriteplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class AddNewPlace extends Fragment {

    public DataBase dataBase ;
    public EditText editName , editAdresse  ,editdescription ;
    public Spinner CatSpinner ;
    public ImageView imageView ;
    public Button btnChooser,btnadd ;
    private Uri uri ;
    public ArrayList<String> categorys ;
    final int REQUEST_GALLERY =1;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_new_place, container, false);
        // changer titre
        getActivity().setTitle("Add New Place");

        // init ma base de donnee
        dataBase = new DataBase(getActivity());

        // ###### init compenent ##############
        editName = (EditText) view.findViewById(R.id.editRes);
        editAdresse = (EditText) view.findViewById(R.id.editadresse);
        CatSpinner = (Spinner) view.findViewById(R.id.spinnerCategory);
        editdescription = (EditText) view.findViewById(R.id.editdescription);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        btnChooser = (Button) view.findViewById(R.id.imageChooser);
        btnadd = (Button) view.findViewById(R.id.btnAdd);


        // remplire une list de categories je prefer ne pas creer une table -_*
        categorys = new ArrayList<String>();
        categorys.add("choose category");
        categorys.add("city");
        categorys.add("restaurants");
        categorys.add("beaches");
        categorys.add("shopping");
        categorys.add("Entertainment");
        categorys.add("Historical");
        categorys.add("Other");

        // creer un adapter pour mon spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,categorys );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        CatSpinner.setAdapter(adapter);

        // quand le btn et clicker je demande la permission de acceder ou gallery si n exist pas
        btnChooser.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY
                );
            }
        });

        // btn clicker j appel l method addplace
        btnadd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addPlace();
            }
        });

        return view;
    }


    // envoyer l utitlisateur pour choisir une image
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_GALLERY)
        {
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST_GALLERY);

            }
            else {
                Toast.makeText(getActivity(),"you don't have permissyion",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // attendre l image choisie par l utitlisateur
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if ( requestCode == 1 && data != null)
        {

            try {
                uri = data.getData();
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // cette method recupere les donnee entre par l utilisateur est remplire un objet
    // pour l insere dans la base de donnee
    public void addPlace()
    {

        if (!editName.getText().toString().isEmpty() && !editAdresse.getText().toString().isEmpty() && CatSpinner.getSelectedItem().toString() !="choose category" )
        {
            String pathImage = getRealPathFromURI(this.uri);
            String imageName = getFileName(this.uri);


            Place place = new Place();
            place.setName(editName.getText().toString());
            place.setAdresse(editAdresse.getText().toString());
            place.setCategory(CatSpinner.getSelectedItem().toString());
            place.setDescription(editdescription.getText().toString());
            place.setCreateAt(String.valueOf(Calendar.getInstance().getTime()));
            place.setImage(pathImage);
            dataBase.addPlace(place);

            editName.setText("");
            editAdresse.setText("");
            editdescription.setText("");
            imageView.setImageResource(R.drawable.ic_image);


            try {
                insertImageToStorage(pathImage,imageName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Intent intent = new Intent(AddNewPlace.this,AllPlaces.class);
            // startActivity(intent);

        }
        else {
            Toast.makeText(getActivity(),"one of the input is empty",Toast.LENGTH_LONG).show();
        }

    }

    // inserer une image dans le storage
    private void insertImageToStorage(String pathImage, String imageName) throws IOException {
        FileOutputStream fos = getActivity().openFileOutput(imageName,getActivity().MODE_APPEND);
        File file = new File(pathImage);
        byte[] bytes = FileUtils.readFileToByteArray(file);

        fos.write(bytes);
        fos.close();
    }


    // recuperer le chemaine d un fichier
    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);

        if (cursor!=null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return  null ;
    }

    // recipere le nom d un fichier
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
