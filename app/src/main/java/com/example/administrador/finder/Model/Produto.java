package com.example.administrador.finder.Model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando Silveira on 30/06/2017.
 */

public class Produto
{
    public String descricao ;
    public String nome;
    public String preco;
    public String categoria;
    public String subcategoria;
    public String tipo;
    public ArrayList<String> codimg1;
    public ArrayList<String> caminhoimg;
    public String dataFinal;
    public String dataInicial;
    public String diasRestantes;
    public String empresa;
    public String meuUid;
    public float classificacao = 0;
    public float qtduserclass = 0;

    public Map<String, Boolean> produt = new HashMap<>();

    public Produto(){ }

    public Produto(String descricao, String nome, String preco, String categoria, String subcategoria, ArrayList<String> codimg, String tipo, String dataInicial, String dataFinal, String diasRestantes, String empresa, String meuUid, ArrayList<String> caminhoimg)
    {
        this.descricao = descricao;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.codimg1 = codimg;
        this.caminhoimg = caminhoimg;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.diasRestantes = diasRestantes;
        this.tipo = tipo;
        this.empresa = empresa;
        this.meuUid = meuUid;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("descricao", descricao);
        result.put("preco", preco);
        result.put("categoria", categoria);
        result.put("subcategoria", subcategoria);
        result.put("codimg1", codimg1);
        result.put("dataInicial", dataInicial);
        result.put("dataFinal", dataFinal);
        result.put("diasRestantes", diasRestantes);
        result.put("tipo",tipo);
        result.put("empresa",empresa);
        result.put("meuUid",meuUid);
        result.put("caminhoimg",caminhoimg);
        result.put("classificacao",classificacao);
        result.put("qtduserclass",qtduserclass);
        return result;
    }
}