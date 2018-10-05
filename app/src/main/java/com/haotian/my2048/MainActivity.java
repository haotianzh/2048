package com.haotian.my2048;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.DialogInterface.OnClickListener;

public class MainActivity extends Activity implements Game2048Layout.OnGame2048Listener{
    private Game2048Layout game2048Layout;
    private TextView mScore;
    private boolean b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("tag", "onCreate: hahahahahah!");
        Log.i("made",b?"true":"false");
        game2048Layout = (Game2048Layout) findViewById(R.id.id_game2048);
        mScore = (TextView) findViewById(R.id.id_score);
        game2048Layout.setOnGame2048Listener(this);

    }

    @Override
    public void onScoreChange(int score) {
        mScore.setText("SCORE: " + score);
    }

    @Override
    public void onGameOver() {
        new AlertDialog.Builder(this).setTitle("GOOD GAME")
                .setMessage("本次分数为： " + mScore.getText())
                .setPositiveButton("重新开始", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        game2048Layout.restart();
                    }
                }).setNegativeButton("退出", new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        }).show();
    }


}
