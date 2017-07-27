package jp.shts.android.library.sample.clockanimationview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.threeten.bp.LocalDateTime;

import jp.shts.android.library.clockanimationview.ClockAnimationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ClockAnimationView clockAnimationView = (ClockAnimationView) findViewById(R.id.image);

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockAnimationView.startAnimation(LocalDateTime.now().withHour(2).withMinute(30));
            }
        });
    }
}
