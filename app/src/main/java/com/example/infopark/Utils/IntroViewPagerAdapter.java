package com.example.infopark.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.infopark.R;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {
    public IntroViewPagerAdapter(Context context, List<ScreenItem> screenList) {
        this.context = context;
        this.screenList = screenList;
    }

    private Context context;
    private List<ScreenItem> screenList;


    @Override
    public int getCount() {
        return screenList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_image);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

        title.setText(screenList.get(position).getTitle());
        description.setText(screenList.get(position).getDescription());
        imgSlide.setImageResource(screenList.get(position).getScreenImage());

        container.addView(layoutScreen);

        return layoutScreen;

    }
}
