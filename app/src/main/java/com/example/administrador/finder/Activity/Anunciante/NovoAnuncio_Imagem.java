package com.example.administrador.finder.Activity.Anunciante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrador.finder.Config.ConfiguracaoFirebase;
import com.example.administrador.finder.Helper.Base64Custom;
import com.example.administrador.finder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class NovoAnuncio_Imagem extends AppCompatActivity implements IPickResult
{
    private ImageButton btnimageprod,btnimage1prod,btnimage2prod;
    private ArrayList<Uri> listUri = new ArrayList<>();
    private ArrayList<String> lisCaminho = new ArrayList<>();
    private ArrayList<String> lisCodImg = new ArrayList<>();
    private int btn = 0;
    private static Uri imagemProd1,imagemProd2,imagemProd3;
    private Button btnSalvarImg;
    private AVLoadingIndicatorView loadingIndicatorView ;

    private String subcategoria = "";
    private String nomeProd = "";
    private String categoria = "";
    private String descricao= "";
    private String preco= "";
    private String tipo = "";
    private String empres = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio__imagem);

        //recebendo dados do fragmento meus anuncios

        Intent intent = getIntent();
        final Bundle dados = intent.getExtras();
        if (dados != null)
        {
            nomeProd = dados.getString("nome");
            categoria = dados.getString("cat");
            descricao = dados.getString("descricao");
            subcategoria = dados.getString("sub");
            preco  = dados.getString("preco");
            tipo = dados.getString("tipo");
            empres = dados.getString("empres");
        }

        btnimageprod =(ImageButton)findViewById(R.id.btnImgProd);
        btnimage1prod =(ImageButton)findViewById(R.id.btnImg2Prod);
        btnimage2prod =(ImageButton)findViewById(R.id.btnImg3Prod);
        btnSalvarImg=(Button) findViewById(R.id.btnSalvarImg_NA);

        final PickSetup setup = new PickSetup()
                .setTitle("Selecione ou tire uma Foto")
                .setCancelText("Cancelar")
                .setCameraButtonText("Camera")
                .setGalleryButtonText("Galeria");

        btnimageprod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 1;

            }
        });
        btnimage2prod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 2;
            }
        });
        btnimage1prod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 3;
            }
        });

        btnSalvarImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (imagemProd1 != null) listUri.add(imagemProd1);
                if (imagemProd2 != null) listUri.add(imagemProd2);
                if (imagemProd3 != null) listUri.add(imagemProd3);
                Log.i("NA_Uri", "Lista URI ="+String.valueOf(listUri.size()));
                switch (listUri.size())
                {
                    case 0 :
                        Log.i("NA_Uri", "Igua a 0 Imagens");
                        Toast.makeText(getApplicationContext(),"adicione imagens antes de Salvar e tente novamente",Toast.LENGTH_LONG).show();
                        break;
                    case 1 :
                        Log.i("NA_Uri", "Igua a 1 Imagens");
                        Toast.makeText(getApplicationContext(),"adicione mais 2 imagens antes de Salvar!",Toast.LENGTH_LONG).show();
                        break;
                    case 2 :
                        Log.i("NA_Uri", "Igua a 2 Imagens");
                        Toast.makeText(getApplicationContext(),"adicione mais 1 imagem antes de Salvar!",Toast.LENGTH_LONG).show();
                        break;
                    case 3 :
                        Log.i("NA_Uri", "Igua a 3 Imagens");
                        salvarImagens(listUri);
                        break;

                }

            }

        });
    }
    private void salvarImagens(final ArrayList<Uri> uri)
    {
        Log.e("NA_IMG", "Clicou salvar "+ uri.size());
        Log.e("NA_IMG", "Entrou no WHILE");
        Log.e("NAI","nome "+nomeProd);
        Log.e("NAI","descricao "+descricao);
        Log.e("NAI","cat "+categoria);
        Log.e("NAI","sub "+subcategoria);
        Log.e("NAI","preco "+preco);
        Log.e("NAI","tipo "+tipo);
        Log.e("NAI","empres "+empres);
        Log.e("NAI","listcam "+lisCaminho);
        Log.e("NAI","listcod "+lisCodImg);

        for (int i = 0; i < uri.size(); i++)
        {
            Log.e("NA_IMG", "Entrou no FORI");

            Log.e("NA_IMG", "URI salvar = "+String.valueOf(uri.get(i)));
            Base64Custom base64Custom = new Base64Custom();
            final String codImag = base64Custom.codificarBase64(uri.get(i).getLastPathSegment());
            lisCaminho.add(codImag);
            Log.e("NA_IMG", "Codigo IMG salvar = "+String.valueOf(codImag));
            StorageReference refprod = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(codImag);
            btnSalvarImg.setClickable(false);
            btnSalvarImg.setText("Salvando Produto ...");
            final int finalI = i;
            refprod.putFile(uri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    lisCodImg.add(String.valueOf(taskSnapshot.getMetadata().getDownloadUrl()));
                    btnSalvarImg.setText("Salvando "+ (finalI+1) + " Imagem de "+uri.size());
                    Log.e("NA_IMG", "Lista Codigo = "+lisCodImg.size());
                    if (lisCodImg.size() == 3)
                    {

                        Intent intent = new Intent(getApplicationContext(),NovoAnuncio_Calendar.class);
                        Bundle dados = new Bundle();
                        dados.putString("nome",nomeProd);
                        dados.putString("descricao",descricao);
                        dados.putString("cat",categoria);
                        dados.putString("sub",subcategoria);
                        dados.putString("empres",empres);
                        dados.putString("preco",preco);
                        dados.putString("tipo",tipo);
                        dados.putStringArrayList("listcam",lisCaminho);
                        dados.putStringArrayList("listcod",lisCodImg);

                        intent.putExtras(dados);
                        startActivity(intent);
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Erro no upload da Primeira Imagem",Toast.LENGTH_SHORT).show();
                    btnSalvarImg.setText("Imagem "+ finalI +" Erro!");
                }
            });

        }

    }
    @Override
    public void onPickResult(PickResult pickResult)
    {
        if (pickResult.getError() == null)
        {
            criarImagens(pickResult.getBitmap(),pickResult.getUri());
        } else
        {
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void criarImagens(Bitmap bitmap, Uri uri)
    {
        Log.i("NA_btn", String.valueOf(btn));
        if (btn == 1)
        {
            btnimageprod.setImageBitmap(bitmap);
            imagemProd1 = uri;
            Log.i("NA_Foto_imagemProd1", String.valueOf(imagemProd1));
        }
        else if (btn == 3)
        {
            btnimage1prod.setImageBitmap(bitmap);
            imagemProd2 = uri;
            Log.i("NA_Foto_imagemProd2", String.valueOf(imagemProd2));
        }
        else if (btn == 2)
        {
            btnimage2prod.setImageBitmap(bitmap);
            imagemProd3 = uri;
            Log.i("NA_Foto_imagemProd3", String.valueOf(imagemProd3));

        }

    }

}
