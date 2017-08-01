package jp.shts.android.library.sample.clockanimationview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import jp.shts.android.library.clockanimationview.ClockAnimationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ClockAnimationView clockAnimationView = (ClockAnimationView) findViewById(R.id.image);
        clockAnimationView.setTime(0, 45);

        final Button b = (Button) findViewById(R.id.button);
        b.setText("to 2:30");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (counter) {
                    case 0:
                        clockAnimationView.animateToTime(2, 30);
                        b.setText("to 7:10");
                        break;
                    case 1:
                        clockAnimationView.animateToTime(7, 10);
                        b.setText("to 9:50");
                        break;
                    case 2:
                        clockAnimationView.animateToTime(9, 50);
                        b.setText("to Finish");
                        break;
                    default:
                        finish();
                        break;
                }
                counter++;
            }
        });
    }
    int counter=0;
}
