package com.example.administrador.finder.Activity.Anunciante;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrador.finder.Helper.Base64Custom;
import com.example.administrador.finder.R;
import com.example.administrador.finder.DialogFragment.AjudaAnuncioEditar;

import com.example.administrador.finder.Validacao.ValidarData;
import com.example.administrador.finder.Adapter.Anunciante.TabAdapterAnunc;
import com.example.administrador.finder.Config.ConfiguracaoFirebase;
import com.example.administrador.finder.Model.Produto;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.timessquare.CalendarPickerView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* classe criada para edição do anuncio ...
   vinculada a lista de anuncios do Anunciante_Mod ...Quando clicado em um item da lista é possivel altera-la através desta classe
*/
public class AnuncioEditar extends AppCompatActivity implements IPickResult
{
    private EditText edtDescrprod;
    private Button btnSalvarProd;
    private TextView categoriaAE,subcategoriaAE,txtpreco,txtNomeprod;
    private ImageButton btnimageprod1,btnimageprod2,btnimageprod3;

    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private AlertDialog alerta;
    private String subcategoria = "";
    private String nomeProd = "";
    private String categoria = "";
    private String descricao= "";
    private String preco= "";
    private String caminhoimg  = "";
    private String tipo = "";
    private String empresa = "";
    private int dias = 0;
    private ArrayList<String> listcod = new ArrayList<>();
    private ArrayList<String> listcam = new ArrayList<>();
    private int btn = 0;
    private Uri imagemProd1,imagemProd2,imagemProd3;
    private CalendarPickerView calendar_view;
    private ArrayList<String> datas = new ArrayList<>();
    private ArrayList<Uri> listUri = new ArrayList<>();
    private ArrayList<String> listDeletCam = new ArrayList<>();
    private ArrayList<String> listDeletCod = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio_editar);
        btnimageprod1 = (ImageButton) findViewById(R.id.btnImgProdAE);
        btnimageprod2 = (ImageButton) findViewById(R.id.btnImg2ProdAE);
        btnimageprod3 = (ImageButton) findViewById(R.id.btnImg3ProdAE);
        categoriaAE = (TextView) findViewById(R.id.categoriaAE);
        subcategoriaAE = (TextView) findViewById(R.id.subcategoria);
        btnSalvarProd = (Button) findViewById(R.id.btnSalvarProdAE);
        edtDescrprod = (EditText) findViewById(R.id.edtDescricaoProdAE);
        txtpreco = (TextView) findViewById(R.id.txtPrecoProdAE);
        txtNomeprod = (TextView) findViewById(R.id.txtNomeProdAE);
        //spinnerduracao = (Spinner) findViewById(R.id.spinnerDuracaoAE);
        calendar_view = (CalendarPickerView) findViewById(R.id.calviewNA);
// ficando atual
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 1);
        Date today = new Date();

