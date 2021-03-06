package com.example.ecreyes.ccompra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.ecreyes.ccompra.Objetos.FirebaseReferences;
import com.example.ecreyes.ccompra.Objetos.Tienda;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TiendasFragment extends Fragment {

    // TODO: Customize parameter argument names
    private OnFragmentInteractionListener mListener;

    public Context ctx;
    DatabaseReference mDatabase;
    RecyclerView mRecycler;
    View view;
    FirebaseRecyclerAdapter<Tienda, TiendasRecyclerViewAdapter.ViewHolder> mAdapter;
    EditText editText;

    public ArrayList<Tienda> tiendas;
    //public TiendasRecyclerViewAdapter mTadapter;
    private Activity mActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public TiendasFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tiendas_list, container, false);
        //Log.w("OnCV", String.valueOf(container.getContext()));
        mRecycler = view.findViewById(R.id.list_recycler);
        editText = (EditText)view.findViewById(R.id.search_text);
        //mRecycler.setHasFixedSize(true);
        tiendas = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference(FirebaseReferences.TIENDA_REFERENCES);
        search("");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()){
                    search(s.toString());
                }
                else {
                    search("");
                }
            }
        });


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void search(String s){
        mDatabase.
                orderByChild("nombre").
                startAt(s).
                endAt(s + '\uf8ff').addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tiendas.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        //final Tienda t = ds.getValue(Tienda.class);
                        //tiendas.add(t);
                        if (ds.child("estado").getValue(Boolean.class)!=null){
                            final Tienda t = ds.getValue(Tienda.class);
                            tiendas.add(t);
                            //Log.w("T", String.valueOf(t.getNombre()));
                        }
                    }
                    TiendasRecyclerViewAdapter mTadapter = new TiendasRecyclerViewAdapter(tiendas, getContext());
                    //Log.i("mAC/mRe", String.valueOf(getContext()));
                    mTadapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String nombreTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).getNombre();
                            String categoriaTienda= tiendas.get(mRecycler.getChildAdapterPosition(v)).getCategoria();
                            String descripcionTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).getDescripcion();
                            String uriTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).getUri();
                            boolean estadoTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).isEstado();
                            String ubicacionTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).getUbicacion();
                            String alertaTienda = tiendas.get(mRecycler.getChildAdapterPosition(v)).getAlerta();

                            Intent myintent = new Intent(view.getContext(),DetalleTienda.class);
                            myintent.putExtra("keynombre",nombreTienda);
                            myintent.putExtra("keycategoria",categoriaTienda);
                            myintent.putExtra("keydescripcion",descripcionTienda);
                            myintent.putExtra("keyuri",uriTienda);
                            myintent.putExtra("keyestado",estadoTienda);
                            myintent.putExtra("keyubicacion", ubicacionTienda);
                            myintent.putExtra("keyalerta", alertaTienda);

                            myintent.putExtra("firstKeyName",nombreTienda);
                            startActivityForResult(myintent,0);

                        }
                    });
                    mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecycler.setAdapter(mTadapter);
                    mTadapter.notifyDataSetChanged();

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
