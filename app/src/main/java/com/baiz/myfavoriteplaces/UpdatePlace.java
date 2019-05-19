package com.baiz.myfavoriteplaces;

import android.Manifest;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class UpdatePlace extends Fragment {

    private EditText update_name,update_adresse,update_description ;
    private ImageView imageView ;
    private Spinner cat_Spinner ;
    private ArrayList<String> categorys ;
    private Place place ;
    private DataBase dataBase ;
    private Button btn_chooser,btnUpdate ;
    final int REQUEST_GALLERY =1;
    private Uri uri ;
    private boolean drapau = false ;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_update_place, container, false);

        // chnager le titre
        getActivity().setTitle("Update Place");

        // init ma base de donnee
        dataBase = new DataBase(getActivity());

        // recuperer objet place a modifier
         place = (Place) getArguments().getSerializable("place");
        Log.d("place",place.toString());

        // ###### init compenent ##############
        update_name = (EditText) view.findViewById(R.id.update_name);
        update_adresse =(EditText) view.findViewById(R.id.update_adresse);
        update_description =(EditText) view.findViewById(R.id.update_description);

         btnUpdate = (Button) view.findViewById(R.id.btn_update);
        btn_chooser = (Button) view.findViewById(R.id.btn_choose_img);
        cat_Spinner = (Spinner) view.findViewById(R.id.spinner_update);
        imageView = (ImageView) view.findViewById(R.id.update_img);

        // #####################

        // remplire une list de categories je prefer ne pas creer une table -_*
        categorys = new ArrayList<String>();
        categorys.add("city");
        categorys.add("restaurants");
        categorys.add("beaches");
        categorys.add("shopping");
        categorys.add("Entertainment");
        categorys.add("Historical");
        categorys.add("Other");

        // creer un adapter pour mon spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),R.layout.support_simple_spinner_dropdown_item,categorys );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        cat_Spinner.setAdapter(adapter);

        // choisir la categorie de l objet place qui sera modifier
        int i =0;
        for (String category : categorys)
        {
            if(place.getCategory().equals(category))
            {
                cat_Spinner.setSelection(i);
            }
                i++;
        }


        // remplire les compenent avec la data de l objet place
        update_name.setText(place.getName());
        update_adresse.setText(place.getAdresse());
        update_description.setText(place.getDescription());

        // test si place a une image ou nn pour l afficher
        if (!place.getImage().equals(""))
        {
            File f=new File(place.getImage());
            try {
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imageView.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        // quand le button update est click les donnee dans la base de donnee seront change
        // l utilisateur sera diriger a placeView pour afficher les changement
        btnUpdate.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v)
            {
                place.setName(update_name.getText().toString());
                place.setAdresse(update_adresse.getText().toString());
                place.setCategory(cat_Spinner.getSelectedItem().toString());
                place.setDescription(update_description.getText().toString());


                if (drapau)
                {
                    String pathImage = getRealPathFromURI(uri);
                    String imageName = getFileName(uri);
                    place.setImage(pathImage);
                    try {
                        insertImageToStorage(pathImage,imageName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                dataBase.updatePlaace(place);

                Bundle bundle = new Bundle();
                bundle.putSerializable("place", (Serializable) place);
                PlaceView placeView = new PlaceView();
                placeView.setArguments(bundle);
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction ft=fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container,placeView);
                ft.commit();


            }
        });


        btn_chooser.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY
                );
            }
        });




        return view;
    }

    // envoyer l utitlisateur pour choisir une image
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
                this.drapau = true ;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