// adiciona um ano ao calendário a partir da data de hoje
        calendar_view.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date)
            {
                datas.add(transformarData(date));
            }
            @Override
            public void onDateUnselected(Date date)
            {
                datas.clear();
            }
        });

        final PickSetup setup = new PickSetup()
                .setTitle("Selecione ou tire uma Foto")
                .setCancelText("Cancelar")
                .setCameraButtonText("Camera")
                .setGalleryButtonText("Galeria");

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

            listcod = dados.getStringArrayList("listcod");
            listcam = dados.getStringArrayList("listcam");

            if (dados.getString("dias").contains("vencido"))
            {
                dias = 0;
            }
            else dias = Integer.parseInt(dados.getString("dias"));
            caminhoimg  = dados.getString("caminho");
            tipo = dados.getString("tipo");
            empresa = dados.getString("empres");
        }
        txtNomeprod.setText(nomeProd);
        txtpreco.setText("R$ "+preco+" ,00 reais a(o) " + tipo );
        edtDescrprod.setText(descricao);
        categoriaAE.setText(categoria);
        subcategoriaAE.setText(subcategoria);

        //download imagem com GLIDE
        Glide.with(getApplicationContext()).load(listcod.get(0)).into(btnimageprod1);
        Glide.with(getApplicationContext()).load(listcod.get(1)).into(btnimageprod2);
        Glide.with(getApplicationContext()).load(listcod.get(2)).into(btnimageprod3);


        txtNomeprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(0),Toast.LENGTH_SHORT).show();
            }
        });
        txtpreco.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),tratarCliques(1),Toast.LENGTH_SHORT).show();
        }
        });
        categoriaAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(3),Toast.LENGTH_SHORT).show();
            }
        });
        subcategoriaAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(4),Toast.LENGTH_SHORT).show();

            }
        });
        btnimageprod1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 1;
            }
        });
        btnimageprod2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 2;
            }
        });
        btnimageprod3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());
                btn = 3;
            }
        });
        btnSalvarProd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("AE","imagemProd1 "+imagemProd1+" e imagemProd2 "+imagemProd2+" e imagemProd3"+imagemProd3);
                if (imagemProd1 != null)
                {
                    listDeletCam.add(listcam.get(0));
                    listDeletCod.add(listcod.get(0));
                    listUri.add(imagemProd1);
                    Log.e("AE","listDelCod SIZE "+listDeletCod.size()+" e dados "+listDeletCod);
                    Log.e("AE","listDelCam SIZE "+listDeletCam.size()+" e dados "+listDeletCam);
                    Log.e("AE","listUri SIZE "+listUri.size()+" e dados "+ listUri);
                    Log.e("AE","listcod SIZE "+listcod.size()+" e dados "+listcod);
                    Log.e("AE","listcam SIZE "+listcam.size()+" e dados "+listcam);
                }
                if (imagemProd2 != null)
                {
                    listUri.add(imagemProd2);
                    listDeletCam.add(listcam.get(1));
                    listDeletCod.add(listcod.get(1));
                    Log.e("AE","listDelCod SIZE "+listDeletCod.size()+" e dados "+listDeletCod);
                    Log.e("AE","listDelCam SIZE "+listDeletCam.size()+" e dados "+listDeletCam);
                    Log.e("AE","listUri SIZE "+listUri.size()+" e dados "+ listUri);
                    Log.e("AE","listcod SIZE "+listcod.size()+" e dados "+listcod);
                    Log.e("AE","listcam SIZE "+listcam.size()+" e dados "+listcam);
                }
                if (imagemProd3 != null)
                {
                    listUri.add(imagemProd3);
                    listDeletCam.add(listcam.get(2));
                    listDeletCod.add(listcod.get(2));
                    Log.e("AE","listDelCod SIZE "+listDeletCod.size()+" e dados "+listDeletCod);
                    Log.e("AE","listDelCam SIZE "+listDeletCam.size()+" e dados "+listDeletCam);
                    Log.e("AE","listUri SIZE "+listUri.size()+" e dados "+ listUri);
                    Log.e("AE","listcod SIZE "+listcod.size()+" e dados "+listcod);
                    Log.e("AE","listcam SIZE "+listcam.size()+" e dados "+listcam);
                }
                for (int i = 0; i < listDeletCam.size(); i++)
                {
                  listcam.remove(listDeletCam.get(i));
                  listcod.remove(listDeletCod.get(i));
                    Log.e("AE","FORI listcod SIZE "+listcod.size()+" e dados "+listcod);
                    Log.e("AE","FORI listcam SIZE "+listcam.size()+" e dados "+listcam);
                }
                /*
                for (int i = 0; i < listDelet.size(); i++)
                {
                 deletarImagens(listDelet.get(i),1);
                }
                salvarImagens(listUri);
                 */
            }
        });
    }
