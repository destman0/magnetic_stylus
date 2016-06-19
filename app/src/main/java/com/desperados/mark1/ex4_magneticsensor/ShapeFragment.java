package com.desperados.mark1.ex4_magneticsensor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ShapeFragment extends Fragment {

    protected TouchEventView mDrawingView;

    public ShapeFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mDrawingView = (TouchEventView) rootView.findViewById(R.id.drawingview);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_smoothline:
                mDrawingView.mCurrentShape = TouchEventView.SMOOTHLINE;
                mDrawingView.reset();
                break;
            case R.id.action_rectangle:
                mDrawingView.mCurrentShape = TouchEventView.RECTANGLE;
                mDrawingView.reset();
                break;
            case R.id.action_circle:
                mDrawingView.mCurrentShape = TouchEventView.CIRCLE;
                mDrawingView.reset();
                break;
            case R.id.action_triangle:
                mDrawingView.mCurrentShape = TouchEventView.TRIANGLE;
                mDrawingView.reset();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
