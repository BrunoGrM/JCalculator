package com.example.jcalculator.ui.notifications;

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

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private View root;
    TextView tv;
    TextView tvPresente;
    TextView txtVlPresente;
    TextView txtDesconto;
    EditText valorNominal;
    EditText taxa;
    EditText tempo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);

        tv = (TextView) root.findViewById(R.id.resultDesconto);
        txtVlPresente = (TextView) root.findViewById(R.id.txtVlPresente);
        txtDesconto = (TextView) root.findViewById(R.id.txtDesconto);
        tvPresente = (TextView) root.findViewById(R.id.resultVlPresente);

        valorNominal = (EditText) root.findViewById(R.id.vlNominalDesconto);
        taxa = (EditText) root.findViewById(R.id.taxaDesconto);
        tempo = (EditText) root.findViewById(R.id.tempoDesconto);

        taxa.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });
        tempo.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });

        Locale mLocale = new Locale("pt", "BR");
        valorNominal.addTextChangedListener(new MoneyTextWatcher(valorNominal, mLocale));

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

        final Button button = (Button) root.findViewById(R.id.calcDesconto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vlNominal = HomeFragment.replaceCharacters(valorNominal.getText().toString());
                String vlTaxa   = taxa.getText().toString();
                String vlTempo   = tempo.getText().toString();

                DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                try {
                    if(!vlNominal.matches("") || !vlTaxa.matches("") || !vlTempo.matches("")) {

                        if (Double.parseDouble(vlTaxa) <= 100) {

                            if(Double.parseDouble(vlTaxa) * Double.parseDouble(vlTempo) < 100){
                                Double descontoSimples = Double.parseDouble(vlNominal) * (Double.parseDouble(vlTaxa)/100) * Double.parseDouble(vlTempo);
                                try {
                                    txtVlPresente.setText("Valor presente");
                                    txtDesconto.setText("Desconto");
                                    tv.setText(String.valueOf(df.format(descontoSimples)));
                                    tvPresente.setText(df.format(Double.parseDouble(vlNominal) - descontoSimples));

                                    valorNominal.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    taxa.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    tempo.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            valorNominal.clearFocus();
                                            taxa.clearFocus();
                                            tempo.clearFocus();
                                        }
                                    }, 200);

                                } catch (NullPointerException e) {}

                            } else {
                                dialog.setTitle("Erro!")
                                        .setMessage("O Desconto não pode ser maior que o valor presente! Preencha novamente os dados!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialoginterface, int i) {
                                                taxa.requestFocus();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        } else {
                            dialog.setTitle("Erro!")
                                .setMessage("Taxa não pode ser maior que 100%!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        taxa.requestFocus();
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