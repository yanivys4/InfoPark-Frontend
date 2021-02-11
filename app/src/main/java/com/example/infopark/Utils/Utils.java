package com.example.infopark.Utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.infopark.R;

/**
 * This class is utils class that serves all the other project classes.
 */
public final class Utils {

    /**
     * This function shows a toast to the user with the given message.
     * @param context the context to show the toast to.
     * @param message the message to display.
     */
    public static void showToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(context.getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be edited
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(context.getResources().getColor(R.color.white));

        toast.show();
    }

}
