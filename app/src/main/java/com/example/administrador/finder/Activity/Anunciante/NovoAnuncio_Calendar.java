package com.example.administrador.finder.Activity.Anunciante;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.administrador.finder.DialogFragment.AjudaAnuncioEditar;
import com.example.administrador.finder.R;
import com.squareup.timessquare.CalendarPickerView;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NovoAnuncio_Calendar extends AppCompatActivity
{
    private Button btnCal;
    private CalendarView calview;
    private  CalendarPickerView calendar_view;

    private String subcategoria = "";
    private String nome = "";
    private String categoria = "";
    private String descricao= "";
    private String preco= "";
    private ArrayList<String> listcaminhoimg  = new ArrayList<>();
    private ArrayList<String> listCodImg  = new ArrayList<>();
    private String tipo = "";
    private String empresa = "";
    private String dataIni = "",dataFinal = "",diasrestantes = "";
    private int dias = 0;
    private String textovalidade  = "";
    private String[] validade = new String[] { "Selecione a Validade","1 dia", "2 dias"," 5 dias","uma semana", "10 dias ", "15 dias","20 dias", "25 dias", "1 mes" };
    private ArrayList<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio__calendar);
        btnCal = (Button) findViewById(R.id.btnSalvarCalendar);

        calendar_view = (CalendarPickerView) findViewById(R.id.calviewNA);
        Intent intent = getIntent();
        final Bundle dados = intent.getExtras();
        if (dados != null)
        {
            nome = dados.getString("nome");
            descricao = dados.getString("descricao");
            categoria = dados.getString("cat");
            subcategoria = dados.getString("sub");
            preco  = dados.getString("preco");
            empresa  = dados.getString("empres");

            listcaminhoimg = dados.getStringArrayList("listcam");
            listCodImg = dados.getStringArrayList("listcod");

            tipo = dados.getString("tipo");

            Log.i("NAC","nome "+nome);
            Log.i("NAC","descricao "+descricao);
            Log.i("NAC","cat "+categoria);
            Log.i("NAC","sub "+subcategoria);
            Log.i("NAC","preco "+preco);
            Log.i("NAC","tipo "+tipo);
            Log.i("NAC","empres "+empresa);
            Log.i("NAC","listcam "+listcaminhoimg);
            Log.i("NAC","listcod "+listCodImg);
        }

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

        btnCal.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("NAC", "tamanho "+ datas.size());

                if (datas.size() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Voce precisa selecionar a data inicial e a final do seu anuncio!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    try {
                    Log.i("NAC", "data final "+datas.get(1));
                    Log.i("NAC","data Inicial "+ datas.get(0));
                    Log.d("NAC","nome "+nome);
                    Log.d("NAC","descricao "+descricao);
                    Log.d("NAC","cat "+categoria);
                    Log.d("NAC","sub "+subcategoria);
                    Log.d("NAC","preco "+preco);
                    Log.d("NAC","tipo "+tipo);
                    Log.d("NAC","empres "+empresa);
                    Log.d("NAC","listcam "+listcaminhoimg);
                    Log.d("NAC","listcod "+listCodImg);
                        Intent intent = new Intent(getApplicationContext(),SalvarAnuncio.class);
                        Bundle dados = new Bundle();
                        dados.putString("nome",nome);
                        dados.putString("descricao",descricao);
                        dados.putString("cat",categoria);
                        dados.putString("sub",subcategoria);
                        dados.putString("preco",preco);
                        dados.putStringArrayList("listcam",listcaminhoimg);
                        dados.putStringArrayList("listcod",listCodImg);
                        dados.putString("dataIni",datas.get(0));
                        dados.putString("dataFin",datas.get(1));
                        dados.putString("diasRest",diasRestantes(datas.get(0),datas.get(1)));
                        dados.putString("tipo",tipo);
                        dados.putString("empres",empresa);
                        intent.putExtras(dados);
                        startActivity(intent);
                    } catch (ParseException e)
                    {
                        e.printStackTrace();
                    }


                }
            }
        });
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
    private void showAlertDialog() throws ParseException {
        FragmentManager fm = getSupportFragmentManager();
        AjudaAnuncioEditar alertDialog = AjudaAnuncioEditar.novaestancia("Reveja sua validade","Data inicial "+datas.get(0),"Data final "+datas.get(1),"Dias restantes "+ diasRestantes(datas.get(0),datas.get(1)),"Vou verificar");
        alertDialog.show(fm, "fragment_alert");
    }

}
