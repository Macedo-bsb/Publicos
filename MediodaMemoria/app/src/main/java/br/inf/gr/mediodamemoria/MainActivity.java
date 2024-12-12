package br.inf.gr.mediodamemoria;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AdManager.AdListener{
    private static final String PREFS_NAME = "MemoryGamePrefs";
    private static final String PREF_DIFFICULTY_LEVEL = "DifficultyLevel";
    private static final String PREF_HIGH_SCORE = "HighScore";
    public static LocaleManager localeManager;
    //private TextView instructionText;
    private TextView scoreText;
    private TextView highScoreText;
    private TextView countdownText;
    private GridLayout optionsGridLayout;
    private Button startButton;
    //private Button restartButton;
    private FrameLayout frameLayout;
    private ImageButton restartButton;
    //private ImageView imageView;
    private TextView imageView;
    private Spinner difficultySpinner;
    private LinearLayout dificultLinearLayout;
    private List<Shape> sequence;
    private int currentLevel;
    private int score;
    private int highScore;
    private int difficultyLevel;
    private Handler handler;
    private List<Shape> userSelection;
    private ImageButton clearButton;
    private Random random;
    private ExecutorService executorService;
    private SharedPreferences sharedPreferences;
    private LanguageSelection languageSelection;
    private AdManager adManager;
    private boolean isRewarded = false;
    private String currentFunctionality = "";
    private String funcaoCount = "Start";
    private static final String TAG = "MainActivity";
    private final int[][] colorLevels = {
            {R.color.green},                             // Nível 1
            {R.color.yellow, R.color.green},             // Nível 2
            {R.color.yellow, R.color.green, R.color.blue},              // Nível 3
            {R.color.yellow, R.color.green, R.color.blue, R.color.red}, //Nível 4
            {R.color.yellow, R.color.green, R.color.blue, R.color.red, R.color.grey} // Nível 5
    };

    private final int[][] shapeLevels = {
            {R.drawable.circulo_shape, R.drawable.quadrado_shape, R.drawable.triangulo_shape},
            {R.drawable.circulo_shape, R.drawable.quadrado_shape, R.drawable.triangulo_shape},
            {R.drawable.circulo_shape, R.drawable.quadrado_shape, R.drawable.triangulo_shape},
            {R.drawable.circulo_shape, R.drawable.quadrado_shape, R.drawable.triangulo_shape, R.drawable.estrela_shape},
            {R.drawable.circulo_shape, R.drawable.quadrado_shape, R.drawable.triangulo_shape, R.drawable.estrela_shape, R.drawable.octogono_shape}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.localeManager.setLocale(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Inicializar o Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "Mobile Ads SDK initialized.");
            adManager = new AdManager(this, this);
            adManager.loadRewardedAd(); // Carregar o anúncio ao inicializar
        });
        //instructionText = findViewById(R.id.instructionText);
        scoreText = findViewById(R.id.scoreText);
        highScoreText = findViewById(R.id.highScoreText);
        clearButton = findViewById(R.id.clear_button);
        countdownText = findViewById(R.id.countdownText);
        optionsGridLayout = findViewById(R.id.optionsGridLayout);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        frameLayout = findViewById(R.id.frame_layout);
        imageView = findViewById(R.id.play_video);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        dificultLinearLayout = findViewById(R.id.dificult);
        handler = new Handler(Looper.getMainLooper());
        random = new Random();
        executorService = Executors.newSingleThreadExecutor();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.difficulty_levels, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultySpinner.setAdapter(adapter);
        // Criar e carregar um anúncio de teste
        AdRequest adRequest = new AdRequest.Builder().build();

        Log.d(TAG, "Ad Request: " + adRequest);
        sequence = new ArrayList<>();
        userSelection = new ArrayList<>();
        currentLevel = 1;
        score = 0;
        highScore = sharedPreferences.getInt(PREF_HIGH_SCORE, 0);

        highScoreText.setText(getString(R.string.record) + highScore);
        startButton.setOnClickListener(v -> startGame());
        restartButton.setOnClickListener(v -> resetGame());
        clearButton.setOnClickListener(v -> clearScore());
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficultyLevel = position;
                saveDifficultyLevel(position); // Salva o nível de dificuldade selecionado

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficultyLevel = 0; // Default to easy if nothing is selected
            }
        });

        frameLayout.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        dificultLinearLayout.setVisibility(View.VISIBLE);
        // Configura o texto sobreposto programaticamente, se necessário

        // Carrega e seleciona o nível de dificuldade salvo
        int savedDifficultyLevel = loadDifficultyLevel();
        difficultySpinner.setSelection(savedDifficultyLevel);
    }

    private void showLanguageDialog() {
        languageSelection = new LanguageSelection();
        languageSelection.show(getSupportFragmentManager(),"languageSelection_tag");
    }

    private void startGame() {
        funcaoCount = "Start";
        startButton.setVisibility(View.GONE);
        dificultLinearLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        scoreText.setText(getString(R.string.pontuacao) + "0");
        score = 0;
        currentLevel = 1;
        showCountdownDialog();
    }

    private void showCountdownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);
        TextView countdownText = dialogView.findViewById(R.id.countdownText);
        builder.setView(dialogView);

        AlertDialog countdownDialog = builder.create();
        countdownDialog.show();

        handler.postDelayed(() -> {
            countdownText.setText("2");
            handler.postDelayed(() -> {
                countdownText.setText("1");
                handler.postDelayed(() -> {
                    countdownDialog.dismiss();
                    if (funcaoCount.equals("Start")) {
                        startNewLevel();
                    } else {
                        showSequenceDialogCorrect();
                    }

                }, 1000);
            }, 1000);
        }, 1000);
    }

    private void startNewLevel() {
        //instructionText.setText(getString(R.string.message));
        generateSequence();
    }

    private void generateSequence() {
        executorService.execute(() -> {
            List<Shape> newSequence = new ArrayList<>();

            for (int i = 0; i < currentLevel; i++) {
                Shape shape;
                do {
                    shape = new Shape(randomShape(), randomColor());
                } while (!newSequence.isEmpty() && shape.equals(newSequence.get(newSequence.size() - 1)));
                newSequence.add(shape);
            }

            handler.post(() -> {
                sequence.clear();
                sequence.addAll(newSequence);
                showSequenceDialog();
            });
        });
    }

    private void showSequenceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_challenge, null);
        ImageView shapeView = dialogView.findViewById(R.id.shapeView);
        builder.setView(dialogView);

        AlertDialog shapeDialog = builder.create();
        shapeDialog.show();

        displayNextShape(shapeDialog, shapeView, 0);
    }

    private void displayNextShape(AlertDialog shapeDialog, ImageView shapeView, int index) {
        if (index < sequence.size()) {
            Shape currentShape = sequence.get(index);
            shapeView.setImageResource(currentShape.getShape());
            shapeView.setColorFilter(getResources().getColor(currentShape.getColor(), null));

            handler.postDelayed(() -> {
                displayNextShape(shapeDialog, shapeView, index + 1);
            }, 1500); // Tempo que cada forma é exibida
        } else {
            shapeDialog.dismiss();
            handler.postDelayed(() -> {
                //instructionText.setVisibility(View.VISIBLE);
                scoreText.setVisibility(View.VISIBLE);
                highScoreText.setVisibility(View.VISIBLE);
                optionsGridLayout.setVisibility(View.VISIBLE);
                displayOptions();
            }, 500); // Pequeno atraso antes de mostrar as opções
        }
    }

    private View createShapeView(Shape shape, int sizeInPx) {
        LinearLayout container = new LinearLayout(this);
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(new GridLayout.LayoutParams());

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(shape.getShape());
        imageView.setColorFilter(getResources().getColor(shape.getColor(), null));
        imageView.setTag(shape); // Set the tag on the ImageView

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
        params.setMargins(16, 16, 16, 16); // Aumenta as margens para adicionar mais espaço entre os shapes
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.shape_selector); // Set the selector as background
        imageView.setPadding(10, 10, 10, 10); // Adiciona padding para garantir que a borda seja visível
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Mantém a proporção da imagem
        imageView.setAdjustViewBounds(true); // Ajusta as bordas para manter a proporção
        imageView.setClipToOutline(true); // Garante que o conteúdo do ImageView seja recortado de acordo com o outline

        container.addView(imageView);
        container.setTag(shape); // Set the tag on the container
        return container;
    }
    private void displayOptions() {
        optionsGridLayout.removeAllViews();
        List<Shape> options = generateAllShapesAndColors();
        Collections.shuffle(options); // Embaralha as opções

        int columns = 4; // Número de colunas
        int rows = (int) Math.ceil((double) options.size() / columns);

        optionsGridLayout.post(() -> {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int cellSize = (int) ((screenWidth / (columns + 1)) * 0.8); // Reduz o tamanho das células para 80%

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int index = i * columns + j;
                    if (index < options.size()) {
                        View shapeView = createShapeView(options.get(index), cellSize);
                        shapeView.setOnClickListener(view -> {
                            Shape selectedShape = (Shape) view.getTag();
                            if (selectedShape != null) {
                                userSelection.add(selectedShape);
                                checkUserSelection();
                            }
                        });
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = cellSize;
                        params.height = cellSize;
                        params.setMargins(16, 16, 16, 16); // Ajuste de margens conforme necessário
                        optionsGridLayout.addView(shapeView, params);
                    }
                }
            }
        });
    }

    private List<Shape> generateAllShapesAndColors() {
        List<Shape> allShapesAndColors = new ArrayList<>();
        int[] shapes = shapeLevels[difficultyLevel];
        int[] colors = colorLevels[difficultyLevel];

        for (int shape : shapes) {
            for (int color : colors) {
                allShapesAndColors.add(new Shape(shape, color));
            }
        }
        return allShapesAndColors;
    }

    private void checkUserSelection() {
        if (userSelection.size() == sequence.size()) {
            boolean isCorrect = true;
            for (int i = 0; i < sequence.size(); i++) {
                Shape userShape = userSelection.get(i);
                Shape sequenceShape = sequence.get(i);

                if (userShape == null || sequenceShape == null || !userShape.equals(sequenceShape)) {
                    isCorrect = false;
                    break;
                }
            }
            showResult(isCorrect);
            if (isCorrect) {
                score++;
                scoreText.setText(getString(R.string.pontuacao) + score);
                userSelection.clear();
                currentLevel++;
                handler.postDelayed(() -> {
                    //instructionText.setText("");
                    optionsGridLayout.removeAllViews();
                    startNewLevel();
                }, 2000); // Tempo para exibir a mensagem antes de iniciar uma nova sequência
            } else {
                updateHighScore();
                handler.postDelayed(this::askRetry, 2000); // Exibir o botão Reiniciar após a mensagem de erro
                optionsGridLayout.removeAllViews(); // Limpa as opções quando o jogo termina
            }
        }
    }

    private void showResult(boolean isCorrect) {
        optionsGridLayout.removeAllViews();
        //instructionText.setVisibility(View.VISIBLE);
        if (isCorrect) {
            showCertoouErrado(getString(R.string.correto));
            playSound(R.raw.correct_sound); // Toca som de acerto
            //Toast.makeText(this, getString(R.string.correto), Toast.LENGTH_SHORT).show();
        } else {
            showCertoouErrado(getString(R.string.errado));
            playSound(R.raw.wrong_sound); // Toca som de erro
            //Toast.makeText(this, getString(R.string.errado), Toast.LENGTH_SHORT).show();
        }

        handler.postDelayed(() -> {
        }, 3000); // Tempo para exibir a mensagem antes de iniciar uma nova sequência ou resetar o jogo
    }

    private void askRetry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_retry, null);
        builder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.dialog_title);
        TextView message = dialogView.findViewById(R.id.dialog_message);
        ImageButton buttonYes = dialogView.findViewById(R.id.button_yes);
        Button buttonNo = dialogView.findViewById(R.id.button_no);

        AlertDialog alert = builder.create();

        buttonYes.setOnClickListener(v -> {
            Log.d(TAG, "Show video button 2 clicked.");
            currentFunctionality = "Functionality2";
            if (adManager.isAdLoaded()) {
                adManager.showRewardedVideo(MainActivity.this);
            } else {
                Toast.makeText(this, "Propaganda não disponível ainda!", Toast.LENGTH_SHORT).show();
                continueExecution();
            }
            alert.dismiss();
        });

        buttonNo.setOnClickListener(v -> {
            frameLayout.setVisibility(View.VISIBLE);
            restartButton.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            alert.dismiss();
        });

        alert.show();
    }

    private void showSequenceDialogCorrect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_challenge, null);
        ImageView shapeView = dialogView.findViewById(R.id.shapeView);
        builder.setView(dialogView);

        AlertDialog shapeDialog = builder.create();
        shapeDialog.show();

        displayCorrectShape(shapeDialog, shapeView, 0);
    }

    private void displayCorrectShape(AlertDialog shapeDialog, ImageView shapeView, int index) {
        if (index < sequence.size()) {
            Shape currentShape = sequence.get(index);
            shapeView.setImageResource(currentShape.getShape());
            shapeView.setColorFilter(getResources().getColor(currentShape.getColor(), null));

            handler.postDelayed(() -> {
                displayCorrectShape(shapeDialog, shapeView, index + 1);
            }, 1500); // Tempo que cada forma é exibida
        } else {
            shapeDialog.dismiss();
            optionsGridLayout.removeAllViews();
            displayOptions();
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreText.setText(getString(R.string.record) + highScore);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREF_HIGH_SCORE, highScore);
            editor.apply();
        }
    }

    private void saveDifficultyLevel(int level) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_DIFFICULTY_LEVEL, level);
        editor.apply();
    }

    private int loadDifficultyLevel() {
        return sharedPreferences.getInt(PREF_DIFFICULTY_LEVEL, 0); // Default to easy (0) if not set
    }

    private int randomShape() {
        int[] shapes = shapeLevels[difficultyLevel];
        return shapes[random.nextInt(shapes.length)];
    }

    private int randomColor() {
        int[] colors = colorLevels[difficultyLevel];
        return colors[random.nextInt(colors.length)];
    }

    private void resetGame() {

        Log.d(TAG, "Show video button 1 clicked.");
        currentFunctionality = "Functionality1";
        if (adManager.isAdLoaded()) {
            adManager.showRewardedVideo(MainActivity.this);
        } else {
            Toast.makeText(this, "Propaganda não disponível ainda!", Toast.LENGTH_SHORT).show();
            continueExecution();
        }

    }

    private void clearScore() {
        highScoreText.setText(getString(R.string.record) + 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_HIGH_SCORE, 0);
        editor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_lang) {
            showLanguageDialog();
            return true;
        }

        if (id == R.id.action_about) {
            Intent profileIntent = new Intent(getApplicationContext(),
                    AboutActivity.class);
            startActivity(profileIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MainApplication.localeManager.setLocale(base));
    }

    private void showCertoouErrado(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);
        TextView certoOuErradoText = dialogView.findViewById(R.id.countdownText);
        builder.setView(dialogView);

        AlertDialog certoOuErrado = builder.create();
        certoOuErradoText.setText(message);
        certoOuErrado.show();

        handler.postDelayed(() -> {
            handler.postDelayed(() -> {
                handler.postDelayed(() -> {
                    certoOuErrado.dismiss();
                }, 1000);
            }, 1000);
        }, 1000);
    }

    @Override
    public void onAdLoaded() {
        Log.d(TAG, "Ad loaded.");
        //Toast.makeText(this, "Ad loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdFailedToLoad(LoadAdError loadAdError) {
        Log.d(TAG, "Ad failed to load: " + loadAdError.getMessage());
        //Toast.makeText(this, "Ad failed to load", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserEarnedReward(boolean rewarded) {
        Log.d(TAG, "User earned reward: " + rewarded);
        isRewarded = rewarded;
        updateResultText();
    }

    @Override
    public void onAdNotLoaded() {
        Log.d(TAG, "Ad not loaded yet.");
        //Toast.makeText(this, "Ad not loaded yet", Toast.LENGTH_SHORT).show();
        // Tentar carregar o anúncio novamente
        adManager.loadRewardedAd();
        continueExecution();
    }

    @Override
    public void onAdFailedToShow() {
        Log.d(TAG, "Ad failed to show.");
        //Toast.makeText(this, "Ad failed to show", Toast.LENGTH_SHORT).show();
        // Continue a execução se o anúncio falhar ao mostrar
        continueExecution();
    }

    @Override
    public void onAdDismissed() {
        Log.d(TAG, "Ad dismissed.");
        //Toast.makeText(this, "Ad dismissed", Toast.LENGTH_SHORT).show();
        // Continue a execução após o anúncio ser dispensado
        continueExecution();
    }

    private void updateResultText() {
        //resultText.setText("Rewarded: " + isRewarded);
    }

    private void continueExecution() {
        // Lógica para continuar a execução após o anúncio ser dispensado ou falhar ao mostrar
        Log.d(TAG, "Continuing execution.");
        if (currentFunctionality.equals("Functionality1")) {
            handleFunctionality1();
        } else if (currentFunctionality.equals("Functionality2")) {
            handleFunctionality2();
        }
    }

    private void handleFunctionality2() {
        // Lógica específica para a funcionalidade 1
        Log.d(TAG, "Handling Functionality 2.");
        userSelection.clear();
        funcaoCount="Retry";
        showCountdownDialog();
        // Sua lógica aqui
    }

    private void handleFunctionality1() {
        // Lógica específica para a funcionalidade 1
        Log.d(TAG, "Handling Functionality 1.");
        score = 0;
        currentLevel = 1;
        scoreText.setText(getString(R.string.pontuacao) + "0");
        frameLayout.setVisibility(View.GONE);
        restartButton.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        startButton.setVisibility(View.VISIBLE);
        dificultLinearLayout.setVisibility(View.VISIBLE);
        sequence.clear();
        userSelection.clear();
        //instructionText.setText("");
        // Sua lógica aqui
    }
    private void playSound(int soundResource) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }
}
