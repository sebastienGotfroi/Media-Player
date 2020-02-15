package View;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;

public class MusicController extends android.widget.MediaController {

    public MusicController (Context c) {
        super(c);
    }

    public void hide () {
        
    }

    public boolean dispatchKeyEvent (KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            ((Activity) getContext()).finish();
        }

        return super.dispatchKeyEvent(event);
    }
}
