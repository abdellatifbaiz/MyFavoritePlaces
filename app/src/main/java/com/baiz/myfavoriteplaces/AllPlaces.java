package com.baiz.myfavoriteplaces;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public class AllPlaces extends Fragment {

    private Spinner catSpinner ;
    private EditText editName ;
    private ListView places_listView ;
    private PlaceAdapter placeAdapter ;
    private  DataBase dataBase ;
    public ArrayList<String> categorys ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_all_places, container, false);
        getActivity().setTitle("Places");
        dataBase = new DataBase(getActivity());
        catSpinner = (Spinner)  view.findViewById(R.id.spinnerCat);
        editName = (EditText)  view.findViewById(R.id.editRes);
        places_listView = (ListView) view.findViewById(R.id.places_listViw);
        placeAdapter = new PlaceAdapter(dataBase.getAllPlaces());
        places_listView.setAdapter(placeAdapter);
        categorys = new ArrayList<String>();
        categorys.add("choose category");
        categorys.add("all");
        categorys.add("city");
        categorys.add("restaurants");
        categorys.add("beaches");
        categorys.add("shopping");
        categorys.add("Entertainment");
        categorys.add("Historical");
        categorys.add("Other");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,categorys );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        catSpinner.setAdapter(adapter);


        // ############ spinner SelectedListener recherche par category ##################
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (categorys.get(position)=="all"|categorys.get(position)=="choose category")
                {
                    placeAdapter = new PlaceAdapter(dataBase.getAllPlaces());
                    places_listView.setAdapter(placeAdapter);
                }else {
                    placeAdapter = new PlaceAdapter(dataBase.getPlacesByCategory(categorys.get(position)));
                    places_listView.setAdapter(placeAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        //############### recherche par le nom ###########################
        editName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PlaceAdapter placeAdapter = new PlaceAdapter(dataBase.getPlacesByName(editName.getText().toString()));
                places_listView.setAdapter(placeAdapter);
            }
        });
        //##################### listview ClickListener voir place #############################
        places_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Place place = (Place) places_listView.getItemAtPosition(position);
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

        //##################### listview LongClickListener #############################
        places_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                PopupMenu popupMenu = new PopupMenu(getContext(),places_listView);
                popupMenu.getMenuInflater().inflate(R.menu.menu_contextuel,popupMenu.getMenu());
                popupMenu.show();

                // ####################### popupMenu clickListener #######################
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    public boolean onMenuItemClick(MenuItem item) {


                        // ##### ouvrir google map #####
                        if (item.getItemId()==R.id.map)
                        {

                            Place place = (Place) places_listView.getItemAtPosition(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("place", (Serializable) place);
                            bundle.putString("from","allPlaces");
                            MapFragment mapFragment = new MapFragment();
                            mapFragment.setArguments(bundle);
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction ft=fragmentManager.beginTransaction();
                            ft.replace(R.id.fragment_container,mapFragment);
                            ft.commit();

                            /*
                            // Map point based on address
                            Uri location = Uri.parse("geo:0,0?q="+dataBase.getAllPlaces().get(position).getAdresse());
                            // Or map point based on latitude/longitude
                            // Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                            startActivity(mapIntent);
                            */
                        }
                        // ##### modifier place #####
                        else if (item.getItemId()==R.id.update)
                        {
                            Place place = (Place) places_listView.getItemAtPosition(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("place", (Serializable) place);
                            UpdatePlace updatePlace = new UpdatePlace();
                            updatePlace.setArguments(bundle);
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction ft=fragmentManager.beginTransaction();
                            ft.replace(R.id.fragment_container,updatePlace);
                            ft.commit();
                        }
                        //##### supprimer place #####
                        else {
                            // ################ confirmation pour supprimer #############################
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    //set icon
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    //set title
                                    .setTitle("Are you sure you wanna delete it all")

                                    //set positive button
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            dataBase.deletePlace(dataBase.getAllPlaces().get(position).getId());
                                            PlaceAdapter adaptar = new PlaceAdapter(dataBase.getAllPlaces());
                                            places_listView.setAdapter(adaptar);

                                        }
                                    })
                                    //set negative button
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .show();

                        }
                        return true;
                    }
                });
                return true;
            }


        });

        return view;
    }


    // pour adapter la listView a l objet place
    public class PlaceAdapter extends BaseAdapter {

        public PlaceAdapter(ArrayList<Place> places) {
            this.places = places;
        }

        ArrayList<Place> places = new ArrayList<Place>();


        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Place getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return places.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.place_items_list,null);
            TextView txName = (TextView) view.findViewById(R.id.txtName);
            TextView txtAdresse = (TextView) view.findViewById(R.id.txtAdresse);
            TextView txtDate = (TextView) view.findViewById(R.id.txtdate);
            txName.setText(this.getItem(position).getName());
            txtAdresse.setText(this.getItem(position).getAdresse());
            txtDate.setText(this.getItem(position).getCreateAt());

            try {

                File f=new File(this.getItem(position).getImage());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                ImageView img=(ImageView) view.findViewById(R.id.item_listView);
                img.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }
}
