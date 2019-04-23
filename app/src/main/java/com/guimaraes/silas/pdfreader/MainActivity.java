package com.guimaraes.silas.pdfreader;

import java.util.HashMap;
import java.util.Locale;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class MainActivity extends Activity implements
        TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private Button startRead;
    private TextView content;
    private TextView numPage;
    private EditText path;
    private EditText starterPage;
    HashMap<String, String> map = new HashMap<>();

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);
        content = findViewById(R.id.content);
        numPage = findViewById(R.id.numPage);
        startRead = findViewById(R.id.startRead);
        path = findViewById(R.id.path);
        starterPage = findViewById(R.id.starterPage);


        startRead.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    if(!tts.isSpeaking()){
                        System.out.println(path.getText().toString());
                        PdfReader pdfReader = new PdfReader(path.getText().toString());
                        int n = pdfReader.getNumberOfPages();
                        for(int i=Integer.valueOf(starterPage.getText().toString()); i<=n; i++){
                            String parsedText = PdfTextExtractor.getTextFromPage(pdfReader, i).trim()+"\n";
                            parsedText = parsedText.replace("_", " ");
                            content.setText(parsedText);
                            numPage.setText(String.valueOf(i-1));
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(i));

                            new Thread(){
                                @Override
                                public  void run(){
                                    tts.speak(content.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);
                                }
                            }.start();
                        }
                        pdfReader.close();
                    }else {
                        tts.stop();
                    }

                }catch (Exception e){
                    System.out.println("Erro: "+e);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Idioma não suportado");
            } else {
                startRead.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Falha no método 'onInit()' ");
        }
    }
}