package com.company.engine.window;

public class WindowOptions {

    public boolean enableCullFaces; //stops rendering faces that aren't in view
    public boolean showFps; //display and fps counter
    public boolean compatibleProfile; //if OpenGL profile is supported
    public boolean frustumCulling; //stops rendering objects outside of frustum
    public boolean showMeshLines; //display mesh's lines NOTE: This breaks particles
//    public boolean lockCursor; //TODO: keep the cursor locked to the window
    public boolean vSync; //enable vertical synchronisation
    public boolean fullscreen; //Should only be changed by Window Class
    public boolean resizable; //Whether the user can resize the window
    public boolean disableAllLighting; //prevents the rendering of all light
    public boolean applicationUpdatesCamera; //Decides whether the application should update the camera which is passed to the renderer

    public int minWidth = 0;
    public int minHeight = 0;
    public int maxWidth = 0;
    public int maxHeight = 0;
//    public int shadowMapSizeMultiplier = 1;
}