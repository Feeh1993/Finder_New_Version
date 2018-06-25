package com.example.administrador.finder.Fragment.Anunciante;
import android.content.Intent;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.administrador.finder.Activity.Anunciante.AnuncioEditar;
import com.example.administrador.finder.Adapter.Consumidor.ProdutoAdapter;
import com.example.administrador.finder.Config.ConexaoTeste;
import com.example.administrador.finder.Config.ConfiguracaoFirebase;
import com.example.administrador.finder.Interface.CustomItemClickListener;
import com.example.administrador.finder.Model.Produto;
import com.example.administrador.finder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MeusAnuncios extends Fragment
{
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private ArrayList<Produto> list = new ArrayList<>();
    private ArrayList<String> lisCat = new ArrayList<>();
    private Button btnerro;
    private ProgressBar prgerro;
    private TextView txterro;
    private RecyclerView recyclerView;

    private List<String> listQualCatRep = new ArrayList<>();



    private LottieAnimationView lav;

    public MeusAnuncios()
    {
    }
    @Override
    public void onResume()
    {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        atualizar(ConfiguracaoFirebase.getUID());
        Log.v("MA QTD LIST", String.valueOf(list.size()));
        ProdutoAdapter rvAdapter = new ProdutoAdapter(getContext(), list, new CustomItemClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                Intent intent = new Intent(getContext(),AnuncioEditar.class);
                Bundle dados = new Bundle();
                dados.putString("nome",list.get(position).nome);
                dados.putString("descricao",list.get(position).descricao);
                dados.putString("cat",list.get(position).categoria);
                dados.putString("sub",list.get(position).subcategoria);
                dados.putString("preco",list.get(position).preco);
                //dados.putString("img",list.get(position).codimg1.get(0));

                dados.putStringArrayList("listcod",list.get(position).codimg1);
                dados.putStringArrayList("listcam",list.get(position).caminhoimg);

                dados.putString("dias",list.get(position).diasRestantes);
                dados.putString("tipo",list.get(position).tipo);
                dados.putString("empres",list.get(position).empresa);
               // dados.putString("caminho",list.get(position).caminhoimg.get(1));
                intent.putExtras(dados);
                startActivity(intent);
                //adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(rvAdapter);

       // setSwipeForRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_meus_anuncios, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_meusanuncios);
        list = new ArrayList<>();
        txterro = (TextView)view.findViewById(R.id.txtajudaMA);
        btnerro =(Button)view.findViewById(R.id.btnCarregarMA);
        prgerro =(ProgressBar)view.findViewById(R.id.prgCarregarMA);

        lav = (LottieAnimationView) view.findViewById(R.id.animation_view_ma);


        btnerro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exibirMsgLoad("NAO");
            }
        });
        if (list.size() == 0)
        {
            txterro.setText("Ops!Parece que voce não possui produtos na sua lista! ");
            txterro.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        lisCat.clear();
        list.clear();
        exibirMsgLoad("NAO");
        recuperarCat();
    }

    private void recuperarCat()
    {
        mDatabase.child("minhascategorias").child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String cat = ds.getKey();
                    Log.d("MA QTD CAT", "QTD ="+String.valueOf(lisCat.size())+"\n E \n CATEGORIAS = "+lisCat.toString());
                    lisCat.add(cat);
                    atualizar(cat);
                }
                if (lisCat.size() == 0)
                {
                    exibirMsgLoad("VAZIA");
                }
                else
                    {
                        exibirMsgLoad("SIM");
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }



    public void exibirMsgLoad(String res)
    {
        ConexaoTeste conexaoTeste = new ConexaoTeste();
        if (res == "SIM")
        {
            txterro.setVisibility(View.GONE);
            btnerro.setVisibility(View.GONE);
            prgerro.setActivated(false);
            prgerro.setVisibility(View.GONE);
            lav.setVisibility(View.GONE);
        }
        else if(res == "NAO")
        {
            String teste = conexaoTeste.checkNetworkType(getContext());
            if (teste == "OK")
            {
                prgerro.setVisibility(View.VISIBLE);
                txterro.setText("Carregando Promoções");
                txterro.setVisibility(View.VISIBLE);
            }
            else
            {
                txterro.setVisibility(View.VISIBLE);
                txterro.setText("Sem conexão co a internet!");
                btnerro.setVisibility(View.VISIBLE);
            }

        }
        else if (res == "CONEXAO")
        {
            txterro.setVisibility(View.VISIBLE);
            btnerro.setVisibility(View.VISIBLE);
        }
        else if(res == "ERRO")
        {
            txterro.setVisibility(View.VISIBLE);
            txterro.setText("Ops,parece que algo de errado aconteceu... Carregar Novamente?");
            btnerro.setVisibility(View.VISIBLE);
        }
        else if (res == "VAZIA")
        {
            prgerro.setVisibility(View.GONE);
            lav.setAnimation(R.raw.caixa_lottie);
            txterro.setVisibility(View.VISIBLE);
            txterro.setText("Voce não possui produtos! Anuncie agora! \n Clique no Icone + e crie seu Primeiro Anuncio!");
        }
    }

/*
    private void setSwipeForRecyclerView()
    {
                final DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
                for (int i = 0; i < list.get(position).caminhoimg.size(); i++)
                {
                    StorageReference desertRef = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(list.get(position).caminhoimg.get(i));
                    desertRef.delete();
                }
                ref.child("produtos").child(list.get(position).subcategoria).child(ConfiguracaoFirebase.getUID()).child(list.get(position).nome).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        for (int i = 0; i < listQualCatRep.size(); i++)
                        {
                            Log.d("MA_cont",listQualCatRep.get(i).toString());
                        }
                        for (int i = 0; i < listQualCatRep.size(); i++)
                        {
                            if (!listQualCatRep.get(i).contains(list.get(position).subcategoria))
                            {
                                ref.child("minhascategorias").child(ConfiguracaoFirebase.getUID()).child(list.get(position).subcategoria).removeValue();
                            }
                        }
                        ref.child("classificacao").child(list.get(position).categoria).child(ConfiguracaoFirebase.getUID()).child(list.get(position).nome).removeValue();
                        Toast.makeText(getContext(),"Anuncio removido com sucesso",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Erro na conexão!",Toast.LENGTH_LONG).show();
                    }
                });
    }

 */

    public void atualizar(final String categoria)
    {
        mDatabase.child("produtos").child(categoria).child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                int cont = 0;
                for ( DataSnapshot dados: dataSnapshot.getChildren() )
                {
                    Produto produto = dados.getValue( Produto.class );
                    list.add( produto );
                    Log.d("MA QTD PROD", "QTD ="+String.valueOf(list.size())+"\n E \n PRODUTOS = "+list.toString());
                    cont++;
                }
                if (cont > 1 )
                {
                    listQualCatRep.add(categoria+":"+cont);
                    Log.d("MA QTD CATREP", "CATEGORIA REP = "+listQualCatRep.toString());
                }
                if (list.size() ==0)
                {
                    exibirMsgLoad("VAZIA");
                }
                else
                    {
                        exibirMsgLoad("SIM");
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                exibirMsgLoad("CONEXAO");
                                throw databaseError.toException();
            }
        });

    }

}