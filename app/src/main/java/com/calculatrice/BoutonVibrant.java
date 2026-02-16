package com.calculatrice;

import android.content.Context;
import android.util.AttributeSet;

public class BoutonVibrant extends androidx.appcompat.widget.AppCompatButton {
    public BoutonVibrant(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        // On déclenche la vibration automatiquement à chaque clic
        performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
        return super.performClick();
    }
}
