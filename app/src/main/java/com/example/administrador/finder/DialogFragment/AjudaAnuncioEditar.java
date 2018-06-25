package com.example.administrador.finder.DialogFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.example.administrador.finder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjudaAnuncioEditar extends DialogFragment
{
    private Button btnOK;
    private TextView lbl1,lbl2,lbl3,lbl;
    public AjudaAnuncioEditar()
    {
    }
    public static AjudaAnuncioEditar novaestancia(String titulo,String lbl1 , String lbl2 , String lbl3 , String button)
    {
        AjudaAnuncioEditar frag = new AjudaAnuncioEditar();
        Bundle args = new Bundle();
        args.putString("titulo",titulo);
        args.putString("lbl1", lbl1);
        args.putString("lbl2", lbl2);
        args.putString("lbl3", lbl3);
        args.putString("button", button);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_ajuda_ae, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        btnOK = (Button)view.findViewById(R.id.btnok);
        lbl = (TextView) view.findViewById(R.id.lbl);
        lbl1 = (TextView) view.findViewById(R.id.lbl1);
        lbl2 = (TextView) view.findViewById(R.id.lbl2);
        lbl3 = (TextView) view.findViewById(R.id.lbl3);

        lbl.setText(getArguments().getString("titulo"));
        lbl1.setText(getArguments().getString("lbl1"));
        lbl2.setText(getArguments().getString("lbl2"));
        lbl3.setText(getArguments().getString("lbl3"));
        btnOK.setText(getArguments().getString("button"));


        getDialog().setTitle("Alteração de email");
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               dismiss();
            }
        });
    }
}