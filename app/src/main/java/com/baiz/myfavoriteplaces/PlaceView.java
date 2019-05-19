package com.baiz.myfavoriteplaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlaceView extends Fragment {

    TextView txt_name,txt_adresse,txt_cat,txt_description,txt_creatAt ;
    ImageView imageView ;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_view, container, false);




        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_adresse = (TextView) view.findViewById(R.id.txt_adresse);
        txt_cat = (TextView) view.findViewById(R.id.txt_cat);
        txt_description = (TextView) view.findViewById(R.id.txt_description);
        txt_creatAt = (TextView)view.findViewById(R.id.txt_createAt);
        imageView = (ImageView) view.findViewById(R.id.image_place);

        // recuperer objet place a affichier
        Place place = (Place) getArguments().getSerializable("place");
        Log.d("place",place.toString());

        // changer titre
        getActivity().setTitle("Place"+" "+place.getName());

        // afficher l objet place
        txt_creatAt.setText(place.getCreateAt());
        txt_name.setText(place.getName());
        txt_adresse.setText(place.getAdresse());
        txt_cat.setText(place.getCategory());
        txt_description.setText(place.getDescription());
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

        return view;

    }
}
