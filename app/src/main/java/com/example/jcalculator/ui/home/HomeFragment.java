package com.example.jcalculator.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.jcalculator.MoneyTextWatcher;
import com.example.jcalculator.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    TextView tv;
    TextView txtParcela;
    TextView txtJuros;
    TextView tvParcela;

    EditText capital;
    EditText juros;
    EditText tempo;
    InputMethodManager imm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        tv = (TextView) root.findViewById(R.id.resultJSimples);
        txtJuros = (TextView) root.findViewById(R.id.txtJuros);
        txtParcela = (TextView) root.findViewById(R.id.txtParcela);
        tvParcela = (TextView) root.findViewById(R.id.resultJSParcela);

        capital = (EditText) root.findViewById(R.id.capitalJSimples);
        juros = (EditText) root.findViewById(R.id.jurosJSimples);
        tempo = (EditText) root.findViewById(R.id.tempoJSimples);

        juros.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });
        tempo.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });

        Locale mLocale = new Locale("pt", "BR");
        capital.addTextChangedListener(new MoneyTextWatcher(capital, mLocale));

        return root;
    }

    public static String replaceCharacters(String s) {
        String s1 = s.replaceAll("\\s+", "");
        String s2 = s1.replace(".","");
        String s3 = s2.replace(",",".");
        String s4 = s3.replace("R","");
        String s5 = s4.replace("$","");
        return s5;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button button = (Button) root.findViewById(R.id.calcJSimples);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vlCapital = HomeFragment.replaceCharacters(capital.getText().toString());
                String vlJuros   = juros.getText().toString();
                String vlTempo   = tempo.getText().toString();

                DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                try {
                    if (!vlCapital.matches("") || !vlJuros.matches("") || !vlTempo.matches("")) {

                        if (Double.parseDouble(vlJuros) <= 100) {
                            Double jurosSimples = Double.parseDouble(vlCapital) * (Double.parseDouble(vlJuros) / 100) * Double.parseDouble(vlTempo);
                            Double parcelaSimples = jurosSimples / Double.parseDouble(vlTempo);

                            try {
                                tv.setText(String.valueOf(df.format(jurosSimples)));
                                txtJuros.setText("Total de juros");
                                txtParcela.setText("Juros mês");
                                tvParcela.setText(String.valueOf(df.format(parcelaSimples)));

                                capital.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                juros.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                tempo.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        capital.clearFocus();
                                        juros.clearFocus();
                                        tempo.clearFocus();
                                    }
                                }, 200);

                            } catch (NullPointerException e) {}

                        } else {
                            dialog.setTitle("Erro!")
                                .setMessage("Juros não pode ser maior que 100%!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        juros.requestFocus();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        }

                    } else {
                        dialog.setTitle("Erro!")
                            .setMessage("Preencha todos os campos!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    }

                } catch (NumberFormatException e) {
                    dialog.setTitle("Erro!")
                        .setMessage(e.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }
            }
        });
    }
}