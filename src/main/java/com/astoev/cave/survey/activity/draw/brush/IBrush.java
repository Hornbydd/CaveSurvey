package com.astoev.cave.survey.activity.draw.brush;

import android.graphics.Path;

/**
 * Created by IntelliJ IDEA.
 * User: almondmendoza
 * Date: 01/12/2010
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IBrush {
    void mouseDown(Path path, float x, float y);

    void mouseMove(Path path, float x, float y);

    void mouseUp(Path path, float x, float y);
}
