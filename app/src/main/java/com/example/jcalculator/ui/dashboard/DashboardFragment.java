package com.example.jcalculator.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.jcalculator.MoneyTextWatcher;
import com.example.jcalculator.R;
import com.example.jcalculator.ui.home.HomeFragment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private View root;
    TextView tv;
    TextView txtAcumulado;
    TextView txtMes;
    TextView txtJurosC;

    EditText capital;
    EditText juros;
    EditText tempo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tv = (TextView) root.findViewById(R.id.resultJurosC);

        txtJurosC = (TextView) root.findViewById(R.id.txtJurosC);
        txtAcumulado = (TextView) root.findViewById(R.id.txtAcumulado);
        txtMes = (TextView) root.findViewById(R.id.txtMes);

        capital = (EditText) root.findViewById(R.id.capitalJComposto);
        juros = (EditText) root.findViewById(R.id.jurosJComposto);
        tempo = (EditText) root.findViewById(R.id.tempoJComposto);

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

        Button button = (Button) root.findViewById(R.id.calcJComposto);
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
                            Double acumulado = 0.00;
                            Double jurosC = 0.00;

                            try {
                                txtMes.setText("Mês");
                                txtJurosC.setText("Juros");
                                txtAcumulado.setText("Acumulado");
                                tv.setText("");

                                for (int i=0; i <= Integer.parseInt(vlTempo); i++){
                                    acumulado = Double.parseDouble(vlCapital) * Math.pow((1 + (Double.parseDouble(vlJuros) / 100)), i);

                                    if(i == 0){
                                        tv.append("   "+i+"ª       R$ 0,00          "+df.format(acumulado)+"\n");

                                    } else {
                                        jurosC = (Double.parseDouble(vlCapital) * Math.pow((1 + (Double.parseDouble(vlJuros) / 100)), i-1) * (Double.parseDouble(vlJuros) / 100));
                                        tv.append("   "+i+"ª       "+df.format(jurosC)+"          "+df.format(acumulado)+"\n");
                                    }
                                }

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
                            .setMessage("Preencha todos os campos!")
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