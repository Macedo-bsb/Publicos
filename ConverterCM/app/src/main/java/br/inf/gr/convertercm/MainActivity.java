package br.inf.gr.convertercm;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText inputText, inputMorseCode; //passwordEditText;
    private TextView morseCodeTextView, decodedTextView;
    private Button convertToMorseButton, copyMorseButton, playMorseAudioButton, convertToTextButton;
    private static final Map<Character, String> TEXT_TO_MORSE = new HashMap<>();
    private static final Map<String, Character> MORSE_TO_TEXT = new HashMap<>();
    private static final int DOT_DURATION = 200; // Duração do ponto em milissegundos
    private static final int DASH_DURATION = DOT_DURATION * 3;
    private String senha = "minhaSenhaSecreta";
    private int keySize = 256; // Pode ser 128, 192, ou 256


    static {
        TEXT_TO_MORSE.put('A', ".-");
        TEXT_TO_MORSE.put('B', "-...");
        TEXT_TO_MORSE.put('C', "-.-.");
        TEXT_TO_MORSE.put('D', "-..");
        TEXT_TO_MORSE.put('E', ".");
        TEXT_TO_MORSE.put('F', "..-.");
        TEXT_TO_MORSE.put('G', "--.");
        TEXT_TO_MORSE.put('H', "....");
        TEXT_TO_MORSE.put('I', "..");
        TEXT_TO_MORSE.put('J', ".---");
        TEXT_TO_MORSE.put('K', "-.-");
        TEXT_TO_MORSE.put('L', ".-..");
        TEXT_TO_MORSE.put('M', "--");
        TEXT_TO_MORSE.put('N', "-.");
        TEXT_TO_MORSE.put('O', "---");
        TEXT_TO_MORSE.put('P', ".--.");
        TEXT_TO_MORSE.put('Q', "--.-");
        TEXT_TO_MORSE.put('R', ".-.");
        TEXT_TO_MORSE.put('S', "...");
        TEXT_TO_MORSE.put('T', "-");
        TEXT_TO_MORSE.put('U', "..-");
        TEXT_TO_MORSE.put('V', "...-");
        TEXT_TO_MORSE.put('W', ".--");
        TEXT_TO_MORSE.put('X', "-..-");
        TEXT_TO_MORSE.put('Y', "-.--");
        TEXT_TO_MORSE.put('Z', "--..");
        TEXT_TO_MORSE.put('1', ".----");
        TEXT_TO_MORSE.put('2', "..---");
        TEXT_TO_MORSE.put('3', "...--");
        TEXT_TO_MORSE.put('4', "....-");
        TEXT_TO_MORSE.put('5', ".....");
        TEXT_TO_MORSE.put('6', "-....");
        TEXT_TO_MORSE.put('7', "--...");
        TEXT_TO_MORSE.put('8', "---..");
        TEXT_TO_MORSE.put('9', "----.");
        TEXT_TO_MORSE.put('0', "-----");
        TEXT_TO_MORSE.put('.', ".-.-.-");
        TEXT_TO_MORSE.put(',', "--..--");
        TEXT_TO_MORSE.put('?', "..--..");
        TEXT_TO_MORSE.put('\'', ".----.");
        TEXT_TO_MORSE.put('!', "-.-.--");
        TEXT_TO_MORSE.put('/', "-..-.");
        TEXT_TO_MORSE.put('(', "-.--.");
        TEXT_TO_MORSE.put(')', "-.--.-");
        TEXT_TO_MORSE.put('&', ".-...");
        TEXT_TO_MORSE.put(':', "---...");
        TEXT_TO_MORSE.put(';', "-.-.-.");
        TEXT_TO_MORSE.put('=', "-...-");
        TEXT_TO_MORSE.put('+', ".-.-.");
        TEXT_TO_MORSE.put('-', "-....-");
        TEXT_TO_MORSE.put('_', "..--.-");
        TEXT_TO_MORSE.put('"', ".-..-.");
        TEXT_TO_MORSE.put('$', "...-..-");
        TEXT_TO_MORSE.put('@', ".--.-.");

        for (Map.Entry<Character, String> entry : TEXT_TO_MORSE.entrySet()) {
            MORSE_TO_TEXT.put(entry.getValue(), entry.getKey());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        inputMorseCode = findViewById(R.id.inputMorseCode);
        //passwordEditText = findViewById(R.id.passwordEditText);
        morseCodeTextView = findViewById(R.id.morseCodeTextView);
        decodedTextView = findViewById(R.id.decodedTextView);
        convertToMorseButton = findViewById(R.id.convertToMorseButton);
        copyMorseButton = findViewById(R.id.copyMorseButton);
        playMorseAudioButton = findViewById(R.id.playMorseAudioButton);
        convertToTextButton = findViewById(R.id.convertToTextButton);

        convertToMorseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString().toUpperCase();
                String textCript = null;
                try {
                    textCript = BouncyCastleTool.criptografar(text,"teste",keySize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Texto original : " + text + "\nTexto Cript: " + textCript);
                decodedTextView.setText(textCript);
                Toast.makeText(MainActivity.this, "Texto original : " + text + "\nTexto Cript: " + textCript, Toast.LENGTH_SHORT).show();
                StringBuilder morseCode = new StringBuilder();
                for (char c : text.toCharArray()) {
                    if (TEXT_TO_MORSE.containsKey(c)) {
                        morseCode.append(TEXT_TO_MORSE.get(c)).append(" ");
                    } else if (c == ' ') {
                        morseCode.append("  "); // Espaço entre palavras
                    }
                }
                morseCodeTextView.setText(morseCode.toString().trim());
            }
        });

        copyMorseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Morse Code", morseCodeTextView.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Código Morse copiado para a área de transferência", Toast.LENGTH_SHORT).show();
            }
        });

        playMorseAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String morseCode = morseCodeTextView.getText().toString();
                playMorseCode(morseCode);
            }
        });

        convertToTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String morseCode = inputMorseCode.getText().toString();
                try {
                    String textCript = BouncyCastleTool.descriptografar(morseCode,"teste",keySize);
                    System.out.println(textCript);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                StringBuilder decodedText = new StringBuilder();
                for (String morseChar : morseCode.split(" ")) {
                    if (MORSE_TO_TEXT.containsKey(morseChar)) {
                        decodedText.append(MORSE_TO_TEXT.get(morseChar));
                    } else if (morseChar.equals("")) {
                        decodedText.append(" "); // Espaço entre palavras
                    }
                }
                decodedTextView.setText(decodedText.toString());
            }
        });
    }

    private void playMorseCode(String morseCode) {
        int sampleRate = 8000;
        int numSamplesDot = sampleRate * DOT_DURATION / 1000;
        int numSamplesDash = sampleRate * DASH_DURATION / 1000;
        int numSamplesSpace = sampleRate * DOT_DURATION / 1000;

        double[] dotSound = new double[numSamplesDot];
        double[] dashSound = new double[numSamplesDash];
        double[] silence = new double[numSamplesSpace];
        byte[] dotSnd = new byte[2 * numSamplesDot];
        byte[] dashSnd = new byte[2 * numSamplesDash];
        byte[] silenceSnd = new byte[2 * numSamplesSpace];

        for (int i = 0; i < numSamplesDot; ++i) {
            dotSound[i] = Math.sin(2 * Math.PI * i / (sampleRate / 440));
        }
        for (int i = 0; i < numSamplesDash; ++i) {
            dashSound[i] = Math.sin(2 * Math.PI * i / (sampleRate / 440));
        }
        for (int i = 0; i < numSamplesSpace; ++i) {
            silence[i] = 0;
        }

        int idx = 0;
        for (final double dVal : dotSound) {
            final short val = (short) ((dVal * 32767));
            dotSnd[idx++] = (byte) (val & 0x00ff);
            dotSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        idx = 0;
        for (final double dVal : dashSound) {
            final short val = (short) ((dVal * 32767));
            dashSnd[idx++] = (byte) (val & 0x00ff);
            dashSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        idx = 0;
        for (final double dVal : silence) {
            final short val = (short) ((dVal * 32767));
            silenceSnd[idx++] = (byte) (val & 0x00ff);
            silenceSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                dotSnd.length + dashSnd.length + silenceSnd.length, AudioTrack.MODE_STREAM);

        audioTrack.play();

        for (char morseChar : morseCode.toCharArray()) {
            if (morseChar == '.') {
                audioTrack.write(dotSnd, 0, dotSnd.length);
            } else if (morseChar == '-') {
                audioTrack.write(dashSnd, 0, dashSnd.length);
            } else {
                audioTrack.write(silenceSnd, 0, silenceSnd.length);
            }
        }

        audioTrack.stop();
        audioTrack.release();
    }
}
