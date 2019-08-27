package com.kjh.pc.ex_0118;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class DrawerActivity extends AppCompatActivity {

    DrawerLayout layout;
    LinearLayout drawer;
    Button open, close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        layout = findViewById(R.id.layout);
        drawer = findViewById(R.id.drawer);
        open = findViewById(R.id.opendrawer);
        close = findViewById(R.id.closedrawer);

        open.setOnClickListener(click);
        close.setOnClickListener(click);

        // 손가락 드래그로 메뉴를 열고 닫을 수 없다.
        // layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // 손으로 메뉴를 열고 닫을 수 있다.
        layout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED );
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.opendrawer: // 서랍열기
                    layout.openDrawer(drawer);
                    break;

                case R.id.closedrawer: // 서랍닫기
                    layout.closeDrawer(drawer);
                    // 열려진 모든 서랍을 한 번에 다는다.
                    // layout.closeDrawers();
                    break;
            }
        }
    };
}
