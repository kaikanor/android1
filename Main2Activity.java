package com.example.myapplication11;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    private TextToSpeech mTextToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView textView = findViewById(R.id.textView_sentence);
        textView.setTextSize(25);
        Intent intent_main = getIntent();
        String result = intent_main.getStringExtra("result");
        if (result.equals("ok"))
        {
            textView.setTextSize(30);
            textView.setText("И это правильный ответ!");
            mTextToSpeech = init_speech();
            TextView textView1 = new TextView(this);

            final String sentence = intent_main.getStringExtra("sentence");
            textView1.setTextSize(27);
            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTextToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                }
            });
            textView1.setText(intent_main.getStringExtra("sentence"));
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.addView(textView1);
        }
        else
        {
            textView.setTextSize(30);
            textView.setText("Неправильно! =(");
            TextView textView_translate = new TextView(this);
            TextView textView_word = new TextView(this);
            TextView textView_sentence = new TextView(this);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.addView(textView_translate);
            linearLayout.addView(textView_word);
            linearLayout.addView(textView_sentence);

            String translate = "Вы выбрали слово <b>" + intent_main.getStringExtra("translate") + "</b>";
            String word = "На английский язык это слово переводится как <b>" + intent_main.getStringExtra("word") + "</b>";
            String sentence = "К примеру: <i>" + intent_main.getStringExtra("sentence") + "</i>";
            textView_translate.setText(Html.fromHtml(translate));
            textView_word.setText(Html.fromHtml(word));
            textView_sentence.setText(Html.fromHtml(sentence));
            textView_translate.setTextSize(28);
            textView_word.setTextSize(28);
            textView_sentence.setTextSize(26);
        }
        Button button = findViewById(R.id.button_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

    }
    private TextToSpeech init_speech()
    {
        return new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTextToSpeech.setSpeechRate(1);
                    //mTextToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }
}
