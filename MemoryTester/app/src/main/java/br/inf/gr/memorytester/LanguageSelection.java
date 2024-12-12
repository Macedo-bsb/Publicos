package br.inf.gr.memorytester;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LanguageSelection extends DialogFragment {
    private Spinner langSelection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_lang_selection, container, false);
        langSelection = view.findViewById(R.id.langSelection);

        langSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String selectedLanguage = adapterView.getSelectedItem().toString();
                switch (position) {
                    case 1:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_GERMAN);
                        restartApp();
                        break;
                    case 2:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_ENGLISH);
                        restartApp();
                        break;
                    case 3:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_ARABIC);
                        restartApp();
                        break;
                    case 4:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_SPANISH);
                        restartApp();
                        break;
                    case 5:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_FRENCH);
                        restartApp();
                        break;
                    case 6:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_ITALIAN);
                        restartApp();
                        break;
                    case 7:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_PORTUGUESE);
                        restartApp();
                        break;
                    case 8:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_CHINESE);
                        restartApp();
                        break;
                    case 9:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_JAPANESE);
                        restartApp();
                        break;
                    case 10:
                        MainApplication.localeManager.setNewLocale(getContext(), LocaleManager.LANGUAGE_RUSSIAN);
                        restartApp();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Nada a fazer aqui
            }
        });

        return view;
    }

    private void restartApp() {
        if (getActivity() != null) {
            Intent intent = getActivity().getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
