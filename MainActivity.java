package com.example.myapplication11;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private TextToSpeech mTextToSpeech;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String db_name;
    public int max_k;
    private float speed;
    private String url_BD;
    private Set<String> names_BD = new ArraySet<String>();
    public Context context = this;
    private Button button_word;
    private Button[] buttons = new Button[4];
    public List<String> words = new ArrayList<String>();
    public TextView textView_progress;
    public Random random = new Random();
    public SharedPreferences memory;
    private Boolean flag_pressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memory = getSharedPreferences("name_data", Context.MODE_PRIVATE);

        db_name = memory.getString("db_name", "no");
        if (db_name.equals("no"))
        {
            db_name = "BASE_10";
            SharedPreferences.Editor editor = memory.edit();
            editor.putString("db_name", db_name);
            editor.apply();
        }
        names_BD = memory.getStringSet("names_BD", new ArraySet<String>());
        if (names_BD.isEmpty())
        {
            names_BD.add(db_name);
            names_BD.add("BASE_20");
            names_BD.add("BASE_10");
            SharedPreferences.Editor editor = memory.edit();
            editor.putStringSet("names_BD", names_BD);
            editor.apply();
        }
        max_k = memory.getInt("max_k", 0);
        if (max_k == 0)
        {
            max_k = 10;
            SharedPreferences.Editor editor = memory.edit();
            editor.putInt("max_k", 10);
            editor.apply();
        }
        speed = memory.getFloat("speed", 0F);
        if (speed == 0F)
        {
            speed = 1F;
            SharedPreferences.Editor editor = memory.edit();
            editor.putFloat("speed", 1F);
            editor.apply();
        }
        url_BD = memory.getString("url_BD", "no");
        if (url_BD.equals("no"))
        {
            url_BD = "";
            SharedPreferences.Editor editor = memory.edit();
            editor.putString("url_BD", "");
            editor.apply();
        }


        button_word = findViewById(R.id.button_word);
        buttons[0] = findViewById(R.id.button_t1);
        buttons[1] = findViewById(R.id.button_t2);
        buttons[2] = findViewById(R.id.button_t3);
        buttons[3] = findViewById(R.id.button_t4);
        for (int i = 0; i < 4; i++) buttons[i].setBackgroundColor(0xffcccccc);
        button_word.setBackgroundColor(Color.GREEN);
        //button_word.setBackgroundColor(Color.CYAN);

        ConstraintLayout constraintLayout = findViewById(R.id.consraintLayout);


        TableLayout tableLayout = findViewById(R.id.tableLayout);

        mTextToSpeech = init_speech();


        button_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound(button_word.getText().toString());
            }
        });
        dbHelper = new DBHelper(getApplicationContext(), db_name, max_k, url_BD);
        //dbHelper.delete_bd(context, db_name);


        //File db_file = getDatabasePath(db_name);
        //if (!(db_file.exists())) db = dbHelper.getWritableDatabase();
        //else db = dbHelper.getReadableDatabase();

        //db = dbHelper.getReadableDatabase();

        textView_progress = findViewById(R.id.textView_progress);
        textView_progress.setTextSize(29);
        textView_progress.setText(dbHelper.view_all(true));

        words = new ArrayList<String>();
        go(words);

        for (Button b : buttons)
        {
            final Button button = b;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag_pressed) return;
                    flag_pressed = true;
                    final Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    if (dbHelper.check(button_word.getText().toString(), button.getText().toString()) == 1)
                    {
                        button.setBackgroundColor(0xff00cccc);
                        intent.putExtra("result", "ok");
                        String word = button_word.getText().toString();
                        intent.putExtra("sentence", dbHelper.get_sentence(word));
                        dbHelper.increase(word);

                    }
                    else if (dbHelper.check(button_word.getText().toString(), button.getText().toString()) == 0)
                    {
                        final String correct = dbHelper.en2ru(button_word.getText().toString());
                        for (int i = 0; i < 4; i++)
                        {
                            if (buttons[i].getText().toString().equals(correct)) buttons[i].setBackgroundColor(0xff00cccc);
                        }
                        button.setBackgroundColor(0xffcc00cc);
                        intent.putExtra("result", "no");
                        intent.putExtra("translate", button.getText().toString());
                        String word = dbHelper.ru2en(button.getText().toString());
                        intent.putExtra("word", word);
                        intent.putExtra("sentence", dbHelper.get_sentence(word));
                    }
                    else
                    {
                        alert("ERROR");
                        return;
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivityForResult(intent, 0);
                        }
                    }, 2000);

                }
            });
        }



    }

    public void go(List<String> words)
    {
        dbHelper.fill_up_list(words);
        if (words.size() == 0)
        {
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            //linearLayout.removeAllViews();
            button_word.setEnabled(false);
            button_word.setText("Все слова были успешно выучены. Можно обнулить прогресс и начать с самого начала.");
            TableLayout tableLayout = findViewById(R.id.tableLayout);
            tableLayout.setVisibility(View.GONE);
            button_word.setTextColor(0xff000000);

            return;
        }
        int buttons_count = words.size() < 4 ? words.size() : 4;
        Set<Integer> set_index = new ArraySet<Integer>();

        while (set_index.size() < buttons_count) set_index.add(random.nextInt(words.size()));

        String[] w = new String[buttons_count];
        int i = 0;
        for (int index : set_index)
        {
            w[i] = words.get(index);
            i++;
        }
        button_word.setText(w[random.nextInt(buttons_count)]);
        List<String> for_out = new ArrayList<String>();
        for(i = 0; i < buttons_count; i++) for_out.add(dbHelper.en2ru(w[i]));
        List<Integer> list_index = new ArrayList<Integer>();
        for (i = 0; i < buttons_count; i++) list_index.add(i);
        Collections.shuffle(list_index);

        for (i = 0; i < 4; i++)
        {
            buttons[i].setText("");
            buttons[i].setEnabled(false);
        }


        for (i = 0; i < buttons_count; i++)
        {
            buttons[list_index.get(i)].setText(for_out.get(i));
            buttons[list_index.get(i)].setEnabled(true);
        }



    }

    private TextToSpeech init_speech()
    {
        return new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTextToSpeech.setSpeechRate(speed);
                    //mTextToSpeech.setLanguage(Locale.ENGLISH);
                }
                else
                {
                    alert("ERROR = " + status);
                }
            }
        });
    }
    public void alert(String str)
    {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
    public void sound(String str)
    {
        mTextToSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                final AlertDialog.Builder alert1 = new AlertDialog.Builder(context);
                alert1.setTitle("Введите число от 1 до (2**32)/2 - 1");
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert1.setView(input);
                alert1.setPositiveButton("Изменить количество повторений", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        max_k = Integer.parseInt(input.getText().toString());
                        dbHelper.max_k = max_k;
                        SharedPreferences.Editor editor = memory.edit();
                        editor.putInt("max_k", max_k);
                        editor.apply();
                        textView_progress.setText(dbHelper.view_all(true));
                    }
                });
                alert1.setNegativeButton("Оставить всё как было", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert(Integer.toString(max_k));
                    }
                });
                alert1.show();
                return true;
            case R.id.menu2:
                dbHelper.nullify_progress();
                words.clear();
                go(words);
                textView_progress.setText(dbHelper.view_all(true));
                for (int i = 0; i < 4; i++) buttons[i].setBackgroundColor(0xffcccccc);
                button_word.setEnabled(true);
                TableLayout tableLayout_main = findViewById(R.id.tableLayout);
                tableLayout_main.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu3:
                final AlertDialog.Builder alert2 = new AlertDialog.Builder(context);
                String str = dbHelper.view_all(false);
                final String[] strs = str.split("\n");
                TableLayout tableLayout = new TableLayout(context);
                final TableRow[] tableRows = new TableRow[strs.length];
                for (int i = 0; i < strs.length; i++)
                {
                    tableRows[i] = new TableRow(context);
                    for (String elem : strs[i].split("---"))
                    {
                        TextView textView = new TextView(context);
                        textView.setText(elem);
                        textView.setMinWidth(25);
                        tableRows[i].addView(textView);
                    }
                    if (i % 2 == 0) tableRows[i].setBackgroundColor(0xffcccccc);
                    final int index = i;
                    tableRows[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert(strs[index]);
                        }
                    });
                    tableLayout.addView(tableRows[i]);
                }
                ScrollView scrollView = new ScrollView(context);
                scrollView.addView(tableLayout);
                alert2.setView(scrollView);
                alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert2.show();
                return true;
            case R.id.menu4:
                final AlertDialog.Builder alert_speed = new AlertDialog.Builder(context);
                alert_speed.setTitle("Введите число от 5 до 500");
                final EditText input_speed = new EditText(context);
                input_speed.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert_speed.setView(input_speed);
                alert_speed.setPositiveButton("Изменить скорость", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        speed = Float.parseFloat(input_speed.getText().toString()) / 100;
                        if ((speed < 0.05F) || (speed > 5))
                        {
                            alert("Число должно быть от 5 до 500");
                            return;
                        }
                        SharedPreferences.Editor editor = memory.edit();
                        editor.putFloat("speed", speed);
                        editor.apply();
                        mTextToSpeech = init_speech();
                    }
                });
                alert_speed.setNegativeButton("Оставить всё как было", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        alert(Integer.toString(Math.round(speed*100)));
                    }
                });
                alert_speed.show();
                return true;
            case R.id.menu5:
                final AlertDialog.Builder alert_BD = new AlertDialog.Builder(context);
                alert_BD.setTitle("Выберите базу данных для слов");
                final Spinner spinner = new Spinner(context);

                String[] BDs = new String[names_BD.size()];
                int i = 0;
                for (String elem : names_BD)
                {
                    BDs[i] = elem;
                    i++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BDs);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                alert_BD.setView(spinner);
                alert_BD.setPositiveButton("Изменить базу", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        db_name = spinner.getSelectedItem().toString();
                        dbHelper = new DBHelper(getApplicationContext(), db_name, max_k, url_BD);
                        SharedPreferences.Editor editor = memory.edit();
                        editor.putString("db_name", db_name);
                        editor.apply();
                        words.clear();
                        go(words);
                        textView_progress.setText(dbHelper.view_all(true));
                        for (int i = 0; i < 4; i++) buttons[i].setBackgroundColor(0xffcccccc);
                    }
                });
                alert_BD.setNegativeButton("Оставить всё как было", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert_BD.show();
                return true;
            default:
                return false;
        }
    }
    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (resultCode == 0)
        {
            final AlertDialog.Builder alert_exit = new AlertDialog.Builder(context);
            alert_exit.setTitle("Вы уверены что хотите выйти?");
            alert_exit.setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                }
            });
            alert_exit.show();
        }
        flag_pressed = false;
        super.onActivityResult(requestCode, resultCode, intent);
        words.clear();
        go(words);
        textView_progress.setText(dbHelper.view_all(true));
        for (int i = 0; i < 4; i++) buttons[i].setBackgroundColor(0xffcccccc);

    }
}