// salvar alterações mapeando os produtos
    private void salvaralteracoes(String dataIni, String dataFinal, String diasrestantes, final String meuUid,ArrayList<String> caminho,ArrayList<String> codimag)
    {
        btnSalvarProd.setText("Alterando anuncio...");
        Produto produto = new Produto(edtDescrprod.getText().toString(),nomeProd,preco,categoria,subcategoria,codimag,tipo,dataIni,dataFinal,diasrestantes,empresa,meuUid,caminho);
        Map<String, Object> postprod= produto.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
       childUpdates.put("/produtos/" + subcategoria+"/"+meuUid+"/"+nomeProd, postprod);
        ref.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ref.child("minhascategorias").child(meuUid).child(subcategoria).setValue("true");
                btnSalvarProd.setText("Anuncio alterado com sucesso!");
                Toast.makeText(AnuncioEditar.this,"Anuncio alterado com sucesso!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(AnuncioEditar.this, TabAdapterAnunc.class));
                finish();
            }
        });

    }
    private void deletarImagens(String caminho, final int del)
    {
        StorageReference desertRef = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(caminho);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
               if (del == 0)
               {
                   deletarAnuncio();
               }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Toast.makeText(AnuncioEditar.this,"Imagem já foi excluida!",Toast.LENGTH_LONG).show();
                btnSalvarProd.setText("Salvar Alterações!");
            }
        });
    }
    private void deletarAnuncio()
    {
        ref.child("classificacao").child(categoria).child(ConfiguracaoFirebase.getUID()).child(nomeProd).removeValue();
        ref.child("produtos").child(subcategoria).child(ConfiguracaoFirebase.getUID()).child(nomeProd).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                Toast.makeText(AnuncioEditar.this,"Anuncio removido com sucesso",Toast.LENGTH_LONG).show();
                btnSalvarProd.setText("Excluido com sucesso!");
                ref.child("minhascategorias").child(ConfiguracaoFirebase.getUID()).child(subcategoria).removeValue();
                startActivity(new Intent(AnuncioEditar.this, TabAdapterAnunc.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(AnuncioEditar.this,"Erro na conexão!",Toast.LENGTH_LONG).show();
                btnSalvarProd.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_anuncio, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuremover_anunc:
                caixadialogo();
                return true;
            case R.id.menuajuda_anunc:
                showAlertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //exibe um alert dialog para exclusão de anuncio
    public void caixadialogo()
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AnuncioEditar.this);
        builder.setTitle("Excluir Anuncio");
        builder.setMessage("Deseja excluir este anuncio da sua lista?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {

                btnSalvarProd.setText("Excluindo anuncio...");
                btnSalvarProd.setEnabled(false);
                for (int i = 0; i < listcod.size(); i++)
                {
                 deletarImagens(listcam.get(i),i);
                }


            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alerta = builder.create();
        alerta.show();
    }
    // tratamento de cliques nos campos que não podem ser editados
    public String tratarCliques(int tipo)
    {
        String resp = "";
        switch (tipo)
        {
            case 0:
                resp = "Voce não pode editar o nome do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 1:
                resp = "Voce não pode editar o preço do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 2:
                resp = "Voce não pode editar o tipo do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 3:
                resp = "Voce não pode editar o categoria do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 4:
                resp = "Voce não pode editar o subcategoria do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 5:
                resp = "Voce não pode alterar a imagem!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
        }
        return resp;
    }

    private void showAlertDialog()
    {
        FragmentManager fm = getSupportFragmentManager();
        AjudaAnuncioEditar alertDialog = AjudaAnuncioEditar.novaestancia("Dicas do aplicativo","1 - Clique em qualquer imagem e altere a qualquer momento!","2 - Voce pode alterar apenas a Descrição do produto e sua validade","3 - Para deletar Clique no icone - e delete seu anuncio,mas lembre-se , seu anuncio não poderá ser mais recuperado","Entendi");
        alertDialog.show(fm, "fragment_alert");
    }

    public void criarImagens(Bitmap bitmap, Uri uri)
    {
        Log.i("AE_btn", String.valueOf(btn));
        if (btn == 1)
        {
            btnimageprod1.setImageBitmap(bitmap);
            imagemProd1 = uri;
            Log.i("AE_Foto_imagemProd1", String.valueOf(imagemProd1));
        }
        else if (btn == 2)
        {
            btnimageprod2.setImageBitmap(bitmap);
            imagemProd2 = uri;
            Log.i("AE_Foto_imagemProd2", String.valueOf(imagemProd2));
        }
        else if (btn == 3)
        {
            btnimageprod3.setImageBitmap(bitmap);
            imagemProd3 = uri;
            Log.i("AE_Foto_imagemProd3", String.valueOf(imagemProd3));
        }

    }

    @Override
    public void onPickResult(PickResult pickResult)
    {
        if (pickResult.getError() == null)
        {
            //btnimageprod.setImageBitmap(pickResult.getBitmap());
            //imagemProd = pickResult.getUri();
            criarImagens(pickResult.getBitmap(),pickResult.getUri());
        } else
        {
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public String transformarData(Date date)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        String dtTrans = sdf2.format(date);
        return dtTrans;
    }
    public String diasRestantes(String dataIni,String dataFin) throws ParseException
    {
        SimpleDateFormat datas = new SimpleDateFormat("dd/MM/yyyy");
        Date dateI;
        Date dateF;
        dateI = datas.parse(dataIni);
        dateF = datas.parse(dataFin);
        long diferenca = Math.abs(dateI.getTime() - dateF.getTime());
        long diferencaDatas = diferenca / (24 * 60 * 60 * 1000);
        String diferencadias = Long.toString(diferencaDatas);
        return diferencadias;
    }
    private void salvarImagens(final ArrayList<Uri> uri)
    {
        for (int i = 0; i < uri.size(); i++)
        {
            Base64Custom base64Custom = new Base64Custom();
            final String codImag = base64Custom.codificarBase64(uri.get(i).getLastPathSegment());
            listcod.add(codImag);
            Log.e("NA_IMG", "Codigo IMG salvar = "+String.valueOf(codImag));
            StorageReference refprod = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(codImag);
            btnSalvarProd.setClickable(false);
            btnSalvarProd.setText("Salvando Produto ...");
            final int finalI = i;
            refprod.putFile(uri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    listcam.add(String.valueOf(taskSnapshot.getMetadata().getDownloadUrl()));
                    btnSalvarProd.setText("Salvando "+ (finalI+1) + " Imagem de "+uri.size());
                    Log.e("NA_IMG", "Lista Codigo = "+listcam.size());
                    if (listcam.size() == 3)
                    {
                        try
                        {
                            salvaralteracoes(datas.get(0),datas.get(1),diasRestantes(datas.get(0),datas.get(1)),ConfiguracaoFirebase.getUID(),listcam,listcod);
                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getApplicationContext(),"Erro no upload da Primeira Imagem",Toast.LENGTH_SHORT).show();
                    btnSalvarProd.setText("Imagem "+ finalI +" Erro!");
                }
            });

        }

    }

}
