package com.example.administrador.finder.Activity.Anunciante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrador.finder.Helper.Base64Custom;
import com.example.administrador.finder.Validacao.ValidarPreco;
import com.example.administrador.finder.Adapter.Anunciante.TabAdapterAnunc;
import com.example.administrador.finder.Config.ConfiguracaoFirebase;
import com.example.administrador.finder.Model.CategoriaProd;
import com.example.administrador.finder.Model.LocalProd;
import com.example.administrador.finder.Model.Produto;
import com.example.administrador.finder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NovoAnuncio_Dados extends AppCompatActivity
{
    private EditText edtpreco,edtNomeprod,edtDescrprod;
    private Spinner spinner;
    private Button btnSalvarProd;

    private String textoCat = "";

    private String textoSubCat= "";
    private String textotpprec = "";

    public static final String[] tipopreco = new String[] {"Escolha o tipo","KG","UN"};
    private CategoriaProd categoriaProd;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private String empres;

     int btn = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio_dados);

        btnSalvarProd=(Button) findViewById(R.id.btnSalvarProd);
        edtDescrprod = (EditText) findViewById(R.id.edtDescricaoProd);
        edtNomeprod = (EditText) findViewById(R.id.edtNomeProd);
        edtpreco = (EditText) findViewById(R.id.edtPrecoProd);
        edtpreco.addTextChangedListener(new ValidarPreco(edtpreco));

        final Spinner spinnervalidade = (Spinner) findViewById(R.id.spinnerDuracao);
        final Spinner spinnertipo = (Spinner) findViewById(R.id.spinnerTipo);

        ArrayAdapter adaptadortipo = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tipopreco);
        adaptadortipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertipo.setAdapter(adaptadortipo);

        spinnertipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar o tipo!",Toast.LENGTH_LONG).show();
            }
            public void onItemSelected(AdapterView parent, View v, int posicao, long id)
            {
                textotpprec = parent.getItemAtPosition(posicao).toString();
            }
        });
        final Spinner spinnercat = (Spinner) findViewById(R.id.spinnercat);
        ArrayAdapter adaptadorcat = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoriaProd.categoria);
        adaptadorcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercat.setAdapter(adaptadorcat);
        spinnercat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                textoCat = parent.getItemAtPosition(position).toString();
                if (textoCat == "Escolha a categoria")
                {
                    spinner =AdicionarSpinner(categoriaProd.nenhumacat);
                }
                else if (textoCat =="arte,entretenimento e lazer")
                {
                    spinner =AdicionarSpinner(categoriaProd.subcatArteEntreLaz);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    });
                }else if(textoCat =="casa,eletrodomésticos e ferramentas")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatCasEletrFerr);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat =="livros,filmes e músicas")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatLivrFilmMus);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "saúde e beleza")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatSaudBelez);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "automobilismo,indústria e varejo")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatAutIndVarej);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "tecnologia e comunicação")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatTecCom);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar o ramo da sua empresa!",Toast.LENGTH_LONG).show();
            }
        });


        btnSalvarProd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             String result = validarCampos(edtNomeprod.getText().toString(),edtDescrprod.getText().toString(),edtpreco.getText().toString()
             ,textotpprec,textoCat,textoSubCat);
             if (result == "ERRO")
             {
                 Toast.makeText(getApplicationContext(),"Todos os campos precisam estar preenchidos e selecionados!",Toast.LENGTH_LONG).show();
             }
             else
             {
                 Intent intent = new Intent(getApplicationContext(),NovoAnuncio_Imagem.class);
                 Bundle dados = new Bundle();
                 dados.putString("nome",edtNomeprod.getText().toString());
                 dados.putString("descricao",edtDescrprod.getText().toString());
                 dados.putString("cat",textoCat);
                 dados.putString("sub",textoSubCat);
                 dados.putString("preco",edtpreco.getText().toString());
                 dados.putString("tipo",textotpprec);
                 dados.putString("empres",empres);
                 intent.putExtras(dados);
                 startActivity(intent);
                 finish();
             }
            }

        });

    }


    private Spinner AdicionarSpinner(String[] strings)
    {
        final Spinner spinnersubcat = (Spinner) findViewById(R.id.spinnersubcat);
        ArrayAdapter adaptadorcat = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,strings );
        adaptadorcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersubcat.setAdapter(adaptadorcat);
        spinnersubcat.setVisibility(View.VISIBLE);
        return spinnersubcat;
    }
    public void onStart()
    {
        super.onStart();
            ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    empres = dataSnapshot.child("nome").getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
    }

  /*
   public int verificarDias(int spinner)
    {
        int dias = 0;
        switch (spinner)
        {
            case 0:
                dias = 0;
                break;
            case 1:
                dias = 1;
                break;
            case 2:
                dias = 2;
                break;
            case 3:
                dias = 5;
                break;
            case 4:
                dias = 7;
                break;
            case 5:
                dias = 10;
                break;
            case 6:
                dias = 15;
                break;
            case 7:
                dias = 20;
                break;
            case 8:
                dias = 25;
                break;
            case 9:
                dias = 30;
                break;
        }
        return dias;
    }
   */

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, TabAdapterAnunc.class));
        finish();
    }
    private String validarCampos(String nome , String descricao , String preco , String tipo, String categoria , String subcategoria)
    {
        String resultado = "OK";
        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty() || tipo.isEmpty() || categoria.isEmpty() || subcategoria.isEmpty())
        {
         resultado = "ERRO";
        }
        return resultado;
    }

}
